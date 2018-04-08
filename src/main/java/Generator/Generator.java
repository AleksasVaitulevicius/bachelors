package Generator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class Generator {

    public DirectedGraph<String, DefaultEdge> test() {
        DirectedGraph<String, DefaultEdge> directedGraph
            = new DefaultDirectedGraph<>(DefaultEdge.class);
        directedGraph.addVertex("v1");
        directedGraph.addVertex("v2");
        directedGraph.addVertex("v3");
        directedGraph.addEdge("v1", "v2");
        return directedGraph;
    }

}
