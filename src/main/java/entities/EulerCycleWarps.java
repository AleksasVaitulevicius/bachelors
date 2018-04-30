package entities;

import entities.network.Network;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import java.util.List;
import java.util.Set;

public class EulerCycleWarps extends SimpleDirectedGraph<List<Integer>, DefaultEdge> {

    public EulerCycleWarps() {
        super(DefaultEdge.class);
    }

    public void constructFrom(Network network) {
        this.putVertices(network.vertexSet());

        network.edgeSet().forEach(edge -> {
            List<Integer> from = List.of(network.getEdgeSource(edge));
            List<Integer> to = List.of(network.getEdgeTarget(edge));
            this.addEdge(from, to);
        });
    }

    public int putVertices(Set<Integer> vertices) {
        int add = 0;

        for(Integer vertex: vertices) {
            add = (this.addVertex(List.of(vertex))) ? add + 1: add;
        }

        return add;
    }

    public void addTargetsIfVerticesExist(List<Integer> newVertex, List<List<Integer>> targets) {
        for(List<Integer> vertex: targets) {
            if(this.containsVertex(vertex)) {
                this.addEdge(newVertex, vertex);
            }
        }

    }

    public void addSourcesIfVerticesExist(List<Integer> newVertex, List<List<Integer>> sources) {
        for(List<Integer> vertex: sources) {
            if(this.containsVertex(vertex)) {
                this.addEdge(vertex, newVertex);
            }
        }

    }
}
