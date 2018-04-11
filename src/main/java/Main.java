import entities.DynamicNetwork;
import entities.update.Update;
import generator.RandomUpdateGenerator;
import gui.GUI;
import generator.RandomNetworkGenerator;

public class Main {

    public static void main(String[] args) {
        DynamicNetwork network;
        Update update;
        RandomNetworkGenerator rng = new RandomNetworkGenerator();
        RandomUpdateGenerator rug = new RandomUpdateGenerator(10, 30);

        try {
            network = rng.generate(3, 6, 10, 30);
            update = rug.generateFor(network);
        }
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        new GUI("before update", network).display(900, 900);

        try {
            update.applyTo(network);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        new GUI("after update", network).display(900, 900);
    }
}
