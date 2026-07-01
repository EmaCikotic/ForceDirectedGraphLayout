package org.example.layout.distributed;
import org.example.graph.Graph;
import org.example.graph.Vertex;
import org.example.layout.LayoutAlgorithm;
import org.example.math.Vector2D;
import mpi.MPI;


public class DistributedFruchtermanReingold implements LayoutAlgorithm {
    private final Graph graph;
    private final double k;
    private  double temperature; //not final it changes every iteration
    private final double height;
    private final double width;

    private final int rank;
    private final int processes;

    private final int vertexCount;
    private final int chunkSize;

    private final int start;

    private final int end;

    private static final int ROOT =0;

    private double repulsiveForce(double distance) {
        return (k * k) / distance;
    }

    public DistributedFruchtermanReingold(Graph graph, double width, double height, double c) {
        this.graph = graph;
        double area = width * height;
        this.k = c * Math.sqrt(area / graph.vertices.size());
        this.temperature = Math.max(width, height) / 50;
        this.height = height;
        this.width = width;

        this.rank=MPI.COMM_WORLD.Rank(); //me
        this.processes=MPI.COMM_WORLD.Size(); //nodes

        this.vertexCount = graph.vertices.size();
        this.chunkSize = (int) Math.ceil((double) this.vertexCount / processes);

       //work partitioning, each process for their own
        this.start = rank * chunkSize;
        this.end = Math.min(start + chunkSize, vertexCount);

        //pls work
        System.out.println("Process: " + rank+ " vertices: start "+ start+ " end "+ (end-1));
    }

    @Override
    public void step() {

        //step 1
        for (int i = start; i < end; i++) {
            Vertex v= graph.vertices.get(i);
            v.displacement.x=0;
            v.displacement.y=0;
        }

        //step 2
        for (int i = start; i < end ; i++) {
            Vertex v = graph.vertices.get(i);

            for(int j = 0; j < graph.vertices.size(); j++){
                Vertex u = graph.vertices.get(j);

                if (v.index!= u.index){
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

        double [] sendbBuffer = new double[(end - start) *2]; //every process sends its own displacement values
        double [] recvBuffer = null;

        //only root receives everyone's data
        if (rank == ROOT){
            recvBuffer = new double[vertexCount * 2];
        }

        for (int i = start; i < end ; i++) {
            Vertex v = graph.vertices.get(i);
            int local= i-start;

           sendbBuffer[2*local]=v.displacement.x;
           sendbBuffer[2*local+1]= v.displacement.y;
        }

        //send to the root
        MPI.COMM_WORLD.Gather(sendbBuffer,0,sendbBuffer.length,MPI.DOUBLE,recvBuffer,0,sendbBuffer.length, MPI.DOUBLE, ROOT);


        //root has all the vertices now
        if (rank == ROOT) {
            for (int i = 0; i < vertexCount; i++) {
                Vertex v = graph.vertices.get(i);

                v.displacement.x = recvBuffer[2*i];
                v.displacement.y = recvBuffer[2*i+1];
            }
        }

    }
}
