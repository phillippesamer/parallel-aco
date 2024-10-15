/**
 *  \file Pheromone.java
 *
 *  Framework interface for managing pheromone matrixes.
 *  The implementing class should have a matrix for each instance, usually
 *  implemented as an multidensional array. The int[] index is simply an array
 *  with as many elements as the dimensions in that matrix.
 *
 *  "Map Ants, Reduce Work": a research project regarding a MapReduce inspired
 *  parallel framework for the Ant Colony Optimization (ACO) algorithm, under
 *  development at UFMG (Universidade Federal de Minas Gerais, Brazil).
 *
 *  \author Phillippe Samer <phillippes@gmail.com>
 *
 *  \date 16.01.2011
 */

// package br.ufmg.dcc.MapRedACO;

public interface Pheromone
{
    public void init(double rho);

    public double get(int[] index);

    public void set(int[] index, double value);

    public Pheromone empty();

    public Pheromone clone();

    public Pheromone merge(Pheromone p);
    
    public void evaporate(double rate);
}
