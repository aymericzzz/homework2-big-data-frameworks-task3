import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Counter;

import java.text.DecimalFormat;

public class Aggregate3 extends Configured implements Tool {

    private static DecimalFormat df2 = new DecimalFormat(".##");

    // cette enum nous servira pour les compteurs de male et female
    static enum GenderCount{
        MALE,
        FEMALE
    }
    // on arrive ici après le lancement de ToolRunner.run(new Exo...)
    public int run(String[] args) throws Exception {
        long num_male = 0;
        long num_female = 0;
        int res;

        Job job = new Job(getConf());
        job.setJarByClass(getClass());
        job.setJobName(getClass().getSimpleName());

        // input path correspond à args[0] = le fichier à étudier
        FileInputFormat.addInputPath(job, new Path(args[0]));
        // output path correspond à args[1] = le fichier à output
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // on set le mapper qui va mapper l'input
        job.setMapperClass(Mapper3.class);
        // on set le combiner qui va faire un pre-reducing et également incrémenter nos compteurs
        job.setCombinerClass(Combiner3.class);
        // on set le reducer pour le reduce job qui va reduce toutes les maps ensemble
        job.setReducerClass(Reducer3.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        res = job.waitForCompletion(true) ? 0 : 1;

        // ici, on va récupérer la valeur du compteur pour les male
        num_male = job.getCounters().findCounter(GenderCount.MALE).getValue();
        // valeur de compteur pour les female
        num_female = job.getCounters().findCounter(GenderCount.FEMALE).getValue();
        // calcul des proportions
        float male_proportion = (((float)num_male / (float)(num_male + num_female)))*100;
        float female_proportion = ((float)num_female / (float)(num_male + num_female))*100;

        // print
        System.out.println("number/proportion of male : " + num_male + " " + df2.format(male_proportion) + "%");
        System.out.println("number/proportion of female : " + num_female + " " + df2.format(female_proportion) + "%");

        return res;
    }

    public static void main(String[] args) throws Exception {
        // lance un tool avec une configuration : Aggregate3 est un objet Tool, et on lui donne les paramètres renseignées dans le terminal (args)
        int rc = ToolRunner.run(new Aggregate3(), args);
        System.exit(rc);
    }
}