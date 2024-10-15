#!/usr/bin/perl
use strict;
use warnings;

my $out_file_name = "tsp_experiments.dat";

# clean previous output file
# system("rm -f $out_file_name") >= 0
# or die "\nAborting execution (impossible to write/remove file: $?)";

my @config_files = create_config_files();

my $count = 1;

foreach my $config (@config_files)
{
    # skip completed experiments
    if($count < 37)
    {
        $count++;
        next;
    }

    # copy config file
    system("cp $config Config.java") >= 0
    or die "\nAborting execution due to config file copy error: $?";

    # compile
    system("rm -f *.class ; javac *.java") >= 0
    or die "\nAborting execution due to compile error: $?";

    # status message
    my($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)=localtime(time);
    print sprintf("[%02d:%02d:%02d] Experiment \# %3d/%d:  %s  ", $hour, $min, $sec, ($count++), scalar(@config_files), $config =~ m/config\/(.+)\.java/);

    # run experiments
    my @exectimes;
    for (my $exec = 0; $exec < 10 ; $exec++)
    {
      # gnu time utility (/usr/bin/time) for "wall/real time" measuring
      # (since time uses STDERR, we must redirect it to STDERR
      my $run = "time -f \"\%e\" java myTSPDriver" . " 2>&1";
      $exectimes[$exec] = `$run`;
    }

    # mean and standard deviation from 30 executions
    my @stats = stats(@exectimes);
    print sprintf("(time: %.4fs +/- %.4fs)\n", $stats[0], $stats[1]);

    # current configuration (for output file)
    $config =~ m/config\/in(\d)_it(\d)_ant(\d)_thr(\d)\.java/;
    my ($in) = $1;
    my ($it) = $2;
    my ($ant) = $3;
    my ($thr) = $4;

    # user-friendly configuration (for output file)
    if    ($in == 1) { $in = 42; }
    elsif ($in == 2) { $in = 96; }
    elsif ($in == 3) { $in = 229; }
    elsif ($in == 4) { $in = 431; }
    elsif ($in == 5) { $in = 666; }

    if    ($it == 1) { $it = 50; }
    elsif ($it == 2) { $it = 150; }
    elsif ($it == 3) { $it = 300; }

    if    ($ant == 1) { $ant = 20; }
    elsif ($ant == 2) { $ant = 50; }
    elsif ($ant == 3) { $ant = 150; }

    # write output file
    open my $out_file, ">>", $out_file_name;
    print $out_file sprintf("$in $it $ant $thr %.4f %.4f\n", $stats[0], $stats[1]);
    close $out_file;
}

# clean *.class files
system("rm *.class");
exit(0);


sub stats
{
    ## execution statistics function, returning [mean, stddev] for the sample in
    ## the given array

    my(@sample) = @_;
    my $len = (scalar @sample);

    # prevent division by 0 error
    return undef unless($len);

    # direct and squared element sum
    my $total1 = 0;
    my $total2 = 0;
    foreach my $num (@sample) {
        $total1 += $num;
        $total2 += $num**2;
    }

    # mean / average
    my $mean = $total1 / $len;

    # standard deviation
    my $diff = $total2/$len - $mean**2;
    my $std_dev = sqrt($diff);

    my @stat = ($mean, $std_dev);
    return @stat;
}

sub create_config_files
{
    ## create experiments config directory and files (framework java Config
    ## class) returns an array containing Config files path

    my @config_iterations =
        ( "    public static final int max_iterations = 50;   // 50 , 150 , 300\n"
        , "    public static final int max_iterations = 150;  // 50 , 150 , 300\n"
        , "    public static final int max_iterations = 300;  // 50 , 150 , 300\n"
        );

    my @config_ants =
        ( "    public static final int num_ants = 20;    // 20 , 50 , 150\n"
        , "    public static final int num_ants = 50;    // 20 , 50 , 150\n"
        , "    public static final int num_ants = 150;   // 20 , 50 , 150\n"
        );

    my @config_threads =
#        ( "    public static final int num_threads = 1;   // 1 , 2 , 3 , 4 , 5 , 6\n"
#        , "    public static final int num_threads = 2;   // 1 , 2 , 3 , 4 , 5 , 6\n"
        ( "    public static final int num_threads = 3;   // 1 , 2 , 3 , 4 , 5 , 6\n"
#        , "    public static final int num_threads = 4;   // 1 , 2 , 3 , 4 , 5 , 6\n"
#        , "    public static final int num_threads = 5;   // 1 , 2 , 3 , 4 , 5 , 6\n"
#        , "    public static final int num_threads = 6;   // 1 , 2 , 3 , 4 , 5 , 6\n"
        );

    my @config_input =
        ( "\n    public static final String input_file = \"input/dantzig42_opt699_disttable.txt\";\n    public static final boolean computed_dist_table = true;\n    public static final int num_cities = 42;\n    public static final long known_optimum = 699;\n"
        , "\n    public static final String input_file = \"input/gr96_opt55209.txt\";\n    public static final boolean computed_dist_table = false;\n    public static final int num_cities = 96;\n    public static final long known_optimum = 55209;\n"
        , "\n    public static final String input_file = \"input/gr229_opt134602.txt\";\n    public static final boolean computed_dist_table = false;\n    public static final int num_cities = 229;\n    public static final long known_optimum = 134602;\n"
        , "\n    public static final String input_file = \"input/gr431_opt171414.txt\";\n    public static final boolean computed_dist_table = false;\n    public static final int num_cities = 431;\n    public static final long known_optimum = 171414;\n"
        , "\n    public static final String input_file = \"input/gr666_opt294358.txt\";\n    public static final boolean computed_dist_table = false;\n    public static final int num_cities = 666;\n    public static final long known_optimum = 294358;\n"
        );

    my @config_base_prefix =
        ( "public class Config"
        , "{"
        , "    public static final double initial_pheromone = 0.33, evaporation_rate = 0.5;"
        , "    public static final double pheromone_weight = 1, heuristic_weight = 2;"
        , "    public static final int num_exec = 1;"
        , "    public static long rand_seed = 1234567;"
        , ""
        , "    ////////////////////////////////////////////////////////////////////////////"
        , ""
        );

    my @config_base_sufix =
        ( "    // dantzig42_opt699_disttable.txt"
        , "    // gr96_opt55209.txt"
        , "    // gr229_opt134602.txt"
        , "    // gr431_opt171414.txt"
        , "    // gr666_opt294358.txt"
        , "    ////////////////////////////////////////////////////////////////////////////"
        , "}"
        , "\n"
        );

    # create experiments config directory and files
    mkdir("config") if !(-e "config");
    my @config_files;

    for (my $input = 1; $input <= @config_input ; $input++)
    {
        for (my $iter = 1; $iter <= @config_iterations ; $iter++)
        {
            for (my $ants = 1; $ants <= @config_ants ; $ants++)
            {
                for (my $threads = 1; $threads <= @config_threads; $threads++)
                {
                    my $file_name = "config/in$input\_it$iter\_ant$ants\_thr$threads.java";
                    push(@config_files, $file_name);

                    open my $config_file, ">", $file_name;
                    print $config_file join("\n", @config_base_prefix);
                    print $config_file $config_iterations[$iter-1];
                    print $config_file $config_ants[$ants-1];
                    print $config_file $config_threads[$threads-1];
                    print $config_file $config_input[$input-1];
                    print $config_file join("\n", @config_base_sufix);
                    close $config_file;
                }
            }
        }
    }

    return @config_files;
}
