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
            = randomNetworkGenerator.generate(3, 6, 1, 1, 20);

        assertEquals(6, result.edgeSet().size());
        assertEquals(3, result.vertexSet().size());
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_With1Or0OrNegativeVertices() throws Exception {
        randomNetworkGenerator.generate(1, 6, 5, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_With1Or0OrNegativeEdges() throws Exception {
        randomNetworkGenerator.generate(4, 1, 3, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_WithBadEdgesAndVertices1() throws Exception {
        randomNetworkGenerator.generate(3, 7, 1, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_WithBadEdgesAndVertices2() throws Exception {
        randomNetworkGenerator.generate(6, 6, 1, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_WithSourceGreaterThanVertices() throws Exception {
        randomNetworkGenerator.generate(4, 6, 5, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_With0OrNegativeSource() throws Exception {
        randomNetworkGenerator.generate(4, 6, 0, 1, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_With0OrNegativeWeightLowerBound() throws Exception {
        randomNetworkGenerator.generate(4, 6, 1, 0, 20);
    }

    @Test(expected = Exception.class)
    public void generate_ShouldThrow_WithWeightLowerBoundGreaterThanWeightUpperBound() throws Exception {
        randomNetworkGenerator.generate(4, 6, 1, 10, 9);
    }

}
