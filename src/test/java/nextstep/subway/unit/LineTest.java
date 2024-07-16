package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LineTest {
    private Station 강남역;
    private Station 신논현역;
    private Line 신분당선;
    private Section 새로운구간;

    @BeforeEach
    void setup() {
        강남역 = new Station("강남역");
        신논현역 = new Station("신논현역");
        신분당선 = new Line("신분당선", "bg-red-600");
        새로운구간 = Section.createSection(
                신분당선,
                강남역,
                신논현역,
                1
        );
    }

    @Test
    void addSection() {
        // given
        Sections 신분당선구간 = 신분당선.getSections();

        // when
        신분당선구간.addSections(새로운구간);

        // then
        assertThat(신분당선구간.size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        // given
        Sections 신분당선구간 = 신분당선.getSections();
        신분당선구간.addSections(새로운구간);

        // when
        List<Station> 신분당선전체역 = 신분당선구간.getStations();

        // then
        assertThat(신분당선전체역.size()).isEqualTo(2);
    }

    @Test
    void removeSection() {
        // given
        Sections 신분당선구간 = 신분당선.getSections();
        신분당선구간.addSections(새로운구간);

        // when
        신분당선구간.deleteLastSection();

        // then
        assertThat(신분당선구간.size()).isEqualTo(0);
    }
}
