package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.presentation.LineRequest;
import nextstep.subway.presentation.LineResponse;
import nextstep.subway.presentation.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "classpath:truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class SectionAcceptanceTest {

    private Long 강남역_ID;
    private Long 신사역_ID;
    private LineRequest 신분당선_request;
    private Long 신분당선_ID;
    private Long 신논현역_ID;

    @BeforeEach
    void setup() {
        강남역_ID = StationSteps.지하철역_생성("강남역").body().jsonPath().getLong("id");
        신사역_ID = StationSteps.지하철역_생성("신사역").body().jsonPath().getLong("id");
        신논현역_ID = StationSteps.지하철역_생성("신논현역").body().jsonPath().getLong("id");

        신분당선_request = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 신사역_ID, 20);
        신분당선_ID = LineSteps.createLine(신분당선_request).body().jsonPath().getLong("id");
    }

    /**
     * Given: 새로운 지하철 구간 정보를 입력하고,
     * When: 관리자가 구간을 생성하면,
     * Then: 해당 구간선이 생성된다.
     * Then: 해당 구간을 포함한 노선의 하행이, 추가된 구간의 하행과 동일하게 변경된다.
     */
    @Test
    @DisplayName("지하철 구간을 생성한다.")
    void createSection() {
        // when
        ExtractableResponse<Response> response = SectionSteps.createSection(신분당선_ID, new SectionRequest(강남역_ID, 신논현역_ID, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getLong("lineId")).isEqualTo(신분당선_ID);
        assertThat(response.body().jsonPath().getLong("downStationId")).isEqualTo(신논현역_ID);
    }

    /**
     * Given: 특장 지하철 구간 정보가 등록되어 있고,
     * When: 관리자가 구간을 추가하면,
     * Then: 해당 구간이 연결된다.
     */
    @Test
    @DisplayName("기존에 구간이 있는 상태에서 새로운 구간을 추가할 수 있다.")
    void addSection() {
        // given

        // when
        SectionSteps.createSection(신분당선_ID, new SectionRequest(신논현역_ID, 신사역_ID, 15));
        LineResponse findLine = LineSteps.findByLineId(신분당선_ID);

        // then
        assertThat(findLine.getStations().size()).isEqualTo(3);
    }

    /**
     * Given: 특장 지하철 구간 정보가 등록되어 있고,
     * When: 관리자가 구간을 삭제하면,
     * Then: 해당 구간선이 삭제된다.
     */
    @Test
    @DisplayName("지하철 구간을 삭제한다.")
    void deleteSection() {
        // given
        ExtractableResponse<Response> response = SectionSteps.createSection(신분당선_ID, new SectionRequest(강남역_ID, 신논현역_ID, 10));

        // when
        Long id = response.body().jsonPath().getLong("sectionId");
        SectionSteps.deleteSection(신분당선_ID, id);
        LineResponse findLine = LineSteps.findByLineId(신분당선_ID);

        // then
        assertThat(findLine.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(findLine.getStations().get(1).getName()).isEqualTo("신논현역");
    }

    /**
     * Given: 여러 지하철 구간이 등록된 노선이 있을 때,
     * When: 관리자가 특정 구간을 삭제하면,
     * Then: 해당 구간이 삭제된다.
     */
    @Test
    @DisplayName("지하철 노선에 포함된 역을 위치에 상관없이 삭제할 수 있다.")
    void deleteMiddleSection() {
        // given
        SectionSteps.createSection(신분당선_ID, new SectionRequest(신논현역_ID, 신사역_ID, 15));

        // when
        LineSteps.deleteStation(신분당선_ID, 신논현역_ID);
        LineResponse findLine = LineSteps.findByLineId(신분당선_ID);

        // then
        assertThat(findLine.getStations().size()).isEqualTo(2);
    }
}
