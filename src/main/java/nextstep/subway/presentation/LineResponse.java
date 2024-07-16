package nextstep.subway.presentation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.Line;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public static LineResponse of(Line line) {
        List<StationResponse> stations = List.of(
                StationResponse.of(line.getUpStation()),
                StationResponse.of(line.getDownStation())
        );
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }
}
