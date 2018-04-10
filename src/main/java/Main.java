import gui.GUI;
import generator.RandomNetworkGenerator;

public class Main {

    public static void main(String[] args) {

        RandomNetworkGenerator rng = new RandomNetworkGenerator();

        try {
            new GUI(
                "beautiful graph",
                rng.generate(3, 6, 1, 1, 20)
            ).display(900, 900);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
