package org.example.layout;

import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.math.Vector2D;

import java.util.concurrent.Callable;
import org.example.graph.Graph;

public class VerticesTask implements Callable<Void> {
    private final Graph graph;
    private final int start;
    private final int end;
    private final double temperature;
    private final double width;
    private final double height;

    public VerticesTask(Graph graph, int start, int end, double temperature, double width, double height){
        this.graph=graph;
        this.start=start;
        this.end= end;
        this.temperature=temperature;
        this.width=width;
        this.height=height;
    }

    @Override
    public Void call(){

        for (int i = start; i < end; i++) {

            Vertex v = graph.vertices.get(i);
            double displLength = v.displacement.length();

            if (displLength > 0) {
                Vector2D move = v.displacement.normalize().multiply(Math.min(displLength, temperature));
                v.position = v.position.add(move);
                v.position.x = Math.min(Math.max(v.position.x, 0), width);
                v.position.y = Math.min(Math.max(v.position.y, 0), height);
            }
        }
        return null;
    }


}
