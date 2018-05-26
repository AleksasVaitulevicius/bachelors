package algorithm;

import algorithm.clustering.DividerToClusters;
import algorithm.fulkerson.FordFulkerson;
import entities.dynamicnetwork.ClustersNetwork;
import entities.dynamicnetwork.DynamicNetwork;
import entities.network.WeightedEdge;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;


public class DynamicNetworkWithMaxFlowAlgorithm {

    private final DividerToClusters divider;
    private final FordFulkerson fulkerson;

    @Getter @Setter
    private int usedEdges = 0;
    @Getter
    private ClustersNetwork clusters;
    @Getter
    private DynamicNetwork network;
    @Getter
    private final Map<Integer, Double> currentMaxFlow = new HashMap<>();

    public DynamicNetworkWithMaxFlowAlgorithm(DividerToClusters divider, FordFulkerson fulkerson) {
        this.divider = divider;
        this.fulkerson = fulkerson;
    }

    public void init(DynamicNetwork network) {
        this.network = network;
        this.clusters = divider.divideToClusters(network);
        Map<Integer, Double> localMaxFlows = new HashMap<>();
        List<DynamicNetwork> calculated = new ArrayList<>();

        clusters.vertexSet().stream()
            .filter(vertex -> vertex.source)
            .forEach(cluster -> {
                Map<Integer, Double> flows = calculate(cluster);
                calculated.add(cluster);

                cluster.getSinks().forEach(sink -> cluster.addMaxFlow(sink, flows.get(sink)));
                putValues(localMaxFlows, flows);
                fulkerson.setUsedEdges(0);
                flows.clear();
            });

        initLoop(calculated, localMaxFlows);
    }

    public void addVertex(Integer vertex) {
        network.addVertex(vertex);
        clusters.vertexSet().stream()
            .filter(cluster -> getAllOutgoingVertices(clusters, cluster).size() == 0 &&
                getAllIncomingVertices(clusters, cluster).size() == 0
            )
            .findFirst().ifPresentOrElse(
                cluster -> cluster.addVertex(vertex),
                () -> {
                    DynamicNetwork newNetwork = new DynamicNetwork();
                    newNetwork.addVertex(vertex);
                    clusters.addVertex(newNetwork);
                }
            );
    }

    public void removeVertex(Integer vertex) {
        network.removeVertex(vertex);
        List<DynamicNetwork> affectedNetworks = determineAndAdjustAffectedClusters(vertex);
        adjustAllAffectedClusters(affectedNetworks, vertex);
        clusters.clearEmpty();
        setNewMaxFlow();
    }

    public void addEdge(Integer source, Integer target, double weight) {
        network.addEdge(source, target);
        network.setEdgeWeight(network.getEdge(source, target), weight);
        List<Integer> eulerCycleVertices = detectEulerCycle(source, target);

        if(!eulerCycleVertices.isEmpty()) {
            List<DynamicNetwork> affectedNetworks = clusters.vertexSet().stream()
                .filter(cluster -> cluster.vertexSet().stream().anyMatch(vertex ->
                        eulerCycleVertices.contains(vertex) && !cluster.getSinks().contains(vertex)
                ))
                .collect(Collectors.toList());
            DynamicNetwork cluster = mergeNetworks(affectedNetworks);
            cluster.addEdge(source, target);
            cluster.setEdgeWeight(cluster.getEdge(source, target), weight);
            clusters.addVertex(cluster);
            adjustAllAffectedClusters(new ArrayList<>(List.of(cluster)), null);

        }
        else {
            List<DynamicNetwork> affectedNetworks = new ArrayList<>();
            clusters.vertexSet().stream()
                .filter(cluster -> cluster.containsVertex(source) && !cluster.getSinks().contains(source)
                )
                .forEach(cluster -> {
                    if(!cluster.containsVertex(target)) {
                        cluster.addVertex(target);
                        cluster.addSink(target);
                        cluster.addMaxFlow(target, 0.0);
                    }
                    cluster.addEdge(source, target);
                    cluster.setEdgeWeight(cluster.getEdge(source, target), weight);
                    affectedNetworks.add(cluster);
                });

            adjustAllAffectedClusters(affectedNetworks, null);
        }
        clusters.clearEmpty();
        setNewMaxFlow();
    }

    public void removeEdge(Integer source, Integer target) {
        network.removeEdge(source, target);
        List<DynamicNetwork> affectedNetworks = determineAndAdjustAffectedClusters(source, target);
        adjustAllAffectedClusters(affectedNetworks, null);
        clusters.clearEmpty();
        setNewMaxFlow();
    }

    public void changeWeight(Integer source, Integer target, double weight) {
        network.setEdgeWeight(network.getEdge(source, target), weight);
        List<DynamicNetwork> affectedNetworks = determineAndAdjustAffectedClusters(source, target, weight);
        adjustAllAffectedClusters(affectedNetworks, null);
        clusters.clearEmpty();
        setNewMaxFlow();
    }

