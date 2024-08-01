package nextstep.subway.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class PathResult {
    private final List<Long> pathStationIds;
    private final int totalDistance;
    public List<Station> getOrderdStationsByPath(List<Station> allStations) {
        return pathStationIds.stream()
                .map(id -> allStations.stream()
                        .filter(station -> station.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Station not found: " + id)))
                .collect(Collectors.toList());
    }
}
