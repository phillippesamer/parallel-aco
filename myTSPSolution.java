/**
 *  \file myTSPSolution.java
 *
 *  TSP client implementing the framework interface for each candidate solution to the problem.
 * 
 *  "Map Ants, Reduce Work": a research project regarding a MapReduce inspired
 *  parallel framework for the Ant Colony Optimization (ACO) algorithm, under
 *  development at UFMG (Universidade Federal de Minas Gerais, Brazil).
 *
 *  \author Phillippe Samer <phillippes@gmail.com>
 *
 *  \date 16.01.2011
 */

public class myTSPSolution implements Solution<Long>
{
    public int[] solution;
    public long value;

    public myTSPSolution()
    {
        solution = new int[Config.num_cities];
    }

    public myTSPSolution(int[] sol, long val)
    {
        solution = sol;
        value = val;
    }

    @Override
    public String toString()
    {
        // user-friendly solution
        String answer = "";
        for (int k = 0; k < Config.num_cities-1; ++k)
            answer += (solution[k] + "-");

        answer += solution[Config.num_cities-1];
        return answer;
    }

    @Override
    public Long getValue()
    {
        // solution quality (~fitness)
        return new Long(value);
    }

    @Override
    public void setValue(long x)
    {
        value = x;
    }

}
