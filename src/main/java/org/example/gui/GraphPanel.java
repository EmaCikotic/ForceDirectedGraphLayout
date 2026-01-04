package org.example.gui;
import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.graph.Edge;
import org.example.layout.FruchtermanReingold;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel{

    private Graph graph;
    private FruchtermanReingold layout;
    private Timer timer;


    public GraphPanel(Graph graph, FruchtermanReingold layout) {
        this.graph = graph;
        this.layout = layout;
    }


    //https://www.tutorialspoint.com/how-can-we-implement-the-paintcomponent-method-of-a-jpanel-in-java

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            double zoom = 0.15; // try 0.05–0.2


            // draw edges
            g2.setColor(Color.LIGHT_GRAY);
            for (Edge e : graph.edges) {
                int x1 = (int) e.v.position.x;
                int y1 = (int) e.v.position.y;
                int x2 = (int) e.u.position.x;
                int y2 = (int) e.u.position.y;
                g2.drawLine(x1, y1, x2, y2);
            }


            // draw vertices
            g2.setColor(Color.BLUE);
            int r = 4;
            for (Vertex v : graph.vertices) {
                int x = (int) v.position.x;
                int y = (int) v.position.y;
                g2.fillOval(x - r, y - r, 2 * r, 2 * r);
            }
        }


    public void startAnimation() {

        //initial mess
        repaint();

       //wait before starting changing the graph
        Timer delayTimer = new Timer(2000, e -> {


            timer = new Timer(10, ev -> {
                layout.step();
                repaint();
            });

            timer.start();
        });

        delayTimer.setRepeats(false);
        delayTimer.start();
    }







}
