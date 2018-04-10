package entities;

import org.jgrapht.graph.DefaultWeightedEdge;

public class WeightedEdge extends DefaultWeightedEdge {

    @Override
    public String toString() {
        return Double.toString(getWeight());
    }

}
