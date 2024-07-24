package nextstep.subway.exception;

public class StationNotFoundException extends SubwayException {
    public StationNotFoundException(Long id) {
        super("지하철 역 ID : " + id + "가 존재하지 않습니다.");
    }
}
