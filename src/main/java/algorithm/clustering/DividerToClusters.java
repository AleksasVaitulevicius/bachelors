package algorithm.clustering;

import entities.EulerCycleWarps;
import entities.dynamicnetwork.ClustersNetwork;
import entities.dynamicnetwork.DynamicNetwork;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;


public class DividerToClusters {

    public ClustersNetwork divideToClusters(DynamicNetwork network) {
        EulerCycleWarps warps = new EulerCycleWarps();
        warps.constructFrom(network);

        network.getSources().forEach(source -> warpEulerCycles(warps, source));
        ClustersNetwork clusters = new ClustersNetwork();
        network.getSources().forEach(source -> divideToClusters(clusters, warps, source, network));
        addRestOfVertices(clusters, warps, network);
//        wireClusters(clusters);

        return clusters;
    }

    private void addRestOfVertices(
        ClustersNetwork clusters, EulerCycleWarps warps, DynamicNetwork networkToCluster
    ) {
        if(warps.vertexSet().isEmpty()) {
            return;
        }
        DynamicNetwork network = new DynamicNetwork();
        warps.vertexSet().forEach(network::putVertices);
        addEdges(network, networkToCluster);
        clusters.addVertex(network);
    }

    private void addEdges(DynamicNetwork network, DynamicNetwork networkToCluster) {
        Set<Integer> vertices = new HashSet<>(network.vertexSet());
        vertices.forEach(vertex -> {
            if (networkToCluster.getSinks().contains(vertex)) {
                network.sink = true;
                network.addSink(vertex);
            }
            if (networkToCluster.getSources().contains(vertex)) {
                network.source = true;
                network.addSource(vertex);
            }
            networkToCluster.outgoingEdgesOf(vertex).forEach(edge -> {
                Integer target = networkToCluster.getEdgeTarget(edge);
                if(!network.containsVertex(target)) {
                    network.addVertex(target);
                    network.addSink(target);
                }
                network.addEdge(vertex, target, edge);
            });
            networkToCluster.incomingEdgesOf(vertex).forEach(edge -> {
                Integer source = networkToCluster.getEdgeSource(edge);
                if(!network.containsVertex(source)) {
                    network.addSource(vertex);
                }
                else {
                    network.addEdge(source, vertex, edge);
                }
            });
        });
    }

    private void divideToClusters(
        ClustersNetwork clusters, EulerCycleWarps warps, Integer source, DynamicNetwork networkToCluster
    ) {

        List<Integer> vertex = warps.vertexSet().stream()
            .filter(vertexIterator -> vertexIterator.contains(source))
            .findFirst().orElse(null);
        DynamicNetwork network = new DynamicNetwork();
        Stack<List<Integer>> branches = new Stack<>();
        Set<List<Integer>> verticesToRemove = new HashSet<>();
        Stack<List<Integer>> forNextCluster = new Stack<>();
        forNextCluster.push(vertex);


        while (!forNextCluster.isEmpty()) {

            vertex = forNextCluster.pop();
            branches.push(vertex);

            if(vertex == null) {
                continue;
            }

            if (vertex.size() > 1) {
                network.putVertices(vertex);
                addEdges(network, networkToCluster);
                clusters.addVertex(network);
                network = new DynamicNetwork();
            } else {
                network.putVertices(vertex);
            }

            while (!branches.isEmpty()) {
                vertex = branches.pop();
                for (List<Integer> vertexIterator : getAllOutgoingVertices(warps, vertex)) {
                    if (vertexIterator != null && vertexIterator.size() > 1) {
                        forNextCluster.push(vertexIterator);
                    } else {
                        network.putVertices(vertexIterator);
                        branches.push(vertexIterator);
                    }
                }
                verticesToRemove.add(vertex);
            }
            if(!network.vertexSet().isEmpty()) {
                if(vertex != null && vertex.contains(source)) {
                    network.addSource(source);
                }
                addEdges(network, networkToCluster);
                clusters.addVertex(network);
                network = new DynamicNetwork();
            }

        }

        warps.removeAllVertices(verticesToRemove);
    }

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
