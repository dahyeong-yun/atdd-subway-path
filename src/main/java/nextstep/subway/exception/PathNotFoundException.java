package nextstep.subway.exception;

public class PathNotFoundException extends SubwayException {
    public PathNotFoundException(Long sourceId, Long targetId) {
        super(String.format("출발역(ID: %d)에서 도착역(ID: %d)까지의 경로를 찾을 수 없습니다.", sourceId, targetId));
    }
}
