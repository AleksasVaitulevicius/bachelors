import algorithm.DynamicNetworkWithMaxFlowAlgorithm;
import algorithm.clustering.DividerToClusters;
import algorithm.fulkerson.BFS;
import algorithm.fulkerson.FordFulkerson;
import experiments.EmpiricalExperiment;
import generator.RandomNetworkGenerator;
import generator.RandomNetworkListGenerator;

import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {

        RandomNetworkGenerator gen = new RandomNetworkGenerator();
        RandomNetworkListGenerator generator = new RandomNetworkListGenerator(gen);
        FordFulkerson fulkerson = new FordFulkerson(new BFS());
        DynamicNetworkWithMaxFlowAlgorithm algorithm =  new DynamicNetworkWithMaxFlowAlgorithm(
            new DividerToClusters(), fulkerson
        );
        EmpiricalExperiment experiment = new EmpiricalExperiment(
            fulkerson, algorithm, generator, new Random()
        );

        experiment.perform();

//        int no = 0;
//
//        for (DynamicNetwork network : generator.generate()) {
//            System.out.println("network nr:" + no++);
//            System.out.println("    number of vertices:" + network.vertexSet().size());
//            System.out.println("    number of edges:" + network.edgeSet().size());
//        }

//        DynamicNetwork network = new DynamicNetwork();
////        network.putVertices(List.of(0, 1, 2, 3, 4, 5));
//        network.putVertices(List.of(0, 1, 2, 3, 4, 5, 6, 7));
//        network.addEdge(0, 1);
//        network.addEdge(0, 2);
//        network.addEdge(1, 3);
//        network.addEdge(1, 2);
//        network.addEdge(2, 1);
//        network.addEdge(2, 4);
////        network.addEdge(3, 2);
//        network.addEdge(3, 5);
////        network.addEdge(4, 3);
//        network.addEdge(4, 5);
////        network.addEdge(6, 1);
////        network.addEdge(7, 6);
////        network.addEdge(6, 7);
//        network.addEdge(0, 7);
//        network.setEdgeWeight(network.getEdge(0,1), 16);
//        network.setEdgeWeight(network.getEdge(0,2), 13);
//        network.setEdgeWeight(network.getEdge(1,3), 12);
//        network.setEdgeWeight(network.getEdge(1,2), 10);
//        network.setEdgeWeight(network.getEdge(2,1), 4);
//        network.setEdgeWeight(network.getEdge(2,4), 14);
////        network.setEdgeWeight(network.getEdge(3,2), 9);
//        network.setEdgeWeight(network.getEdge(3,5), 20);
////        network.setEdgeWeight(network.getEdge(4,3), 7);
//        network.setEdgeWeight(network.getEdge(4,5), 4);
////        network.addEdge(0, 1);
////        network.addEdge(1, 2);
////        network.addEdge(1, 5);
////        network.addEdge(2, 3);
////        network.addEdge(5, 4);
////        network.addEdge(4, 0);
////        network.addEdge(6, 3);
//
//        network.addSource(0);
//        network.addSink(5);
////        network.addSource(3);
//        DynamicNetworkWithMaxFlowAlgorithm finder = new DynamicNetworkWithMaxFlowAlgorithm(new DividerToClusters(), new FordFulkerson(new BFS()));
//        finder.init(network);
//        finder.removeEdge(1, 3);
//        finder.removeVertex(1);
//        finder.removeVertex(2);
//        finder.changeWeight(1, 3, 13);
//        finder.addEdge(4, 3, 11.0);
//        System.out.println(finder.getCurrentMaxFlow());
//        new GUI("network", network).display(900, 900);
//        FordFulkerson fulkerson = new FordFulkerson(new BFS());
//        Map<Integer, Double> result = fulkerson.maxFlow(network, network.getSources(), network.getSinks());
//
//        fulkerson.printMaxFlow(900, 900);
//
//        System.out.println(result);
//        new GUI("network", network).display(900, 900);
//
//        finder.getClusters().vertexSet().forEach(cluster ->
//            new GUI("" + cluster, cluster).display(900, 900)
//        );

//
//        List<Integer> sources = List.of(0);
//        List<Integer> sinks = List.of(5);
//        FordFulkerson algorithm.fulkerson = new FordFulkerson(new BFS());
//
//        System.out.println("maxFlow:" + algorithm.fulkerson.maxFlow(network, sources, sinks));
//        algorithm.fulkerson.printMaxFlow(900, 900);
    }

}