    private List<Integer> detectEulerCycle(Integer source, Integer target) {

        Stack<List<Integer>> possibleCycles = new Stack<>();
        possibleCycles.push(List.of(source, target));
        List<Integer> verticesOfCycles = new ArrayList<>();

        while (!possibleCycles.isEmpty()) {
            List<Integer> possibleCycle = possibleCycles.pop();
            this.network.outgoingEdgesOf(possibleCycle.get(possibleCycle.size() - 1)).forEach(edge -> {
                Integer edgeTarget = network.getEdgeTarget(edge);
                if (edgeTarget.equals(source)) {
                    verticesOfCycles.addAll(
                        possibleCycle.stream()
                            .filter(vertex -> !verticesOfCycles.contains(vertex))
                            .collect(Collectors.toList())
                    );
                }
                else {
                    if(!possibleCycle.contains(edgeTarget)) {
                        List<Integer> newPossibleCycle = new ArrayList<>(possibleCycle);
                        newPossibleCycle.add(edgeTarget);
                        possibleCycles.push(newPossibleCycle);
                    }
                }
            });
        }

        return verticesOfCycles;
    }

    private void adjustAllAffectedClusters(
        List<DynamicNetwork> affectedNetworks, Integer vertexToRemove
    ) {
        Map<Integer, Double> maxFlowChanges = new HashMap<>();

        while(!affectedNetworks.isEmpty()) {
            calculateAndUpdateChanges(affectedNetworks, maxFlowChanges);
            maxFlowChanges.forEach((changedVertex, change) ->
                clusters.vertexSet().stream()
                    .filter(cluster -> cluster.getSources().contains(changedVertex))
                    .forEach(cluster -> {
                        double newFlow =
                            cluster.getEdgeWeight(cluster.getEdge(-changedVertex, changedVertex)) + change;
                        if(newFlow == 0) {
                            if(vertexToRemove != null && changedVertex.equals(vertexToRemove)) {
                                cluster.removeVertex(changedVertex);
                            }
                            cluster.removeVertex(-changedVertex);
                            cluster.removeSource(changedVertex);
                        }
                        else {
                            cluster.setEdgeWeight(cluster.getEdge(-changedVertex, changedVertex), newFlow);
                        }
                        if(!affectedNetworks.contains(cluster)) {
                            affectedNetworks.add(cluster);
                        }
                    }));
            maxFlowChanges.clear();
        }
    }

    private DynamicNetwork mergeNetworks(List<DynamicNetwork> networks) {
        DynamicNetwork network = networks.remove(0);

        networks.forEach(networkToMerge -> {
            networkToMerge.vertexSet().stream()
                .filter(vertex -> !network.containsVertex(vertex) && vertex > 0)
                .forEach(network::addVertex);
            networkToMerge.getSources().forEach(source -> {
                WeightedEdge edge = networkToMerge.getEdge(-source, source);
                double weight = networkToMerge.getEdgeWeight(edge);
                if(!network.getSinks().contains(source)) {
                    extractMaxFlow(network, source, weight);
                }
                else {
                    if(!network.getMaxFlows().get(source).equals(weight)) {
                        extractMaxFlow(network, source, weight - network.getMaxFlows().get(source));
                    }
                    network.removeSink(source);
                    network.removeMaxFlow(source);
                }
            });
            networkToMerge.getSinks().forEach(sink -> {
                if(!network.getSources().contains(sink)) {
                    network.addSink(sink);
                    network.addMaxFlow(sink, networkToMerge.getMaxFlows().get(sink));
                }
                else {
                    WeightedEdge edge = network.getEdge(-sink, sink);
                    double weight = network.getEdgeWeight(edge);
                    if(networkToMerge.getMaxFlows().get(sink).equals(weight)) {
                        network.removeSource(sink);
                        network.removeVertex(-sink);
                    }
                    else {
                        network.setEdgeWeight(edge, weight - networkToMerge.getMaxFlows().get(sink));
                    }
                }
            });
            networkToMerge.edgeSet().stream()
                .filter(edge -> !network.containsEdge(network.getEdgeSource(edge), network.getEdgeTarget(edge)))
                .forEach(edge -> {
                    network.addEdge(networkToMerge.getEdgeSource(edge), networkToMerge.getEdgeTarget(edge));
                    WeightedEdge networkEdge = network.getEdge(
                        networkToMerge.getEdgeSource(edge),
                        networkToMerge.getEdgeTarget(edge)
                    );
                    network.setEdgeWeight(networkEdge, networkToMerge.getEdgeWeight(edge));
                });

            this.clusters.removeVertex(networkToMerge);
        });
        this.clusters.removeVertex(network);

        return network;
    }

    private void extractMaxFlow(DynamicNetwork to, Integer source, double maxFlow) {
        to.addSource(source);
        to.addVertex(-source);
        to.addEdge(-source, source);
        WeightedEdge edge = to.getEdge(-source, source);
        network.setEdgeWeight(edge, maxFlow);
    }

