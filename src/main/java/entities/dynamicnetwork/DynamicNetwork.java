package entities.dynamicnetwork;

import entities.network.Network;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DynamicNetwork extends Network {
    @Getter
    private List<Integer> sources = new ArrayList<>();
    @Getter
    private List<Integer> sinks = new ArrayList<>();

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
        return sources.remove(sink);
    }

    @Override
    public String toString() {
        return super.toString() + "sources=" + sources + "sinks=" + sinks;
    }
}
