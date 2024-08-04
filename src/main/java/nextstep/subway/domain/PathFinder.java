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
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public static PathFinder initializePathGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> allSection, List<Station> allStations) {
        allStations.forEach(graph::addVertex);
        allSection.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getSectionDistance().getDistance()
        ));
        return new PathFinder(graph);
    }

    public PathResult getShortestPath(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new PathNotFoundException(sourceStation.getId(), targetStation.getId());
        }

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> shortestPathStationGraph = dijkstraAlg.getPath(sourceStation, targetStation);

        if (shortestPathStationGraph == null) {
            throw new PathNotFoundException(sourceStation.getId(), targetStation.getId());
        }

        List<Station> path = shortestPathStationGraph.getVertexList();
        int totalDistance = (int) shortestPathStationGraph.getWeight();

        return new PathResult(path, totalDistance);
    }
}
