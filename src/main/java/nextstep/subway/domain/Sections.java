package nextstep.subway.domain;

import nextstep.subway.exception.InvalidSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        validateSectionDistance(newSection);

        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        List<Station> stations = getStations();
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        boolean isUpStationConnected = stations.contains(upStation);
        boolean isDownStationConnected = stations.contains(downStation);

        validateStationConnections(isUpStationConnected, isDownStationConnected);

        if (isUpStationConnected) {
            addSectionWithConnectedUpStation(newSection);
            return;
        }
        addSectionWithConnectedDownStation(newSection);
    }

    public void deleteLastSection() {
        if (!sections.isEmpty()) {
            sections.remove(sections.size() - 1);
        } else {
            throw new InvalidSectionException("삭제할 수 있는 지하철 구간이 없습니다.");
        }
    }

    public int size() {
        return sections.size();
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateStationConnections(boolean isUpStationConnected, boolean isDownStationConnected) {
        if (!isUpStationConnected && !isDownStationConnected) {
            throw new InvalidSectionException("새로운 구간의 양쪽 역 모두 기존 노선에 연결되어 있지 않습니다.");
        }
        if (isUpStationConnected && isDownStationConnected) {
            throw new InvalidSectionException("새로운 구간이 기존 구간을 완전히 포함합니다.");
        }
    }

    private void validateSectionDistance(Section newSection) {
        double newDistance = newSection.getDistance();
        if (newDistance <= 0) {
            throw new InvalidSectionException("구간 거리는 0보다 커야 합니다.");
        }
        // 여기에 필요한 경우 추가적인 거리 검증 로직을 구현할 수 있습니다.
    }

    private void updateSection(Section existingSection, Section newSection, boolean isUpStationConnected) {
        Station newUpStation = newSection.getUpStation();
        Station newDownStation = newSection.getDownStation();
        Integer newDistance = newSection.getDistance();

        if (existingSection.getDistance() <= newDistance) {
            throw new InvalidSectionException("기존 구간의 길이가 새 구간보다 길어야 합니다.");
        }

        Section updatedSection;
        if (isUpStationConnected) {
            updatedSection = Section.createSection(
                    existingSection.getLine(),
                    newDownStation,
                    existingSection.getDownStation(),
                    existingSection.getDistance() - newDistance
            );
        } else {
            updatedSection = Section.createSection(
                    existingSection.getLine(),
                    existingSection.getUpStation(),
                    newUpStation,
                    existingSection.getDistance() - newDistance
            );
        }

        sections.add(newSection);
        sections.add(updatedSection);
        sections.remove(existingSection);
    }

    private void addSectionWithConnectedUpStation(Section newSection) {
        Station newUpStation = newSection.getUpStation();

        for (Section section : sections) {
            if (section.getUpStation().equals(newUpStation)) {
                updateSection(section, newSection, true);
                return;
            }
        }

        // 끝에 추가되는 경우
        sections.add(newSection);
    }

    private void addSectionWithConnectedDownStation(Section newSection) {
        Station newDownStation = newSection.getDownStation();

        for (Section section : sections) {
            if (section.getDownStation().equals(newDownStation)) {
                updateSection(section, newSection, false);
                return;
            }
        }

        // 끝에 추가되는 경우 (기존 로직 유지)
        sections.add(0, newSection);
    }
}
