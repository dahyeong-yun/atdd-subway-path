package nextstep.subway.domain;

import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class PathFinder {
    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public static PathFinder createPath(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Section> allSection, List<Station> allStations) {
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

    public PathResult getShortestPath(Long source, Long target) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        GraphPath<Long, DefaultWeightedEdge> shortestPath = dijkstraAlg.getPath(source, target);

        if (shortestPath == null) {
            return new PathResult(Collections.emptyList(), 0);
        }

        List<Long> path = shortestPath.getVertexList();
        int totalDistance = (int) shortestPath.getWeight(); // double을 int로 캐스팅

        return new PathResult(path, totalDistance);
    }
}
