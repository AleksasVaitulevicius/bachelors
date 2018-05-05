package entities.dynamicnetwork;

import entities.network.Network;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicNetwork extends Network {

    public boolean source = false;
    public boolean sink = false;
    public boolean path = true;

    @Getter
    private List<Integer> sources = new ArrayList<>();
    @Getter
    private List<Integer> sinks = new ArrayList<>();
    @Getter
    private Map<Integer, Double> maxFlows = new HashMap<>();

    public void addMaxFlow(Integer key, Double value) {
        maxFlows.put(key, value);
    }

    public boolean addSource(Integer source) {
        return !sources.contains(source) && this.vertexSet().contains(source) && sources.add(source);
    }

    public boolean addSink(Integer sink) {
        return !sinks.contains(sink) && this.vertexSet().contains(sink) && sinks.add(sink);
    }

    public boolean removeSource(Integer source) {
        return sources.remove(source);
    }

    public boolean removeSink(Integer sink) {
        return sinks.remove(sink);
    }

    public void removeVertexSafely(Integer vertex) {
        this.removeVertex(vertex);
        if(this.getSources().contains(vertex)) {
            this.removeSource(vertex);
            this.removeVertex(-1 * vertex);
        }
        if(this.getSinks().contains(vertex)) {
            this.removeSink(vertex);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "sources=" + sources + "sinks=" + sinks;
    }
}
