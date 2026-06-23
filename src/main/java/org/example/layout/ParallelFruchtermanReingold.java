package org.example.layout;

import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.math.Vector2D;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelFruchtermanReingold {


    private Graph graph;
    private double k;
    private  double temperature;

    private double height;

    private double width;

    private final int numThreads = Runtime.getRuntime().availableProcessors();

    private final ExecutorService pool = Executors.newFixedThreadPool(numThreads);



    private double repulsiveForce(double distance) {
        return (k * k) / distance;
    }
    private double attractiveForce(double distance) {
        return (distance * distance) / k;
    }

    public ParallelFruchtermanReingold( Graph graph, double width, double height, double c ){
        this.graph=graph;
        double area= width*height;
        this.k= c*Math.sqrt(area/graph.vertices.size());
        this.temperature=Math.max(width,height)/50;  // 10 was too aggressive
        this.height=height;
        this.width=width;
    }

    public void step(){

        //step 1: reset displacement

        //chunk size
        int n = graph.vertices.size();
        int chunkSize = n / numThreads;

        List<Future> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {

            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? n : start + chunkSize;

            Future future = pool.submit(() -> {

                for (int j = start; j < end; j++) {
                    graph.vertices.get(j).displacement =
                            new Vector2D(0, 0);
                }

            });

            futures.add(future);
        }

// wait for all tasks to finish
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //step 2: repulsive forces
        for (Vertex v : graph.vertices){
            for (Vertex u: graph.vertices){
                if (v!= u){
                    Vector2D delta= v.position.subtract(u.position);
                    double distance=delta.length(); //how far apart are the vertices

                    //potential overflow safeguard
                    if (distance>0){
                        Vector2D force= delta.normalize().multiply(repulsiveForce(distance));
                        v.displacement=v.displacement.add(force);
                    }
                }
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
                Vector2D move =
                        v.displacement.normalize()
                                .multiply(Math.min(displLength, temperature));

                v.position = v.position.add(move);

                v.position.x = Math.min(
                        Math.max(v.position.x, 0),
                        width
                );

                v.position.y = Math.min(
                        Math.max(v.position.y, 0),
                        height
                );
            }
        }


        temperature*=0.95;
    }

}
