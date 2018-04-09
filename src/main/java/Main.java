import GUI.GUI;
import Generator.RandomNetworkGenerator;

public class Main {

    public static void main(String[] args) {

        RandomNetworkGenerator rng = new RandomNetworkGenerator();

        try {
            new GUI("beautiful graph", rng.generate(10, 10, 2, 5))
                .display(900, 900);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
