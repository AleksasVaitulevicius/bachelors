package entities.dynamicnetwork;

import entities.network.Network;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DynamicNetwork extends Network {
    @Getter @Setter
    private List<Integer> sources = new ArrayList<>();
    @Getter @Setter
    private List<Integer> sinks = new ArrayList<>();
}
