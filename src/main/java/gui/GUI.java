package gui;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;

import javax.swing.*;

import entities.EulerCycleWarps;
import entities.dynamicnetwork.ClustersNetwork;
import entities.dynamicnetwork.DynamicNetwork;
import entities.network.Network;
import entities.network.WeightedEdge;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class GUI extends JFrame {

    private static final long serialVersionUID = -2707712944901661771L;

    public GUI(String name, Network network) {
        super(name);

        JGraphXAdapter<Integer, WeightedEdge> graphAdapter =
                new JGraphXAdapter<>(network);

        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        this.add(new mxGraphComponent(graphAdapter));
        this.pack();
    }

    public GUI(String name, EulerCycleWarps network) {
        super(name);

        JGraphXAdapter<List<Integer>, DefaultEdge> graphAdapter =
                new JGraphXAdapter<>(network);

        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        this.add(new mxGraphComponent(graphAdapter));
        this.pack();
    }

    public GUI(String name, ClustersNetwork network) {
        super(name);

        JGraphXAdapter<DynamicNetwork, WeightedEdge> graphAdapter =
                new JGraphXAdapter<>(network);

        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        this.add(new mxGraphComponent(graphAdapter));
        this.pack();
    }

    public void display(int width, int height) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(width, height);
        this.setVisible(true);
    }
}