package experiments;

import entities.UpdateType;
import entities.dynamicnetwork.DynamicNetwork;
import gui.GUI;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExperimentData {

    public void saveUsedEdges(
        Map<UpdateType, List<Integer>> algorithm, Map<UpdateType, List<Integer>> fulkerson
    ) {

        StringBuilder data = new StringBuilder("vertices_number,edges_number");
        data.append(",algorithm_").append(UpdateType.ADD_VERTEX);
        data.append(",algorithm_").append(UpdateType.ADD_EDGE);
        data.append(",algorithm_").append(UpdateType.REMOVE_VERTEX);
        data.append(",algorithm_").append(UpdateType.REMOVE_EDGE);
        data.append(",algorithm_").append(UpdateType.UPDATE_WEIGHT);
        data.append(",fulkerson_").append(UpdateType.ADD_VERTEX);
        data.append(",fulkerson_").append(UpdateType.ADD_EDGE);
        data.append(",fulkerson_").append(UpdateType.REMOVE_VERTEX);
        data.append(",fulkerson_").append(UpdateType.REMOVE_EDGE);
        data.append(",fulkerson_").append(UpdateType.UPDATE_WEIGHT);
        data.append("\n");
        for (int it = 0; it < algorithm.get(UpdateType.ADD_VERTEX).size(); it++) {
            int vertices = (it / 30 + 1) * 10;
            String edges = "average";
            if(it % 30 >= 10) {
                edges = "small";
            }
            if(it % 30 >= 20) {
                edges = "big";
            }
            data.append(vertices).append(",").append(edges).append(",");
            data.append(algorithm.get(UpdateType.ADD_VERTEX).get(it)).append(",");
            data.append(algorithm.get(UpdateType.ADD_EDGE).get(it)).append(",");
            data.append(algorithm.get(UpdateType.REMOVE_VERTEX).get(it)).append(",");
            data.append(algorithm.get(UpdateType.REMOVE_EDGE).get(it)).append(",");
            data.append(algorithm.get(UpdateType.UPDATE_WEIGHT).get(it)).append(",");
            data.append(fulkerson.get(UpdateType.ADD_VERTEX).get(it)).append(",");
            data.append(fulkerson.get(UpdateType.ADD_EDGE).get(it)).append(",");
            data.append(fulkerson.get(UpdateType.REMOVE_VERTEX).get(it)).append(",");
            data.append(fulkerson.get(UpdateType.REMOVE_EDGE).get(it)).append(",");
            data.append(fulkerson.get(UpdateType.UPDATE_WEIGHT).get(it)).append("\n");
        }
        try {
            FileWriter fileStream = new FileWriter(
                    "used edges/usedEdges.csv"
            );
            fileStream.write(data.toString());
            fileStream.close();
        }
        catch(IOException e){
            System.out.print("Error: " + e);
            System.exit(1);
        }
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

    public void clearNetworks() {
        File folder = new File("incorrect");
        File[] listOfFolders = folder.listFiles();

        for (File listOfFolder : Objects.requireNonNull(listOfFolders)) {
            File subFolder = new File("incorrect/" + listOfFolder.getName());
            File[] listOfFiles = subFolder.listFiles();
            for (File file : Objects.requireNonNull(listOfFiles)) {
                file.delete();
            }
        }
    }


    public void loadAndDisplayAll() {
        File folder = new File("incorrect");
        File[] listOfFolders = folder.listFiles();

        for (File listOfFolder : Objects.requireNonNull(listOfFolders)) {
            File subFolder = new File("incorrect/" + listOfFolder.getName());
            File[] listOfFiles = subFolder.listFiles();
            for (File listOfFile : Objects.requireNonNull(listOfFiles)) {
                loadAndDisplayNetwork(
                        "incorrect/" + listOfFolder.getName() + "/" + listOfFile.getName()
                );
            }
        }
    }

    public void loadAndDisplayNetwork(String filename) {
        try {
            FileInputStream fileStream = new FileInputStream(filename);
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            DynamicNetwork network = (DynamicNetwork) objectStream.readObject();

            System.out.println(filename);
            System.out.println(network);
            System.out.println("vertices: " + network.vertexSet().size());
            System.out.println("edges: " + network.edgeSet().size());
            System.out.println();

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
