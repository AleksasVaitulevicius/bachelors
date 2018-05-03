package entities.dynamicnetwork;

import entities.network.WeightedEdge;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;

public class ClustersNetwork extends SimpleDirectedWeightedGraph<DynamicNetwork, WeightedEdge> {

    @Getter @Setter
    private List<Integer> sources;
    @Getter @Setter
    private List<Integer> sinks;

    public ClustersNetwork() {
        super(WeightedEdge.class);
    }

}
