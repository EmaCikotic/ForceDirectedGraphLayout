package org.example.layout.distributed;

import mpi.MPI;

public class DistributedMain {

    public static void main(String[] args) {

        MPI.Init(args);

        new DistributedGraphInput();

        MPI.Finalize();
    }
}