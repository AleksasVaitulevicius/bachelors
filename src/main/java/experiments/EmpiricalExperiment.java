package experiments;

import algorithm.DynamicNetworkWithMaxFlowAlgorithm;
import algorithm.fulkerson.FordFulkerson;
import entities.UpdateType;
import entities.dynamicnetwork.DynamicNetwork;
import generator.RandomNetworkListGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpiricalExperiment {

    private FordFulkerson fulkerson;
    private DynamicNetworkWithMaxFlowAlgorithm algorithm;
    private RandomNetworkListGenerator generator;

    @Getter
    private List<DynamicNetwork> incorrectNetwork = new ArrayList<>();
    @Getter
    private List<UpdateType> incorrectAction = new ArrayList<>();
    @Getter
    private Map<UpdateType, List<Integer>> algorithmUsedEdges = new HashMap<>();

    public EmpiricalExperiment(
        FordFulkerson fulkerson,
        DynamicNetworkWithMaxFlowAlgorithm algorithm,
        RandomNetworkListGenerator generator
    ) {
        this.fulkerson = fulkerson;
        this.algorithm = algorithm;
        this.generator = generator;
    }

    public void perform() throws Exception {

        List<DynamicNetwork> networks = generator.generate();

        networks = List.of();

        networks.forEach(network -> {

        });
    }

}
