package entities.dynamicnetwork;

import entities.network.WeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class ClustersNetwork extends SimpleDirectedWeightedGraph<DynamicNetwork, WeightedEdge> {

    public ClustersNetwork() {
        super(WeightedEdge.class);
    }

    public void intoClusters(DynamicNetwork network) {

    }

}
