package experiments;

import algorithm.DynamicNetworkWithMaxFlowAlgorithm;
import algorithm.fulkerson.FordFulkerson;
import generator.RandomNetworkListGenerator;

public class EmpiricalExperiment {

    private FordFulkerson fulkerson;
    private DynamicNetworkWithMaxFlowAlgorithm algorithm;
    private RandomNetworkListGenerator generator;

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
        generator.generate().forEach(network -> {

        });
    }

}
