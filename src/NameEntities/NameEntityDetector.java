package NameEntities;

import java.io.*;
import java.util.*;

import Histogram.HistogramDrawer;
import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class NameEntityDetector extends Configured implements Tool {


    private static int datasetNum = 3;
    private static int numRepetition;
    private static int numDataHistogram = 10;

    private static String[] texts;

    private static void loadDatasets(String path) throws IOException{

        for(int i=1; i<=datasetNum * numRepetition;i++) {

            int num = i%datasetNum + 1;
            String filename =  path + "/Dataset" + num + ".xml";
            System.out.println(filename);
            BufferedReader br = new BufferedReader((new FileReader((filename))));
            br.mark(0);
            StringBuilder text = new StringBuilder();
            String line = br.readLine();

            while(line != null) {
                text.append(line);
                line = br.readLine();
            }

            texts[i-1] = text.toString();
        }


    }

    private static void saveTweets(String path) throws IOException{
        for(int i = 0; i < texts.length; i++) {
            int num = i + 1;
            String location = path + "/dataset" + num + ".xml";
            File file = new File(location);

            FileWriter writer = new FileWriter(file);

            writer.write(texts[i]);
            writer.close();



        }
    }

    public int run(String[] args) throws Exception {

        Configuration conf = getConf();
        String model = args[2];
        conf.set("model", model);
        //System.out.println("model = " + conf.get("model"));

        Job job = Job.getInstance(conf, "nameEntities");
        job.setJarByClass(NameEntityDetector.class);
        job.setMapperClass(NameMapper.class);
        job.setCombinerClass(NameReducer.class);
        job.setReducerClass(NameReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //FileInputFormat.addInputPath(job, new Path("/home/giulia/IdeaProjects/Hadoop_NE/tweets"));
        //FileOutputFormat.setOutputPath(job, new Path("/home/giulia/IdeaProjects/Hadoop_NE/output"));
        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[3]));

        boolean success = job.waitForCompletion(true);
        return success ? 0 : 1;
    }

    private static TreeMap<String,Integer> sortResult(String path) throws IOException {
        HashMap<String,Integer> res = new HashMap<>();
        String filename = path + "/part-r-00000";

        BufferedReader br = new BufferedReader(new FileReader(filename));
        br.mark(0);


        String line = br.readLine();
        while(line != null) {

            String text = line;
            String[] words = text.split("\t");

            res.put(words[0],Integer.parseInt(words[1]));

            line = br.readLine();

        }

        return sortMap(res);

    }

    private static TreeMap<String,Integer> sortMap(HashMap<String,Integer> hm) throws  IOException {
        ValueComparator comparator = new ValueComparator(hm);
        TreeMap<String,Integer> tm = new TreeMap<String,Integer>(comparator);
        tm.putAll(hm);

        return tm;

    }

    private static void drawHistograms(TreeMap<String, Integer> persons, TreeMap<String, Integer> locations, TreeMap<String, Integer> organizations) {
        HistogramDrawer pD = new HistogramDrawer(persons.size());
        HistogramDrawer lD = new HistogramDrawer(locations.size());
        HistogramDrawer oD = new HistogramDrawer(organizations.size());

        int i = -1;

        for(Map.Entry<String, Integer> e : persons.entrySet()) {

            pD.addData(++i, e.getKey(), e.getValue());

        }

        i = -1;

        for(Map.Entry<String, Integer> e : locations.entrySet()) {

            lD.addData(++i, e.getKey(), e.getValue());
        }

        i = -1;

        for(Map.Entry<String, Integer> e : organizations.entrySet()) {

            oD.addData(++i, e.getKey(), e.getValue());
        }

        pD.draw();
        lD.draw();
        oD.draw();

    }

    private static void printHistogram(TreeMap<String, Integer> map, int n){
        System.out.println(" ");
        int max = 0;

        int z=0;
        for(Map.Entry<String, Integer> e : map.entrySet()) {
            if(z < n) {
                String k = e.getKey();
                int m = k.length();
                if (m > max) max = m;
                z++;
            }
        }

        int i=0;
        for(Map.Entry<String, Integer> e : map.entrySet()){
            if(i < n){
                StringBuilder k = new StringBuilder(e.getKey());
                int v = e.getValue();

                int k_len = k.length();

                for(int j = 0; j < max - k_len; j++){
                    k.append(" ");
                }

                StringBuilder bar = new StringBuilder();

                for(int j = 0; j < v; j++){
                    bar.append("*");
                }

                System.out.println(k + " : " + bar + "\t" + "(" + v + ")");
                i++;
            }
        }

        System.out.println(" ");
    }

    private static void saveResults(TreeMap<String, Integer> map, String path) throws IOException {
        File file = new File(path);

        FileWriter writer = new FileWriter(file);

        writer.write(map.toString());


        writer.close();

    }

    private static void saveData(TreeMap<String, Integer> map, String path) throws IOException {
        File file = new File(path);

        FileWriter writer = new FileWriter(file);

        String[] s = path.split("/");
        String name = s[s.length - 2].toUpperCase();



        writer.write(name + " found: " + map.size() + '\n');

        Map.Entry<String,Integer> e = map.firstEntry();
        writer.write("The most frequent is '" + e.getKey() + "', which has been detected " + e.getValue() + " times.");

        writer.close();

    }

    private static void savePartialHistogram(TreeMap<String, Integer> map, String path, int n) throws IOException {
        File file = new File(path);

        FileWriter writer = new FileWriter(file);


        int max = 0;

        int z=0;
        for(Map.Entry<String, Integer> e : map.entrySet()) {
            if(z < n) {
                String k = e.getKey();
                int m = k.length();
                if (m > max) max = m;
                z++;
            }
        }

        int i=0;
        for(Map.Entry<String, Integer> e : map.entrySet()){
            if(i < n){
                StringBuilder k = new StringBuilder(e.getKey());
                int v = e.getValue();

                int k_len = k.length();

                for(int j = 0; j < max - k_len; j++){
                    k.append(" ");
                }

                StringBuilder bar = new StringBuilder();

                for(int j = 0; j < v; j++){
                    bar.append("*");
                }

                writer.write(k + " : " + bar + "\t" + "(" + v + ")" + "\n");
                i++;
            }
        }


        writer.close();

    }

    public static void main(String[] args) throws Exception {

        numRepetition = Integer.parseInt(args[5]);

        texts = new String[datasetNum*numRepetition];

        for(int i = 0; i < args.length; i++) {
            System.out.println("args["+i+"] = " + args[i]);
        }

        loadDatasets(args[0]);

        for(int i=0; i < texts.length;i++) {
            String text = texts[i];

            Chunker chunker = new TweetSelect();
            Chunking chunking = chunker.chunk(text);

            StringBuilder allTweet = new StringBuilder();

            for (Chunk chunk : chunking.chunkSet()) {
                int start = chunk.start();
                int end = chunk.end();
                String tweet = text.substring(start, end);


                allTweet.append(tweet); //.append(System.lineSeparator());

            }

            texts[i] = allTweet.toString();

        }

        saveTweets(args[1]);

        //System.out.println("args: " + args[0]);

        long inizio = System.currentTimeMillis();
        int res = ToolRunner.run(new Configuration(), new NameEntityDetector(), args);
        long fine = System.currentTimeMillis();

        long tempo = fine - inizio;


        if(res == 0) {
            TreeMap<String,Integer> sort_result = sortResult(args[3]);
            //System.out.println(sort_result);

            HashMap<String,Integer> p = new HashMap<>();
            HashMap<String,Integer> l = new HashMap<>();
            HashMap<String,Integer> o = new HashMap<>();


            for(Map.Entry<String, Integer> e : sort_result.entrySet()){
                String key = e.getKey();
                Integer val = e.getValue();

                if(key.endsWith("_PERSON")) {
                    key = key.replace("_PERSON","");
                    p.put(key,val);

                }
                if(key.endsWith("_LOCATION")) {
                    key = key.replace("_LOCATION", "");
                    l.put(key,val);
                }
                if(key.endsWith("_ORGANIZATION")) {
                    key = key.replace("_ORGANIZATION", "");
                    o.put(key,val);
                }

            }

            TreeMap<String,Integer> persons = sortMap(p);
            TreeMap<String,Integer> locations = sortMap(l);
            TreeMap<String,Integer> organizations = sortMap(o);

            //System.out.println(persons);
            //System.out.println(locations);
            //System.out.println(organizations);

            drawHistograms(persons,locations,organizations);

            System.out.println("PERSONS: " + persons.size());
            printHistogram(persons,numDataHistogram);

            System.out.println("LOCATIONS: " + locations.size());
            printHistogram(locations,numDataHistogram);

            System.out.println("ORGANIZATIONS: " + organizations.size());
            printHistogram(organizations,numDataHistogram);


            //PERSONS
            saveResults(persons,args[4] + "/persons/results.txt");
            saveData(persons, args[4] + "/persons/data.txt");
            savePartialHistogram(persons, args[4] + "/persons/histogram.txt", numDataHistogram);

            //LOCATIONS
            saveResults(locations,args[4] + "/locations/results.txt");
            saveData(locations, args[4] + "/locations/data.txt");
            savePartialHistogram(locations, args[4] + "/locations/histogram.txt", numDataHistogram);

            //ORGANIZATIONS
            saveResults(organizations,args[4] + "/organizations/results.txt");
            saveData(organizations, args[4] + "/organizations/data.txt");
            savePartialHistogram(organizations, args[4] + "/organizations/histogram.txt", numDataHistogram);

            System.out.println("\nTempo di esecuzione: " + tempo);

        }


    }


}
