package entities.dynamicnetwork;

import java.util.*;
import java.util.stream.Collectors;

public class ClustersNetwork {

    private Set<DynamicNetwork> vertices = new HashSet<>();

    public void addVertex(DynamicNetwork network) {
        if(!vertices.contains(network)) {
            vertices.add(network);
        }
    }

    public Set<DynamicNetwork> vertexSet() {
        return vertices;
    }

    public void removeVertex(DynamicNetwork network) {
        vertices = vertices.stream()
            .filter(cluster -> !cluster.equals(network))
            .collect(Collectors.toSet());
    }

    public void clearEmpty() {
        vertices = vertices.stream()
            .filter(cluster -> !cluster.vertexSet().isEmpty())
            .collect(Collectors.toSet());
    }

}
