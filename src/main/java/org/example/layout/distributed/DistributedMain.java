package org.example.layout.distributed;

import mpi.MPI;
public class DistributedMain {

    public static void main(String[] args) {

        MPI.Init(args);

        if (MPI.COMM_WORLD.Rank() == 0) {

            DistributedGraphInput input = new DistributedGraphInput();

            // Wait until the user clicks "Generate"
            while (!input.isGenerated()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            //test
            System.out.println(input.getWidth());
            System.out.println(input.getHeight());
            System.out.println(input.getVertices());
            System.out.println(input.getEdges());
            System.out.println(input.getSeed());
            System.out.println(input.getC());
        }

        MPI.Finalize();
    }
}