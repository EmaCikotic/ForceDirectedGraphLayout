package org.example.gui;

import org.example.graph.Graph;
import org.example.layout.FruchtermanReingold;
import org.example.layout.Mode;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;


public class GraphInput {

    private JFrame frame;

    private JTextField heightText;

    private JTextField widthText;

    private JTextField cText;
    private JTextField verticesText;
    private JTextField edgesText;

    private JTextField seedText;

    private JTextField runMode;

    final JComboBox<Mode> modeDropdown;

    private static final int MAX_ITER = 500;


    public GraphInput() {

        frame = new JFrame("Force Directed Graph Layout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel heightLabel = new JLabel("Height:");
        JLabel widthLabel = new JLabel("Width:");
        JLabel cLabel = new JLabel("C:");
        JLabel verticesLabel = new JLabel("Number of vertices:");
        JLabel edgesLabel = new JLabel("Number of edges:");
        JLabel seedLabel = new JLabel("Seed:");
        JLabel modeLabel=new JLabel("Execution mode:");

        heightText = new JTextField( "3000", 10);
        widthText = new JTextField("3000", 10);
        cText = new JTextField("1.0", 10);
        seedText = new JTextField("42", 10);
        verticesText = new JTextField("1000", 10);
        edgesText = new JTextField("1000", 10);

        //dropdown for the mode choice
        modeDropdown = new JComboBox<>(Mode.values());



        //letters not allowed
        ((AbstractDocument) heightText.getDocument())
                .setDocumentFilter(new IntegerFilter());
        ((AbstractDocument) widthText.getDocument())
                .setDocumentFilter(new IntegerFilter());
        ((AbstractDocument) verticesText.getDocument())
                .setDocumentFilter(new IntegerFilter());
        ((AbstractDocument) edgesText.getDocument())
                .setDocumentFilter(new IntegerFilter());
        ((AbstractDocument) seedText.getDocument())
                .setDocumentFilter(new IntegerFilter());

        JButton button = new JButton("Generate Graph");

        panel.add(heightLabel);
        panel.add(heightText);

        panel.add(widthLabel);
        panel.add(widthText);

        panel.add(cLabel);
        panel.add(cText);

        panel.add(seedLabel);
        panel.add(seedText);


        panel.add(verticesLabel);
        panel.add(verticesText);

        panel.add(edgesLabel);
        panel.add(edgesText);

        panel.add(modeLabel);
        panel.add(modeDropdown);

        panel.add(button);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        button.addActionListener(e -> generateGraph());
    }

    private void generateGraph() {
        if ( heightText.getText().isEmpty() || widthText.getText().isEmpty() || verticesText.getText().isEmpty() ||
                edgesText.getText().isEmpty() || cText.getText().isEmpty() || seedText.getText().isEmpty()){
            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter all the values",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        //reading values from text fields
        int width = Integer.parseInt(widthText.getText());
        int height = Integer.parseInt(heightText.getText());
        double c = Double.parseDouble(cText.getText());
        long seed = Long.parseLong(seedText.getText());
        int V = Integer.parseInt(verticesText.getText());
        int E = Integer.parseInt(edgesText.getText());
        Mode mode= (Mode) modeDropdown.getSelectedItem();

        //this for now before we actually call different execution modes
        switch (mode){
            case SEQUENTIAL -> System.out.println("Sequential execution");
            case PARALLEL-> System.out.println("Parallel execution");
            case DISTRIBUTED -> System.out.println("Distributed execution");
        }


        //graph for animation
        Graph graphAnim = Graph.randomGraph(V, E, width, height, seed);
        FruchtermanReingold layoutAnim =
                new FruchtermanReingold(graphAnim, width, height, c);

        //graph for time measurement
        Graph graphTime = Graph.randomGraph(V, E, width, height, seed);
        FruchtermanReingold layoutTime =
                new FruchtermanReingold(graphTime, width, height, c);


        //measure time (no gui)
        long start = System.nanoTime();

        for (int i = 0; i < MAX_ITER; i++) {
            layoutTime.step();
        }

        long end = System.nanoTime();

        double durationMs = (end - start) / 1_000_000.0;
        double averageIterationsMS = durationMs/MAX_ITER;

        System.out.println(
                "Execution time (" + MAX_ITER + " iterations): "
                        + durationMs + " ms."
        );
        System.out.println(
                "Average iteration time: "
                        + averageIterationsMS + " ms"
        );

        //animation
        GraphPanel graphPanel = new GraphPanel(graphAnim, layoutAnim, durationMs, averageIterationsMS);
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
