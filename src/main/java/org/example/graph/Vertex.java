package org.example.graph;

import org.example.math.Vector2D;

public class Vertex {

    public Vector2D position;
    public Vector2D displacement;

    public Vertex( Vector2D position){
        this.position=position;
      // this.displacement = new Vector2D(0,0); //vertex has not been pushed yet
    }
}
