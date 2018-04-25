package entities.dynamicnetwork;

import entities.network.Network;

import java.util.ArrayList;
import java.util.List;

public class DynamicNetwork extends Network {
    public List<Integer> sources = new ArrayList<>();
    public List<Integer> sinks = new ArrayList<>();
}
