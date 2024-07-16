package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @OneToMany(mappedBy = "upStation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sectionAsUpStation;

    @OneToMany(mappedBy = "downStation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sectionAsDownStation;

    public Station(String name) {
        this.name = name;
    }
}
