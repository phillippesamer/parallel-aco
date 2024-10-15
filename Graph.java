/**
 *  \file Graph.java
 *
 *  Framework interface for reading the problem graph.
 *  The implementing class should have a graph structure which can be accessed
 *  as an adjacency matrix, usually implemented as an multidensional array. The
 *  int[] index is simply an array with as many elements as the dimensions in
 *  that matrix, and T should be either Long or Double, according to the problem
 *  graph structure.
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

public interface Graph<T>
{
    public void init();

    public T get(int[] index);

    public void set(int[] index, T value);
}
