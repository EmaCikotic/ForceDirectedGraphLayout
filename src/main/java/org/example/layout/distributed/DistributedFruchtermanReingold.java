package org.example.layout.distributed;
import org.example.graph.Edge;
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

    private final boolean debug;

    private final int vertexCount;
    private final int chunkSize;

    private final int start;
    private final int end;

    private final int[] recvCounts;
    private final int[] displacements;
    private final double[] sendBuffer;
    private final double[] recvBuffer;
    private final double[] positionBuffer;
    private static final int ROOT =0;

    private double repulsiveForce(double distance) {
        return (k * k) / distance;
    }
    private double attractiveForce(double distance) {
        return (distance * distance) / k;
    }

    //the first one like a default
    //the second one i need boolean so i dont get chunks printed in the performance benchmark
    public DistributedFruchtermanReingold(
            Graph graph,
            double width,
            double height,
            double c
    ) {
        this(graph, width, height, c, true);
    }

    public DistributedFruchtermanReingold(Graph graph, double width, double height, double c, boolean debug) {
        this.graph = graph;
        double area = width * height;
        this.k = c * Math.sqrt(area / graph.vertices.size());
        this.temperature = Math.max(width, height) / 50;
        this.height = height;
        this.width = width;

        this.rank=MPI.COMM_WORLD.Rank(); //me
        this.processes=MPI.COMM_WORLD.Size(); //nodes
        this.debug=debug;

        this.vertexCount = graph.vertices.size();
        this.chunkSize = (int) Math.ceil((double) this.vertexCount / processes);

       //work partitioning, each process for their own
        this.start = rank * chunkSize;
        this.end = Math.min(start + chunkSize, vertexCount);

        //to see which vertices are assigned to each process
        if (debug) {
            System.out.println(
                    "Rank " + rank +
                            ": start=" + start +
                            ", end=" + end +
                            ", vertices=" + (end - start)
            );
        }


        //moved from step(): Calculated once since the vertex distribution does not change between iterations
        this.recvCounts = new int[processes];
        this.displacements = new int[processes];

        for (int r = 0; r < processes; r++) {
            int rankStart = r * chunkSize;
            int rankEnd = Math.min(rankStart + chunkSize, vertexCount);

            recvCounts[r] = (rankEnd - rankStart) * 2;
            displacements[r] = rankStart * 2;
        }

        this.sendBuffer = new double[(end - start) * 2];
        this.recvBuffer = new double[vertexCount * 2];
        this.positionBuffer = new double[vertexCount * 2];
     }

        @Override
        public void step() {

            //step 1
            for (int i = start; i < end; i++) {
                Vertex v = graph.vertices.get(i);
                v.displacement.x = 0;
                v.displacement.y = 0;
            }

            //step 2
            for (int i = start; i < end; i++) {
                Vertex v = graph.vertices.get(i);

                for (int j = 0; j < graph.vertices.size(); j++) {
                    Vertex u = graph.vertices.get(j);

                    if (v.index != u.index) {
                        Vector2D delta = v.position.subtract(u.position);
                        double distance = delta.length(); //how far apart are the vertices

                        //potential overflow safeguard
                        if (distance > 0) {
                            Vector2D force = delta.normalize().multiply(repulsiveForce(distance));
                            v.displacement = v.displacement.add(force);
                        }
                    }
                }
            }
            //edges
            for (Edge e : graph.edges) {

                Vertex v = e.v;
                Vertex u = e.u;
                Vector2D delta = v.position.subtract(u.position);
                double distance = delta.length();


                if (distance > 0) {
                    Vector2D force = delta.normalize().multiply(attractiveForce(distance));
                    // Only update vertices owned by this process
                    if (v.index >= start && v.index < end) {
                        v.displacement = v.displacement.subtract(force);
                    }
                    if (u.index >= start && u.index < end) {
                        u.displacement = u.displacement.add(force);
                    }
                }
            }

            for (int i = start; i < end; i++) {
                Vertex v = graph.vertices.get(i);
                int local = i - start;

                sendBuffer[2 * local] = v.displacement.x;
                sendBuffer[2 * local + 1] = v.displacement.y;
            }

            //send to the root
            //http://mpjexpress.org/docs/javadocs/mpi/Intracomm.html for gatehrv
            //Gatherv for different-sized chunks of displacement values from all processes to root
            MPI.COMM_WORLD.Gatherv(sendBuffer, 0, sendBuffer.length, MPI.DOUBLE,
                    recvBuffer, 0, recvCounts, displacements, MPI.DOUBLE, ROOT);

            //root has all the vertices now
            if (rank == ROOT) {
                for (int i = 0; i < vertexCount; i++) {
                    Vertex v = graph.vertices.get(i);

                    v.displacement.x = recvBuffer[2 * i];
                    v.displacement.y = recvBuffer[2 * i + 1];
                }

                //update vertex positions
                for (Vertex v : graph.vertices) {
                    double displLength = v.displacement.length();

                    if (displLength > 0) {
                        Vector2D move = v.displacement.normalize().multiply(Math.min(displLength, temperature));
                        v.position = v.position.add(move);
                        v.position.x = Math.min(Math.max(v.position.x, 0), width);
                        v.position.y = Math.min(Math.max(v.position.y, 0), height);
                    }
                }

                //pack updated positions into positionBuffer
                for (int i = 0; i < vertexCount; i++) {
                    Vertex v = graph.vertices.get(i);
                    positionBuffer[2 * i] = v.position.x;
                    positionBuffer[2 * i + 1] = v.position.y;
                }
            }

            //every process has received the updated position
            MPI.COMM_WORLD.Bcast(positionBuffer, 0, positionBuffer.length, MPI.DOUBLE, ROOT);
            for (int i = 0; i < vertexCount; i++) {
                Vertex v = graph.vertices.get(i);
                v.position.x = positionBuffer[2 * i];
                v.position.y = positionBuffer[2 * i + 1];
            }
            //the same cooling for every process
            temperature*=0.95;
        }
}

