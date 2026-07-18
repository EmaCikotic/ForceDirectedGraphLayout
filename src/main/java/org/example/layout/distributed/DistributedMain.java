package org.example.layout.distributed;

import mpi.MPI;
import org.example.graph.Graph;
import org.example.layout.Mode;

import javax.swing.*;
import java.awt.*;

public class DistributedMain {

    private static final int MAX_ITER = 500;

    public static void main(String[] args) {

        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();

        int[] intParams = new int[4];
        long[] seedParam = new long[1];
        double[] cParam = new double[1];


        if (rank == 0) {

            DistributedGraphInput input = new DistributedGraphInput();

            while (!input.isGenerated()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            intParams[0] = input.getWidth();
            intParams[1] = input.getHeight();
            intParams[2] = input.getVertices();
            intParams[3] = input.getEdges();

            seedParam[0] = input.getSeed();
            cParam[0] = input.getC();
        }

        MPI.COMM_WORLD.Bcast(intParams, 0, intParams.length, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(seedParam, 0, 1, MPI.LONG, 0);
        MPI.COMM_WORLD.Bcast(cParam, 0, 1, MPI.DOUBLE, 0);

       //first graph for timing
        Graph graphTime = Graph.randomGraph(
                intParams[2],
                intParams[3],
                intParams[0],
                intParams[1],
                seedParam[0]
        );

        DistributedFruchtermanReingold layoutTime =
                new DistributedFruchtermanReingold(
                        graphTime,
                        intParams[0],
                        intParams[1],
                        cParam[0]);

        long start = System.nanoTime();

        for (int i = 0; i < MAX_ITER; i++) {
            layoutTime.step();
        }

        long end = System.nanoTime();

        double durationMs = (end - start) / 1_000_000.0;
        double averageIterationMs = durationMs / MAX_ITER;

        //second graph for visualization
        Graph graphAnim = Graph.randomGraph(
                intParams[2],
                intParams[3],
                intParams[0],
                intParams[1],
                seedParam[0]
        );

        DistributedFruchtermanReingold layoutAnim =
                new DistributedFruchtermanReingold(
                        graphAnim,
                        intParams[0],
                        intParams[1],
                        cParam[0]);

        DistributedGraphPanel panel = null;

        if (rank == 0) {

            JFrame frame = new JFrame("Distributed Force Directed Graph Layout");

            panel = new DistributedGraphPanel(
                    graphAnim,
                    layoutAnim,
                    durationMs,
                    averageIterationMs,
                    Mode.DISTRIBUTED
            );

            panel.setBackground(Color.GRAY);
            frame.add(panel);
            frame.setSize(900, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        // Show initial graph
        if (rank == 0) {
            panel.repaint();
        }

     // Everyone waits
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Everyone runs the algorithm
        for (int i = 0; i < MAX_ITER; i++) {

            layoutAnim.step();

            if (rank == 0) {
                panel.repaint();
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        MPI.Finalize();
    }
}