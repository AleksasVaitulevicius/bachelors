package Utils;

import entities.dynamicnetwork.DynamicNetwork;
import entities.network.Network;

import java.util.List;

public class NetworkExamples {

    public Network getNetwork0() {
        Network network = new Network();
        network.putVertices(List.of(0, 1, 2, 3, 4, 5));
        network.addEdge(0, 1);
        network.addEdge(0, 2);
        network.addEdge(1, 3);
        network.addEdge(1, 2);
        network.addEdge(2, 1);
        network.addEdge(2, 4);
        network.addEdge(3, 2);
        network.addEdge(3, 5);
        network.addEdge(4, 3);
        network.addEdge(4, 5);
        network.setEdgeWeight(network.getEdge(0,1), 16);
        network.setEdgeWeight(network.getEdge(0,2), 13);
        network.setEdgeWeight(network.getEdge(1,3), 12);
        network.setEdgeWeight(network.getEdge(1,2), 10);
        network.setEdgeWeight(network.getEdge(2,1), 4);
        network.setEdgeWeight(network.getEdge(2,4), 14);
        network.setEdgeWeight(network.getEdge(3,2), 9);
        network.setEdgeWeight(network.getEdge(3,5), 20);
        network.setEdgeWeight(network.getEdge(4,3), 7);
        network.setEdgeWeight(network.getEdge(4,5), 4);
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
