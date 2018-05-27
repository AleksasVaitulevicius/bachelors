import algorithm.DynamicNetworkWithMaxFlowAlgorithm;
import algorithm.clustering.DividerToClusters;
import algorithm.fulkerson.BFS;
import algorithm.fulkerson.FordFulkerson;
import entities.dynamicnetwork.DynamicNetwork;
import experiments.EmpiricalExperiment;
import experiments.ExperimentData;
import generator.RandomNetworkGenerator;
import generator.RandomNetworkListGenerator;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String input;
        Scanner sc = new Scanner(System.in);

        System.out.println("type help");
        input = sc.nextLine();
        ExperimentData data = new ExperimentData();
        while(!input.equals("run")) {
            switch(input) {
                case "help":
                    System.out.println("run - runs experiments");
                    System.out.println("display <file> - displays network in <file>");
                    System.out.println("display all - displays all");
                    System.out.println("quit - exits");
                    break;
                case "display all":
                    data.loadAndDisplayAll();
                    break;
                case "quit":
                    return;
            }
            if(input.contains("display ") && !input.equals("display all")) {
                data.loadAndDisplayNetwork(input.substring(8, input.length()));
            }
            input = sc.nextLine();
        }

        data.clearNetworks();
        RandomNetworkGenerator gen = new RandomNetworkGenerator();
        RandomNetworkListGenerator generator = new RandomNetworkListGenerator(gen);
        FordFulkerson fulkerson = new FordFulkerson(new BFS());
        DynamicNetworkWithMaxFlowAlgorithm algorithm =  new DynamicNetworkWithMaxFlowAlgorithm(
            new DividerToClusters(), fulkerson
        );
        EmpiricalExperiment experiment = new EmpiricalExperiment(
            fulkerson, algorithm, generator, new Random()
        );

//        data.loadAndDisplayNetwork("incorrect/INIT/network0.ser");
//        DynamicNetwork network = data.getNetwork();
//
//        algorithm.init(network);
//
//        fulkerson.reset();
//        Map<Integer, Double> expected = fulkerson.maxFlow(network, network.getSources(), network.getSinks());
//
//        System.out.println("expected: " + expected);
//        System.out.println("actual: " + algorithm.getCurrentMaxFlow());

        experiment.perform();

        data.saveNetworks(experiment.getIncorrectNetwork(), experiment.getIncorrectAction());
        data.saveUsedEdges(experiment.getAlgorithmUsedEdges(), experiment.getFulkersonUsedEdges());

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
//
//        ExperimentData data = new ExperimentData();
//        data.saveNetworks(List.of(network), List.of(UpdateType.UPDATE_WEIGHT));
//        data.loadAndDisplayNetwork("incorrect/ADD_VERTEX/network0.ser");
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

//        ExperimentData data = new ExperimentData();
//
//        Map<UpdateType, List<Integer>> usedEdges = new HashMap<>();
//
//        List<Integer> array = new ArrayList<>();
//
//        for(int it = 0; it != 300; it++) {
//            array.add(it);
//        }
//
//        usedEdges.put(UpdateType.ADD_VERTEX,array);
//        usedEdges.put(UpdateType.ADD_EDGE, array);
//        usedEdges.put(UpdateType.REMOVE_VERTEX, array);
//        usedEdges.put(UpdateType.REMOVE_EDGE, array);
//        usedEdges.put(UpdateType.UPDATE_WEIGHT, array);
//
//        data.saveUsedEdges(usedEdges, "test");


    }

}
