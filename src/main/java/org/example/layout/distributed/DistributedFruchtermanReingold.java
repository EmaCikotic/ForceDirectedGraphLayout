package org.example.layout.distributed;

import org.example.graph.Graph;

import org.example.layout.LayoutAlgorithm;

public class DistributedFruchtermanReingold implements LayoutAlgorithm {
    private Graph graph;
    private double k;
    private  double temperature;
    private double height;
    private double width;

    public DistributedFruchtermanReingold(Graph graph, double width, double height, double c) {
        this.graph = graph;
        double area = width * height;
        this.k = c * Math.sqrt(area / graph.vertices.size());
        this.temperature = Math.max(width, height) / 50;
        this.height = height;
        this.width = width;
    }
    @Override
    public void step() {
        throw new UnsupportedOperationException(
                "Distributed implementation not yet available."
        );
    }
}
