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

        networks.forEach(network -> {
            Map<Integer, Double> actual = calculateActual(network);
            algorithm.init(network);
            isEqualResult(network, actual, UpdateType.INIT);

            this.algorithm.setUsedEdges(0);
            algorithm.addVertex(getNewVertex(network));
            gatherResults(network, UpdateType.ADD_VERTEX);

            this.algorithm.setUsedEdges(0);
            int vertex = this.rng.nextInt(network.vertexSet().size());
            algorithm.removeVertex(vertex);
            gatherResults(network, UpdateType.REMOVE_VERTEX);

            addRandomEdge(network);
            gatherResults(network, UpdateType.ADD_EDGE);

            removeUpdateRandomEdge(network, null);
            gatherResults(network, UpdateType.REMOVE_EDGE);

            removeUpdateRandomEdge(network, 0.0);
            gatherResults(network, UpdateType.UPDATE_WEIGHT);
        });
    }

    private void addRandomEdge(DynamicNetwork network) {

        int target;
        int source;
        do {
            source = this.rng.nextInt(network.vertexSet().size());
        } while (network.outDegreeOf(source) == network.vertexSet().size() - 1);
        do {
            target = this.rng.nextInt(network.vertexSet().size());
        } while (target == source || network.containsEdge(source, target));
        this.algorithm.setUsedEdges(0);
        algorithm.addEdge(source, target, rng.nextDouble() % 25);
    }

    private void removeUpdateRandomEdge(DynamicNetwork network, Double weight) {

        if(weight != null) {
            weight = rng.nextDouble() % 25;
        }

        int source = 0, target = 0;
        int it = rng.nextInt(network.edgeSet().size());
        for (WeightedEdge edge : network.edgeSet()) {
            it--;
            if(it == 0) {
                source = network.getEdgeSource(edge);
                target = network.getEdgeTarget(edge);
                break;
            }
        }
        this.algorithm.setUsedEdges(0);
        if(weight != null) {
            algorithm.changeWeight(source, target, weight);
        }
        else {
            algorithm.removeEdge(source, target);
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
            incorrectNetwork.add(network);
            incorrectAction.add(action);
        }
    }

}
