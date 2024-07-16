package nextstep.subway.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import nextstep.subway.infrastructure.LineRepository;
import nextstep.subway.infrastructure.StationRepository;
import nextstep.subway.presentation.LineRequest;
import nextstep.subway.presentation.LineResponse;
import nextstep.subway.presentation.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(lineRequest.getUpStationId()));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(lineRequest.getDownStationId()));

        Line createdline = Line.createLine(upStation, downStation, lineRequest);
        lineRepository.save(createdline);

        return LineResponse.of(createdline);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(id));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(id));
        line.changeName(lineUpdateRequest.getName());
        line.changeColor(lineUpdateRequest.getColor());

        lineRepository.save(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(id));
        lineRepository.delete(line);
    }
}

