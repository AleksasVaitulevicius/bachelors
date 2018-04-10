package entities;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class DynamicNetwork extends SimpleDirectedWeightedGraph<Integer, WeightedEdge> {

    public DynamicNetwork() {
        super(WeightedEdge.class);
    }

}
