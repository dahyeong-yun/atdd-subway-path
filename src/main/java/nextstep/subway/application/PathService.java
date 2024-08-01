package nextstep.subway.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.PathResult;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.infrastructure.SectionRepository;
import nextstep.subway.infrastructure.StationRepository;
import nextstep.subway.presentation.PathResponse;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PathService {
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public PathResponse getPath(Long source, Long target) {
        List<Section> allSection = sectionRepository.findAll();
        List<Station> allStations = stationRepository.findAll();

        PathFinder pathFinder = PathFinder.createPath(new WeightedMultigraph<>(DefaultWeightedEdge.class), allSection, allStations);
        PathResult pathResult = pathFinder.getShortestPath(source, target);
        List<Station> orderedPathStations = pathResult.getOrderdStationsByPath(allStations);

        return PathResponse.of(orderedPathStations, pathResult.getTotalDistance());
    }


}
