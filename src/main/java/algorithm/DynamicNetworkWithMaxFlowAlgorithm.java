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
    private List<Double> currentMaxFlow = new ArrayList<>();

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

        network.getSinks().forEach(sink -> currentMaxFlow.add(localMaxFlows.get(sink)));
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
                System.out.println("cluster:" + cluster);
                System.out.println(cluster.getMaxFlows());
                cluster.getSinks().stream()
                    .filter(sink -> cluster.containsEdge(vertex, sink))
                    .forEach(target -> clusters.vertexSet().stream()
                        .filter(sinkCluster -> sinkCluster.containsEdge(-target, target))
                        .forEach(sinkCluster -> {
                            System.out.println("sinkCluster:" + sinkCluster);
                            WeightedEdge edge = sinkCluster.getEdge(-target, target);
                            double edgeWeight = sinkCluster.getEdgeWeight(edge);
                            System.out.println(edgeWeight);
                            if(edgeWeight < cluster.getMaxFlows().get(target)) {
                                sinkCluster.setEdgeWeight(
                                    edge,
                                    cluster.getMaxFlows().get(target) - edgeWeight
                                );
                            }
                            else {
                                cluster.removeSink(target);
                                cluster.removeVertex(-target);
                            }
                            affectedNetworks.add(cluster);
                        }));
                cluster.removeVertexSafely(vertex);
                affectedNetworks.add(cluster);
            });
        System.out.println(affectedNetworks);
    }

    public void addEdge(Integer source, Integer target, double weight) {
        network.addEdge(source, target);
        network.setEdgeWeight(network.getEdge(source, target), weight);

    }

    public void removeEdge(Integer source, Integer target) {
        network.removeEdge(source, target);
        List<DynamicNetwork> affectedNetworks = new ArrayList<>();
        determineAffectedClusters(source, target)
            .forEach(cluster -> {
                if(cluster.containsVertex(source)) {
                    cluster.removeEdge(source, target);
                    if(cluster.getSinks().contains(target) && !network.getSinks().contains(target)) {
                        cluster.removeVertex(target);
                        cluster.removeSink(target);
                    }
                }
                else {
                    cluster.removeVertex(-target);
                    cluster.removeSource(target);
                }
                affectedNetworks.add(cluster);
            });
    }

    public void changeWeight(Integer source, Integer target, int weight) {
        network.setEdgeWeight(network.getEdge(source, target), weight);

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
        flows.forEach((key, value) -> {
            if ((!localMaxFlows.containsKey(key))) {
                localMaxFlows.put(key, value);
            }
            else {
                localMaxFlows.put(key, localMaxFlows.get(key) + value);
            }
        });
        flows.clear();
    }

    private List<DynamicNetwork> getAllIncomingVertices(ClustersNetwork network, DynamicNetwork vertex) {
        List<DynamicNetwork> branchesFromVertex = new ArrayList<>();

        for (WeightedEdge edge : network.incomingEdgesOf(vertex)) {
            branchesFromVertex.add(network.getEdgeSource(edge));
        }

        return branchesFromVertex;
    }

    private void removeVertexFromDynamicNetwork() {

    }

}
