package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        if (args.length < 2) {
            System.err.println("Usage: Build Name <inputPath> <outputPath>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://namenode:9000");
        conf.set("yarn.resourcemanager.address", "resourcemanager:8032");
        conf.set("mapreduce.framework.name", "yarn");

        Job job = Job.getInstance(conf, "Anagram Grouping");

        job.setJarByClass(Main.class);
        job.setMapperClass(AnagramMapper.class);
        job.setReducerClass(AnagramReducer.class);
        job.setPartitionerClass(HashPartitioner.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        TextInputFormat.addInputPath(job, new Path(args[0]));
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setNumReduceTasks(1);
        System.exit(job.waitForCompletion(true) ? 0 : 2);
    }
}