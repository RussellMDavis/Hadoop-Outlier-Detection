package org.apache.hadoop.examples;

import java.io.IOException;
import java.lang.Object.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.awt.geom.*;
import java.util.ArrayList;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;


public class MR2New {

	public static class KNNMapper extends Mapper<Object, Text, Text, Text> {
	
	double maxDist;
    
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		double getMaxDist;
		Configuration conf = context.getConfiguration();
		 getMaxDist = Double.parseDouble(conf.get("maxDist", "10.55"));
		maxDist = getMaxDist;
    }
    
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
	 
	int gridIDInt;
	String flag = "";


	List<Integer> span = Arrays.asList(-1, 0, 1);
	
	int gridSweep = (int)maxDist / 1000 + 1;
	 
	
    String line = value.toString();
	String[] tokens = line.split(",");
	String xPtStr = tokens[0];
	String yPtStr = tokens[1];
    int xPt = Integer.parseInt(xPtStr);
	int yPt = Integer.parseInt(yPtStr);

	int gridIDX =  (xPt < 1000) ? 1 : (String.valueOf(Math.abs((long)xPt)).charAt(0) + 1);
	int gridIDY =  (yPt < 1000) ? 1 : (String.valueOf(Math.abs((long)yPt)).charAt(0) + 1);
    
	String gridIDStr = ("Y"+Integer.toString(gridIDY) + " X" + Integer.toString(gridIDX));
	
	context.write(new Text(gridIDStr), new Text(line + ",Full"));
	int suppIDX;
	int suppIDY;	
	for(int i : span){
		if(((gridIDX + i) < 1) || ((gridIDX + i) > 10)){
			continue;
		}
		for(int iNest : span){
			if(((gridIDY + iNest) < 1) || ((gridIDY + iNest) > 10)){
				continue;
			}
			suppIDX = gridIDX + i;
			suppIDY = gridIDY + iNest;
			flag = "Supp";
			if(i == 0 && iNest == 0){
			}
			else{
				gridIDStr = ("Y"+Integer.toString(suppIDY) + " X" +	Integer.toString(suppIDX));
				context.write(new Text(gridIDStr), new Text(line + ",Supp"));
			}
		}
	}
		
		
	

	}
}

public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		
		ArrayList<String> pointList = new ArrayList<String>();
       	        Double pointDist;
		double pointX;
		double pointY;
		double compPointX;
		double compPointY;
		int kValue = 6;
		
		for (Text val: values)
		{
			String vString = val.toString();
			pointList.add(vString);
			context.write(key, new Text(vString));
		}
		for (String point : pointList){
			ArrayList<String> NNeighbors = new ArrayList<String>();
			List<String> kNN = new ArrayList<String>();

			String[] pVector = point.split(",");
			if(pVector[2] == "Supp"){
				continue;
			}
			pointX = Double.parseDouble(pVector[0]);
			pointY = Double.parseDouble(pVector[1]);
			String pointOut = pVector[0] + "," + pVector[1];	
			for(String compPoint : pointList) {
				String[] cPVector = compPoint.split(",");
				compPointX = Double.parseDouble(cPVector[0]);
				compPointY = Double.parseDouble(cPVector[1]);
				pointDist = Point2D.distance(pointX, pointY, compPointX, compPointY);
				String NNOut = pointDist + "," + pointOut;
				if(pointDist > 0) {
					NNeighbors.add(NNOut);
				}
			}
			
			Collections.sort(NNeighbors, new Comparator<String>() {
					public int compare(String str1, String str2) {
						String[] str1Vector = str1.split(",");
						String[] str2Vector = str2.split(",");	
						return Double.compare(Double.parseDouble(str1Vector[0]),Double.parseDouble(str2Vector[0]));
					}
			});
			
			kNN = NNeighbors.subList(0, kValue);
			StringBuffer sb = new StringBuffer();
			for (String s: kNN) {
				String[] kNNVector = s.split(",");
				sb.append(kNNVector[1]);
				sb.append(" ");
			}
			String kNNStr = sb.toString();
			//context.write(new Text(pointOut), new Text(kNNStr)); 

			
		}
			
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

		job.setJarByClass(MR2New.class);
		job.setReducerClass(Reduce.class);
    		job.setMapperClass(KNNMapper.class);
    		job.setMapOutputKeyClass(Text.class);
    		job.setMapOutputValueClass(Text.class);  
    		job.setOutputKeyClass(Text.class);
    		job.setOutputValueClass(Text.class);

    
		FileInputFormat.addInputPath(job, new Path(args[0]));
   		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		conf.set("maxDist", args[2]);


		System.exit(job.waitForCompletion(true) ? 0 : 1);
		conf.set("mapred.task.timeout", "6000000");
	}
}
