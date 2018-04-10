package generator;

import entities.WeightedEdge;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomNetworkGenerator {

    public SimpleDirectedWeightedGraph<Integer, WeightedEdge> generate(
        int vertices, int edges, int source, int weightLowerBound, int weightUpperBound
    ) throws Exception {

        validate(vertices, edges, source, weightLowerBound, weightUpperBound);

        SimpleDirectedWeightedGraph<Integer, WeightedEdge> network
            = new SimpleDirectedWeightedGraph<>(WeightedEdge.class);

        addAllVertices(network, vertices);
        randomlyConnectVertices(network, source, vertices);
        addRemainingEdges(network, vertices, edges - vertices + 1);
        addWeights(network, weightLowerBound, weightUpperBound);

        return network;
    }

    private void addAllVertices(Graph<Integer, WeightedEdge> network, int vertices) {
        List<Integer> sequence = IntStream.rangeClosed(1, vertices).boxed().collect(Collectors.toList());

        for(Integer vertex: sequence) {
            network.addVertex(vertex);
        }

    }

    private void randomlyConnectVertices(
        Graph<Integer, WeightedEdge> network, Integer source, int vertices
    ) {

        Random rand = new Random();
        List<Integer> notConnected = IntStream.rangeClosed(1, vertices).boxed().collect(Collectors.toList());
        List<Integer> connected = new ArrayList<>();
        connected.add(source);
        notConnected.remove(source);

        while(!notConnected.isEmpty()) {
            int index = rand.nextInt(notConnected.size());
            int vertexTo = notConnected.remove(index);
            index = rand.nextInt(connected.size());
            int vertexFrom = connected.get(index);
            network.addEdge(vertexFrom, vertexTo);
            connected.add(vertexTo);
        }

    }

    private void addRemainingEdges(Graph<Integer, WeightedEdge> network, int vertices, int edges) {

        Random rand = new Random();
        int vertexTo, vertexFrom;

        for(int it = 0; it < edges; it++) {
            do {
                vertexTo = rand.nextInt(vertices) + 1;
                do {
                    vertexFrom = rand.nextInt(vertices) + 1;
                } while (vertexFrom == vertexTo);
            } while(network.containsEdge(vertexFrom, vertexTo));

            network.addEdge(vertexFrom, vertexTo);
        }

    }

    private void addWeights(
        Graph<Integer, WeightedEdge> network, int weightLowerBound, int weightUpperBound
    ) {
        Random rand = new Random();

        for(WeightedEdge edge: network.edgeSet()) {
            network.setEdgeWeight(edge, rand.nextInt(weightUpperBound) + weightLowerBound);
        }

    }

    private void validate(
        int vertices, int edges, int source, int weightLowerBound, int weightUpperBound
    ) throws Exception {

        if(vertices <= 1) {
            throw new Exception("Vertices cannot be 0 or negative:" + vertices);
        }

        if(edges <= 1) {
            throw new Exception("Edges cannot be 0 or negative:" + edges);
        }

        if(edges < vertices + 1 || edges > vertices * (vertices - 1)) {
            throw new Exception("Cannot generate network with " +
                vertices + " vertices and "
                + edges + " edges");
        }

        if(source > vertices || source < 0) {
            throw new Exception("Illegal source value:" + source);
        }

        if(weightLowerBound < 1 || weightLowerBound > weightUpperBound) {
            throw new Exception("Cannot generate network with "
                + weightLowerBound + " weightLowerBound and "
                + weightUpperBound + " weightUpperBound");
        }

    }

}
