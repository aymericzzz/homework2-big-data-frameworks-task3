import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapreduce.Counter;

import java.io.IOException;

public class Reducer3 extends Reducer<Text, LongWritable, Text, LongWritable> {

    // valeur apr�s reduce
    private LongWritable result = new LongWritable();

    // reduce(key, values, context)
    public void reduce(Text key, Iterable<LongWritable> values, Context context)throws IOException, InterruptedException{
        long temp = 0;
        // on stocke la cl� dans un string dont on se servira pour incr�menter un compteur
        String token = key.toString();
        // on lit chaque valeur du tableau values
        // puis, on somme chaque val
        for(LongWritable val: values){
            temp += val.get();

            // parall�lement, on incr�mente un de nos compteurs en fonction de la cl�
            if(GenderUtils.isAMale(token.charAt(0)))
                context.getCounter(Aggregate3.GenderCount.MALE).increment(1);
            else if(GenderUtils.isAFemale(token.charAt(0)))
                context.getCounter(Aggregate3.GenderCount.FEMALE).increment(1);
        }

        // on set la somme des valeurs dans un objet LongWritable
        result.set(temp);
        // on �crit une nouvelle paire contenant la cl� ainsi que la somme des valeurs ayant cette m�me cl�
        context.write(key, result);
    }
}