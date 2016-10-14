import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by Aymeric on 13/10/2016.
 */
public class Combiner3 extends Reducer<Text, LongWritable, Text, LongWritable> {
    // valeur après reduce
    private LongWritable result = new LongWritable();
    // reduce(key, values, context)
    public void reduce(Text key, Iterable<LongWritable> values, Context context)throws IOException, InterruptedException{
        long temp = 0;
        // on stocke la clé dans un string dont on se servira pour incrémenter un compteur
        String token = key.toString();
        // on lit chaque valeur du tableau values
        // puis, on somme chaque val
        for(LongWritable val: values){
            temp += val.get();
            if(GenderUtils.isAMale(token.charAt(0)))
                context.getCounter(Aggregate3.GenderCount.MALE).increment(1);
            else if(GenderUtils.isAFemale(token.charAt(0)))
                context.getCounter(Aggregate3.GenderCount.FEMALE).increment(1);
        }

        // on set la somme des valeurs dans un objet LongWritable
        result.set(temp);
        // on écrit une nouvelle paire contenant la clé ainsi que la somme des valeurs ayant cette même clé
        context.write(key, result);
    }
}
