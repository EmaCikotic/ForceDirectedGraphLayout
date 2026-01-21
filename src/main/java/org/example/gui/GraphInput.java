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

    private int iterations = 0;
    private static final int MAX_ITER = 500;


    public GraphInput() {

        frame = new JFrame("Force Directed Graph Layout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 5, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

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


        //graph for animation
        Graph graphAnim = Graph.randomGraph(V, E, 3000, 3000);
        FruchtermanReingold layoutAnim =
                new FruchtermanReingold(graphAnim, 3000, 3000);

        //graph for time measurement
        Graph graphTime = Graph.randomGraph(V, E, 3000, 3000);
        FruchtermanReingold layoutTime =
                new FruchtermanReingold(graphTime, 3000, 3000);


        //measure time (no gui)
        long start = System.nanoTime();

        for (int i = 0; i < MAX_ITER; i++) {
            layoutTime.step();
        }

        long end = System.nanoTime();

        double durationMs = (end - start) / 1_000_000.0;

        System.out.println(
                "Execution time (" + MAX_ITER + " iterations): "
                        + durationMs + " ms"
        );

        //animation
        GraphPanel graphPanel = new GraphPanel(graphAnim, layoutAnim, durationMs);
        graphPanel.setSize(100,100);
        graphPanel.setBackground(Color.GRAY);

        /*JScrollPane scrollPane = new JScrollPane(graphPanel);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);*/

        //remove the input gui, and show the graph transformation
        frame.getContentPane().removeAll();
        frame.add(graphPanel, BorderLayout.CENTER);
        frame.setSize(900, 700);
        frame.revalidate();
        frame.repaint();

        graphPanel.startAnimation();
    }
}
