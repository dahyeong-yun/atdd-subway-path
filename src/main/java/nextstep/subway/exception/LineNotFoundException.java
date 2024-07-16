package nextstep.subway.exception;

public class LineNotFoundException extends SubwayException {
    public LineNotFoundException(Long id) {
        super("지하철 노선 ID " + id + " 가 존재하지 않습니다.");
    }
}
