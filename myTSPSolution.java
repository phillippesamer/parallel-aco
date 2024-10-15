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
