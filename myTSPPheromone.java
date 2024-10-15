/**
 *  \file myTSPPheromone.java
 *
 *  TSP client implementing the framework interface for managing pheromone matrices.
 *
 *  "Map Ants, Reduce Work": a research project regarding a MapReduce inspired
 *  parallel framework for the Ant Colony Optimization (ACO) algorithm, under
 *  development at UFMG (Universidade Federal de Minas Gerais, Brazil).
 *
 *  \author Phillippe Samer <phillippes@gmail.com>
 *
 *  \date 16.01.2011
 */

public class myTSPPheromone implements Pheromone
{
    private double[][] matrix;

    public myTSPPheromone(int n)
    {
        matrix = new double[n][n];
    }

    @Override
    public void init(double rho)
    {
        int dim = this.matrix.length;

        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j)
                this.matrix[i][j] = rho;
    }

    @Override
    public double get(int[] index)
    {
        return matrix[index[0]][index[1]];
    }

    @Override
    public void set(int[] index, double value)
    {
        matrix[index[0]][index[1]] = value;
    }

    @Override
    public Pheromone empty()
    {
        return new myTSPPheromone(this.matrix.length);
    }

    @Override
    public Pheromone clone()
    {
        int dim = this.matrix.length;
        myTSPPheromone p = new myTSPPheromone(dim);

        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j)
                p.matrix[i][j] = this.matrix[i][j];

        return p;
    }

    @Override
    public Pheromone merge(Pheromone p)
    {
        /* merge method which just sums up every correspondent element
         * pair from two Pheromone matrixes
         */
        int dim = this.matrix.length;
        Pheromone result = new myTSPPheromone(dim);
        
        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j)
            {
                int[] index = {i, j};
                result.set( index , this.matrix[i][j] + p.get(index) );
            }

        return result;
    }

    @Override
    public void evaporate(double rate)
    {
        int dim = this.matrix.length;

        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j)
                matrix[i][j] = (matrix[i][j] <= 0) ? 0: matrix[i][j] * (1-rate);
    }

}
