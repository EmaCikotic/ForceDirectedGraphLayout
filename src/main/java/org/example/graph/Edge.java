package org.example.graph;



public class Edge {

    //vertex connected to two edges: v and u
    public Vertex v;
    public Vertex u;

    public Edge(Vertex v, Vertex u){
        this.v=v;
        this.u=u;
    }
}
