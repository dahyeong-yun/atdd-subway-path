package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.presentation.LineRequest;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line createLine(Station upStation, Station downStation, LineRequest lineRequest) {
        Line createdLine = new Line(lineRequest.getName(), lineRequest.getColor());

        Sections sections = createdLine.getSections();
        Section requestSection = Section.createSection(
                createdLine,
                upStation,
                downStation,
                lineRequest.getDistance()
        );
        sections.addSections(requestSection);

        return createdLine;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }
}
