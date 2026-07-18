package org.example.benchmark;
import org.example.graph.Graph;
import org.example.layout.LayoutAlgorithm;
import org.example.layout.Mode;
import org.example.layout.parallel.ParallelFruchtermanReingold;
import org.example.layout.sequential.FruchtermanReingold;

public class PerformanceBenchmark {

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

    private static final int RUNS = 5; //repeat benchmark 5 (or n )  times
    private static final int WIDTH = 3000;
    private static final int HEIGHT = 3000;
    private static final int EDGES = 1000;
    private static final long SEED = 42;
    private static final double C = 1.0;

    public static void main(String[] args) {


       for(  int vertex  : VERTEX_SIZES){

           System.out.println("\nTesting " + vertex + " vertices");
           System.out.println("----------------------------------");

           double totalSequentialTime = 0;
           double totalParallelTime = 0;


           for (int run = 1; run <= RUNS; run++) {

               System.out.println("\nRun " + run + " of " + RUNS);

               Graph sequentialGraph =
                       Graph.randomGraph(vertex, EDGES, WIDTH, HEIGHT, SEED);

               Graph parallelGraph =
                       Graph.randomGraph(vertex, EDGES, WIDTH, HEIGHT, SEED);

               // sequential and parallel layouts
               FruchtermanReingold sequential = new FruchtermanReingold(sequentialGraph,WIDTH,HEIGHT,C);
               ParallelFruchtermanReingold parallel = new ParallelFruchtermanReingold(parallelGraph,WIDTH,HEIGHT,C);

               totalSequentialTime += benchmark(sequential, Mode.SEQUENTIAL);
               totalParallelTime += benchmark(parallel, Mode.PARALLEL);
           }

           double avgSequentialTime = totalSequentialTime / RUNS;
           double avgParallelTime = totalParallelTime / RUNS;

           System.out.println("\n===== RESULTS FOR " + vertex + " VERTICES =====");
           System.out.println("Average Sequential: " + avgSequentialTime + " ms");
           System.out.println("Average Parallel: " + avgParallelTime + " ms");

       }

    }
}
