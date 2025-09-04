package org.example;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AnagramMapper extends Mapper<LongWritable, Text, Text, Text> {
    private final Text val = new Text();
    private final Text finKey = new Text();

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        String str = value.toString();
        if (str.isEmpty()) return;

        char[] chars = str.toCharArray();
        int[] arr = new int[26];

        for (char c : chars) {
            if (isAlphaNum(c)) {
                continue;
            }
            if ('A' <= c && 'Z' >= c) c |= (1 << 5);
            arr[c - 'a']++;
        }

        StringBuilder sb = new StringBuilder(26 * 3);
        for (int i = 0; i < 26; i++) {
            if (i > 0) sb.append('-');
            sb.append(arr[i]);
        }

        finKey.set(sb.toString());
        val.set(str);
        context.write(finKey, val);
    }

    private static boolean isAlphaNum(char c) {
        return ('a' > c || 'z' < c) && ('0' > c || '9' < c);
    }
}
