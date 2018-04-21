package fulkerson;

import entities.Network;

import java.util.*;

public class BFS {

    public Map<Integer, Integer> bfs(Network network, List<Integer> sources, List<Integer> sinks) {
        List<Integer> visitedVertices = new ArrayList<>();
        LinkedList<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> flow = new HashMap<>();

        sources.forEach(source -> {
            visitedVertices.add(source);
            queue.add(source);
        });

        while(!queue.isEmpty()) {

            Integer from = queue.poll();

            if(sinks.contains(from)) {
                continue;
            }

            network.outgoingEdgesOf(from).forEach(edge -> {
                Integer to = network.getEdgeTarget(edge);
                if (!visitedVertices.contains(to)) {
                    flow.put(to, from);
                    queue.add(to);
                    visitedVertices.add(to);
                }
            });
        }

        return flow;
    }
}
