import entities.Network;
import fulkerson.BFS;
import fulkerson.FordFulkerson;
import gui.GUI;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        BFS bfs = new BFS();

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

//        Update update;
//        RandomNetworkGenerator rng = new RandomNetworkGenerator();
//        RandomUpdateGenerator rug = new RandomUpdateGenerator(10, 30);
//
//        try {
//            network = rng.generate(3, 6, 10, 30);
//            update = rug.generateFor(network);
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//            return;
//        }

        new GUI("before update", network).display(900, 900);

//        try {
//            update.applyTo(network);
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }
        List<Integer> sources = List.of(0);
        List<Integer> sinks = List.of(5);
        FordFulkerson fulkerson = new FordFulkerson(new BFS());
        System.out.println("maxFlow:" + fulkerson.maxFlow(network, sources, sinks));
        new GUI("after update", fulkerson.getMaxFlow()).display(900, 900);
        System.out.println(bfs.bfs(network, sources, sinks));
    }

}
