package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.email.EmailService;
import com.eduforum.api.common.email.dto.EmailRequest;
import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.dto.CourseJoinRequest;
import com.eduforum.api.domain.course.dto.EnrollmentResponse;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.Enrollment;
import com.eduforum.api.domain.course.entity.EnrollmentStatus;
import com.eduforum.api.domain.course.entity.InviteLink;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.course.repository.EnrollmentRepository;
import com.eduforum.api.domain.course.repository.InviteLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final InviteLinkRepository inviteLinkRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    public EnrollmentResponse joinCourse(CourseJoinRequest request) {
        log.info("Joining course with invite code: {}", request.getInviteCode());

        User currentUser = getCurrentUser();

        // Find and validate invite link
        InviteLink inviteLink = inviteLinkRepository.findByCode(request.getInviteCode())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "유효하지 않은 초대 코드입니다"));

        if (!inviteLink.canBeUsed()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                "초대 링크가 만료되었거나 사용 가능한 횟수를 초과했습니다");
        }

        Course course = inviteLink.getCourse();

        // Check if already enrolled
        if (enrollmentRepository.existsByUserAndCourse(currentUser, course)) {
            throw new BusinessException(ErrorCode.ALREADY_ENROLLED);
        }

        // Check course capacity
        Long currentStudents = enrollmentRepository.countActiveStudents(course);
        if (currentStudents >= course.getMaxStudents()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_CLOSED, "코스 정원이 가득 찼습니다");
        }

        // Create enrollment
        Enrollment enrollment = Enrollment.builder()
            .user(currentUser)
            .course(course)
            .role(inviteLink.getRole())
            .status(EnrollmentStatus.ACTIVE)
            .build();

        enrollment = enrollmentRepository.save(enrollment);

        // Increment invite link usage
        inviteLink.incrementUsage();
        inviteLinkRepository.save(inviteLink);

        log.info("User {} joined course {} successfully", currentUser.getEmail(), course.getId());

        return mapToEnrollmentResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyCourses() {
        User currentUser = getCurrentUser();

        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByUser(currentUser);
        return enrollments.stream()
            .map(this::mapToEnrollmentResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<EnrollmentResponse> bulkEnrollFromCsv(Long courseId, MultipartFile file) {
        log.info("Bulk enrolling students for course: {} from CSV", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        List<EnrollmentResponse> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip header
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = line.split(",");
                    if (parts.length < 3) {
                        log.warn("Invalid CSV format at line {}: {}", lineNumber, line);
                        continue;
                    }

                    String email = parts[0].trim();
                    String firstName = parts[1].trim();
                    String lastName = parts[2].trim();

                    // Find user (사용자가 없으면 초대 이메일 발송)
                    User user = userRepository.findByEmail(email).orElse(null);

                    if (user == null) {
                        // 사용자가 없으면 초대 이메일 발송
                        sendCourseInvitationEmail(email, firstName, lastName, course);
                        log.info("Invitation email sent to non-existing user: {}", email);
                        continue;
                    }

                    // Check if already enrolled
                    if (enrollmentRepository.existsByUserAndCourse(user, course)) {
                        log.info("User {} already enrolled in course {}", email, courseId);
                        continue;
                    }

                    // Create enrollment
                    Enrollment enrollment = Enrollment.builder()
                        .user(user)
                        .course(course)
                        .role(com.eduforum.api.domain.course.entity.EnrollmentRole.STUDENT)
                        .status(EnrollmentStatus.ACTIVE)
                        .build();

                    enrollment = enrollmentRepository.save(enrollment);
                    results.add(mapToEnrollmentResponse(enrollment));

                    log.info("Enrolled user {} in course {}", email, courseId);

                } catch (Exception e) {
                    log.error("Error processing line {}: {}", lineNumber, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error reading CSV file", e);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "CSV 파일을 읽는 중 오류가 발생했습니다");
        }

        log.info("Bulk enrollment completed. {} students enrolled.", results.size());
        return results;
    }

    // Helper methods

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
            .filter(c -> !c.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateProfessorAccess(Course course) {
        User currentUser = getCurrentUser();
        if (!course.getProfessor().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
            .id(enrollment.getId())
            .course(EnrollmentResponse.CourseInfo.builder()
                .id(enrollment.getCourse().getId())
                .code(enrollment.getCourse().getCode())
                .title(enrollment.getCourse().getTitle())
                .semester(enrollment.getCourse().getSemester())
                .year(enrollment.getCourse().getYear())
                .thumbnailUrl(enrollment.getCourse().getThumbnailUrl())
                .build())
            .student(EnrollmentResponse.StudentInfo.builder()
                .id(enrollment.getUser().getId())
                .email(enrollment.getUser().getEmail())
                .name(enrollment.getUser().getFullName())
                .profileImageUrl(enrollment.getUser().getProfileImageUrl())
                .build())
            .role(enrollment.getRole().name())
            .status(enrollment.getStatus().name())
            .joinedAt(enrollment.getJoinedAt())
            .build();
    }

    /**
     * 코스 초대 이메일 발송
     */
    private void sendCourseInvitationEmail(String email, String firstName, String lastName, Course course) {
        try {
            String userName = (firstName + " " + lastName).trim();
            if (userName.isEmpty()) {
                userName = email;
            }

            // 초대 링크 생성 (InviteLink가 있다면 해당 코드 사용)
            String enrollmentUrl = frontendUrl + "/courses/" + course.getId() + "/join";

            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("instructorName", course.getProfessor().getFullName());
            variables.put("courseTitle", course.getTitle());
            variables.put("courseCode", course.getCode());
            variables.put("courseDescription", course.getDescription() != null ? course.getDescription() : "");
            variables.put("semester", course.getSemester());
            variables.put("startDate", course.getStartDate() != null ? course.getStartDate().toString() : "TBD");
            variables.put("enrollmentUrl", enrollmentUrl);
            variables.put("expiresIn", "7");

            EmailRequest request = EmailRequest.builder()
                    .to(email)
                    .templateName("course-invitation")
                    .variables(variables)
                    .build();

            emailService.sendAsync(request);

            log.info("Course invitation email queued for: {}", email);
        } catch (Exception e) {
            log.error("Failed to send course invitation email to: {}", email, e);
            // 이메일 발송 실패는 등록 프로세스를 중단하지 않음
        }
    }
}