    private void calculateAndUpdateChanges(
        List<DynamicNetwork> affectedNetworks, Map<Integer, Double> maxFlowChanges
    ) {
        affectedNetworks.forEach(cluster -> {
            Map<Integer, Double> maxFlowChangesInCluster = calculate(cluster);
            maxFlowChangesInCluster.forEach((key, value) -> {
                if (!cluster.getMaxFlows().get(key).equals(value)) {
                    putValue(maxFlowChanges, key, value - cluster.getMaxFlows().get(key));
                    if(value != 0) {
                        cluster.addMaxFlow(key, value);
                    }
                    else {
                        if(!network.getSinks().contains(key)) {
                            cluster.removeMaxFlow(key);
                            cluster.removeSink(key);
                            cluster.removeVertex(key);
                        }
                        else {
                            cluster.addMaxFlow(key, 0.0);
                        }
                    }
                }
            });
            maxFlowChangesInCluster.clear();
        });
        affectedNetworks.clear();
    }

    private Map<Integer, Double> calculate(DynamicNetwork cluster) {
        Map<Integer, Double> result;

        fulkerson.setUsedEdges(0);
        if (cluster.source) {
            result = fulkerson.maxFlow(cluster, cluster.getSources(), cluster.getSinks());
        }
        else {
            result = fulkerson.maxFlow(
                cluster,
                cluster.getSources().stream()
                    .filter(source -> cluster.containsVertex(-source))
                    .map(source -> source * (-1))
                    .collect(Collectors.toList()),
                cluster.getSinks()
            );
        }

        this.usedEdges += fulkerson.getUsedEdges();
        fulkerson.setUsedEdges(0);
        return result;
    }

    private void setNewMaxFlow() {
        clusters.vertexSet().stream()
            .filter(cluster -> cluster.sink)
            .forEach(cluster -> cluster.getMaxFlows().forEach((key, value) -> {
                network.addMaxFlow(key, value);
                currentMaxFlow.put(key, value);
            }));
    }

    private List<DynamicNetwork> determineAndAdjustAffectedClusters(Integer vertex) {
        return clusters.vertexSet().stream()
            .filter(cluster -> cluster.containsVertex(vertex) && !cluster.containsVertex(-vertex))
            .peek(cluster -> cluster.removeVertex(vertex))
            .collect(Collectors.toList());
    }

    private List<DynamicNetwork> determineAndAdjustAffectedClusters(Integer source, Integer target) {
        return clusters.vertexSet().stream()
            .filter(cluster -> cluster.containsVertex(source) && cluster.containsVertex(target))
            .peek(cluster -> cluster.removeEdge(source, target))
            .collect(Collectors.toList());
    }

    private List<DynamicNetwork> determineAndAdjustAffectedClusters(
        Integer source, Integer target, double weight
    ) {
        return clusters.vertexSet().stream()
            .filter(cluster -> cluster.containsEdge(source, target))
            .peek(cluster -> cluster.setEdgeWeight(cluster.getEdge(source, target), weight))
            .collect(Collectors.toList());
    }

    private void putValues(Map<Integer, Double> localMaxFlows, Map<Integer, Double> flows) {
        flows.forEach((key, value) -> putValue(localMaxFlows, key, value));
    }

    private void putValue(Map<Integer, Double> localMaxFlows, Integer key, Double value) {
        if ((!localMaxFlows.containsKey(key))) {
            localMaxFlows.put(key, value);
        }
        else {
            localMaxFlows.put(key, localMaxFlows.get(key) + value);
        }
    }

    private void initLoop(List<DynamicNetwork> calculated, Map<Integer, Double> localMaxFlows) {
        List<DynamicNetwork> sinkNetworks = clusters.vertexSet().stream().filter(vertex -> vertex.sink)
            .collect(Collectors.toList());

        while(!calculated.containsAll(sinkNetworks)) {
            clusters.vertexSet().stream()
                .filter(cluster -> !calculated.contains(cluster) &&
                    getAllIncomingVertices(clusters, cluster).stream().allMatch(calculated::contains)
                )
                .forEach(cluster -> {
                    localMaxFlows.forEach((key, value) -> {
                        if(cluster.getSources().contains(key)) {
                            cluster.addVertex(-key);
                            cluster.addEdge(-key, key);
                            cluster.setEdgeWeight(cluster.getEdge(-key, key), value);
                        }
                    });
                    Map<Integer, Double> flows = calculate(cluster);

                    putValues(localMaxFlows, flows);
                    cluster.getSinks().forEach(sink -> cluster.addMaxFlow(sink, flows.get(sink)));
                    flows.clear();
                    calculated.add(cluster);
                });
        }
        network.getSinks().forEach(sink -> currentMaxFlow.put(sink, localMaxFlows.get(sink)));
    }

    private List<DynamicNetwork> getAllOutgoingVertices(ClustersNetwork clusters, DynamicNetwork vertex) {
        return clusters.vertexSet().stream()
            .filter(cluster -> cluster.getSources().stream()
                .anyMatch(source -> vertex.getSinks().contains(source))
            )
            .collect(Collectors.toList());
    }
    private List<DynamicNetwork> getAllIncomingVertices(ClustersNetwork clusters, DynamicNetwork vertex) {
        return clusters.vertexSet().stream()
            .filter(cluster -> cluster.getSinks().stream()
                    .anyMatch(sink -> vertex.getSources().contains(sink))
            )
            .collect(Collectors.toList());
    }
}
