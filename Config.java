public class Config
{
    public static final double initial_pheromone = 0.33, evaporation_rate = 0.5;
    public static final double pheromone_weight = 1, heuristic_weight = 2;
    public static final int num_exec = 1;
    public static long rand_seed = 1234567;

    ////////////////////////////////////////////////////////////////////////////
    public static final int max_iterations = 300;  // 50 , 150 , 300
    public static final int num_ants = 150;   // 20 , 50 , 150
    public static final int num_threads = 3;   // 1 , 2 , 3 , 4 , 5 , 6

    public static final String input_file = "input/gr431_opt171414.txt";
    public static final boolean computed_dist_table = false;
    public static final int num_cities = 431;
    public static final long known_optimum = 171414;
    // dantzig42_opt699_disttable.txt
    // gr96_opt55209.txt
    // gr229_opt134602.txt
    // gr431_opt171414.txt
    // gr666_opt294358.txt
    ////////////////////////////////////////////////////////////////////////////
}

