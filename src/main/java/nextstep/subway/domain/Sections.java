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

    public int size() { // TODO 오로지 테스트를 위한 메서드 이므로 삭제를 고려
        return sections.size();
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void deleteLastSection() {
        if (!sections.isEmpty()) {
            sections.remove(sections.size() - 1);
            return;
        }
        throw new InvalidSectionException("삭제할 수 있는 지하철 구간이 없습니다.");
    }

    void addSection(Section newSection) {
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

    void deleteStation(Station station) {
        List<Station> stations = this.getStations();
        if (!stations.contains(station)) {
            throw new InvalidSectionException("노선에 포함되지 않은 역을 제거할 수 없습니다.");
        }
        Section previousSection = findSectionByDownStation(station);
        Section nextSection = findSectionByUpStation(station);

        if (previousSection == null && nextSection == null) {
            throw new InvalidSectionException("삭제할 수 있는 지하철 구간이 없습니다.");
        }

        if (previousSection != null && nextSection == null) {
            sections.remove(previousSection);
            return;
        }

        if (previousSection == null) {
            sections.remove(nextSection);
            return;
        }

        mergeSections(previousSection, nextSection);
    }

    private void validateStationConnections(boolean isUpStationConnected, boolean isDownStationConnected) {
        if (!isUpStationConnected && !isDownStationConnected) {
            throw new InvalidSectionException("새로운 구간의 양쪽 역 모두 기존 노선에 연결되어 있지 않습니다.");
        }
        if (isUpStationConnected && isDownStationConnected) {
            throw new InvalidSectionException("새로운 구간이 기존 구간을 완전히 포함합니다.");
        }
    }

    private void updateSection(Section existingSection, Section newSection, boolean isUpStationConnected) {
        Station newUpStation = newSection.getUpStation();
        Station newDownStation = newSection.getDownStation();
        SectionDistance newSectionDistance = newSection.getSectionDistance();

        if (existingSection.getSectionDistance().isLessThanOrEqualTo(newSectionDistance)) {
            throw new InvalidSectionException("기존 구간의 길이가 새 구간보다 길어야 합니다.");
        }

        Station updatedUpStation = isUpStationConnected ? newDownStation : existingSection.getUpStation();
        Station updatedDownStation = isUpStationConnected ? existingSection.getDownStation() : newUpStation;

        Section updatedSection = Section.createSection(
                existingSection.getLine(),
                updatedUpStation,
                updatedDownStation,
                existingSection.getSectionDistance().minus(newSectionDistance)
        );

        sections.add(newSection);
        sections.add(updatedSection);
        sections.remove(existingSection);
    }

    private void addSectionWithConnectedUpStation(Section newSection) {
        Station newUpStation = newSection.getUpStation();
        sections.stream()
                .filter((section) -> section.getUpStation().equals(newUpStation))
                .findFirst()
                .ifPresentOrElse(
                        (section) -> updateSection(section, newSection, true),
                        () -> sections.add(newSection));
    }

    private void addSectionWithConnectedDownStation(Section newSection) {
        Station newDownStation = newSection.getDownStation();
        sections.stream()
                .filter((section) -> section.getDownStation().equals(newDownStation))
                .findFirst()
                .ifPresentOrElse(
                        (section) -> updateSection(section, newSection, false),
                        () -> sections.add(0, newSection));
    }

    private void mergeSections(Section previousSection, Section nextSection) {
        Section mergedSection = Section.createSection(
                previousSection.getLine(),
                previousSection.getUpStation(),
                nextSection.getDownStation(),
                previousSection.getSectionDistance().plus(nextSection.getSectionDistance())
        );
        sections.remove(previousSection);
        sections.remove(nextSection);
        sections.add(mergedSection);
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElse(null);
    }
}
