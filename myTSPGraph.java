import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;

public class myTSPGraph implements Graph<Long>
{
    private long[][] graph;

    public myTSPGraph(int n)
    {
        graph = new long[n][n];
    }

    @Override
    public void init()
    {
        if (Config.computed_dist_table)
            // read computed intercity distance table
            graph = readComputedTable(Config.input_file, Config.num_cities);
        else
            // read and parses tsplib input file
            graph = readTSPLIBGeoInput(Config.input_file, Config.num_cities);
    }

    @Override
    public Long get(int[] index)
    {
        return new Long( graph[index[0]][index[1]] );
    }

    @Override
    public void set(int[] index, Long value)
    {
        graph[index[0]][index[1]] = value.longValue();
    }

    private static long[][] readComputedTable(String input_file, int num_cidades)
    {
        Scanner inputFont = null;
        int vertice;
        long[][] distancias = new long[num_cidades][num_cidades];

        try {
            inputFont = new Scanner(new File(input_file));
        } catch (IOException e) {
            System.err.print("Error: " + e.getMessage());
            System.exit(1);
        }

        // reads each integer in file input to the table
        for (int i = 0; i<num_cidades; ++i)
            for (int j = 0; j<num_cidades; ++j)
                distancias[i][j] = Integer.parseInt(inputFont.next());

        return distancias;
    }

    private static long[][] readTSPLIBGeoInput(String input_file, int num_cidades)
    {
        Scanner inputFont = null;
        int vertice;
        int verticesAnteriores;
        double[][] vetorCidades = new double[num_cidades][2];
        long[][] distancias = new long[num_cidades][num_cidades];

        final double RRR = 6378.388;   // raio do globo terrestre (em km)

        try {
            inputFont = new Scanner(new File(input_file));
        } catch (IOException e) {
            System.err.print("Error: " + e.getMessage());
            System.exit(1);
        }

        while (inputFont.hasNext())
        {
            /* calculo da distancia entre pontos de tipo GEO (coordenadas
             * geograficas de latitude e longitude) no formato TSPLIB. Referencia:
             * www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/TSPFAQ.html
             */
            vertice = Integer.parseInt(inputFont.next()) - 1;
            verticesAnteriores = vertice - 1;

            // converte medida de graus/minutos para radianos
            vetorCidades[vertice][0] = deg2rad( Double.parseDouble(inputFont.next()) );
            vetorCidades[vertice][1] = deg2rad( Double.parseDouble(inputFont.next()) );

            // calcula distancia a todos os vertices ja inseridos
            while (verticesAnteriores >= 0)
            {
                double lat1 = vetorCidades[vertice][0];
                double long1 = vetorCidades[vertice][1];
                double lat2 = vetorCidades[verticesAnteriores][0];
                double long2 = vetorCidades[verticesAnteriores][1];

                double q1 = Math.cos( long1 - long2 );
                double q2 = Math.cos( lat1 - lat2 );
                double q3 = Math.cos( lat1 + lat2 );

                long distDoisPontos = Math.round( ( RRR * Math.acos( (double) 0.5*((1.0+q1)*q2 - (1.0-q1)*q3) ) + 1.0) );

                distancias[vertice][verticesAnteriores] = distDoisPontos;
                distancias[verticesAnteriores][vertice] = distDoisPontos;
                verticesAnteriores--;
            }

        }

        return distancias;
    }

    private static double deg2rad(double x)
    {
        /* conversao de coordenadas de entrada para latitute e longitude em
         * radianos. Referencia:
         * www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/TSPFAQ.html
         */
        long deg = Math.round(x);
        double min = (double) x - deg;
        double rad = Math.PI * ( (double) ((double) deg + 5.0 * min/3.0) / 180.0);
        return rad;
    }

}
