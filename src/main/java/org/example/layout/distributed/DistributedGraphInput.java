package org.example.layout.distributed;
import org.example.gui.DoubleFilter;
import org.example.gui.IntegerFilter;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;

public class DistributedGraphInput {

    private JFrame frame;
    private JTextField heightText;
    private JTextField widthText;
    private JTextField cText;
    private JTextField verticesText;
    private JTextField edgesText;
    private JTextField seedText;

    private int width;
    private int height;
    private double c;
    private long seed;
    private int V;
    private int E;

    private boolean generated = false;

    public DistributedGraphInput(){


        frame = new JFrame("Distributed Force Directed Graph Layout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel heightLabel = new JLabel("Height:");
        JLabel widthLabel = new JLabel("Width:");
        JLabel cLabel = new JLabel("C:");
        JLabel verticesLabel = new JLabel("Number of vertices:");
        JLabel edgesLabel = new JLabel("Number of edges:");
        JLabel seedLabel = new JLabel("Seed:");

        heightText = new JTextField( "3000", 10);
        widthText = new JTextField("3000", 10);
        cText = new JTextField("1.0", 10);
        seedText = new JTextField("42", 10);
        verticesText = new JTextField("1000", 10);
        edgesText = new JTextField("1000", 10);

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
        ((AbstractDocument) cText.getDocument())
                .setDocumentFilter(new DoubleFilter());


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
         width = Integer.parseInt(widthText.getText());
        height = Integer.parseInt(heightText.getText());
         c = Double.parseDouble(cText.getText());
         seed = Long.parseLong(seedText.getText());
         V = Integer.parseInt(verticesText.getText());
         E = Integer.parseInt(edgesText.getText());






        generated = true;
        frame.dispose();

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getC() {
        return c;
    }

    public long getSeed() {
        return seed;
    }

    public int getVertices() {
        return V;
    }

    public int getEdges() {
        return E;
    }

    public boolean isGenerated() {
        return generated;
    }



}
