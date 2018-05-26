package algorithm;

import Utils.NetworkExamples;
import algorithm.clustering.DividerToClusters;
import algorithm.fulkerson.BFS;
import algorithm.fulkerson.FordFulkerson;
import entities.UpdateType;
import experiments.EmpiricalExperiment;
import generator.RandomNetworkListGenerator;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DynamicNetworkWithMaxFlowAlgorithmTests {

    @Test
    public void perform_WithIntegrationParts() throws Exception {

        NetworkExamples examples = new NetworkExamples();
        RandomNetworkListGenerator generator = mock(RandomNetworkListGenerator.class);
        Random rng = mock(Random.class);
        when(generator.generate()).thenReturn(List.of(examples.getNetwork0()));
        when(rng.nextInt(7)).thenReturn(3);
        when(rng.nextInt(6)).thenReturn(0).thenReturn(4);
        when(rng.nextDouble()).thenReturn(28.0);

        FordFulkerson fulkerson = new FordFulkerson(new BFS());
        DynamicNetworkWithMaxFlowAlgorithm algorithm =  new DynamicNetworkWithMaxFlowAlgorithm(
                new DividerToClusters(), fulkerson
        );
        EmpiricalExperiment experiment = new EmpiricalExperiment(
                fulkerson, algorithm, generator, rng
        );

        experiment.perform();

        assertEquals(List.of(), experiment.getIncorrectNetwork());
        assertEquals(List.of(), experiment.getIncorrectAction());

        Integer expectedValue = 0;
        assertEquals(
            expectedValue, experiment.getAlgorithmUsedEdges().get(UpdateType.ADD_VERTEX).get(0)
        );
        expectedValue = 29;
        assertEquals(
                expectedValue, experiment.getFulkersonUsedEdges().get(UpdateType.ADD_VERTEX).get(0)
        );
        expectedValue = 15;
        assertEquals(
                expectedValue, experiment.getAlgorithmUsedEdges().get(UpdateType.REMOVE_VERTEX).get(0)
        );
        expectedValue = 11;
        assertEquals(
                expectedValue, experiment.getFulkersonUsedEdges().get(UpdateType.REMOVE_VERTEX).get(0)
        );
        expectedValue = 6;
        assertEquals(
                expectedValue, experiment.getAlgorithmUsedEdges().get(UpdateType.ADD_EDGE).get(0)
        );
        expectedValue = 18;
        assertEquals(
                expectedValue, experiment.getFulkersonUsedEdges().get(UpdateType.ADD_EDGE).get(0)
        );
        expectedValue = 11;
        assertEquals(
                expectedValue, experiment.getAlgorithmUsedEdges().get(UpdateType.REMOVE_EDGE).get(0)
        );
        expectedValue = 15;
        assertEquals(
                expectedValue, experiment.getFulkersonUsedEdges().get(UpdateType.REMOVE_EDGE).get(0)
        );
    }

}
