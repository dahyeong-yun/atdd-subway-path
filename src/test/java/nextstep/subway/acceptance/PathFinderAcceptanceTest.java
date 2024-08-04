package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.presentation.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "classpath:truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PathFinderAcceptanceTest {

    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 교대역_ID;
    private Long 남부터미널역_ID;

    private Long _2호선_ID;
    private Long _3호선_ID;
    private Long 신분당선_ID;

    private LineRequest 신분당선_request;
    private LineRequest _2호선_request;
    private LineRequest _3호선_request;

    @BeforeEach
    void setup() {
        강남역_ID = StationSteps.createStation("강남역").body().jsonPath().getLong("id");
        양재역_ID = StationSteps.createStation("양재역").body().jsonPath().getLong("id");
        교대역_ID = StationSteps.createStation("교대역").body().jsonPath().getLong("id");
        남부터미널역_ID = StationSteps.createStation("남부터미널역").body().jsonPath().getLong("id");

        신분당선_request = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 양재역_ID, 20);
        _2호선_request = new LineRequest("2호선", "bg-red-600", 교대역_ID, 강남역_ID, 15);
        _3호선_request = new LineRequest("3호선", "bg-red-600", 교대역_ID, 남부터미널역_ID, 20);

        신분당선_ID = LineSteps.createLine(신분당선_request).body().jsonPath().getLong("id");
        _2호선_ID = LineSteps.createLine(_2호선_request).body().jsonPath().getLong("id");
        _3호선_ID = LineSteps.createLine(_3호선_request).body().jsonPath().getLong("id");
    }

    /**
     * 교대역    --- *2호선* (15) ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * (20)                        (20)
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
     */

    /**
     * Given: 지하철 노선과 구간이 등록되어 있고,
     * When: 시작 역과 도착 역을 전달하면,
     * Then: 경로와 거리를 알 수 있다.
     */
    @Test
    @DisplayName("지하철 노선과 구간이 등록되어 있고, 시작 역과 도착 역을 전달하면 경로와 거리를 알 수 있다.")
    void findShortestPath() {
        // given

        // when
        ExtractableResponse<Response> response = PathSteps.findPath(교대역_ID, 양재역_ID);
        List<String> stationNames = response.jsonPath().getList("stations.name");
        List<Long> stationIds = response.jsonPath().getList("stations.id", Long.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationNames).contains("교대역", "강남역", "양재역");
        assertThat(stationIds).containsExactly(교대역_ID, 강남역_ID, 양재역_ID);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(35);
    }
}
