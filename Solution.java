/**
 *  \file Solution.java
 *
 *  Framework interface for each candidate solution to the problem.
 *  The implementing class should have the appropriate data structure to store
 *  a candidate solution built during the ACO. Its "fitness" should be retrieved
 *  as a numerical value T, which should be either Long or Double, according to
 *  the problem.
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

public interface Solution<T>
{
    public String toString();

    public T getValue();
    
    public void setValue(long x);
}
