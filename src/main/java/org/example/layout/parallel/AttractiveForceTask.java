package org.example.layout.parallel;

import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.math.Vector2D;

import java.util.concurrent.Callable;

public class AttractiveForceTask implements Callable<Vector2D[]> {

    private final Graph graph;
    private final int start;
    private final int end;
    private final double k;

    private final Vector2D[] localDisplacement;

    public AttractiveForceTask(Graph graph, int start, int end, double k) {
        this.graph = graph;
        this.start = start;
        this.end = end;
        this.k = k;
        this.localDisplacement= new Vector2D[graph.vertices.size()]; // not in the parameter becasue every task creates its own arary

        for (int i = 0; i < graph.vertices.size(); i++) {
            this.localDisplacement[i] = new Vector2D(0, 0);
        }
    }


    private double attractiveForce(double distance) {
        return (distance * distance) / k;
    }

    @Override
    public Vector2D[] call() {

        //reset every vector
        for (int i = 0; i < graph.vertices.size(); i++) {
            localDisplacement[i].x = 0;
            localDisplacement[i].y = 0;
        }


        for (int i = start; i < end; i++) {
            Edge e = graph.edges.get(i);
            Vertex v = e.v;
            Vertex u = e.u;

            Vector2D delta = v.position.subtract(u.position);
            double distance = delta.length();

            if (distance > 0) {
                Vector2D force = delta.normalize().multiply(attractiveForce(distance));
                localDisplacement[v.index].subtractInPlace(force);
                localDisplacement[u.index].addInPlace(force);
            }
        }

        return localDisplacement;
    }
}