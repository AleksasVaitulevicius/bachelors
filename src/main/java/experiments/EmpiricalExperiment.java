package experiments;

import algorithm.DynamicNetworkWithMaxFlowAlgorithm;
import algorithm.fulkerson.FordFulkerson;
import entities.UpdateType;
import entities.dynamicnetwork.DynamicNetwork;
import entities.network.WeightedEdge;
import generator.RandomNetworkListGenerator;
import lombok.Getter;

import java.util.*;

public class EmpiricalExperiment {

    private final FordFulkerson fulkerson;
    private final DynamicNetworkWithMaxFlowAlgorithm algorithm;
    private final RandomNetworkListGenerator generator;
    private final Random rng;

    @Getter
    private final List<DynamicNetwork> incorrectNetwork = new ArrayList<>();
    @Getter
    private final List<UpdateType> incorrectAction = new ArrayList<>();
    @Getter
    private final Map<UpdateType, List<Integer>> algorithmUsedEdges = new HashMap<>();
    @Getter
    private final Map<UpdateType, List<Integer>> fulkersonUsedEdges = new HashMap<>();

    public EmpiricalExperiment(
        FordFulkerson fulkerson,
        DynamicNetworkWithMaxFlowAlgorithm algorithm,
        RandomNetworkListGenerator generator,
        Random rng
    ) {
        this.fulkerson = fulkerson;
        this.algorithm = algorithm;
        this.generator = generator;
        this.rng = rng;
    }

    public void perform() throws Exception {
        List<DynamicNetwork> networks;
        try {
            networks = generator.generate();
        }
        catch (Exception e) {
            throw new Exception("Failed to generate networks", e);
        }
        initUsedEdges();

        int it = 1;
        for (DynamicNetwork network : networks) {
            System.out.println("progress:" + it + "/300");
            it++;
            try {
                Map<Integer, Double> actual = calculateActual(network);
                algorithm.init(network);
                isEqualResult(network, actual, UpdateType.INIT);

                this.algorithm.setUsedEdges(0);
                algorithm.addVertex(getNewVertex(network));
                gatherResults(network, UpdateType.ADD_VERTEX);

                this.algorithm.setUsedEdges(0);
                int max = network.vertexSet().stream().mapToInt(value -> value).max().orElse(0);
                int vertex = this.rng.nextInt(max);
                int start = vertex;
                while (
                    !network.vertexSet().contains(vertex) ||
                    network.getSinks().contains(vertex) ||
                    network.getSources().contains(vertex)
                ) {
                    vertex = (vertex + 1) % max;
                    if(vertex == start) {
                        throw new Exception("cannot pick vertex to remove");
                    }
                }
                try{
                    algorithm.removeVertex(vertex);
                }
                catch (Exception e) {
                    throw new Exception( "Failed to remove vertex: " + vertex, e);
                }
                gatherResults(network, UpdateType.REMOVE_VERTEX);

                addRandomEdge(network);
                gatherResults(network, UpdateType.ADD_EDGE);

                removeUpdateRandomEdge(network, null);
                gatherResults(network, UpdateType.REMOVE_EDGE);

                removeUpdateRandomEdge(network, rng.nextDouble() % 25);
                gatherResults(network, UpdateType.UPDATE_WEIGHT);
            } catch (Exception ex) {
                incorrectNetwork.add(network);
                incorrectAction.add(UpdateType.ERROR);
                System.out.println(network);
                ex.printStackTrace();
            }
        }
    }

