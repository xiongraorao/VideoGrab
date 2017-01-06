package com.xrr.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader; 

/**
 * created by raorao 2017/1/4
 * function: extract feature test
 * main args examples: /home/sh/workplace/objectDetection/ssd/caffe/examples/sh_ssd/demo2.py images/dog-2.jpg images/bird-1.jpg
 * @author ubuntu
 *
 */

public class FeatureExtract {
	static class PrintThreadTest extends Thread
	{
		private BufferedReader in;
		
		public PrintThreadTest(BufferedReader in)
		{
			this.in = in; 
		}
		
		public void run()
		{
			 String line;  
			 try
			 {
             while ((line = in.readLine()) != null) {  
                 System.out.println(line);  
             }  
             in.close();
			 }catch(IOException e)
			 {
				 e.printStackTrace();
			 }
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			 try{  
	             System.out.println("start");  
	             String argStr = "python ";
	             for(int i = 0;i < args.length; i++)
	             {
	            	 argStr += args[i]+" ";
	             }
	             Process pr = Runtime.getRuntime().exec(argStr);  
	             
	             BufferedReader in = new BufferedReader(new  
	                     InputStreamReader(pr.getErrorStream()));  
	             new PrintThreadTest(in).start();
	             in = new BufferedReader(new  
	                     InputStreamReader(pr.getInputStream())); 
	             new PrintThreadTest(in).start();
	             pr.waitFor();  
	             System.out.println("end");  
	     } catch (Exception e){  
	                 e.printStackTrace();  
	             }  
	     }  
}