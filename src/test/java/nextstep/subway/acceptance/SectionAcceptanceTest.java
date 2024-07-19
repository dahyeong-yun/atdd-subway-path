package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.presentation.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import nextstep.subway.presentation.LineRequest;
import nextstep.subway.presentation.LineResponse;
import nextstep.subway.presentation.SectionRequest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "classpath:truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class SectionAcceptanceTest {

    private Long 강남역_ID;
    private Long 을지로4가역_ID;
    private LineRequest 신분당선_request;
    private Long 신분당선_ID;
    private Long 또다른역_ID;

    @BeforeEach
    void setup() {
        강남역_ID = StationSteps.createStation("강남역").body().jsonPath().getLong("id");
        을지로4가역_ID = StationSteps.createStation("을지로4가역").body().jsonPath().getLong("id");
        신분당선_request = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 을지로4가역_ID, 10);
        신분당선_ID = LineSteps.createLine(신분당선_request).body().jsonPath().getLong("id");
        또다른역_ID = StationSteps.createStation("또다른역").body().jsonPath().getLong("id");
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
        ExtractableResponse<Response> response = SectionSteps.createSection(신분당선_ID, new SectionRequest(을지로4가역_ID, 또다른역_ID, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getLong("lineId")).isEqualTo(신분당선_ID);
        assertThat(response.body().jsonPath().getLong("downStationId")).isEqualTo(또다른역_ID);

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
        ExtractableResponse<Response> response = SectionSteps.createSection(신분당선_ID, new SectionRequest(을지로4가역_ID, 또다른역_ID, 10));

        // when
        Long id = response.body().jsonPath().getLong("sectionId");
        SectionSteps.deleteSection(신분당선_ID, id);
        LineResponse findline = LineSteps.findByLineId(신분당선_ID);

        // then
        assertThat(findline.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(findline.getStations().get(1).getName()).isEqualTo("을지로4가역");
    }

    /**
     * Given: 특장 지하철 구간 정보가 등록되어 있고,
     * When: 관리자가 구간을 추가제하면,
     * Then: 해당 구간이 연결된다.
     */
    @Test
    @DisplayName("기존에 구간이 있는 상태에서 새로운 구간을 추가할 수 있다.")
    void addSection() {
        // given
        SectionSteps.createSection(신분당선_ID, new SectionRequest(강남역_ID, 또다른역_ID, 20));

        // when
        SectionSteps.createSection(신분당선_ID, new SectionRequest(을지로4가역_ID, 또다른역_ID, 15));
        LineResponse findLine = LineSteps.findByLineId(신분당선_ID);

        // then
        assertThat(findLine.getStations().size()).isEqualTo(3);
    }
}
