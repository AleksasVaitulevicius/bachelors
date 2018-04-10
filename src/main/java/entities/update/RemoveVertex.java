package entities.update;

import entities.DynamicNetwork;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RemoveVertex implements Update {

    private final Integer vertex;

    @Override
    public void applyTo(DynamicNetwork network) throws Exception {

        if(!network.containsVertex(vertex)) {
            throw new Exception("Network does not contain vertex " + vertex);
        }

        network.removeVertex(vertex);
    }

}
