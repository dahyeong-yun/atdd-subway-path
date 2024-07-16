package nextstep.subway.presentation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.Section;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SectionResponse {
    @Getter
    private final Long lineId;
    @Getter
    private final Long sectionId;
    private final Long upStationId;
    @Getter
    private final Long downStationId;
    private final Integer distance;

    public static SectionResponse of(Section createdSection) {
        return new SectionResponse(
                createdSection.getLine().getId(),
                createdSection.getId(),
                createdSection.getUpStation().getId(),
                createdSection.getDownStation().getId(),
                createdSection.getDistance()
        );
    }

}
