import java.util.Collection;
import java.util.Vector;

public class myTSPDriver extends Driver
{
    private static myTSPSolution bestKnown;

    @Override
    public void buildMapperTasks(int n)
    {
        for (int id = 0; id < n ; ++id)
            try { Driver.tasks.add( new myTSPAntMapper(id) ); }
            catch (Exception e)
            {
                System.err.print("Error: " + e.getMessage());
                System.exit(1);
            }
    }

    @Override
    public Pheromone initPheromone()
    {
        Pheromone p = new myTSPPheromone(Config.num_cities);
        p.init(Config.initial_pheromone);
        return p;
    }

    @Override
    public Graph initGraph()
    {
        Graph g = new myTSPGraph(2);
        g.init();
        return g;
    }

    @Override
    public void setup()
    {
        bestKnown = new myTSPSolution();
        bestKnown.setValue(Long.MAX_VALUE);
    }

    @Override
    public void reduceActions()
    {
        myTSPAntMapper.collectStatistics();

        if (bestKnown.getValue() > myTSPAntMapper.bestMapper.getValue())
            bestKnown = myTSPAntMapper.bestMapper;
    }

    @Override
    public void cleanup()
    {
        // best solution found
//         System.out.println("Execution #" + 1 + " :: the best solution after "
//                            + Config.max_iterations + " iterations has length "
//                            + bestKnown.getValue() + " (shown below):"
//                            + "\n> " + bestKnown.toString() );
    }

    public static void main(String[] Args)
    {
        new myTSPDriver().run();
    }
}
