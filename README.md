# Force Directed Graph Layout

Java implementation of the Fruchterman-Reingold force-directed graph drawing algorithm using sequential, parallel, and distributed approaches.

## Features

- Random graph generation and visualization
- Configurable graph and layout parameters
- Sequential and multithreaded parallel execution
- Distributed execution using MPJ Express
- Performance benchmarking

## Requirements

- Java 21
- Maven
- MPJ Express 0.44 (for distributed execution)

## Running

Run the main class:

```text
org.example.app.Main
```

Sequential and parallel modes can be run directly from the graphical interface.

For distributed execution, MPJ Express is required. IntelliJ run configurations are provided in the `.run` directory. Update the `MPJ_HOME` path in the configuration to match the location of MPJ Express on your system before running.

## Benchmarks

Performance benchmark classes are located in:

```text
org.example.benchmark
```

Benchmark results are saved as CSV files in the `benchmarkingResults` directory.