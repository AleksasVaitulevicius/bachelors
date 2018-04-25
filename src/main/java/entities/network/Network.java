package entities.network;

import entities.UpdateType;
import lombok.Getter;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Network extends SimpleDirectedWeightedGraph<Integer, WeightedEdge> {

    public Network() {
        super(WeightedEdge.class);
    }

    /**
     *
     * @param vertices - vertices to add
     * @return how many vertices were added
     */
    public int putVertices(List<Integer> vertices) {

        int added = 0;

        for (Integer vertex : vertices) {
            added = this.addVertex(vertex)? added + 1: added;
        }

        return added;
    }

    public List<UpdateType> possibleUpdates() {

        List<UpdateType> result = new ArrayList<>(List.of(UpdateType.ADD_VERTEX, UpdateType.UPDATE_WEIGHT));

        int verticesCount = this.vertexSet().size();

        if(this.edgeSet().size() < verticesCount * (verticesCount - 1)) {
            result.add(UpdateType.ADD_EDGE);
        }

        if(!this.vertexSet().isEmpty()) {
            result.add(UpdateType.REMOVE_VERTEX);
        }

        if(!this.edgeSet().isEmpty()) {
            result.add(UpdateType.REMOVE_EDGE);
        }

        return result;
    }

}
