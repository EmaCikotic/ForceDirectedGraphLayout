package org.example.layout.parallel;

import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.math.Vector2D;
import java.util.concurrent.Callable;

public class RepulsiveForceTask implements Callable<Void>{

    private final Graph graph;
    private final int start;
    private final int end;
    private final double k;

    public RepulsiveForceTask( Graph graph, int start, int end, double k) {
        this.graph=graph;
        this.start=start;
        this.end=end;
        this.k=k;
    }

    @Override
    public Void call() {

        for (int j = start; j < end; j++) {

            Vertex v = graph.vertices.get(j);
            for (Vertex u : graph.vertices) {

                if (v != u) {
                    Vector2D delta = v.position.subtract(u.position);
                    double distance = delta.length();

                    if (distance > 0) {
                        double repulsiveForce = (k * k) / distance;
                        Vector2D force = delta.normalize().multiply(repulsiveForce);
                        v.displacement = v.displacement.add(force);
                    }
                }
            }
        }

        return null;
    }
}
