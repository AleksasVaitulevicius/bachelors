package algorithm.fulkerson;

import Utils.NetworkExamples;
import entities.network.Network;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FordFulkersonTests {

    @Test
    public void maxFlow_shouldReturnExpectedResult() {
        Network network = new NetworkExamples().getNetwork0();
        FordFulkerson fulkerson = new FordFulkerson(new BFS());

        Map<Integer, Double> maxFlow = fulkerson.maxFlow(network, List.of(0), List.of(5));

        assertEquals(fulkerson.getUsedEdges(), 32);

        assertEquals(Map.of(5, 23.0), maxFlow);
        assertEquals(
                12.0,
                fulkerson.getMaxFlow().getEdgeWeight(fulkerson.getMaxFlow().getEdge(0, 1)),
                0.5
        );
        assertEquals(
                11.0,
                fulkerson.getMaxFlow().getEdgeWeight(fulkerson.getMaxFlow().getEdge(0, 2)),
                0.5
        );
        assertEquals(
                12.0,
                fulkerson.getMaxFlow().getEdgeWeight(fulkerson.getMaxFlow().getEdge(1, 3)),
                0.5
        );
        assertEquals(
                11.0,
                fulkerson.getMaxFlow().getEdgeWeight(fulkerson.getMaxFlow().getEdge(2, 4)),
                0.5
        );
        assertEquals(
                19.0,
                fulkerson.getMaxFlow().getEdgeWeight(fulkerson.getMaxFlow().getEdge(3, 5)),
                0.5
        );
        assertEquals(
                7.0,
                fulkerson.getMaxFlow().getEdgeWeight(fulkerson.getMaxFlow().getEdge(4, 3)),
                0.5
        );
        assertEquals(
                4.0,
                fulkerson.getMaxFlow().getEdgeWeight(fulkerson.getMaxFlow().getEdge(4, 5)),
                0.5
        );
    }
}
