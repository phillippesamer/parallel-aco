/**
 *  \file Driver.java
 *
 *  Core framework class, managing job setup and execution
 *  This is the main framework class, responsible for thread management, IO /
 *  setup calls, and the main map-reduce loop.
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

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

public abstract class Driver {
    static ExecutorService threadExecutor;
    static Collection<AntMapper> tasks;

    /* Must be implemented by the user class extending Driver, actually filling
     * the tasks collection by instances of the user class extending AntMapper.
     */
    public abstract void buildMapperTasks(int n);

    /* Must be implemented by the user class extending Driver to initialize the
     * pheromone through an instance of the user class implementing 'Pheromone'.
     */
    public abstract Pheromone initPheromone();

    /* Must be implemented by the user class extending Driver to initialize the
     * problem graph through an instance of the user class implementing 'Graph'.
     */
    public abstract Graph initGraph();

    public void setup()
    {
        // Should be implemented for any additional job setup operation not
        // including Graph and Pheromone initialization. Executed once, before
        // the mapreduce loop.
    }

    public void run()
    {
        setup();

        /* prepare central data structures supporting the ACO algorithm */
        AntMapper.currentPheromone = initPheromone();
        AntMapper.problemGraph = initGraph();

        /* core mapreduce loop */
        for (int it = 1; it<=Config.max_iterations ; ++it)
        {
            // status message
//             System.out.print("Mapreduce iteration #" + it);
//             if (it < Config.max_iterations)
//             {
//                 System.out.print("\r");
//                 System.out.flush();
//             }

            /* java concurrency api thread manager */
            threadExecutor = Executors.newFixedThreadPool(Config.num_threads);

            /* prepare tasks that'll run on each thread */
            tasks = new Vector<AntMapper>(Config.num_ants);
            buildMapperTasks(Config.num_ants);

            // optional setup before map phase
            tasks.iterator().next().mapsetup();

            try {
                /* MAP PHASE: main thread blocks, waiting for mapper ones to
                 * finish working in the described tasks.
                 */
                List<Future<Pheromone>> results = threadExecutor.invokeAll(tasks);

                // optional cleanup after map phase
                tasks.iterator().next().mapcleanup();

                /* REDUCE PHASE: merge results into a single matrix, which is
                 * added to the current one (after its evaporation), and collect
                 * statistics through reduceActions().
                 */
                Pheromone update = AntMapper.currentPheromone.empty();
                for (Future<Pheromone> r : results)
                    update = update.merge(r.get());
                AntMapper.currentPheromone.evaporate(Config.evaporation_rate);
                AntMapper.currentPheromone = update.merge(AntMapper.currentPheromone);

                reduceActions();
            }
            catch (InterruptedException e)
            {
                System.err.print("InterruptedException: " + e.getMessage());
                System.exit(1);
            }
            catch (ExecutionException e)
            {
                System.err.print("ExecutionException: " + e.getMessage());
                System.exit(1);
            }

            threadExecutor.shutdown();
        }

//         System.out.print("\r");
//         System.out.flush();
//         System.out.println("Mapreduce iteration ...done");

        cleanup();
    }

    public void reduceActions()
    {
        // Should be implemented for any additional operation during reduce
        // phase, such as statistics computation. Executed at the end of each
        // mapreduce iteration.
    }

    public void cleanup()
    {
        // Should be implemented for job cleanup, such as pending IO operations.
        // Executed once, after the mapreduce loop.
    }

}
