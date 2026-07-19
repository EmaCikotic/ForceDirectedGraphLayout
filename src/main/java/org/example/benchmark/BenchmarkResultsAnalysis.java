package org.example.benchmark;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class BenchmarkResultsAnalysis {

    private static final String RESULTS_FOLDER = "benchmarkingResults/";
    private static final String SEQUENTIAL_PARALLEL_FILE =  RESULTS_FOLDER + "benchmark_results.csv";

    private static final String[] DISTRIBUTED_FILES = {
            RESULTS_FOLDER + "distributed_benchmark_1_process.csv",
            RESULTS_FOLDER + "distributed_benchmark_2_processes.csv",
            RESULTS_FOLDER + "distributed_benchmark_3_processes.csv",
            RESULTS_FOLDER + "distributed_benchmark_4_processes.csv"
    };

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

            System.out.println("\n===== DISTRIBUTED RESULTS =====");
            System.out.println("                                  ");

            int vertexIndex = 0;
            int edgeIndex = 0;

            double[] vertexBaselineTimes = new double[4]; //4 experiments
            double[] edgeBaselineTimes = new double[4]; //4 experiments

            for (String file : DISTRIBUTED_FILES) {

                //have to be reset to 0 for each file
                int currentVertexIndex = 0;
                int currentEdgeIndex = 0;

                BufferedReader distributedReader = new BufferedReader(new FileReader(file));

                distributedReader.readLine(); // skip CSV header

                while ((line = distributedReader.readLine()) != null) {

                    String[] values = line.split(",");

                    String experiment = values[0];
                    int vertices = Integer.parseInt(values[1]);
                    int edges = Integer.parseInt(values[2]);
                    int processes = Integer.parseInt(values[3]);
                    double distributedTime = Double.parseDouble(values[4]);

                    if (processes == 1) {

                        if (experiment.equals("VERTEX")) {
                            vertexBaselineTimes[vertexIndex] = distributedTime;
                            vertexIndex++;
                        } else if (experiment.equals("EDGE")) {
                            edgeBaselineTimes[edgeIndex] = distributedTime;
                            edgeIndex++;
                        }
                    }

                    if (experiment.equals("VERTEX")) {

                        double speedup = vertexBaselineTimes[currentVertexIndex] / distributedTime;
                        double efficiency = speedup / processes;

                        System.out.printf(
                                Locale.US,
                                "Vertices: %d | Processes: %d | Distributed: %.3f ms | Speedup: %.3fx | Efficiency: %.2f%%%n",
                                vertices,
                                processes,
                                distributedTime,
                                speedup,
                                efficiency * 100
                        );

                        currentVertexIndex++;

                    } else if (experiment.equals("EDGE")) {

                        double speedup = edgeBaselineTimes[currentEdgeIndex] / distributedTime;
                        double efficiency = speedup / processes;

                        System.out.printf(
                                Locale.US,
                                "Edges: %d | Processes: %d | Distributed: %.3f ms | Speedup: %.3fx | Efficiency: %.2f%%%n",
                                edges,
                                processes,
                                distributedTime,
                                speedup,
                                efficiency * 100 //for %
                        );
                        currentEdgeIndex++;
                    }
                }
                distributedReader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}