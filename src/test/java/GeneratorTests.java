import Generator.Generator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Test;

public class GeneratorTests {

    private Generator generator;

    @Before
    public void setUp() {
        this.generator = new Generator();
    }

    @Test
    public void test_ShouldReturnDirectedGraph() {
        DirectedGraph<String, DefaultEdge> result = generator.test();
    }
}
