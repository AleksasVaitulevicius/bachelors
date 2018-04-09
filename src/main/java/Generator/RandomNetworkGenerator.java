package Generator;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class RandomNetworkGenerator {

    public SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> generate(
        int vertices, int edges, int source, int sink
    ) throws Exception {

        validate(vertices, edges, source, sink);

        SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> result
            = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        for(int it = 0; it < vertices; it++) {
            result.addVertex(it);
        }

        return result;
    }

    private void validate(int vertices, int edges, int source, int sink) throws Exception {
        if(vertices <= 0) {
            throw new Exception("Vertices cannot be 0 or negative");
        }

        if(edges <= 0) {
            throw new Exception("Edges cannot be 0 or negative");
        }

        if(source >= vertices) {
            throw new Exception("Source cannot be greater than vertices");
        }

        if(sink >= vertices) {
            throw new Exception("Sink cannot be greater than vertices");
        }

    }

}
