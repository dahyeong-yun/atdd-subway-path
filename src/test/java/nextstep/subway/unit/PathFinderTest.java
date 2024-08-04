package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.exception.PathNotFoundException;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathFinderTest {

    public static final Long 강남역_ID = 1L;
    public static final Long 신논현역_ID = 2L;
    public static final Long 신사역_ID = 3L;
    public static final Long 판교역_ID = 4L;
    public static final Long 정자역_ID = 5L;
    private PathFinder pathFinder;
    private WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    @BeforeEach
    void setUp() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        Station 강남역 = new Station("강남역");
        Station 신논현역 = new Station("신논현역");
        Station 신사역 = new Station("신사역");
        Station 판교역 = new Station("판교역");
        Station 정자역 = new Station("정자역");

        ReflectionTestUtils.setField(강남역, "id", 강남역_ID);
        ReflectionTestUtils.setField(신논현역, "id", 신논현역_ID);
        ReflectionTestUtils.setField(신사역, "id", 신사역_ID);
        ReflectionTestUtils.setField(판교역, "id", 판교역_ID);
        ReflectionTestUtils.setField(정자역, "id", 정자역_ID);

        List<Station> allStations = Arrays.asList(강남역, 신논현역, 신사역, 판교역, 정자역);

        Line 신분당선 = new Line("신분당선", "bg-red-600");
        Line 다른노선 = new Line("다른노선", "bg-blue-600");

        Section section1 = Section.createSection(신분당선, 강남역, 신논현역, 10);
        Section section2 = Section.createSection(신분당선, 신논현역, 신사역, 15);
        Section section3 = Section.createSection(다른노선, 신사역, 판교역, 20);
        Section section4 = Section.createSection(다른노선, 판교역, 정자역, 25);
        Section section5 = Section.createSection(다른노선, 신논현역, 판교역, 30);

        List<Section> allSections = Arrays.asList(section1, section2, section3, section4, section5);

        pathFinder = PathFinder.initializePathGraph(graph, allSections, allStations);
    }

    @Test
    @DisplayName("경로 그래프 초기화")
    void initializePathGraph() {
        assertThat(pathFinder).isNotNull();
        assertThat(graph.vertexSet()).containsExactlyInAnyOrder(강남역_ID, 신논현역_ID, 신사역_ID, 판교역_ID, 정자역_ID);
        assertThat(graph.edgeSet()).hasSize(5);

        assertThat(graph.getEdgeWeight(graph.getEdge(강남역_ID, 신논현역_ID))).isEqualTo(10);
        assertThat(graph.getEdgeWeight(graph.getEdge(신논현역_ID, 신사역_ID))).isEqualTo(15);
        assertThat(graph.getEdgeWeight(graph.getEdge(신사역_ID, 판교역_ID))).isEqualTo(20);
        assertThat(graph.getEdgeWeight(graph.getEdge(신논현역_ID, 판교역_ID))).isEqualTo(30);
        assertThat(graph.getEdgeWeight(graph.getEdge(판교역_ID, 정자역_ID))).isEqualTo(25);

        assertThat(graph.containsEdge(강남역_ID, 신논현역_ID)).isTrue();
        assertThat(graph.containsEdge(신논현역_ID, 신사역_ID)).isTrue();
        assertThat(graph.containsEdge(신사역_ID, 판교역_ID)).isTrue();
        assertThat(graph.containsEdge(판교역_ID, 정자역_ID)).isTrue();
        assertThat(graph.containsEdge(신논현역_ID, 판교역_ID)).isTrue();
    }

    @Test
    @DisplayName("최소 길이 경로 찾기")
    void getShortestPath() {
        PathResult shortestPath = pathFinder.getShortestPath(강남역_ID, 정자역_ID);

        assertThat(shortestPath).isNotNull();
        assertThat(shortestPath.getPathStationIds()).containsExactly(강남역_ID, 신논현역_ID, 판교역_ID, 정자역_ID);
    }

    @Test
    @DisplayName("연결되지 않은 경로를 찾을 떄 예외 발생")
    void getShortestPathNoRoute() {
        Station 고립역 = new Station("고립역");
        Long 고립역_ID = 6L;
        ReflectionTestUtils.setField(고립역, "id", 고립역_ID);
        graph.addVertex(고립역_ID);

        PathNotFoundException exception = assertThrows(PathNotFoundException.class, () -> {
            pathFinder.getShortestPath(강남역_ID, 고립역_ID);
        });

        assertThat(exception.getMessage()).contains("1", "6");
    }

    @Test
    @DisplayName("출발역과 도착역이 동일할 때 예외 발생")
    void getShortestPathSameStations() {
        PathNotFoundException exception = assertThrows(PathNotFoundException.class, () -> {
            pathFinder.getShortestPath(강남역_ID, 강남역_ID);
        });

        assertThat(exception.getMessage())
                .contains(String.format("출발역과 도착역(ID: %d)이 동일하여 경로를 찾을 수 없습니다.", 강남역_ID));
    }
}
