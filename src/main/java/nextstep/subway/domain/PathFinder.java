package nextstep.subway.domain;

import lombok.RequiredArgsConstructor;
import nextstep.subway.exception.PathNotFoundException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@RequiredArgsConstructor
public class PathFinder {
    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public static PathFinder initializePathGraph(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Section> allSection, List<Station> allStations) {
        allStations.forEach(station -> graph.addVertex(station.getId()));
        allSection.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(
                        section.getUpStation().getId(),
                        section.getDownStation().getId()
                ),
                section.getSectionDistance().getDistance()
        ));
        return new PathFinder(graph);
    }

    public PathResult getShortestPath(Long sourceStationId, Long targetStationId) {
        if (sourceStationId.equals(targetStationId)) {
            throw new PathNotFoundException(sourceStationId, targetStationId);
        }

        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        GraphPath<Long, DefaultWeightedEdge> shortestPathStationIdsGraph = dijkstraAlg.getPath(sourceStationId, targetStationId);

        if (shortestPathStationIdsGraph == null) {
            throw new PathNotFoundException(sourceStationId, targetStationId);
        }

        List<Long> path = shortestPathStationIdsGraph.getVertexList();
        int totalDistance = (int) shortestPathStationIdsGraph.getWeight();

        return new PathResult(path, totalDistance);
    }
}