    private void addRandomEdge(DynamicNetwork network) throws Exception {

        int max = network.vertexSet().stream().mapToInt(value -> value).max().orElse(0);
        int target = 0;
        int source = this.rng.nextInt(max);
        int start = source;
        while (
            !network.containsVertex(source) || network.outDegreeOf(source) >= network.vertexSet().size() - 1
        ) {
            source = (source + 1) % max;
            if(source == start) {
                throw new Exception("cannot pick source");
            }
        }
        for(Integer vertex: network.vertexSet()) {
            if(vertex != source && !network.containsEdge(source, target)) {
                target = vertex;
                break;
            }
        }
        if(target == 0) {
            throw new Exception("cannot pick target");
        }
        this.algorithm.setUsedEdges(0);
        double weight = rng.nextDouble() % 25;
        try {
            algorithm.addEdge(source, target, weight);
        }
        catch (Exception e) {
            throw new Exception(
                "Failed to add edge: (" + source + ", " + target + "," + weight + ")", e
            );
        }
    }

    private void removeUpdateRandomEdge(DynamicNetwork network, Double weight) throws Exception {

        int source = 0, target = 0;
        int it = rng.nextInt(network.edgeSet().size()) - 1;
        for (WeightedEdge edge : network.edgeSet()) {
            source = network.getEdgeSource(edge);
            target = network.getEdgeTarget(edge);
            it--;
            if (it == 0) {
                break;
            }
        }
        this.algorithm.setUsedEdges(0);
        if(weight != null) {
            try{
                algorithm.changeWeight(source, target, weight);
            }
            catch (Exception e) {
                throw new Exception(
                    "Failed to set weight: (" + source + ", " + target + "," + weight + ")", e
                );
            }
        }
        else {
            try {
                algorithm.removeEdge(source, target);
            }
            catch (Exception e) {
                throw new Exception("Failed to remove edge: (" + source + ", " + target + ")", e);
            }
        }
    }

    private void gatherResults(DynamicNetwork network, UpdateType action) {
        this.algorithmUsedEdges.get(action).add(this.algorithm.getUsedEdges());
        this.fulkerson.setUsedEdges(0);
        Map<Integer, Double> actual = calculateActual(network);
        int usedEdges = this.fulkerson.getUsedEdges();
        isEqualResult(network, actual, action);
        this.fulkersonUsedEdges.get(action).add(usedEdges);
    }

    private void initUsedEdges() {
        this.algorithmUsedEdges.put(UpdateType.ADD_VERTEX, new ArrayList<>());
        this.algorithmUsedEdges.put(UpdateType.ADD_EDGE, new ArrayList<>());
        this.algorithmUsedEdges.put(UpdateType.REMOVE_VERTEX, new ArrayList<>());
        this.algorithmUsedEdges.put(UpdateType.REMOVE_EDGE, new ArrayList<>());
        this.algorithmUsedEdges.put(UpdateType.UPDATE_WEIGHT, new ArrayList<>());
        this.fulkersonUsedEdges.put(UpdateType.ADD_VERTEX, new ArrayList<>());
        this.fulkersonUsedEdges.put(UpdateType.ADD_EDGE, new ArrayList<>());
        this.fulkersonUsedEdges.put(UpdateType.REMOVE_VERTEX, new ArrayList<>());
        this.fulkersonUsedEdges.put(UpdateType.REMOVE_EDGE, new ArrayList<>());
        this.fulkersonUsedEdges.put(UpdateType.UPDATE_WEIGHT, new ArrayList<>());
    }

    private Integer getNewVertex(DynamicNetwork network) {
        try {
            return network.vertexSet().stream()
                .mapToInt(value-> value).max().orElseThrow(Exception::new) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    private Map<Integer, Double> calculateActual(DynamicNetwork network) {
        Map<Integer, Double> result = new HashMap<>(fulkerson.maxFlow(
                network, network.getSources(), network.getSinks()
        ));
        fulkerson.reset();
        return result;
    }

    private void isEqualResult(DynamicNetwork network, Map<Integer, Double> actual, UpdateType action) {
        Map<Integer, Double> expected = algorithm.getCurrentMaxFlow();
        double actualValue = actual.get(network.getSinks().get(0));
        double expectedValue = expected.get(network.getSinks().get(0));

        if(actualValue != expectedValue) {
            System.out.println("Failed to " + action);
            incorrectNetwork.add(network);
            incorrectAction.add(action);
        }
    }

}
