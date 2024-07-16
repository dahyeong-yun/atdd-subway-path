package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Integer distance;

    private Integer sort;

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section createSection(Line line, Station upStation, Station downStation, Integer distance) {
        assert line != null;
        assert upStation != null;
        assert downStation != null;
        assert distance > 0;

        return new Section(line, upStation, downStation, distance);
    }

    // line과 upStation이 동일한 Section은 존재할 수 없음
    // public Section createSection
}
