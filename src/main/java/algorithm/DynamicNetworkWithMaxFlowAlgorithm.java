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

    private DividerToClusters divider;
    private FordFulkerson fulkerson;

    @Getter
    private ClustersNetwork clusters;
    @Getter
    private DynamicNetwork network;
    @Getter
    private Map<Integer, Double> currentMaxFlow = new HashMap<>();

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
                Map<Integer, Double> flows = fulkerson.maxFlow(cluster, cluster.getSources(), cluster.getSinks());
                calculated.add(cluster);

                putValues(localMaxFlows, flows);
                cluster.getSinks().forEach(sink -> cluster.addMaxFlow(sink, localMaxFlows.get(sink)));
            });

        List<DynamicNetwork> pathNetworks = clusters.vertexSet().stream().filter(vertex -> vertex.path)
            .collect(Collectors.toList());

        while(calculated.size() != pathNetworks.size()) {
            pathNetworks.stream()
                .filter(vertex -> !calculated.contains(vertex) && getAllIncomingVertices(clusters, vertex).stream()
                        .filter(cluster -> cluster.path)
                        .allMatch(calculated::contains)
                )
                .forEach(cluster -> {
                    localMaxFlows.forEach((key, value) -> {
                        if(cluster.getSources().contains(key)) {
                            cluster.addVertex(-1 * key);
                            cluster.addEdge(-1 * key, key);
                            cluster.setEdgeWeight(cluster.getEdge(-1 * key, key), value);
                        }
                    });
                    Map<Integer, Double> flows =
                        fulkerson.maxFlow(
                            cluster,
                            cluster.getSources().stream()
                                .map(source -> source * (-1)).collect(Collectors.toList()),
                            cluster.getSinks()
                        );

                    putValues(localMaxFlows, flows);
                    cluster.getSinks().forEach(sink -> cluster.addMaxFlow(sink, localMaxFlows.get(sink)));
                    calculated.add(cluster);
                });
        }
        network.getSinks().forEach(sink -> currentMaxFlow.put(sink, localMaxFlows.get(sink)));
    }

    public void addVertex(Integer vertex) {
        network.addVertex(vertex);
        clusters.vertexSet().stream()
            .filter(cluster -> !cluster.path)
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
        List<DynamicNetwork> affectedNetworks = new ArrayList<>();
        determineAffectedClusters(vertex)
            .forEach(cluster -> {
                cluster.removeVertex(vertex);
                if(cluster.containsVertex(-vertex)) {
                    cluster.removeVertex(-vertex);
                }
                affectedNetworks.add(cluster);
            });
        List<DynamicNetwork> toRecalculate = determineFirstToCalculate(affectedNetworks);
        Map<Integer, Double> maxFlowChanges = new HashMap<>();

        while(!toRecalculate.isEmpty()) {
            toRecalculate.forEach(cluster -> {
                Map<Integer, Double> maxFlowChangesInCluster;
                if (cluster.source) {
                    maxFlowChangesInCluster = fulkerson.maxFlow(cluster, cluster.getSources(), cluster.getSinks());
                } else {
                    maxFlowChangesInCluster = fulkerson.maxFlow(
                            cluster,
                            cluster.getSources().stream()
                                .map(source -> source * (-1))
                                .collect(Collectors.toList()),
                            cluster.getSinks()
                    );
                }
                maxFlowChangesInCluster.forEach((key, value) -> {
                    if (!cluster.getMaxFlows().get(key).equals(value)) {
                        putValue(maxFlowChanges, key, value - cluster.getMaxFlows().get(key));
                        if (value == 0) {
                            cluster.removeSink(key);
                            cluster.removeVertex(key);
                        } else {
                            cluster.addMaxFlow(key, value);
                        }
                    }
                });
                maxFlowChangesInCluster.clear();
            });
            toRecalculate.clear();
            maxFlowChanges.forEach((changedVertex, change) -> {
                clusters.vertexSet().stream()
                    .filter(cluster -> cluster.getSources().contains(changedVertex))
                    .forEach(cluster -> {
                        if(cluster.containsEdge(-changedVertex, changedVertex)) {
                            double newFlow =
                                cluster.getEdgeWeight(cluster.getEdge(-changedVertex, changedVertex)) + change;
                            if(newFlow == 0) {
                                cluster.removeVertex(-changedVertex);
                                cluster.removeSource(changedVertex);
                            }
                            else {
                                cluster.setEdgeWeight(cluster.getEdge(-changedVertex, changedVertex), newFlow);
                            }
                        }
                        else {
                            cluster.removeSource(changedVertex);
                        }
                        if(!toRecalculate.contains(cluster)) {
                            toRecalculate.add(cluster);
                        }
                    });
            });
            maxFlowChanges.clear();
        }

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
        List<DynamicNetwork> affectedNetworks = new ArrayList<>();
        determineAffectedClusters(source, target)
            .forEach(cluster -> {
                if(cluster.containsVertex(source)) {
                    cluster.removeEdge(source, target);
                }
                affectedNetworks.add(cluster);
            });
    }

    public void changeWeight(Integer source, Integer target, double weight) {
        network.setEdgeWeight(network.getEdge(source, target), weight);

        List<DynamicNetwork> affectedNetworks = clusters.vertexSet().stream()
                .filter(cluster ->
                    (cluster.containsVertex(target) && !cluster.getSinks().contains(target))
                    ||
                    (cluster.containsVertex(source)) && !cluster.getSinks().contains(source)
                )
                .collect(Collectors.toList());
    }

    private void setNewMaxFlow() {
        clusters.vertexSet().stream()
            .filter(cluster -> cluster.sink)
            .forEach(cluster -> cluster.getMaxFlows().forEach((key, value) -> {
                network.addMaxFlow(key, value);
                currentMaxFlow.put(key, value);
            }));
    }

    private List<DynamicNetwork> determineFirstToCalculate(List<DynamicNetwork> networks) {
        return networks.stream()
            .filter(cluster ->
                getAllIncomingVertices(clusters, cluster).stream()
                    .noneMatch(networks::contains)
            )
            .collect(Collectors.toList());
    }

    private List<DynamicNetwork> determineAffectedClusters(Integer source, Integer target) {
        return clusters.vertexSet().stream()
            .filter(cluster -> cluster.containsVertex(target))
            .collect(Collectors.toList());
    }

    private List<DynamicNetwork> determineAffectedClusters(Integer vertex) {
        List<DynamicNetwork> affectedClusters = new ArrayList<>();
        clusters.vertexSet().stream()
            .filter(cluster -> cluster.containsVertex(vertex))
            .forEach(affectedClusters::add);

        return affectedClusters;
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

}
