package org.example.math;


public class Vector2D {

    //public for now
    public double x;
    public double y;


    //contructor
    public  Vector2D(double x, double y){
        this.x=x;
        this.y=y;

    }


    //methods, other is just another vector , Vector A and Vector B
    public  Vector2D add (Vector2D other){
        return  new Vector2D(this.x+other.x,this.y+ other.y);

    }
    public  Vector2D subtract (Vector2D other){
        return  new Vector2D(this.x-other.x,this.y- other.y);

    }

    //multiply a vector by a scalar
    public  Vector2D multiply (double scalar){
        return  new Vector2D(this.x * scalar,this.y * scalar);

    }

    //returns the distance of the vector from (0,0)
    public  double length (){
        return Math.sqrt(x*x+y*y);

    }
    public Vector2D normalize() {
        double len = length();
        if (len == 0) {
            return new Vector2D(0, 0);
        }
        return new Vector2D(x / len, y / len);
    }


}
