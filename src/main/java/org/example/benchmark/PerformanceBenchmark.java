package org.example.benchmark;
import org.example.graph.Graph;
import org.example.layout.LayoutAlgorithm;
import org.example.layout.Mode;
import org.example.layout.parallel.ParallelFruchtermanReingold;
import org.example.layout.sequential.FruchtermanReingold;
import java.io.PrintWriter; //for CSV https://www.geeksforgeeks.org/java/java-io-printwriter-class-java-set-1/
import java.util.Locale; //so decimal numbers have a dot and not a comma

public class PerformanceBenchmark {

    private static void warmup(LayoutAlgorithm algorithm) {

        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            algorithm.step();
        }
    }

    private static double  benchmark(LayoutAlgorithm algorithm, Mode mode) {

        //measure time (no gui)
        long start = System.nanoTime();

        for (int i = 0; i < ITERATIONS; i++) {
            algorithm.step(); //can refer to both sequential and parallel because of the interface
        }
        long end = System.nanoTime();
        double durationMs = (end - start) / 1_000_000.0;
        double averageIterationsMS = durationMs/ITERATIONS;

        //printing the time
        System.out.println("----------------------------------");
        System.out.println("                                                       ");
        System.out.println(mode + " mode");
        System.out.println("Execution time: " + durationMs + " ms");
        System.out.println("Average iteration time: " + averageIterationsMS + " ms");
        System.out.println("                                                       ");

        return durationMs;

    }

    private static final int[] VERTEX_SIZES = {500, 1000, 2000, 3000};
    private static final int ITERATIONS = 500;

    private static final int WARMUP_ITERATIONS = 50; //run a few times before start actually measuring
    private static final int EDGES = 1000; // fixed for vertex test
    private static final int[] EDGE_SIZES = {500, 1000, 2000, 3000};

    private static final int FIXED_VERTICES = 1000;//fixed for EDGE text

    private static final int RUNS = 5; //repeat benchmark 5 (or n )  times
    private static final int WIDTH = 3000;
    private static final int HEIGHT = 3000;

    private static final long SEED = 42;
    private static final double C = 1.0;

    public static void main(String[] args) {

        try {
            PrintWriter writer = new PrintWriter("benchmarkingResults/benchmark_results.csv");

            //header
            writer.println(
                    "experiment," +
                            "vertices," +
                            "edges," +
                            "avg_sequential_ms," +
                            "avg_parallel_ms," +
                            "avg_iteration_sequential_ms," +
                            "avg_iteration_parallel_ms," +
                            "speedup"
            );

            // warm-up before benchmark measurements
            Graph warmupSequentialGraph = Graph.randomGraph(1000, 1000, WIDTH, HEIGHT, SEED);

            Graph warmupParallelGraph = Graph.randomGraph(1000, 1000, WIDTH, HEIGHT, SEED);

            FruchtermanReingold warmupSequential = new FruchtermanReingold(warmupSequentialGraph, WIDTH, HEIGHT, C);
            ParallelFruchtermanReingold warmupParallel = new ParallelFruchtermanReingold(warmupParallelGraph, WIDTH, HEIGHT, C, false);

            warmup(warmupSequential);
            warmup(warmupParallel);
            warmupParallel.shutdown();

            System.out.println("Warm-up complete.");


            //TEST 1: VERTEX TEXT
            for(  int vertex  : VERTEX_SIZES){

                System.out.println("\nTesting " + vertex + " vertices");
                System.out.println("----------------------------------");

                double totalSequentialTime = 0;
                double totalParallelTime = 0;


                for (int run = 1; run <= RUNS; run++) {

                    System.out.println("\nRun " + run + " of " + RUNS);

                    Graph sequentialGraph = Graph.randomGraph(vertex, EDGES, WIDTH, HEIGHT, SEED);

                    Graph parallelGraph = Graph.randomGraph(vertex, EDGES, WIDTH, HEIGHT, SEED);

                    // sequential and parallel layouts
                    FruchtermanReingold sequential = new FruchtermanReingold(sequentialGraph,WIDTH,HEIGHT,C);
                    ParallelFruchtermanReingold parallel = new ParallelFruchtermanReingold(parallelGraph,WIDTH,HEIGHT,C, false);

                    totalSequentialTime += benchmark(sequential, Mode.SEQUENTIAL);
                    totalParallelTime += benchmark(parallel, Mode.PARALLEL);
                    parallel.shutdown();
                }

                double avgSequentialTime = totalSequentialTime / RUNS;
                double avgParallelTime = totalParallelTime / RUNS;

                double speedup = avgSequentialTime / avgParallelTime;

                double avgSequentialIteration = avgSequentialTime / ITERATIONS;
                double avgParallelIteration = avgParallelTime / ITERATIONS;

                System.out.println("\n===== RESULTS FOR " + vertex + " VERTICES =====");
                System.out.println("Average Sequential: " + avgSequentialTime + " ms");
                System.out.println("Average Parallel: " + avgParallelTime + " ms");
                System.out.println("Speedup: " + speedup + "x");

                // save vertex result to CSV
                writer.printf(
                        Locale.US,
                        "VERTEX,%d,%d,%.3f,%.3f,%.3f,%.3f,%.3f%n",
                        vertex,
                        EDGES,
                        avgSequentialTime,
                        avgParallelTime,
                        avgSequentialIteration,
                        avgParallelIteration,
                        speedup
                );

            }

            // TEST 2: edge benchmark - keep vertices fixed and change number of edges
            for (int edge : EDGE_SIZES) {

                System.out.println("\nTesting " + edge + " edges");
                System.out.println("----------------------------------");

                double totalSequentialTime = 0;
                double totalParallelTime = 0;

                for (int run = 1; run <= RUNS; run++) {

                    System.out.println("\nRun " + run + " of " + RUNS);

                    Graph sequentialGraph = Graph.randomGraph(FIXED_VERTICES, edge, WIDTH, HEIGHT, SEED);

                    Graph parallelGraph = Graph.randomGraph(FIXED_VERTICES, edge, WIDTH, HEIGHT, SEED);

                    // sequential and parallel layouts
                    FruchtermanReingold sequential = new FruchtermanReingold(sequentialGraph, WIDTH, HEIGHT, C);

                    ParallelFruchtermanReingold parallel = new ParallelFruchtermanReingold(parallelGraph, WIDTH, HEIGHT, C, false);

                    totalSequentialTime += benchmark(sequential, Mode.SEQUENTIAL);
                    totalParallelTime += benchmark(parallel, Mode.PARALLEL);
                    parallel.shutdown();
                }

                double avgSequentialTime = totalSequentialTime / RUNS;
                double avgParallelTime = totalParallelTime / RUNS;

                double speedup = avgSequentialTime / avgParallelTime;

                double avgSequentialIteration = avgSequentialTime / ITERATIONS;
                double avgParallelIteration = avgParallelTime / ITERATIONS;

                System.out.println("\n===== RESULTS FOR " + edge + " EDGES =====");
                System.out.println("Average Sequential: " + avgSequentialTime + " ms");
                System.out.println("Average Parallel: " + avgParallelTime + " ms");
                System.out.println("Speedup: " + speedup + "x");

                // save edge result to CSV
                writer.printf(
                        Locale.US,
                        "EDGE,%d,%d,%.3f,%.3f,%.3f,%.3f,%.3f%n",
                        FIXED_VERTICES,
                        edge,
                        avgSequentialTime,
                        avgParallelTime,
                        avgSequentialIteration,
                        avgParallelIteration,
                        speedup
                );
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
