package GUI;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;

import javax.swing.*;
import org.jgrapht.*;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;

public class GUI extends JFrame {

    private static final long serialVersionUID = -2707712944901661771L;

    public GUI(String name, Graph<Integer, DefaultWeightedEdge> network) {
        super(name);

        JGraphXAdapter<Integer, DefaultWeightedEdge> graphAdapter =
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