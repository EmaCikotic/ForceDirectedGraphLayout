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

    // big canvas so scrollbars appear
    private static final int CANVAS_WIDTH = 10_000;
    private static final int CANVAS_HEIGHT = 10_000;

    public GraphPanel(Graph graph, FruchtermanReingold layout) {
        this.graph = graph;
        this.layout = layout;
        setBackground(Color.WHITE);
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    public void startAnimation() {
        timer = new Timer(20, e -> {
            layout.step();
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Vertex v : graph.vertices) {
            minX = Math.min(minX, v.position.x);
            minY = Math.min(minY, v.position.y);
        }

        //so the border doesn't touch the screen
        int padding = 20;
        g2.translate(-minX + padding, -minY + padding);

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

}
