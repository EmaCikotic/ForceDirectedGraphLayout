package org.example.layout;

import org.example.graph.Edge;
import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.math.Vector2D;

public class FruchtermanReingold {


    private Graph graph;
    private double k;
    private double temperature;


    private double repulsiveForce(double distance) {
        return (k * k) / distance;
    }
    private double attractiveForce(double distance) {
        return (distance * distance) / k;
    }

    public FruchtermanReingold( Graph graph, double width, double height ){
        this.graph=graph;
        double area= width*height;
        this.k= Math.sqrt(area/graph.vertices.size());
        this.temperature=Math.max(width,height)/10;
    }

    public void step(){

        //step 1: reset displacement
        for (Vertex v : graph.vertices) v.displacement= new Vector2D (0 ,0);

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
                        3000
                );

                v.position.y = Math.min(
                        Math.max(v.position.y, 0),
                        3000
                );
            }
        }


        temperature*=0.95;
    }

}
