package generator;

import entities.DynamicNetwork;
import entities.UpdateType;
import entities.WeightedEdge;
import entities.update.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Setter @Getter
@AllArgsConstructor
public class RandomUpdateGenerator {

    private int weightLowerBound;
    private int weightUpperBound;

    public Update generateFor(DynamicNetwork network) throws Exception {

        if(network.vertexSet().size() <= 1) {
            throw new Exception("Empty network");
        }

        if(network.edgeSet().isEmpty()) {
            throw new Exception("Edgeless network");
        }

        switch(UpdateType.random()) {
            case ADD_VERTEX:
                return new AddVertex(getRandomVertex(network), new Random().nextBoolean());
            case REMOVE_VERTEX:
                return new RemoveVertex(getRandomVertex(network));
            case ADD_EDGE:
                return addEdge(network);
            case REMOVE_EDGE:
                return new RemoveEdge(getRandomEdge(network));
            case UPDATE_WEIGHT:
                return updateWeight(network);
            default:
                throw new Exception("Unexpected update type");
        }

    }

    private AddEdge addEdge(DynamicNetwork network) {
        int vertexFrom = getRandomVertex(network);
        int vertexTo;
        do {
            vertexTo = getRandomVertex(network);
        } while (vertexTo == vertexFrom);

        return new AddEdge(vertexFrom, vertexTo);
    }

    private UpdateWeight updateWeight(DynamicNetwork network) {
        int weight = new Random().nextInt(weightUpperBound - weightLowerBound) + weightLowerBound;
        return new UpdateWeight(getRandomEdge(network), weight);
    }

    private Integer getRandomVertex(DynamicNetwork network) {
        List<Integer> vertices = new ArrayList<>(network.vertexSet());
        Random rand = new Random();

        return vertices.get(rand.nextInt(vertices.size()));
    }
    private WeightedEdge getRandomEdge(DynamicNetwork network) {
        List<WeightedEdge> edges = new ArrayList<>(network.edgeSet());
        Random rand = new Random();

        return edges.get(rand.nextInt(edges.size()));
    }

}
