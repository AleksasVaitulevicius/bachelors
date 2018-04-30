package algorithm.fulkerson;

import Utils.NetworkExamples;
import entities.network.Network;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BFSTests {

    @Test
    public void bfs_shouldReturnExpectedResult() {
        BFS bfs = new BFS();
        Network network = new NetworkExamples().getNetwork0();
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(1, 0);
        expected.put(2, 0);
        expected.put(3, 1);
        expected.put(4, 2);
        expected.put(5, 3);

        Map<Integer, Integer> result = bfs.bfs(network, List.of(0), List.of(5));

        assertEquals(expected, result);
    }
}
