package fulkerson;

import entities.Network;
import entities.WeightedEdge;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FordFulkerson {

    private final BFS bfs;
    @Getter
    private Network maxFlow;
    @Getter
    private final Map<Integer, Double> maxFlowValues = new HashMap<>();

    public FordFulkerson(BFS bfs) {
        this.bfs = bfs;
    }

    public Map<Integer, Double> maxFlow(Network network, List<Integer> sources, List<Integer> sinks) {
        Network residual = (Network) network.clone();
        initMaxFlowValues(sinks);

        boolean containsSink = true;
        Map<Integer, Integer> parents;

        while(containsSink) {
            parents = bfs.bfs(residual, sources, sinks);
            containsSink = false;

            for (Integer sink : sinks) {
                if (parents.containsKey(sink)) {
                    containsSink = true;
                    double pathFlow = minPathFlow(parents, residual, sources, sink);
                    maxFlowValues.put(sink, maxFlowValues.get(sink) + pathFlow);
                    residual = updateResidual(parents, residual, pathFlow, sources, sink);
                }
            }

        }

        this.maxFlow = residual;

        return this.maxFlowValues;
    }

    private void initMaxFlowValues(List<Integer> sinks) {
        sinks.forEach(sink -> maxFlowValues.put(sink, 0.0));
    }

    private Network updateResidual(
            Map<Integer, Integer> flow, Network network, double pathFlow, List<Integer> sources, Integer vertex
    ) {
        for(int next = flow.get(vertex); !sources.contains(next); next = flow.get(vertex)) {
            WeightedEdge edge = network.getEdge(next, vertex);
            double currentWeight = network.getEdgeWeight(edge);

            if(currentWeight - pathFlow != 0) {
                network.setEdgeWeight(edge, currentWeight - pathFlow);
            }
            else {
                network.removeEdge(edge);
            }
            vertex = next;
        }
        return network;
    }

    private Double minPathFlow(
            Map<Integer, Integer> flow, Network network, List<Integer> sources, Integer vertex
    ) {
        double pathFlow = Double.MAX_VALUE;

        for(int next = flow.get(vertex); !sources.contains(next); next = flow.get(vertex)) {
            pathFlow = Double.min(pathFlow, network.getEdgeWeight(network.getEdge(next, vertex)));
            vertex = next;
        }

        return pathFlow;
    }

}
