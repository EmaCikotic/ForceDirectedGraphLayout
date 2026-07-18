package org.example.layout.parallel;

import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.layout.LayoutAlgorithm;
import org.example.math.Vector2D;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class ParallelFruchtermanReingold implements LayoutAlgorithm {
    private Graph graph;
    private double k;
    double temperature;

    private double height;

    private double width;

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private static final int THREADS=CORES-1;
    private  final ExecutorService pool = Executors.newFixedThreadPool(THREADS);
    private final List<Future<?>> futures= new ArrayList<>();
    private final List<Future<Vector2D[]>> attractiveFutures= new ArrayList<>(); //for step 3


    //compute once
    private final int vertexCount;
    private final int chunkSize;

    private final boolean debug;


    //to avoid duplication
    private void waitForTasks(){
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    //constructor overloading
    //the first one like a default
    //the second one i need boolean so i dont get chunks printed in the performance benchmark
    public ParallelFruchtermanReingold(
            Graph graph,
            double width,
            double height,
            double c
    ) {
        this(graph, width, height, c, true);
    }
    public ParallelFruchtermanReingold(Graph graph, double width, double height, double c, boolean debug) {
        this.graph = graph;
        double area = width * height;
        this.k = c * Math.sqrt(area / graph.vertices.size());
        this.temperature = Math.max(width, height) / 50;
        this.height = height;
        this.width = width;

        this.vertexCount = graph.vertices.size();
        this.chunkSize = (int) Math.ceil((double) this.vertexCount / THREADS);
        this.debug=debug;

        if (debug) {
            for (int i = 0; i < THREADS; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, vertexCount);
                System.out.println(
                        "Chunk " + i + " -> vertices " + start + " to " + (end - 1)
                );
            }
        }
    }

    @Override public void step(){

        //step 1: reset displacement
        for (Vertex v : graph.vertices) {
            v.displacement.x = 0;
            v.displacement.y = 0;
        }

        // STEP 2: repulsive forces
        futures.clear();

        for (int i = 0; i < THREADS; i++) {

            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, vertexCount);

            Future<?> future = pool.submit(new RepulsiveForceTask(graph, start, end, k));
            futures.add(future);
        }
        //wait for all workers
        waitForTasks();

        //step 3: attractive forces, work done on edges
        attractiveFutures.clear();

        int edgeCount = graph.edges.size();
        int edgeChunkSize = (int) Math.ceil((double) edgeCount / THREADS);
        for (int i = 0; i < THREADS; i++) {

            int start = i * edgeChunkSize;
            int end = Math.min(start + edgeChunkSize, edgeCount);

            Future<Vector2D[]> future = pool.submit(new AttractiveForceTask(graph, start, end, k));
            attractiveFutures.add(future);
        }
        // collect and merge results
        for (Future<Vector2D[]> future : attractiveFutures) {

            try {
                Vector2D[] localDisplacement = future.get();
                for (int i = 0; i < vertexCount; i++) {
                    graph.vertices.get(i).displacement.addInPlace(localDisplacement[i]);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        //step 4: adjust displacement
        futures.clear();

        for (int i = 0; i < THREADS; i++) {

            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, vertexCount);
            Future<?> future = pool.submit(new VerticesTask(graph, start, end, temperature, width, height));
            futures.add(future);
        }
        waitForTasks();

        temperature*=0.95;
    }

}
