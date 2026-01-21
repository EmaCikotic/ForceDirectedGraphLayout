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

    private double durationMs;



    private Timer timer;
    private int iterations = 0;
    private static final int MAX_ITER = 500;

    public GraphPanel(Graph graph, FruchtermanReingold layout, double durationMs ) {
        this.graph = graph;
        this.layout = layout;
        this.durationMs = durationMs;

        setBackground(Color.WHITE);
    }

    public void startAnimation() {
        timer = new Timer(20, ev -> {

            if (iterations >= MAX_ITER) {
                timer.stop();
                return;
            }

            layout.step();
            iterations++;
            repaint();
        });

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //display time also one the gui panel
        g2.setColor(Color.BLACK);
        g2.drawString(
                String.format("Execution time (500 iterations): %.2f ms",
                        durationMs),
                15, 20
        );

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

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

        int padding = 40;
        double scale = Math.min(
                (getWidth() - padding) / graphWidth,
                (getHeight() - padding) / graphHeight
        );

        double offsetX = (getWidth() - graphWidth * scale) / 2;
        double offsetY = (getHeight() - graphHeight * scale) / 2;

        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);
        g2.translate(-minX, -minY);

        g2.setColor(Color.LIGHT_GRAY);
        for (Edge e : graph.edges) {
            g2.drawLine(
                    (int) e.v.position.x,
                    (int) e.v.position.y,
                    (int) e.u.position.x,
                    (int) e.u.position.y
            );
        }

        g2.setColor(Color.BLUE);
        int r = 4;
        for (Vertex v : graph.vertices) {
            g2.fillOval(
                    (int) v.position.x - r,
                    (int) v.position.y - r,
                    6 * r,
                    6 * r
            );
        }
    }
}
