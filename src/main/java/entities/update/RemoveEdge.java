package entities.update;

import entities.DynamicNetwork;
import entities.WeightedEdge;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RemoveEdge implements Update {

    private final WeightedEdge edge;

    @Override
    public void applyTo(DynamicNetwork network) throws Exception {

        if(!network.containsEdge(edge)) {
            throw new Exception("Network does not contain vertex");
        }

        network.removeEdge(edge);
    }

}
