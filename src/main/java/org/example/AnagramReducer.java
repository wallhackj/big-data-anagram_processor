package org.example;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.StringJoiner;

public class AnagramReducer extends Reducer<Text, Text, NullWritable, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, NullWritable, Text>.Context context) throws IOException, InterruptedException {
        StringJoiner sj = new StringJoiner(" ");
        for (Text val : values) {
            sj.add(val.toString());
        }
        context.write(NullWritable.get(), new Text(sj.toString()));
    }
}
