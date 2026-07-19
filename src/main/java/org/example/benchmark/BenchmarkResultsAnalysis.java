package org.example.benchmark;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BenchmarkResultsAnalysis {

    private static final String RESULTS_FOLDER = "benchmarkingResults/";

    private static final String SEQUENTIAL_PARALLEL_FILE =  RESULTS_FOLDER + "benchmark_results.csv";

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(SEQUENTIAL_PARALLEL_FILE));

            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] values = line.split(",");

                String experiment = values[0];
                int vertices = Integer.parseInt(values[1]);
                int edges = Integer.parseInt(values[2]);

                double sequentialTime = Double.parseDouble(values[3]);
                double parallelTime = Double.parseDouble(values[4]);

                double sequentialIterationTime = Double.parseDouble(values[5]);
                double parallelIterationTime = Double.parseDouble(values[6]);

                double speedup = Double.parseDouble(values[7]);

                if (experiment.equals("VERTEX")) {
                    System.out.println(
                            "Vertices: " + vertices +
                                    " | Sequential: " + sequentialTime + " ms" +
                                    " | Parallel: " + parallelTime + " ms" +
                                    " | Speedup: " + speedup + "x"
                    );
                } else if (experiment.equals("EDGE")) {
                    System.out.println(
                            "Edges: " + edges +
                                    " | Sequential: " + sequentialTime + " ms" +
                                    " | Parallel: " + parallelTime + " ms" +
                                    " | Speedup: " + speedup + "x"
                    );
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}