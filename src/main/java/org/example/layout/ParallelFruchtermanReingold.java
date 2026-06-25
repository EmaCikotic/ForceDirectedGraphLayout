package org.example.layout;

import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.math.Vector2D;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class ParallelFruchtermanReingold implements LayoutAlgorithm{


    private Graph graph;
    private double k;
    private  double temperature;

    private double height;

    private double width;

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private static final int THREADS=CORES-1;

    private  final ExecutorService pool = Executors.newFixedThreadPool(THREADS);

    //compute once
    private final int vertexCount;
    private final int chunkSize;
    private double repulsiveForce(double distance) {
        return (k * k) / distance;
    }
    private double attractiveForce(double distance) {
        return (distance * distance) / k;
    }

    public ParallelFruchtermanReingold(Graph graph, double width, double height, double c) {
        this.graph = graph;
        double area = width * height;
        this.k = c * Math.sqrt(area / graph.vertices.size());
        this.temperature = Math.max(width, height) / 50;
        this.height = height;
        this.width = width;

        this.vertexCount = graph.vertices.size();
        this.chunkSize = (int) Math.ceil((double) this.vertexCount / THREADS);

        for (int i = 0; i < THREADS; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, vertexCount);

            System.out.println(
                    "Chunk " + i + " -> vertices " + start + " to " + (end - 1)
            );
        }
    }

    public void step(){

        //step 1: reset displacement
        for (Vertex v : graph.vertices) v.displacement= new Vector2D (0 ,0);


        // STEP 2: repulsive forces
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {

            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, vertexCount);

            Future<?> future = pool.submit(() -> {

                for (int j = start; j < end; j++) {

                    Vertex v = graph.vertices.get(j);
                    for (Vertex u : graph.vertices) {

                        if (v != u) {
                            Vector2D delta = v.position.subtract(u.position);

                            double distance = delta.length();

                            if (distance > 0) {
                                Vector2D force = delta.normalize().multiply(repulsiveForce(distance));
                                v.displacement = v.displacement.add(force);
                            }
                        }
                    }
                }
            });

            futures.add(future);
        }

        //wait for all workers
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        //step 3: attractive forces
        for (Edge e: graph.edges){
            Vertex v = e.v;
            Vertex u = e.u;
            Vector2D delta=v.position.subtract(u.position);
            double distance= delta.length();

            if (distance>0){
                Vector2D force=delta.normalize().multiply(attractiveForce(distance));
                v.displacement = v.displacement.subtract(force);
                u.displacement = u.displacement.add(force);
            }
        }

        //step 4: adjust displacement

        for (Vertex v : graph.vertices) {
            double displLength = v.displacement.length();

            if (displLength > 0) {
                Vector2D move = v.displacement.normalize().multiply(Math.min(displLength, temperature));

                v.position = v.position.add(move);

                v.position.x = Math.min(Math.max(v.position.x, 0), width
                );

                v.position.y = Math.min(Math.max(v.position.y, 0), height
                );
            }
        }


        temperature*=0.95;
    }

}
