package algorithm;

import algorithm.clustering.DividerToClusters;
import algorithm.fulkerson.FordFulkerson;
import entities.dynamicnetwork.ClustersNetwork;
import entities.dynamicnetwork.DynamicNetwork;
import entities.network.WeightedEdge;
import gui.GUI;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;


public class MaxFlowInDynamicNetworkFinder {

    private DividerToClusters divider;
    private FordFulkerson fulkerson;

    @Getter
    private ClustersNetwork clusters;
    @Getter
    private DynamicNetwork network;
    @Getter
    private List<Double> currentMaxFlow = new ArrayList<>();

    public MaxFlowInDynamicNetworkFinder(DividerToClusters divider, FordFulkerson fulkerson) {
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
                    calculated.add(cluster);
                    putValues(localMaxFlows, flows);
                });
        }

        network.getSinks().forEach(sink -> currentMaxFlow.add(localMaxFlows.get(sink)));
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


}
