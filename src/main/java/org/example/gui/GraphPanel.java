package org.example.gui;

import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.layout.FruchtermanReingold;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {

    private final Graph graph;
    private final FruchtermanReingold layout;
    private Timer timer;

    /* big canvas so scrollbars appear
    private static final int CANVAS_WIDTH = 10_000;
    private static final int CANVAS_HEIGHT = 10_000;*/

    public GraphPanel(Graph graph, FruchtermanReingold layout) {
        this.graph = graph;
        this.layout = layout;
        setBackground(Color.WHITE);
    }


    //@Override
    /*public Dimension getPreferredSize() {
        return new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
    }*/

    public void startAnimation() {

        //initial delay so the ugly graph is seen at the start
        int delayMs = 2500;

        Timer delayTimer = new Timer(delayMs, e -> {

            // graph animation
            timer = new Timer(20, ev -> {
                layout.step();
                repaint();
            });

            timer.start();

            // stop the delay timer
            ((Timer) e.getSource()).stop();
        });

        delayTimer.setRepeats(false); // run once
        delayTimer.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        //  Compute bounding box of the graph, so it fits the screen
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (Vertex v : graph.vertices) {
            minX = Math.min(minX, v.position.x);
            maxX = Math.max(maxX, v.position.x);
            minY = Math.min(minY, v.position.y);
            maxY = Math.max(maxY, v.position.y);
        }

        double graphWidth = maxX - minX;
        double graphHeight = maxY - minY;

        if (graphWidth == 0 || graphHeight == 0) return;

        //  Scale to fit the panel
        int padding = 40;
        double scaleX = (getWidth() - padding) / graphWidth;
        double scaleY = (getHeight() - padding) / graphHeight;
        double scale = Math.min(scaleX, scaleY);

        //  Center graph
        double offsetX = (getWidth() - graphWidth * scale) / 2;
        double offsetY = (getHeight() - graphHeight * scale) / 2;

        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);
        g2.translate(-minX, -minY);

        // 4) Draw edges
        g2.setColor(Color.LIGHT_GRAY);
        for (Edge e : graph.edges) {
            g2.drawLine(
                    (int) e.v.position.x,
                    (int) e.v.position.y,
                    (int) e.u.position.x,
                    (int) e.u.position.y
            );
        }

        // 5) Draw vertices
        g2.setColor(Color.BLUE);
        int r = 4;
        for (Vertex v : graph.vertices) {
            g2.fillOval(
                    (int) v.position.x - r,
                    (int) v.position.y - r,
                    2 * r,
                    2 * r
            );
        }
    }


}
