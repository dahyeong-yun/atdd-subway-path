package nextstep.subway.exception;

public class SectionNotFoundException extends SubwayException {
    public SectionNotFoundException(Long id) {
        super("지하철 구간 ID " + id + " 가 존재하지 않습니다.");
    }
}
