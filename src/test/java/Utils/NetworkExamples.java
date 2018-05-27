package Utils;

import entities.dynamicnetwork.DynamicNetwork;

import java.util.List;

public class NetworkExamples {

    public DynamicNetwork getNetwork0() {
        DynamicNetwork network = new DynamicNetwork();
        network.putVertices(List.of(1, 2, 3, 4, 5, 6));
        network.addEdge(1, 2);
        network.addEdge(1, 3);
        network.addEdge(2, 4);
        network.addEdge(2, 3);
        network.addEdge(3, 2);
        network.addEdge(3, 5);
//        network.addEdge(3, 2);
        network.addEdge(4, 6);
        network.addEdge(5, 4);
        network.addEdge(5, 6);
        network.setEdgeWeight(network.getEdge(1,2), 16);
        network.setEdgeWeight(network.getEdge(1,3), 13);
        network.setEdgeWeight(network.getEdge(2,4), 12);
        network.setEdgeWeight(network.getEdge(2,3), 10);
        network.setEdgeWeight(network.getEdge(3,2), 4);
        network.setEdgeWeight(network.getEdge(3,5), 14);
//        network.setEdgeWeight(network.getEdge(3,2), 9);
        network.setEdgeWeight(network.getEdge(4,6), 20);
        network.setEdgeWeight(network.getEdge(5,4), 7);
        network.setEdgeWeight(network.getEdge(5,6), 4);

        network.addSource(0);
        network.addSink(6);
        return network;
    }
    public DynamicNetwork getDynamicNetwork0() {
        DynamicNetwork network = new DynamicNetwork();
        network.putVertices(List.of(0, 1, 2, 3, 4, 5));
        network.addEdge(0, 1);
        network.addEdge(1, 2);
        network.addEdge(1, 5);
        network.addEdge(2, 3);
        network.addEdge(5, 4);
        network.addEdge(4, 0);

        network.addSource(0);
        return network;
    }
}
