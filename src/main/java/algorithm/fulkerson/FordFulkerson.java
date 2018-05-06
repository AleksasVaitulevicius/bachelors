package algorithm.fulkerson;

import entities.network.Network;
import entities.network.WeightedEdge;
import gui.GUI;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FordFulkerson {

    private final BFS bfs;
    @Getter
    private final Map<Integer, Double> maxFlowValues = new HashMap<>();
    @Getter
    private Network maxFlow;

    public FordFulkerson(BFS bfs) {
        this.bfs = bfs;
        this.maxFlow = new Network();
    }

    public void printMaxFlow(int width, int height) {
        new GUI("max flow", this.maxFlow).display(width, height);
    }

    public void reset() {
        this.maxFlow = new Network();
    }

    public Map<Integer, Double> maxFlow(Network network, List<Integer> sources, List<Integer> sinks) {
        Network residual = network.clone();
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

        return this.maxFlowValues;
    }

    private void initMaxFlowValues(List<Integer> sinks) {
        sinks.forEach(sink -> maxFlowValues.put(sink, 0.0));
    }

    private void updateMaxFlow(int source, int target, double weight) {

        if(!this.maxFlow.containsVertex(source)) {
            this.maxFlow.addVertex(source);
        }

        if(!this.maxFlow.containsVertex(target)) {
            this.maxFlow.addVertex(target);
        }

        if(!this.maxFlow.containsEdge(source, target)) {
            this.maxFlow.addEdge(source, target);
            this.maxFlow.setEdgeWeight(this.maxFlow.getEdge(source, target), 0);
        }

        WeightedEdge edge = this.maxFlow.getEdge(source, target);
        this.maxFlow.setEdgeWeight(edge, this.maxFlow.getEdgeWeight(edge) + weight);
    }

    private Network updateResidual(
            Map<Integer, Integer> flow, Network network, double pathFlow, List<Integer> sources, Integer vertex
    ) {
        for(int next; !sources.contains(vertex); vertex = next) {
            next = flow.get(vertex);
            WeightedEdge edge = network.getEdge(next, vertex);
            double currentWeight = network.getEdgeWeight(edge);
            updateMaxFlow(next, vertex, pathFlow);

            if(currentWeight - pathFlow != 0) {
                network.setEdgeWeight(edge, currentWeight - pathFlow);
            }
            else {
                network.removeEdge(edge);
            }
        }
        return network;
    }

    private Double minPathFlow(
            Map<Integer, Integer> flow, Network network, List<Integer> sources, Integer vertex
    ) {
        double pathFlow = Double.MAX_VALUE;

        for(int next; !sources.contains(vertex); vertex = next) {
            next = flow.get(vertex);
            pathFlow = Double.min(pathFlow, network.getEdgeWeight(network.getEdge(next, vertex)));
        }

        return pathFlow;
    }

}
