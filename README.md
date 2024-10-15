# Parallel ACO
Simple Java framework for multithreaded ant colony optimization in graph problems.

### Disclaimer
Just started bringing an archived package back to life, hoping to make something useful out of it later. This repository refers back to an undergraduate project I developed in 2010 when taking a course in cloud computing. The idea is to give a simple java framework that is easy to extend towards running the Ant Colony Optimization (ACO) metaheuristic in a graph optimization problem. The parallel execution exploits multicore (shared memory) concurrency in a natural _MapReduce_ programming model in a completely transparent API to the client.

The original project experimented with distributed memory architectures using Apache Hadoop, and later introduced the current framework. The proof of concept was presented in 2011 as the "Map ants, Reduce work" paper in the IX Metaheuristics International Conference, in Udine, Italy.


### Workflow

TO DO:
- [ ] Write up usage tutorial

### This repository, compiling and running

TO DO:
- [ ] Play with Java 21 and adjust as needed
- [ ] Set up github actions

### Examples of client solvers based on the framework

TO DO:
- [x] Upload TSP (traveling salesperson problem) example to kickstart
- [ ] Upload KP (knapsack problem) example and reestructure the repository
- [ ] Upload 2DHP (2D hydrophobic-polar model for protein folding pathway prediction) example
