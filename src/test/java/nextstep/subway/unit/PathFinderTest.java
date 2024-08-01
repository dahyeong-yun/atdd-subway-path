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

        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(신논현역, "id", 2L);
        ReflectionTestUtils.setField(신사역, "id", 3L);
        ReflectionTestUtils.setField(판교역, "id", 4L);
        ReflectionTestUtils.setField(정자역, "id", 5L);

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
        assertThat(graph.vertexSet()).containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L);
        assertThat(graph.edgeSet()).hasSize(5);

        assertThat(graph.getEdgeWeight(graph.getEdge(1L, 2L))).isEqualTo(10);
        assertThat(graph.getEdgeWeight(graph.getEdge(2L, 3L))).isEqualTo(15);
        assertThat(graph.getEdgeWeight(graph.getEdge(3L, 4L))).isEqualTo(20);
        assertThat(graph.getEdgeWeight(graph.getEdge(2L, 4L))).isEqualTo(30);
        assertThat(graph.getEdgeWeight(graph.getEdge(4L, 5L))).isEqualTo(25);

        assertThat(graph.containsEdge(1L, 2L)).isTrue();
        assertThat(graph.containsEdge(2L, 3L)).isTrue();
        assertThat(graph.containsEdge(3L, 4L)).isTrue();
        assertThat(graph.containsEdge(4L, 5L)).isTrue();
        assertThat(graph.containsEdge(2L, 4L)).isTrue();
    }

    @Test
    @DisplayName("최소 길이 경로 찾기")
    void getShortestPath() {
        PathResult shortestPath = pathFinder.getShortestPath(1L, 5L);

        assertThat(shortestPath).isNotNull();
        assertThat(shortestPath.getPathStationIds()).containsExactly(1L, 2L, 4L, 5L);
    }

    @Test
    @DisplayName("연결되지 않은 경로를 찾을 떄 예외 발생")
    void getShortestPathNoRoute() {
        Station 고립역 = new Station("고립역");
        ReflectionTestUtils.setField(고립역, "id", 6L);
        graph.addVertex(6L);

        PathNotFoundException exception = assertThrows(PathNotFoundException.class, () -> {
            pathFinder.getShortestPath(1L, 6L);
        });

        assertThat(exception.getMessage()).contains("1", "6");
    }
}
