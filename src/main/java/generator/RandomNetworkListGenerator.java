package generator;

import entities.dynamicnetwork.DynamicNetwork;

import java.util.ArrayList;
import java.util.List;

public class RandomNetworkListGenerator {

    private RandomNetworkGenerator networkGenerator;

    public RandomNetworkListGenerator(RandomNetworkGenerator networkGenerator) {
        this.networkGenerator = networkGenerator;
    }

    public List<DynamicNetwork> generate() throws Exception {
        List<DynamicNetwork> result = new ArrayList<>();

        for(int verticesNumber = 10; verticesNumber <= 100; verticesNumber += 10) {
            for(int it = 0; it < 10; it++) {
                int averageEdgeNumber = (verticesNumber * verticesNumber - 1) / 2;
                DynamicNetwork network = networkGenerator.generate(
                        verticesNumber, averageEdgeNumber, 1, 25
                );
                network.addSource(1);
                network.addSink(verticesNumber);
                result.add(network);

                int minEdgeNumber = (averageEdgeNumber + verticesNumber - 1) / 2;
                network = networkGenerator.generate(
                        verticesNumber, minEdgeNumber, 1, 25
                );
                network.addSource(1);
                network.addSink(verticesNumber);
                result.add(network);

                int maxEdgeNumber = (averageEdgeNumber + verticesNumber * (verticesNumber - 1)) / 2;
                network = networkGenerator.generate(
                        verticesNumber, maxEdgeNumber, 1, 25
                );
                network.addSource(1);
                network.addSink(verticesNumber);
                result.add(network);
            }
        }
        return result;
    }

}
