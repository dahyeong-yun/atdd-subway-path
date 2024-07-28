package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.InvalidSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    private Station 강남역;
    private Station 신논현역;
    private Station 신사역;
    private Station 판교역;
    private Line 신분당선;
    private Section 새로운구간;

    @BeforeEach
    void setup() {
        강남역 = new Station("강남역");
        신논현역 = new Station("신논현역");
        신사역 = new Station("신사역");
        판교역 = new Station("판교역");
        신분당선 = new Line("신분당선", "bg-red-600");
        새로운구간 = Section.createSection(
                신분당선,
                강남역,
                신논현역,
                3
        );
    }

    @Test
    @DisplayName("지하철 최초 구간 추가")
    void addSectionToEmptySections() {
        // given
        Sections 신분당선구간 = 신분당선.getSections();

        // when
        신분당선.addSection(새로운구간);

        // then
        assertThat(신분당선구간.getStations().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철 노선의 중간역 삭제")
    void deleteMiddleStation() {
        // given
        신분당선.addSection(새로운구간);
        신분당선.addSection(Section.createSection(신분당선, 신논현역, 신사역, 2));

        // when
        신분당선.deleteStation(신논현역);

        // then
        Sections 신분당선구간 = 신분당선.getSections();
        assertThat(신분당선구간.getStations().size()).isEqualTo(2);
        assertThat(신분당선구간.getStations()).containsExactly(강남역, 신사역);
    }

    @Test
    @DisplayName("지하철 노선의 첫 역 삭제")
    void deleteFirstStation() {
        // given
        신분당선.addSection(새로운구간);
        신분당선.addSection(Section.createSection(신분당선, 신논현역, 신사역, 2));

        // when
        신분당선.deleteStation(강남역);

        // then
        Sections 신분당선구간 = 신분당선.getSections();
        assertThat(신분당선구간.getStations().size()).isEqualTo(2);
        assertThat(신분당선구간.getStations()).containsExactly(신논현역, 신사역);
    }

    @Test
    @DisplayName("지하철 노선의 마지막 역 삭제")
    void deleteLastStation() {
        // given
        신분당선.addSection(새로운구간);
        신분당선.addSection(Section.createSection(신분당선, 신논현역, 신사역, 2));

        // when
        신분당선.deleteStation(신사역);

        // then
        Sections 신분당선구간 = 신분당선.getSections();
        assertThat(신분당선구간.getStations().size()).isEqualTo(2);
        assertThat(신분당선구간.getStations()).containsExactly(강남역, 신논현역);
    }

    @Test
    @DisplayName("지하철 노선에 없는 역 삭제 시도")
    void deleteNonExistentStation() {
        // given
        신분당선.addSection(새로운구간);

        // when & then
        assertThatThrownBy(() -> 신분당선.deleteStation(판교역))
                .isInstanceOf(InvalidSectionException.class)
                .hasMessage("노선에 포함되지 않은 역을 제거할 수 없습니다.");
    }
}
