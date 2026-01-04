package org.example.graph;
import org.example.math.Vector2D;
import java.util.ArrayList;
import java.util.Random;

public class Graph {

    public ArrayList<Vertex> vertices = new ArrayList<>();
    public ArrayList<Edge> edges = new ArrayList<>();

    public static Graph randomGraph(int V, int E, int width, int height) {
        Graph graph = new Graph();
        Random random = new Random(42); // fixed seed

        // create vertices
        for (int i = 0; i < V; i++) {
            double x = random.nextDouble() * width;
            double y = random.nextDouble() * height;

            Vertex v = new Vertex(new Vector2D(x, y));
            graph.vertices.add(v);
        }

        // create edges
        for (int i = 0; i < E; i++) {
            Vertex v = graph.vertices.get(random.nextInt(V));
            Vertex u = graph.vertices.get(random.nextInt(V));

            if (v != u) {
                graph.edges.add(new Edge(v, u));
            }
        }

        return graph;
    }
}
