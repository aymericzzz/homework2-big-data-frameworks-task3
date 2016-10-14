import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Counter;

import java.io.IOException;

public class Reducer3 extends Reducer<Text, LongWritable, Text, FloatWritable> {
    Float proportion;
    long sum = 0;
    // valeur à écrire dans le hdfs file
    private FloatWritable result = new FloatWritable();
    // variable qui va stocker nos compteurs
    private long counter;
    
    // on récupère la valeur de nos compteurs dans cette fonction
    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        Cluster cluster = new Cluster(conf);
        Job currentJob = cluster.getJob(context.getJobID());
        counter = currentJob.getCounters().findCounter(Aggregate3.GenderCount.MALE).getValue() + currentJob.getCounters().findCounter(Aggregate3.GenderCount.FEMALE).getValue();
    }
    // reduce(key, values, context)
    public void reduce(Text key, Iterable<LongWritable> values, Context context)throws IOException, InterruptedException{
        sum = 0;
        // on somme les valeurs de la key
        for(LongWritable val:values){
          sum+=val.get();
      }
        // on calcule la proportion en divisant par le total de personnes
        proportion = ((float)sum/(float)counter)*100;
        result.set(proportion);
        // on écrit dans hdfs
        context.write(key, result);
    }
}