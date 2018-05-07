package algorithm;

import algorithm.clustering.DividerToClusters;
import algorithm.fulkerson.FordFulkerson;
import entities.dynamicnetwork.ClustersNetwork;
import entities.dynamicnetwork.DynamicNetwork;
import entities.network.WeightedEdge;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;


public class DynamicNetworkWithMaxFlowAlgorithm {

    private final DividerToClusters divider;
    private final FordFulkerson fulkerson;

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

                putValues(localMaxFlows, flows);
                cluster.getSinks().forEach(sink -> cluster.addMaxFlow(sink, localMaxFlows.get(sink)));
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
        setNewMaxFlow();
    }

    public void addEdge(Integer source, Integer target, double weight) {
        network.addEdge(source, target);
        network.setEdgeWeight(network.getEdge(source, target), weight);

        List<DynamicNetwork> affectedNetworks = clusters.vertexSet().stream()
            .filter(cluster ->
                (cluster.containsVertex(target) && !cluster.getSinks().contains(target))
                ||
                (cluster.containsVertex(source)) && !cluster.getSinks().contains(source)
            )
            .collect(Collectors.toList());
    }

    public void removeEdge(Integer source, Integer target) {
        network.removeEdge(source, target);
        List<DynamicNetwork> affectedNetworks = determineAndAdjustAffectedClusters(source, target);
        adjustAllAffectedClusters(affectedNetworks, null);
        setNewMaxFlow();
    }

    public void changeWeight(Integer source, Integer target, double weight) {
        network.setEdgeWeight(network.getEdge(source, target), weight);
        List<DynamicNetwork> affectedNetworks = determineAndAdjustAffectedClusters(source, target, weight);
        adjustAllAffectedClusters(affectedNetworks, null);
        setNewMaxFlow();
    }

    private void adjustAllAffectedClusters(
        List<DynamicNetwork> affectedNetworks, Integer vertexToRemove
    ) {
        Map<Integer, Double> maxFlowChanges = new HashMap<>();

        while(!affectedNetworks.isEmpty()) {
            calculateAndUpdateChanges(affectedNetworks, maxFlowChanges);
            maxFlowChanges.forEach((changedVertex, change) -> {
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
                        });
            });
            maxFlowChanges.clear();
        }
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
                        cluster.removeMaxFlow(key);
                        cluster.removeSink(key);
                        cluster.removeVertex(key);
                    }
                }
            });
            maxFlowChangesInCluster.clear();
        });
        affectedNetworks.clear();
    }

    private Map<Integer, Double> calculate(DynamicNetwork cluster) {
        if (cluster.source) {
            return fulkerson.maxFlow(cluster, cluster.getSources(), cluster.getSinks());
        }
        else {
            return fulkerson.maxFlow(
                cluster,
                cluster.getSources().stream()
                    .map(source -> source * (-1))
                    .collect(Collectors.toList()),
                cluster.getSinks()
            );
        }
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
            .filter(cluster -> cluster.containsVertex(source) && cluster.containsVertex(target))
            .peek(cluster -> cluster.setEdgeWeight(cluster.getEdge(source, target), weight))
            .collect(Collectors.toList());
    }

    private void putValues(Map<Integer, Double> localMaxFlows, Map<Integer, Double> flows) {
        flows.forEach((key, value) -> putValue(localMaxFlows, key, value));
        flows.clear();
    }

    private void putValue(Map<Integer, Double> localMaxFlows, Integer key, Double value) {
        if ((!localMaxFlows.containsKey(key))) {
            localMaxFlows.put(key, value);
        }
        else {
            localMaxFlows.put(key, localMaxFlows.get(key) + value);
        }
    }

    private List<DynamicNetwork> getAllIncomingVertices(ClustersNetwork network, DynamicNetwork vertex) {
        List<DynamicNetwork> branchesFromVertex = new ArrayList<>();
        Set<WeightedEdge> edges;
        try {
            edges = network.incomingEdgesOf(vertex);
        }
        catch(Exception e) {
            edges = network.edgeSet().stream()
                    .filter(edge -> network.getEdgeTarget(edge).equals(vertex))
                    .collect(Collectors.toSet());
        }

        for (WeightedEdge edge : edges) {
            branchesFromVertex.add(network.getEdgeSource(edge));
        }

        return branchesFromVertex;
    }

    private List<DynamicNetwork> getAllOutgoingVertices(ClustersNetwork network, DynamicNetwork vertex) {
        List<DynamicNetwork> branchesFromVertex = new ArrayList<>();
        Set<WeightedEdge> edges;
        try {
            edges = network.outgoingEdgesOf(vertex);
        }
        catch(Exception e) {
            edges = network.edgeSet().stream()
                    .filter(edge -> network.getEdgeSource(edge).equals(vertex))
                    .collect(Collectors.toSet());
        }

        for (WeightedEdge edge : edges) {
            branchesFromVertex.add(network.getEdgeTarget(edge));
        }

        return branchesFromVertex;
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
                    cluster.getSinks().forEach(sink -> cluster.addMaxFlow(sink, localMaxFlows.get(sink)));
                    calculated.add(cluster);
                });
        }
        network.getSinks().forEach(sink -> currentMaxFlow.put(sink, localMaxFlows.get(sink)));
    }

}
