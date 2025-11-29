package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.Role;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.entity.UserRole;
import com.eduforum.api.domain.auth.entity.UserStatus;
import com.eduforum.api.domain.auth.repository.RoleRepository;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.auth.repository.UserRoleRepository;
import com.eduforum.api.domain.course.dto.BulkEnrollmentRequest;
import com.eduforum.api.domain.course.dto.BulkEnrollmentResult;
import com.eduforum.api.domain.course.dto.EnrollmentPreview;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.Enrollment;
import com.eduforum.api.domain.course.entity.EnrollmentRole;
import com.eduforum.api.domain.course.entity.EnrollmentStatus;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.course.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for bulk enrollment operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BulkEnrollmentService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int PASSWORD_LENGTH = 12;

    /**
     * Preview bulk enrollment from CSV
     */
    @Transactional(readOnly = true)
    public EnrollmentPreview previewBulkEnrollment(Long courseId, BulkEnrollmentRequest request) {
        log.info("Previewing bulk enrollment for course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        List<CsvEntry> entries = parseCSV(request.getCsvContent());
        List<EnrollmentPreview.PreviewItem> items = new ArrayList<>();

        int validCount = 0;
        int invalidCount = 0;
        int newUsers = 0;
        int existingUsers = 0;

        for (CsvEntry entry : entries) {
            EnrollmentPreview.PreviewItem item = validateEntry(entry);
            items.add(item);

            if (item.getIsValid()) {
                validCount++;
                if (item.getIsNewUser()) {
                    newUsers++;
                } else {
                    existingUsers++;
                }
            } else {
                invalidCount++;
            }
        }

        return EnrollmentPreview.builder()
            .totalItems(entries.size())
            .validItems(validCount)
            .invalidItems(invalidCount)
            .newUsers(newUsers)
            .existingUsers(existingUsers)
            .items(items)
            .build();
    }

    /**
     * Perform bulk enrollment
     */
    @Transactional
    public BulkEnrollmentResult bulkEnroll(Long courseId, BulkEnrollmentRequest request) {
        log.info("Performing bulk enrollment for course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        List<CsvEntry> entries = parseCSV(request.getCsvContent());
        List<BulkEnrollmentResult.EnrollmentItem> successItems = new ArrayList<>();
        List<BulkEnrollmentResult.EnrollmentError> failureItems = new ArrayList<>();

        int newUsersCreated = 0;
        int existingUsersEnrolled = 0;

        for (CsvEntry entry : entries) {
            try {
                User user = userRepository.findByEmail(entry.email).orElse(null);
                boolean isNewUser = (user == null);
                String tempPassword = null;

                if (isNewUser) {
                    // Create new user
                    tempPassword = generateTempPassword();
                    user = createUser(entry, tempPassword);
                    newUsersCreated++;
                } else {
                    // Check if already enrolled
                    boolean alreadyEnrolled = enrollmentRepository.existsByCourseAndUser(course, user);
                    if (alreadyEnrolled) {
                        if (request.getSkipExisting()) {
                            log.debug("User already enrolled, skipping: {}", entry.email);
                            continue;
                        } else {
                            throw new BusinessException(ErrorCode.ALREADY_ENROLLED,
                                "사용자가 이미 등록되어 있습니다: " + entry.email);
                        }
                    }
                    existingUsersEnrolled++;
                }

                // Enroll user in course
                enrollUser(course, user, entry.role);

                successItems.add(BulkEnrollmentResult.EnrollmentItem.builder()
                    .email(entry.email)
                    .name(user.getFullName())
                    .role(entry.role)
                    .userId(user.getId())
                    .tempPassword(tempPassword)
                    .isNewUser(isNewUser)
                    .build());

                // TODO: Send email if requested
                if (request.getSendEmail() && isNewUser) {
                    // Email sending logic would go here
                    log.info("TODO: Send welcome email to {} with temp password", entry.email);
                }

            } catch (Exception e) {
                log.error("Failed to enroll user: {}", entry.email, e);
                failureItems.add(BulkEnrollmentResult.EnrollmentError.builder()
                    .lineNumber(entry.lineNumber)
                    .email(entry.email)
                    .errorMessage(e.getMessage())
                    .build());
            }
        }

        log.info("Bulk enrollment completed: {} success, {} failures",
            successItems.size(), failureItems.size());

        return BulkEnrollmentResult.builder()
            .totalProcessed(entries.size())
            .successCount(successItems.size())
            .failureCount(failureItems.size())
            .newUsersCreated(newUsersCreated)
            .existingUsersEnrolled(existingUsersEnrolled)
            .successItems(successItems)
            .failureItems(failureItems)
            .build();
    }

    // Helper methods

    /**
     * Parse CSV content into entries
     */
    private List<CsvEntry> parseCSV(String csvContent) {
        List<CsvEntry> entries = new ArrayList<>();
        String[] lines = csvContent.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 4) {
                log.warn("Invalid CSV line {}: {}", i + 1, line);
                continue;
            }

            entries.add(new CsvEntry(
                i + 1,
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim()
            ));
        }

        return entries;
    }

    /**
     * Validate CSV entry
     */
    private EnrollmentPreview.PreviewItem validateEntry(CsvEntry entry) {
        String errorMessage = null;
        boolean isValid = true;
        boolean isNewUser = false;

        // Validate email
        if (!EMAIL_PATTERN.matcher(entry.email).matches()) {
            errorMessage = "Invalid email format";
            isValid = false;
        }

        // Validate role
        if (isValid && !isValidRole(entry.role)) {
            errorMessage = "Invalid role: " + entry.role;
            isValid = false;
        }

        // Check if user exists
        if (isValid) {
            User existingUser = userRepository.findByEmail(entry.email).orElse(null);
            isNewUser = (existingUser == null);
        }

        return EnrollmentPreview.PreviewItem.builder()
            .lineNumber(entry.lineNumber)
            .email(entry.email)
            .firstName(entry.firstName)
            .lastName(entry.lastName)
            .role(entry.role)
            .isValid(isValid)
            .isNewUser(isNewUser)
            .errorMessage(errorMessage)
            .build();
    }

    /**
     * Check if role is valid
     */
    private boolean isValidRole(String roleName) {
        try {
            EnrollmentRole.valueOf(roleName.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Create new user
     */
    private User createUser(CsvEntry entry, String tempPassword) {
        User user = User.builder()
            .email(entry.email)
            .passwordHash(passwordEncoder.encode(tempPassword))
            .firstName(entry.firstName)
            .lastName(entry.lastName)
            .status(UserStatus.ACTIVE)
            .build();

        user = userRepository.save(user);

        // Assign role
        Role role = roleRepository.findByName(entry.role)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
                "역할을 찾을 수 없습니다: " + entry.role));

        UserRole userRole = UserRole.builder()
            .user(user)
            .role(role)
            .build();

        userRoleRepository.save(userRole);

        log.info("Created new user: {}", entry.email);
        return user;
    }

    /**
     * Enroll user in course
     */
    private void enrollUser(Course course, User user, String roleName) {
        EnrollmentRole enrollmentRole = EnrollmentRole.valueOf(roleName.toUpperCase());

        Enrollment enrollment = Enrollment.builder()
            .course(course)
            .user(user)
            .role(enrollmentRole)
            .status(EnrollmentStatus.ACTIVE)
            .build();

        enrollmentRepository.save(enrollment);
        log.info("Enrolled user {} in course {} as {}", user.getEmail(), course.getId(), enrollmentRole);
    }

    /**
     * Generate temporary password
     */
    private String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }

        return password.toString();
    }

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
            .filter(c -> !c.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }

    private void validateProfessorAccess(Course course) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!course.getProfessor().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "코스 교수만 접근할 수 있습니다");
        }
    }

    /**
     * Internal CSV entry class
     */
    private static class CsvEntry {
        int lineNumber;
        String email;
        String firstName;
        String lastName;
        String role;

        CsvEntry(int lineNumber, String email, String firstName, String lastName, String role) {
            this.lineNumber = lineNumber;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }
    }
}
