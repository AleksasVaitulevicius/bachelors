package generator;

import entities.WeightedEdge;
import generator.RandomNetworkGenerator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomNetworkGeneratorTests {

    private RandomNetworkGenerator randomNetworkGenerator;

    @Before
    public void setUp() {
        this.randomNetworkGenerator = new RandomNetworkGenerator();
    }

    @Test
    public void generate_ShouldReturnDirectedWeightedGraph() throws Exception {
        SimpleDirectedWeightedGraph<Integer, WeightedEdge> result
            = randomNetworkGenerator.generate(3, 6, 1, 20);

        assertEquals(6, result.edgeSet().size());
        assertEquals(3, result.vertexSet().size());
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_WithNegativeVertices() throws Exception {
        randomNetworkGenerator.generate(-1, 6, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_WithNegativeEdges() throws Exception {
        randomNetworkGenerator.generate(4, -1, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_WithBadEdgesAndVertices() throws Exception {
        randomNetworkGenerator.generate(3, 7, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_With0OrNegativeWeightLowerBound() throws Exception {
        randomNetworkGenerator.generate(4, 6, 0, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_WithWeightLowerBoundGreaterThanWeightUpperBound() throws Exception {
        randomNetworkGenerator.generate(4, 6, 10, 9);
    }

}
