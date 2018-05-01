import algorithm.clustering.DividerToClusters;
import entities.dynamicnetwork.DynamicNetwork;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        DynamicNetwork network = new DynamicNetwork();
        network.putVertices(List.of(0, 1, 2, 3, 4, 5, 6));
        network.addEdge(0, 1);
        network.addEdge(0, 2);
        network.addEdge(1, 3);
        network.addEdge(1, 2);
        network.addEdge(2, 1);
        network.addEdge(2, 4);
//        network.addEdge(3, 2);
        network.addEdge(3, 5);
        network.addEdge(4, 3);
        network.addEdge(4, 5);
        network.setEdgeWeight(network.getEdge(0,1), 16);
        network.setEdgeWeight(network.getEdge(0,2), 13);
        network.setEdgeWeight(network.getEdge(1,3), 12);
        network.setEdgeWeight(network.getEdge(1,2), 10);
        network.setEdgeWeight(network.getEdge(2,1), 4);
        network.setEdgeWeight(network.getEdge(2,4), 14);
//        network.setEdgeWeight(network.getEdge(3,2), 9);
        network.setEdgeWeight(network.getEdge(3,5), 20);
        network.setEdgeWeight(network.getEdge(4,3), 7);
        network.setEdgeWeight(network.getEdge(4,5), 4);
//        network.addEdge(0, 1);
//        network.addEdge(1, 2);
//        network.addEdge(1, 5);
//        network.addEdge(2, 3);
//        network.addEdge(5, 4);
//        network.addEdge(4, 0);
//        network.addEdge(6, 3);

        network.addSource(0);
//        network.addSource(3);
        new DividerToClusters().divideToClusters(network);

//        new GUI("before update", network).display(900, 900);
//
//        List<Integer> sources = List.of(0);
//        List<Integer> sinks = List.of(5);
//        FordFulkerson algorithm.fulkerson = new FordFulkerson(new BFS());
//
//        System.out.println("maxFlow:" + algorithm.fulkerson.maxFlow(network, sources, sinks));
//        algorithm.fulkerson.printMaxFlow(900, 900);
    }

}
