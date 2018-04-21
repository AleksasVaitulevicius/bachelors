package entities.update;

import entities.Network;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RemoveVertex implements Update {

    private final Integer vertex;

    @Override
    public void applyTo(Network network) throws Exception {

        if(!network.containsVertex(vertex)) {
            throw new Exception("Network does not contain vertex " + vertex);
        }

        network.removeVertex(vertex);
    }

}
