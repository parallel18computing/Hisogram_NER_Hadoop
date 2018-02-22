package NameEntities;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class NameMapper extends Mapper<Object,Text,Text,IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private String model;

    public void setup(Context context) throws IOException, InterruptedException {
        model = context.getConfiguration().get("model") + "/model.AbstractCharLmRescoringChunker";
        //System.out.println("model = " + model);
    }


    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {

        File modelFile = new File(model);

        String line = value.toString();
        //System.out.println("linea:::: " + line);
        line = line.replaceAll("\\<.*?>",""); // removes tags

        line = line.replaceAll("bit.ly", " ");
        line = line.replaceAll(".com", " ");
        line = line.replaceAll("http", " ");

        line = line.replaceAll("[^A-Za-z0-9]", " ");// removes non alphanumeric characters

        line = line.trim().replaceAll(" +", " "); // removes multiple spaces


        try {
            Chunker chunker = (Chunker) AbstractExternalizable.readObject(modelFile);

            Chunking chunking = chunker.chunk(line);
            Set<Chunk> chunkSet = chunking.chunkSet();
            CharSequence cs = chunking.charSequence();

            for (Chunk chunk : chunkSet) {
                int start = chunk.start();
                int end = chunk.end();
                CharSequence str = cs.subSequence(start,end);
                //double distance = chunk.score();
                String match = chunk.type();
                //System.out.printf("%15s  %15s \n", str, match);
                String name = str.toString();
                if(name.length()>1) {
                    context.write(new Text(str.toString().toLowerCase() + '_' + match), one);
                }


            }



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
