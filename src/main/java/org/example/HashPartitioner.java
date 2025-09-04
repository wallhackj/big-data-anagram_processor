package org.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;


public class HashPartitioner extends Partitioner<Text, Text> {
    @Override
    public int getPartition(Text text, Text text2, int i) {
        int hash = text.hashCode();
        return (hash & Integer.MAX_VALUE) % i;
    }
}
