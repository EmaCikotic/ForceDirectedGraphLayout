package org.example.benchmark;
import org.example.graph.Graph;
import org.example.layout.LayoutAlgorithm;
import org.example.layout.Mode;
import org.example.layout.parallel.ParallelFruchtermanReingold;
import org.example.layout.sequential.FruchtermanReingold;

import javax.sound.midi.Soundbank;


public class PerformanceBenchmark {

    private static void benchmark(LayoutAlgorithm algorithm, Mode mode) {

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

    }

    private static final int[] VERTEX_SIZES = {500, 1000, 2000, 3000};
    private static final int ITERATIONS = 500;
    private static final int WIDTH = 3000;
    private static final int HEIGHT = 3000;
    private static final int EDGES = 1000;
    private static final long SEED = 42;
    private static final double C = 1.0;

    public static void main(String[] args) {


       for(  int vertex  : VERTEX_SIZES){

           System.out.println("\nTesting " + vertex + " vertices");
           System.out.println("----------------------------------");


            Graph sequentialGraph =
                    Graph.randomGraph(vertex, EDGES, WIDTH, HEIGHT, SEED);

            Graph parallelGraph =
                    Graph.randomGraph(vertex, EDGES, WIDTH, HEIGHT, SEED);

            // sequential and parallel layouts
            FruchtermanReingold sequential = new FruchtermanReingold(sequentialGraph,WIDTH,HEIGHT,C);
            ParallelFruchtermanReingold parallel = new ParallelFruchtermanReingold(parallelGraph,WIDTH,HEIGHT,C);

            benchmark(sequential, Mode.SEQUENTIAL);
            benchmark(parallel, Mode.PARALLEL);
       }

    }
}
