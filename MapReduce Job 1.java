package org.apache.hadoop.examples;

import java.lang.Math;
import java.io.IOException;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;

public class MR1 {

	public static class PtMapper extends Mapper<Object, Text, Text, Text> {
     protected void setup(Context context) throws IOException, InterruptedException {
      int numReduceTasks;
      Configuration conf = context.getConfiguration();
		      	numReduceTasks = Int.parseInt(conf.get("numReduceTasks"));
     }
    
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			int hashPartInt;
      String line = value.toString();
			String[] tokens = line.split(",");
			String xPt = tokens[0];
			String yPt = tokens[1];
      hashPartInt = getPartition(xPt, yPt, numReduceTasks)
      String hashPart = String.valueOf(hashPartInt);
			context.write(new Text(hashPart), new Text(xPt, "," , yPt); 
		}
}

public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			double maxDist = -1;
      double distance = -1
      ArrayList<String> pointList = new ArrayList<String>();
      for (Text val : values) {
         	String vString = val.toString();
          pointList.add(vString);
      }
      
     for (int i = 0; i < pointList.size(); i++) {
        for (int j = i+1; j < pointList.size(); j++) {
              String[] p1 = list.get(i).split(","); //.split might not work
              String[] p2 = list.get(j).split(",";
              int x1 = Integer.parseInt(p1[0]);
              int y1 = Integer.parseInt(p1[1]); 

              int x2 = Integer.parseInt(p2[0]); 
              int y2 = Integer.parseInt(p2[1]); 

            distance = Math.hypot(x1-x2, y1-y2);
            if (distance > maxDist) {
					    maxDist = distance;
            }
            
     context.write(key, new doubleWritable(maxDist)); 
}}

			}
}


	private static void setTextOutputFormatSeparator(Configuration conf, String separator) {
		conf.set("mapred.textoutputformat.separator", separator); // Prior to Hadoop 2 (YARN)
		conf.set("mapreduce.textoutputformat.separator", separator); // Hadoop v2+ (YARN)
		conf.set("mapreduce.output.textoutputformat.separator", separator);
		conf.set("mapreduce.output.key.field.separator", separator);
		conf.set("mapred.textoutputformat.separatorText", separator); // ?
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
	
		Configuration conf = new Configuration();
		setTextOutputFormatSeparator(conf, ",");
		Job job = Job.getInstance(conf);

		job.setJarByClass(MR1.class);
		job.setReducerClass(Reduce.class);
    job.setNumReduceTasks(10);
    
    job.setOutputKeyClass(Text.class);
    j.setOutputValueClass(DoubleWritable.class); 
    
		FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		FileOutputFormat.setOutputPath(job, outputPath);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		conf.set("mapred.task.timeout", "6000000");
	}
}
