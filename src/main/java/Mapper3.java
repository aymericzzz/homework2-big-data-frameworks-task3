import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class Mapper3 extends Mapper<LongWritable, Text, Text, LongWritable>{

    // objet LongWritable pour la valeur
    private LongWritable one = new LongWritable(1);
    // objet Text pour la clé
    private Text word = new Text();

    // map(key, eache line, mapper context)
    protected void map(LongWritable key, Text line, Context context) throws IOException, InterruptedException{
        // on sépare chq catégorie délimitée par un ; -> first name;genders;origins;value
        String[] split = line.toString().split(";");
        // on prend la 2e case qui liste un ou les deux genders et on sépare chq gender, qu'on stocke dans genders
        String[] genders = split[1].split(",");

        // à chq fois qu'on a un gender, on monte une paire (gender, one)
        for(String gender:genders){
            try{
                word.set(gender);
                context.write(word, one); // paire (key, value) => (gender, one)
            }catch (InterruptedException e) {
            }
        }
    }
}