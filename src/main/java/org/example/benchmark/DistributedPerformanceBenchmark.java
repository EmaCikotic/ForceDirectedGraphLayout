package org.example.benchmark;
import mpi.MPI;
import org.example.graph.Graph;
import org.example.layout.distributed.DistributedFruchtermanReingold;

import java.io.FileNotFoundException;
import java.io.PrintWriter; //for CSV https://www.geeksforgeeks.org/java/java-io-printwriter-class-java-set-1/
import java.util.Locale; //so decimal numbers have a dot and not a comma

public class DistributedPerformanceBenchmark {

    private static final int[] VERTEX_SIZES = {500, 1000, 2000, 3000};
    private static final int[] EDGE_SIZES = {500, 1000, 2000, 3000};

    private static final int ITERATIONS = 500;
    private static final int WARMUP_ITERATIONS = 50;

    private static final int RUNS = 5;

    private static final int EDGES = 1000;
    private static final int FIXED_VERTICES = 1000;

    private static final int WIDTH = 3000;
    private static final int HEIGHT = 3000;

    private static final long SEED = 42;
    private static final double C = 1.0;

    private static void warmup(DistributedFruchtermanReingold algorithm) {

        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            algorithm.step();
        }
    }

    private static double benchmark(
            DistributedFruchtermanReingold algorithm,
            int rank
    ) {

        // Make sure all processes are ready before timing
        MPI.COMM_WORLD.Barrier();

        long start = System.nanoTime();

        for (int i = 0; i < ITERATIONS; i++) {
            algorithm.step();
        }

        // Make sure all processes have finished
        MPI.COMM_WORLD.Barrier();

        long end = System.nanoTime();

        double durationMs = (end - start) / 1_000_000.0;
        double averageIterationsMS = durationMs / ITERATIONS;

        if (rank == 0) {
            System.out.println("----------------------------------");
            System.out.println("                                                       ");
            System.out.println("DISTRIBUTED mode");
            System.out.println("Execution time: " + durationMs + " ms");
            System.out.println("Average iteration time: " + averageIterationsMS + " ms");
            System.out.println("                                                       ");
        }

        return durationMs;
    }

    public static void main(String[] args) {

            MPI.Init(args);
            int rank = MPI.COMM_WORLD.Rank();
            int processes = MPI.COMM_WORLD.Size();
            PrintWriter writer = null;

        try {

            if (rank == 0){
                writer = new PrintWriter("distributed_benchmark_" + processes + "_processes.csv"); //so different number of processes get saved to different files
                writer.println(
                        "experiment," +
                                "vertices," +
                                "edges," +
                                "processes," +
                                "avg_distributed_ms," +
                                "avg_iteration_distributed_ms"
                );
            }


            Graph warmupGraph = Graph.randomGraph(1000, 1000, WIDTH, HEIGHT, SEED);

            DistributedFruchtermanReingold warmupLayout = new DistributedFruchtermanReingold(warmupGraph, WIDTH, HEIGHT, C, false);
            warmup(warmupLayout);

            if (rank == 0) {
                System.out.println("Warm-up complete.");

            }
            // TEST 1: VERTEX TEST
            for (int vertex : VERTEX_SIZES) {

                if (rank == 0) {
                    System.out.println("\nTesting " + vertex + " vertices");
                    System.out.println("----------------------------------");
                }

                double totalDistributedTime = 0;
                for (int run = 1; run <= RUNS; run++) {

                    if (rank == 0) {
                        System.out.println("\nRun " + run + " of " + RUNS);
                    }

                    Graph graph = Graph.randomGraph(vertex, EDGES, WIDTH, HEIGHT, SEED);

                    DistributedFruchtermanReingold distributed = new DistributedFruchtermanReingold(graph, WIDTH, HEIGHT, C, false);

                    totalDistributedTime += benchmark(distributed, rank);

                }
                double avgDistributedTime = totalDistributedTime / RUNS;
                double avgDistributedIteration = avgDistributedTime / ITERATIONS;

                if (rank == 0) {
                    System.out.println("\n===== RESULTS FOR " + vertex + " VERTICES =====");
                    System.out.println("Average Distributed: " + avgDistributedTime + " ms");
                    System.out.println("Average iteration: " + avgDistributedIteration + " ms");

                    writer.printf(
                            Locale.US,
                            "VERTEX,%d,%d,%d,%.3f,%.3f%n",
                            vertex,
                            EDGES,
                            processes,
                            avgDistributedTime,
                            avgDistributedIteration
                    );
                }

            }
            // TEST 2: EDGE TEST
            for (int edge : EDGE_SIZES) {

                if (rank == 0) {
                    System.out.println("                                  ");
                    System.out.println("\nTesting " + edge + " edges");
                    System.out.println("----------------------------------");
                }

                double totalDistributedTime = 0;
                for (int run = 1; run <= RUNS; run++) {

                    if (rank == 0) {
                        System.out.println("\nRun " + run + " of " + RUNS);
                    }

                    Graph graph = Graph.randomGraph(FIXED_VERTICES, edge, WIDTH, HEIGHT, SEED);

                    DistributedFruchtermanReingold distributed = new DistributedFruchtermanReingold(graph, WIDTH, HEIGHT, C, false);

                    totalDistributedTime += benchmark(distributed, rank);
                }
                double avgDistributedTime = totalDistributedTime / RUNS;
                double avgDistributedIteration = avgDistributedTime / ITERATIONS;

                if (rank == 0) {
                    System.out.println("\n===== RESULTS FOR " + edge + " EDGES =====");
                    System.out.println("                                             ");
                    System.out.println("Average Distributed: " + avgDistributedTime + " ms");
                    System.out.println("Average iteration: " + avgDistributedIteration + " ms");

                    writer.printf(
                            Locale.US,
                            "EDGE,%d,%d,%d,%.3f,%.3f%n",
                            FIXED_VERTICES,
                            edge,
                            processes,
                            avgDistributedTime,
                            avgDistributedIteration
                    );
                }
            }
            if (rank == 0) {
                writer.close();
            }

            MPI.Finalize();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
