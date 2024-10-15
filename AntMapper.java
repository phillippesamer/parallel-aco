/**
 *  \file AntMapper.java
 *
 *  Framework class representing each map task (thread work)
 *  One of the central classes in the framework, the AntMapper expresses the
 *  tasks for which each map thread is responsible.
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

import java.util.concurrent.Callable;
import java.util.Vector;

public abstract class AntMapper implements Callable<Pheromone>
{
    protected static Pheromone currentPheromone;
    protected static Graph problemGraph;

    public Pheromone call()
    {
        return map();
    }

    public void mapsetup()
    {
        setup();
    }

    public void setup()
    {
        // should be implemented if there's something that should be
        // done before the mappers start working (executed once, before any map)
    }

    public Pheromone map()
    {
        // each task (~thread) applies the core ant operations
        antRandomPosition();
        antBuildSolution();
        antEvalSolution();
        return antUpdatePheromone();
    }

    public void mapcleanup()
    {
        cleanup();
    }

    public void cleanup()
    {
        // should be implemented if there's something that should be
        // done after the mappers finish working (executed once after all maps)
    }

    public abstract void antRandomPosition();

    public abstract void antBuildSolution();

    public abstract void antEvalSolution();

    public abstract Pheromone antUpdatePheromone();
}
