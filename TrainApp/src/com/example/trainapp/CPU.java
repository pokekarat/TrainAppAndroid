package com.example.trainapp;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


@SuppressLint("DefaultLocale")
public class CPU {
	
	
	//public static int BATT_LEVEL;
	public static String cpu_prev = "";
	public static String cpu_cur = "";
	public static Process p = null;
	public static boolean isDataSet = false;
	public static double realCPUutil = 0.0;
	public static double realCPUfreq = 0.0;
	public static boolean isStartTrain = false;
	public static Thread myThread = new Thread();
	
	public static Process strcProcess;
	public static void killTrainApp()
	{
		
		Process strcProcessKill=null;
		try{
			strcProcessKill = Runtime.getRuntime().exec("su");
			DataOutputStream dos = new DataOutputStream(strcProcessKill.getOutputStream());
			
			try {
				
				dos.writeBytes("killall strc "+"\n");
				dos.writeBytes("exit\n");
				dos.flush();
				dos.close();
				strcProcessKill.waitFor();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			
	    }
		catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
		
		//strcProcessKill.destroy();
		//strcProcess.destroy();
	}
	
	public static void setUtil(int percent){
			
		 try{
				strcProcess = Runtime.getRuntime().exec("su");
				DataOutputStream dos = new DataOutputStream(strcProcess.getOutputStream());
				
				try {
					
					dos.writeBytes("./data/local/tmp/strc " + percent + "\n");
					dos.writeBytes("exit\n");
					dos.flush();
					dos.close();
					//strcProcess.waitFor();
					Log.i("CPU.java [StartTrainApp]","Called strc "+percent);
			 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		    }
			catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	}
	
	public static boolean setData(String gov, String freq){

		try 
 		{
			
 			Process process2 = Runtime.getRuntime().exec("su");
 			DataOutputStream dos = new DataOutputStream(process2.getOutputStream());
 			
 			try {
 				
 				//dos.writeBytes("echo '"+gov+"' > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor" + "\n");
 				dos.writeBytes("echo "+freq+" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"  + "\n");
 				dos.writeBytes("echo "+freq+" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"  + "\n");
 				dos.writeBytes("exit\n");
 				dos.flush();
 				dos.close();
 				
 				try
 				{
 					process2.waitFor();
 					
 					if(process2.exitValue() != 255){
 						Log.i("CPU.java [SetData]","root & set freq = "+freq);
 					}else{
 						Log.i("CPU.java [SetData]","Not root");
 					}
 				}
 				catch (InterruptedException e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}
 				
			 
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 			
 		} 
 		catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
		
		
		return true;
	}
	
	public static boolean setGovernor(String govName)
	{
		RandomAccessFile govFile = null;		
    	String govPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
			
    	try {
		
    		govFile = new RandomAccessFile(govPath, "rw");
    		govFile.writeBytes(govName + "\n");
    		govFile.close();
			
			Log.i("CPU.java [SetGovernor]","write file"+ govName);
			//Toast.makeText(null, max_freq, Toast.LENGTH_LONG).show();
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
            try { 
           
                if (govFile != null)
                	govFile.close();
                 
            } catch (IOException ex) {
                ex.printStackTrace();
            } 
        } 
    	
    	return true;
	}
	
	public static boolean setFreq(String min, String max){
	
    	RandomAccessFile minFreq = null;
    	RandomAccessFile maxFreq = null;
    		
    	String min_freq = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
		String max_freq = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
		
    	try {
		
			minFreq = new RandomAccessFile(min_freq, "rw");
			minFreq.writeBytes(min + "\n");
			
			minFreq.close();
			maxFreq = new RandomAccessFile(max_freq, "rw");
			maxFreq.writeBytes(max + "\n");
			maxFreq.close();
			
			Log.i("CPU.java [SetFreq]","write file"+min_freq);
			//Toast.makeText(null, max_freq, Toast.LENGTH_LONG).show();
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
            try { 
           
                if (minFreq != null)
                	minFreq.close();
                
                if (maxFreq != null)
                	maxFreq.close();
                 
            } catch (IOException ex) {
                ex.printStackTrace();
            } 
        } 
    	
    	return true;
	}
	
	/*
	public static void setUtil(int percent)
	{
		
		int TOTALCYCLE = 50000;
		
		int pause = (100-percent) * (TOTALCYCLE / 100);
		
		while(!Thread.currentThread().isInterrupted()){
		
			//Log.i("CPU.java","Init batt level = "+ CPU.INIT_BATT_LEVEL);
			//Log.i("CPU.java","current batt level = "+ Battery.getBatteryLevel());
	    	    			
			long start_usec = System.nanoTime() / 1000;
			long start_sec = start_usec;
			start_usec += TOTALCYCLE - pause;
			
			while(start_usec > 1000000){
				start_usec -= 1000000;
				start_sec++;
			}
			
			long end_usec = System.nanoTime() / 1000;
			long end_sec = end_usec;
			while(wakeUp(start_sec,start_usec, end_sec, end_usec)){
				end_usec = System.nanoTime() / 1000;
				end_sec = end_usec;
			}
			
			//try {
			//	Thread.sleep(pause);
			/*} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Thread.currentThread().interrupt();
				return;
			}*/
			
			//if(Battery.Wait()) break;
	
		//}
	//}
	
	
	public static synchronized void stopThread()
	{
		/*
	    if (myThread != null)
	    {
	        myThread.interrupt();
	    }*/
	    
	    try{  
	    	Thread.currentThread().interrupt();  
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	public static synchronized void startThread()
	{
	    if (myThread.isInterrupted())
	    {
	    	myThread = null;
	    	myThread = new Thread();
	        myThread.start();
	    }
	}
	
	static boolean wakeUp(long s_sec, long s_usec, long e_sec, long e_usec){
		
		if(e_sec > s_sec) return true;
		
		if(e_sec == s_sec && e_usec > s_usec)return true;
		
		return false;
	}
	
	static double cpu_prev_total_util = 0;
	static double cpu_prev_idle = 0;
	static double cpu_cur_total_util = 0;
	static double cpu_cur_idle = 0;
	
	public static double parseCPU(String proc)
	{
		
		String[] cpu_cur_arr = proc.split(" ");
		
		for(int i=1; i<=7; i++)
		{	
			cpu_cur_total_util += Double.parseDouble(cpu_cur_arr[i]);
		}
		
		cpu_cur_idle = Double.parseDouble(cpu_cur_arr[4]);
		
		double diff_idle = cpu_cur_idle - cpu_prev_idle;
		double diff_total = cpu_cur_total_util - cpu_prev_total_util;
		double diff_util = (1000 * (diff_total - diff_idle) / diff_total) / 10;
		
		cpu_prev_idle = cpu_cur_idle;
        cpu_prev_total_util = cpu_cur_total_util;
        cpu_cur_total_util = 0;
        cpu_cur_idle = 0;
         
		return diff_util; // String.valueOf(String.format("%.2f", diff_util));
				
	}	
	
	
	public static String parseCacheUse(String path)
	{
		String result = "";
		
		try 
		{
		
			RandomAccessFile memFile = new RandomAccessFile(path, "r");
			
			String line = "";
			
			while((line = memFile.readLine()) != null)
			{
				//result += line + "\n";			
				if(line.contains("Cached"))
				{
					String[] data = line.split(" ");
					for(int i=0; i<data.length; i++)
					{
						if(Util.isInteger(data[i])){
							result = data[i];
							break;
						}
					}
					break;
				}
			}
			 
			memFile.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return result;
		
	}
	
	public static String parseMemUse(String path)
	{
		
		String result = "";
		
		try 
		{
		
			RandomAccessFile memFile = new RandomAccessFile(path, "r");
			
			String	totalStr = memFile.readLine();
			String  freeStr = memFile.readLine();
			
			String[] x = totalStr.split(" ");
			String[] y = freeStr.split(" ");
			
			double total = 0.0;
			double free = 0.0;
			
			for(int i=0; i<x.length; i++)
			{
				if(Util.isInteger(x[i])){
					total = Double.parseDouble(x[i]);
					break;
				}
			}
			
			for(int j=0; j<y.length; j++)
			{
				if(Util.isInteger(y[j])){
					free = Double.parseDouble(y[j]);
					break;
				}
			}
			
			
			double use = ((total - free )/ total ) * 100;
			
			result = String.valueOf(String.format("%.2f", use));
						 
			memFile.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return result;
		
	}
	
	
}
