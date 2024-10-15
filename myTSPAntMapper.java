/**
 *  \file myTSPAntMapper.java
 *
 *  TSP client implementing the framework class representing each map task (thread work).
 *
 *  "Map Ants, Reduce Work": a research project regarding a MapReduce inspired
 *  parallel framework for the Ant Colony Optimization (ACO) algorithm, under
 *  development at UFMG (Universidade Federal de Minas Gerais, Brazil).
 *
 *  \author Phillippe Samer <phillippes@gmail.com>
 *
 *  \date 16.01.2011
 */

import java.util.Collection;
import java.util.Vector;
import java.util.Arrays;
import java.util.Random;

public class myTSPAntMapper extends AntMapper
{
    private int antMapperID;
    private Random rand;
    private boolean[] visitedCities;
    private myTSPSolution builtSolution;

    protected static Vector<myTSPSolution> solutions;

    public static double solutionQualityMean;
    public static myTSPSolution bestMapper;
    public static myTSPSolution worstMapper;
    public static int redundantSolutionCount;
    private static boolean[] checkedRedundantSolution;

    myTSPAntMapper(int id)
    {
        antMapperID = id;
        rand = new Random(Config.rand_seed++);
        visitedCities = new boolean[Config.num_cities];
        builtSolution = new myTSPSolution();
    }

    @Override
    public void setup()
    {
        // clears and initializes the solutions vector
        if (solutions != null) solutions.clear();
        solutions = new Vector<myTSPSolution>(Config.num_ants);
        for (int ind = 0; ind < Config.num_ants; ++ind)
            solutions.add(new myTSPSolution());

        checkedRedundantSolution = new boolean[Config.num_ants];
    }

    @Override
    public void antRandomPosition()
    {
        int start = rand.nextInt(Config.num_cities);
        builtSolution.solution[0] = start;
        visitedCities[start] = true;
    }

    @Override
    public void antBuildSolution()
    {
        int step = 1;

        // choose a city to add to the path, while it isn't complete
        while (step<Config.num_cities)
        {
            double[] prob = new double[Config.num_cities];
            double totalProb = 0.;

            int currentCity = builtSolution.solution[step-1];

            /* probabilistically evaluates every city according to a random
             * proportional transition rule
             */
            for (int c = 0; c<Config.num_cities; ++c)
            {
                // for each unvisited city
                if (!visitedCities[c])
                {
                    // evaluates heuristic ("desirability" = 1/d)
                    int[] index = {currentCity, c};
                    double heuristic = (double) 1./( ((Long)AntMapper.problemGraph.get(index)).doubleValue());
                    heuristic = Math.pow(heuristic, Config.heuristic_weight);

                    // probabilistic transition function numerator (prior to normalization)
                    prob[c] = heuristic * Math.pow(AntMapper.currentPheromone.get(index), Config.pheromone_weight);

                    totalProb += prob[c];
                }
            }

            // full transition function, normalized in [0,1]
            for (int i=0; i<Config.num_cities; ++i)
                prob[i] /= totalProb;

            // probabilistically chooses next city
            int city = -1;
            while (city<0)
            {
                double r1 = rand.nextDouble();
                Vector<Integer> vertices = new Vector<Integer>();

                // consider (includes in the vector) only cities with prob >= r1
                for (int k = 0; k<Config.num_cities; ++k) {
                    if (prob[k]>=r1 && prob[k]>0)
                        vertices.addElement( new Integer(k) );
                }

                // start again (using a new prob), case no city was chosen
                if (vertices.size()<=0)
                    continue;

                // randomly chooses one city in the vector
                int r2 = rand.nextInt(vertices.size());
                city = (vertices.elementAt(r2)).intValue();
            }

            // adds new city to the solution, and marks it as visited
            builtSolution.solution[step] = city;
            visitedCities[city] = true;

            ++step;
        }

    }

    @Override
    public void antEvalSolution()
    {
        // evaluates built solution fitness, i.e. the chosen path distance

        // solution not yet built
        if (builtSolution.solution[1]==builtSolution.solution[2])
        {
            builtSolution.setValue(0);
            return;
        }

        // starting edge vertices
        int from = builtSolution.solution[0];
        int to = builtSolution.solution[1];
        int step = 2;
        long solution_val = 0;

        // sums up weights of chosen edges
        while (step<=Config.num_cities)
        {
            int[] index = {from, to};
            solution_val += ((Long)AntMapper.problemGraph.get(index)).longValue();
            from = to;
            to = builtSolution.solution[step++ % Config.num_cities];
        }

        // final edge weight (return path to the starting vertex)
        int[] index = {from, to};
        solution_val += ((Long)AntMapper.problemGraph.get(index)).longValue();

        // evaluation done: saves value in the built solution and stores it
        builtSolution.setValue(solution_val);
        solutions.setElementAt(builtSolution, antMapperID);
        checkedRedundantSolution[antMapperID] = false;
    }

    @Override
    public Pheromone antUpdatePheromone()
    {
        /* important: returns a new pheromone matrix including ONLY THE UPDATE
         * to be applied to those edges used in the built solution
         */
        Pheromone updatedPheromone = AntMapper.currentPheromone.empty();
        double update = (double) 1./(builtSolution.getValue().doubleValue());

        // starting edge vertices
        int from = builtSolution.solution[0];
        int to = builtSolution.solution[1];
        int step = 2;

        // increases pheromone associated to each used edge
        while (step<=Config.num_cities)
        {
            int[] index = {from, to};
            updatedPheromone.set(index, updatedPheromone.get(index)+update);
            from = to;
            to = builtSolution.solution[step++ % Config.num_cities];
        }

        // increases pheromone associated to the last used edge (back to the starting one)
        int[] index = {from, to};
        updatedPheromone.set(index, updatedPheromone.get(index)+update);

        return updatedPheromone;
    }

    public static void collectStatistics()
    {
        // collects best, worst, mean and redundant solutions
        long best_value = Long.MAX_VALUE;
        long worst_value = Long.MIN_VALUE;
        solutionQualityMean = 0.;

        bestMapper = new myTSPSolution();
        worstMapper = new myTSPSolution();
        bestMapper.setValue(best_value);
        worstMapper.setValue(worst_value);

        for (myTSPSolution r : solutions)
        {
            long r_value = r.getValue().longValue();

            if (r_value < best_value)
            {
                bestMapper = r;
                best_value = r_value;
            }

            if (r_value > worst_value)
            {
                worstMapper = r;
                worst_value = r_value;
            }

            solutionQualityMean += (double) r_value;
        }

        // finish mean evaluation
        solutionQualityMean /= (double) Config.num_ants;

        // count redundant solutions
        int redundant = 0;
        for (int i = 0; i < Config.num_ants; ++i)
            for (int j = i+1; j < Config.num_ants; ++j)
                if ( Arrays.equals(solutions.elementAt(i).solution,
                                   solutions.elementAt(j).solution) )
                    if (!checkedRedundantSolution[j])
                    {
                        ++redundant;
                        checkedRedundantSolution[j] = true;
                    }
    }

}
