package experiments;

import entities.UpdateType;
import entities.dynamicnetwork.DynamicNetwork;
import gui.GUI;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ExperimentData {

    public void saveUsedEdges(Map<UpdateType, List<Integer>> usedEdges, String file) {

        usedEdges.forEach((action, actionUsedEdges) -> {
            StringBuilder data = new StringBuilder("vertices_number,edges_number,used_edges_number\n");
            for (int it = 0; it < actionUsedEdges.size(); it++) {
                int usedEdgesNumber = actionUsedEdges.get(it);
                int vertices = (it / 30 + 1) * 10;
                String edges = "average";
                if(it % 30 >= 10) {
                    edges = "small";
                }
                if(it % 30 >= 20) {
                    edges = "big";
                }
                data
                    .append(vertices).append(",")
                    .append(edges).append(",")
                    .append(usedEdgesNumber).append("\n");
            }
            try {
                FileWriter fileStream = new FileWriter(
                    "used edges/" + file + "_" + action + ".csv"
                );
                fileStream.write(data.toString());
                fileStream.close();
            }
            catch(IOException e){
                System.out.print("Error: " + e);
                System.exit(1);
            }
        });
    }

    public void saveNetworks(List<DynamicNetwork> networks, List<UpdateType> actions) {

        try {
            for (int it = 0; it < networks.size(); it++) {
                DynamicNetwork network = networks.get(it);
                FileOutputStream fileStream = new FileOutputStream(
                    "incorrect/" + actions.get(it)+ "/network" + it + ".ser"
                );
                ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
                objectStream.writeObject(network);
                fileStream.close();
                objectStream.close();
            }
        } catch (IOException e) {
            System.out.print("Error: " + e);
            System.exit(1);
        }
    }

    public void loadAndDisplayNetwork(String filename) {
        try {
            FileInputStream fileStream = new FileInputStream(filename);
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            DynamicNetwork network = (DynamicNetwork) objectStream.readObject();
            new GUI(filename, network).display(900, 900);
            fileStream.close();
            objectStream.close();
        } catch (IOException e) {
            System.out.print("Error: " + e);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
