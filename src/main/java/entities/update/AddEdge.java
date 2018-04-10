package entities.update;

import entities.DynamicNetwork;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddEdge implements Update {

    private final Integer vertexFrom;
    private final Integer vertexTo;

    @Override
    public void applyTo(DynamicNetwork network) throws Exception {

        if(!network.containsVertex(vertexFrom)) {
            throw new Exception("Network does not contain vertex " + vertexFrom);
        }

        if(!network.containsVertex(vertexTo)) {
            throw new Exception("Network does not contain vertex " + vertexTo);
        }

        if(network.containsEdge(vertexFrom, vertexTo)) {
            throw new Exception("Network already contains edge");
        }

        network.addEdge(vertexFrom, vertexTo);
    }

}
