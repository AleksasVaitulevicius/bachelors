package generator;

import entities.dynamicnetwork.DynamicNetwork;
import entities.network.Network;
import entities.network.WeightedEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomNetworkGenerator {

    public DynamicNetwork generate(
        int vertices, int edges, int weightLowerBound, int weightUpperBound
    ) throws Exception {

        validate(vertices, edges, weightLowerBound, weightUpperBound);

        DynamicNetwork network = new DynamicNetwork();
        network.putVertices(IntStream.rangeClosed(1, vertices).boxed().collect(Collectors.toList()));
        addEdges(network, vertices, edges);
        addWeights(network, weightLowerBound, weightUpperBound);

        return network;
    }

    private void addEdges(Network network, int vertices, int edges) {

        Random rand = new Random();
        List<Integer> verticesFrom = new ArrayList<>();
        List<Integer> verticesTo = new ArrayList<>();

        for(int row = 1; row <= vertices; row++) {
            for(int column = 1; column <= vertices; column++) {
                if(row != column) {
                    verticesFrom.add(row);
                    verticesTo.add(column);
                }
            }
        }

        for(int it = 0; it < edges; it++) {
            int index = rand.nextInt(verticesFrom.size());
            network.addEdge(verticesFrom.remove(index), verticesTo.remove(index));
        }

    }

    private void addWeights(Network network, int weightLowerBound, int weightUpperBound) {
        Random rand = new Random();

        for(WeightedEdge edge: network.edgeSet()) {
            network.setEdgeWeight(
                edge,
                rand.nextInt(weightUpperBound - weightLowerBound) + weightLowerBound
            );
        }

    }

    private void validate(int vertices, int edges, int weightLowerBound, int weightUpperBound) throws Exception {

        if(vertices < 0) {
            throw new Exception("Vertices cannot be negative:" + vertices);
        }

        if(edges < 0) {
            throw new Exception("Edges cannot be negative:" + edges);
        }

        if(edges > vertices * (vertices - 1)) {
            throw new Exception("Cannot generate network with " +
                vertices + " vertices and "
                + edges + " edges");
        }

        if(weightLowerBound < 1 || weightLowerBound > weightUpperBound) {
            throw new Exception("Cannot generate network with "
                + weightLowerBound + " weightLowerBound and "
                + weightUpperBound + " weightUpperBound");
        }

    }

}
