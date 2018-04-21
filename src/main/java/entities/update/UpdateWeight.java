package entities.update;

import entities.Network;
import entities.WeightedEdge;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateWeight implements Update {

    private final WeightedEdge edge;
    private final Integer newValue;

    @Override
    public void applyTo(Network network) throws Exception {

        if(!network.containsEdge(edge)) {
            throw new Exception("Network does not contain edge");
        }

        network.setEdgeWeight(edge, newValue);
    }

}
