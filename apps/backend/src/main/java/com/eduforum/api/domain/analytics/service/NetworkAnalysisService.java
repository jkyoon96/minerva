package com.eduforum.api.domain.analytics.service;

import com.eduforum.api.domain.analytics.dto.network.*;
import com.eduforum.api.domain.analytics.entity.*;
import com.eduforum.api.domain.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetworkAnalysisService {
    private final InteractionLogRepository logRepository;
    private final NetworkNodeRepository nodeRepository;
    private final NetworkEdgeRepository edgeRepository;
    private final StudentClusterRepository clusterRepository;
    private final Random random = new Random();

    @Transactional
    public void logInteraction(InteractionLogRequest request) {
        InteractionLog log = InteractionLog.builder()
            .courseId(request.getCourseId())
            .sessionId(request.getSessionId())
            .fromStudentId(request.getFromStudentId())
            .toStudentId(request.getToStudentId())
            .interactionType(request.getInteractionType())
            .interactionTime(OffsetDateTime.now())
            .weight(request.getWeight())
            .context(request.getContext())
            .metadata(request.getMetadata() != null ? request.getMetadata() : Map.of())
            .build();
        
        logRepository.save(log);
        updateNetworkGraph(request.getCourseId(), request.getFromStudentId(), request.getToStudentId(), request.getWeight());
    }

    @Transactional(readOnly = true)
    public NetworkGraphResponse getNetworkGraph(Long courseId) {
        log.info("Getting network graph for course: {}", courseId);
        
        List<NetworkNode> nodes = nodeRepository.findByCourseId(courseId);
        List<NetworkEdge> edges = edgeRepository.findByCourseId(courseId);
        
        int totalNodes = nodes.size();
        int totalEdges = edges.size();
        double density = calculateDensity(totalNodes, totalEdges);
        double avgDegree = totalNodes > 0 ? (double) totalEdges * 2 / totalNodes : 0.0;
        
        return NetworkGraphResponse.builder()
            .courseId(courseId)
            .totalNodes(totalNodes)
            .totalEdges(totalEdges)
            .density(density)
            .averageDegree(avgDegree)
            .nodes(nodes.stream().map(this::toNodeData).collect(Collectors.toList()))
            .edges(edges.stream().map(this::toEdgeData).collect(Collectors.toList()))
            .statistics(Map.of("clustered", nodes.stream().filter(n -> n.getClusterId() != null).count()))
            .build();
    }

    @Transactional
    public void analyzeNetwork(Long courseId) {
        log.info("Analyzing network for course: {}", courseId);
        
        List<NetworkNode> nodes = nodeRepository.findByCourseId(courseId);
        List<NetworkEdge> edges = edgeRepository.findByCourseId(courseId);
        
        // Simulate centrality calculations
        for (NetworkNode node : nodes) {
            node.updateCentralities(
                random.nextDouble(),
                random.nextDouble() * 0.5,
                random.nextDouble() * 0.8
            );
            node.setClusteringCoefficient(random.nextDouble());
            nodeRepository.save(node);
        }
        
        // Simulate clustering
        performClustering(courseId, nodes);
    }

    @Transactional(readOnly = true)
    public List<ClusterResponse> getClusters(Long courseId) {
        return clusterRepository.findByCourseIdOrderByClusterNumberAsc(courseId).stream()
            .map(this::toClusterResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentConnectionResponse getStudentConnections(Long courseId, Long studentId) {
        NetworkNode node = nodeRepository.findByCourseIdAndStudentId(courseId, studentId)
            .orElse(createEmptyNode(courseId, studentId));
        
        List<NetworkEdge> connections = edgeRepository.findByStudentId(courseId, studentId);
        
        return StudentConnectionResponse.builder()
            .studentId(studentId)
            .courseId(courseId)
            .totalConnections(node.getTotalConnections())
            .degreeCentrality(node.getDegreeCentrality())
            .betweennessCentrality(node.getBetweennessCentrality())
            .closenessCentrality(node.getClosenessCentrality())
            .clusteringCoefficient(node.getClusteringCoefficient())
            .clusterId(node.getClusterId())
            .connections(connections.stream().map(this::toConnection).collect(Collectors.toList()))
            .interactionSummary(Map.of("total", connections.size()))
            .isolated(node.isIsolated())
            .hub(node.isHub())
            .build();
    }

    private void updateNetworkGraph(Long courseId, Long fromId, Long toId, Integer weight) {
        NetworkEdge edge = edgeRepository.findByCourseIdAndFromStudentIdAndToStudentId(courseId, fromId, toId)
            .orElse(NetworkEdge.builder()
                .courseId(courseId)
                .fromStudentId(fromId)
                .toStudentId(toId)
                .interactionCount(0)
                .totalWeight(0)
                .edgeAttributes(new HashMap<>())
                .build());
        
        edge.incrementInteraction(weight);
        edgeRepository.save(edge);
        
        updateNodeConnections(courseId, fromId);
        updateNodeConnections(courseId, toId);
    }

    private void updateNodeConnections(Long courseId, Long studentId) {
        NetworkNode node = nodeRepository.findByCourseIdAndStudentId(courseId, studentId)
            .orElse(NetworkNode.builder()
                .courseId(courseId)
                .studentId(studentId)
                .totalConnections(0)
                .nodeAttributes(new HashMap<>())
                .build());
        
        int connections = edgeRepository.findByStudentId(courseId, studentId).size();
        node.setTotalConnections(connections);
        nodeRepository.save(node);
    }

    private void performClustering(Long courseId, List<NetworkNode> nodes) {
        int numClusters = Math.max(1, nodes.size() / 10);
        
        for (int i = 0; i < numClusters; i++) {
            StudentCluster cluster = StudentCluster.builder()
                .courseId(courseId)
                .clusterName("Cluster " + (i + 1))
                .clusterNumber(i + 1)
                .memberCount(0)
                .avgInteractionScore(random.nextDouble() * 100)
                .density(random.nextDouble())
                .description("Auto-generated cluster")
                .memberIds(new ArrayList<>())
                .clusterStats(Map.of("size", 0))
                .build();
            
            clusterRepository.save(cluster);
        }
    }

    private double calculateDensity(int nodes, int edges) {
        if (nodes <= 1) return 0.0;
        int maxEdges = nodes * (nodes - 1) / 2;
        return maxEdges > 0 ? (double) edges / maxEdges : 0.0;
    }

    private NetworkNode createEmptyNode(Long courseId, Long studentId) {
        return NetworkNode.builder()
            .courseId(courseId)
            .studentId(studentId)
            .totalConnections(0)
            .degreeCentrality(0.0)
            .betweennessCentrality(0.0)
            .closenessCentrality(0.0)
            .clusteringCoefficient(0.0)
            .nodeAttributes(Map.of())
            .build();
    }

    private NetworkGraphResponse.NodeData toNodeData(NetworkNode node) {
        return NetworkGraphResponse.NodeData.builder()
            .id(node.getId())
            .studentId(node.getStudentId())
            .degreeCentrality(node.getDegreeCentrality())
            .betweennessCentrality(node.getBetweennessCentrality())
            .closenessCentrality(node.getClosenessCentrality())
            .clusterId(node.getClusterId())
            .totalConnections(node.getTotalConnections())
            .attributes(node.getNodeAttributes())
            .build();
    }

    private NetworkGraphResponse.EdgeData toEdgeData(NetworkEdge edge) {
        return NetworkGraphResponse.EdgeData.builder()
            .id(edge.getId())
            .fromStudentId(edge.getFromStudentId())
            .toStudentId(edge.getToStudentId())
            .interactionCount(edge.getInteractionCount())
            .totalWeight(edge.getTotalWeight())
            .strength(edge.getStrength())
            .attributes(edge.getEdgeAttributes())
            .build();
    }

    private ClusterResponse toClusterResponse(StudentCluster cluster) {
        return ClusterResponse.builder()
            .id(cluster.getId())
            .courseId(cluster.getCourseId())
            .clusterName(cluster.getClusterName())
            .clusterNumber(cluster.getClusterNumber())
            .memberCount(cluster.getMemberCount())
            .avgInteractionScore(cluster.getAvgInteractionScore())
            .density(cluster.getDensity())
            .description(cluster.getDescription())
            .memberIds(cluster.getMemberIds())
            .clusterStats(cluster.getClusterStats())
            .createdAt(cluster.getCreatedAt())
            .build();
    }

    private StudentConnectionResponse.Connection toConnection(NetworkEdge edge) {
        return StudentConnectionResponse.Connection.builder()
            .connectedStudentId(edge.getToStudentId())
            .interactionCount(edge.getInteractionCount())
            .totalWeight(edge.getTotalWeight())
            .strength(edge.isStrong() ? "STRONG" : edge.isWeak() ? "WEAK" : "MEDIUM")
            .build();
    }
}
