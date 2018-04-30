package algorithm.clustering;

import entities.EulerCycleWarps;
import entities.dynamicnetwork.ClustersNetwork;
import entities.dynamicnetwork.DynamicNetwork;
import gui.GUI;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;


public class DividerToClusters {

    @Getter @Setter
    private int nonEulerCycleClusterMaxSize = 4;

    public ClustersNetwork divideToClusters(DynamicNetwork network) {
        EulerCycleWarps warps = new EulerCycleWarps();
        warps.constructFrom(network);
        new GUI("Dynamic Network", network).display(900, 900);

        network.getSources().forEach(source -> warpEulerCycles(warps, source));

        new GUI("warps", warps).display(900, 900);



        return null;
    }

//    private ClustersNetwork divideToClusters(DynamicNetwork network, EulerCycleWarps warps) {
//
//        List<Integer> clusterVertices = new ArrayList<>();
//
//        network.getSources().forEach(source -> {
//
//        });
//
//    }

    private void warpEulerCycles(EulerCycleWarps warps, Integer source) {
        Map<List<Integer>, List<List<Integer>>> branches = new HashMap<>();
        List<Integer> vertex = List.of(source);

        Stack<List<Integer>> path = new Stack<>();
        path.push(vertex);

        while(!path.isEmpty()) {

            List<List<Integer>> branchesFromVertex = (!branches.containsKey(vertex)) ?
                    getAllOutgoingVertices(warps, vertex) : branches.get(vertex);

            if (!branchesFromVertex.isEmpty()) {
                List<Integer> newVertex = branchesFromVertex.remove(0);

                if(!warps.containsVertex(newVertex)) {
                    final List<Integer> oldVertex = newVertex;
                    newVertex = warps.vertexSet().stream()
                        .filter(warpedVertex -> warpedVertex.containsAll(oldVertex))
                        .findFirst().orElse(null);
                }

                if(!path.contains(newVertex)) {
                    path.push(newVertex);
                }
                else {
                    path.push(newVertex);
                    path = warpVertices(warps, path);
                    newVertex = path.peek();
                }
                branches.put(vertex, branchesFromVertex);
                vertex = newVertex;
            }
            else {
                path.pop();
                if(!path.isEmpty()) {
                    vertex = path.peek();
                }
            }
        }

    }

    private Stack<List<Integer>> warpVertices(EulerCycleWarps warps, Stack<List<Integer>> path) {

        List<Integer> startingVertex = path.pop();
        List<Integer> newVertex = new ArrayList<>(startingVertex);
        List<List<Integer>> outgoingVertices = new ArrayList<>(getAllOutgoingVertices(warps, startingVertex));
        List<List<Integer>> incomingVertices = new ArrayList<>(getAllIncomingVertices(warps, startingVertex));
        warps.removeVertex(startingVertex);

        while(!path.peek().equals(startingVertex)) {
            List<Integer> nextVertex = path.pop();
            outgoingVertices.addAll(getAllOutgoingVertices(warps, nextVertex));
            incomingVertices.addAll(getAllIncomingVertices(warps, nextVertex));
            warps.removeVertex(nextVertex);
            newVertex.addAll(nextVertex);
        }

        warps.addVertex(newVertex);
        warps.addSourcesIfVerticesExist(newVertex, incomingVertices);
        warps.addTargetsIfVerticesExist(newVertex, outgoingVertices);
        path.pop();
        path.push(newVertex);

        return path;
    }

    private List<List<Integer>> getAllOutgoingVertices(EulerCycleWarps warps, List<Integer> vertex) {
        List<List<Integer>> branchesFromVertex = new ArrayList<>();

        for (DefaultEdge edge : warps.outgoingEdgesOf(vertex)) {
            branchesFromVertex.add(warps.getEdgeTarget(edge));
        }

        return branchesFromVertex;
    }

    private List<List<Integer>> getAllIncomingVertices(EulerCycleWarps warps, List<Integer> vertex) {
        List<List<Integer>> branchesFromVertex = new ArrayList<>();

        for (DefaultEdge edge : warps.incomingEdgesOf(vertex)) {
            branchesFromVertex.add(warps.getEdgeSource(edge));
        }

        return branchesFromVertex;
    }

}
