package org.example.gui;

import org.example.graph.Graph;
import org.example.layout.FruchtermanReingold;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;

public class GraphInput {

    private JFrame frame;
    private JTextField verticesText;
    private JTextField edgesText;

    public GraphInput() {

        frame = new JFrame("Force Directed Graph Layout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel verticesLabel = new JLabel("Number of vertices:");
        JLabel edgesLabel = new JLabel("Number of edges:");

        verticesText = new JTextField(10);
        edgesText = new JTextField(10);

        ((AbstractDocument) verticesText.getDocument())
                .setDocumentFilter(new IntegerFilter());
        ((AbstractDocument) edgesText.getDocument())
                .setDocumentFilter(new IntegerFilter());

        JButton button = new JButton("Generate Graph");

        panel.add(verticesLabel);
        panel.add(verticesText);
        panel.add(edgesLabel);
        panel.add(edgesText);
        panel.add(button);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        button.addActionListener(e -> generateGraph());
    }

    private void generateGraph() {
        if (verticesText.getText().isEmpty() || edgesText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter both values",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int V = Integer.parseInt(verticesText.getText());
        int E = Integer.parseInt(edgesText.getText());

        Graph graph = Graph.randomGraph(V, E, 3000, 3039);
        FruchtermanReingold layout =
                new FruchtermanReingold(graph, 3000, 3000);

        GraphPanel graphPanel = new GraphPanel(graph, layout);

        JScrollPane scrollPane = new JScrollPane(graphPanel);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //remove the input gui, and show the graph transformation
        frame.getContentPane().removeAll();
        scrollPane.setSize(3000,3000);
        frame.add(scrollPane);

        frame.revalidate();
        frame.repaint();

        graphPanel.startAnimation();
    }
}
