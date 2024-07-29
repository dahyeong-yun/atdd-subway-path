package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.presentation.LineRequest;
import nextstep.subway.presentation.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "classpath:truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class LineAcceptanceTest {
    private Long 강남역_ID;
    private Long 신논현역_ID;
    private Long 신사역_ID;
    private LineRequest 신분당선_request;

    @BeforeEach
    void setup() {
        강남역_ID = StationSteps.createStation("강남역").body().jsonPath().getLong("id");
        신논현역_ID = StationSteps.createStation("신논현역").body().jsonPath().getLong("id");
        신사역_ID = StationSteps.createStation("신사역").body().jsonPath().getLong("id");
        신분당선_request = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 신논현역_ID, 10);
    }

    /**
     * Given: 새로운 지하철 노선 정보를 입력하고,
     * When: 관리자가 노선을 생성하면,
     * Then: 해당 노선이 생성되고 노선 목록에 포함된다.
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        LineRequest newLine = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 신논현역_ID, 10);

        // when
        ExtractableResponse<Response> response = LineSteps.createLine(newLine);

        // then
        List<String> allLineNames = LineSteps.findAllLineNames();
        assertThat(allLineNames).containsAnyOf("신분당선");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given: 여러 개의 지하철 노선이 등록되어 있고,
     * When: 관리자가 지하철 노선 목록을 조회하면,
     * Then: 모든 지하철 노선 목록이 반환된다.
     */
    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    void retrieveAllLines() {
        // given
        LineRequest _5호선_request = new LineRequest("5호선", "bg-purple-400", 강남역_ID, 신사역_ID, 10);

        LineSteps.createLine(신분당선_request);
        LineSteps.createLine(_5호선_request);

        // when
        List<String> allLineNames = LineSteps.findAllLineNames();

        // then
        assertThat(allLineNames).contains("신분당선", "5호선");
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 조회하면,
     * Then: 해당 노선의 정보가 반환된다.
     */
    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void retrieveLine() {
        // given
        LineRequest _5호선_request = new LineRequest("5호선", "bg-purple-400", 강남역_ID, 신사역_ID, 10);
        LineSteps.createLine(신분당선_request);

        ExtractableResponse<Response> response = LineSteps.createLine(_5호선_request);
        Long _5호선_id = response.body().jsonPath().getLong("id");

        // when
        LineResponse findLine = LineSteps.findByLineId(_5호선_id);

        // then
        assertThat(findLine.getName()).isEqualTo("5호선");
        assertThat(findLine.getColor()).isEqualTo("bg-purple-400");
        assertThat(findLine.getStations().size()).isEqualTo(2);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 수정하면,
     * Then: 해당 노선의 정보가 수정된다.
     */
    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void updateLine() {
        // given
        ExtractableResponse<Response> response = LineSteps.createLine(신분당선_request);
        Long 신분당선_ID = Long.valueOf(response.body().jsonPath().getString("id"));

        // when
        LineSteps.updateLine(신분당선_ID, "2호선", "bg-red-700");
        LineResponse findLine = LineSteps.findByLineId(신분당선_ID);

        // then
        assertThat(findLine.getName()).isEqualTo("2호선");
        assertThat(findLine.getColor()).isEqualTo("bg-red-700");
        assertThat(findLine.getStations().size()).isEqualTo(2);
    }


    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 삭제하면,
     * Then: 관리자가 해당 노선을 삭제하면,
     */
    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void deleteLine() {
        // given
        ExtractableResponse<Response> response = LineSteps.createLine(신분당선_request);
        String 신분당선_ID = response.body().jsonPath().getString("id");

        // when
        LineSteps.deleteLine(신분당선_ID);
        List<String> allLineNames = LineSteps.findAllLineNames();

        // then
        assertThat(allLineNames).doesNotContain("신분당선");
        assertThat(allLineNames.size()).isEqualTo(0);
    }
}
