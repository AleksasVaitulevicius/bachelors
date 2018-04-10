package entities.update;

import entities.DynamicNetwork;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddVertex implements Update {

    private final Integer edgeWith;
    private final Boolean directedIntoVertex;

    @Override
    public void applyTo(DynamicNetwork network) throws Exception {

        Integer newVertex = network.vertexSet()
            .stream().mapToInt(value -> value).max().orElseThrow(Exception::new) + 1;

        if(!network.containsVertex(edgeWith)) {
            throw new Exception("Network does not contain vertex " + edgeWith);
        }

        network.addVertex(newVertex);

        if (directedIntoVertex) {
            network.addEdge(edgeWith, newVertex);
        } else {
            network.addEdge(newVertex, edgeWith);
        }
    }

}
