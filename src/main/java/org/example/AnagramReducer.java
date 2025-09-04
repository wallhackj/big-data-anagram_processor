package org.example;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.StringJoiner;

public class AnagramReducer extends Reducer<Text, Text, NullWritable, Text> {
    private final Text out = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, NullWritable, Text>.Context context) throws IOException, InterruptedException {
        StringJoiner sj = new StringJoiner(" ");
        long count = 0;

        for (Text val : values) {
            sj.add(val.toString());
            count++;
            out.set(sj.toString());
            if (count % 100_000 == 0) {
                context.write(NullWritable.get(), out);
                sj = new StringJoiner(" ");
            }

            if (sj.length() > 0) {
                context.write(NullWritable.get(), out);
            }
        }
    }

}
