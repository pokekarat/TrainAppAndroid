package com.example.trainapp;

import java.io.DataOutputStream;
import java.io.IOException;


import android.os.SystemClock;
import android.util.Log;


public class CPU {
	
	public static int INIT_BATT_LEVEL;
	public static int BATT_LEVEL;
	public static String cpu_prev = "";
	public static String cpu_cur = "";
	
	public static void Initial(int freq, String governor){
		
		DataOutputStream os = null;
		try {
			
			Process p = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(p.getOutputStream());
			
			os.writeBytes("echo '" + governor + "' > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			os.writeBytes("echo " + freq + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			os.writeBytes("echo " + freq + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			os.writeBytes("exit\n");
			os.flush();
			
			Log.i("CPU.java","Governor"+ governor+" freq = "+freq);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
	        if (os != null) {
	            try {
	                os.close();
	            } catch (IOException e) {
	                Log.d("CPU.java", "Could not close os");
	            }
	        }
	    }
		
	}
	
	public static void TrainUtil(int percent){
		
		int TOTALCYCLE = 50000;
		
		int pause = (100-percent) * (TOTALCYCLE / 100);
		
		while(true){
		
			//Log.i("CPU.java","Init batt level = "+ CPU.INIT_BATT_LEVEL);
			//Log.i("CPU.java","current batt level = "+ Battery.getBatteryLevel());
	    	    			
			long start_usec = System.nanoTime() / 1000;
			long start_sec = start_usec / 1000;
			start_usec += TOTALCYCLE - pause;
			
			while(start_usec > 1000000){
				start_usec -= 1000000;
				start_sec++;
			}
			
			long end_usec = System.nanoTime() / 1000;
			long end_sec = end_usec / 1000;
			while(wakeUp(start_sec,start_usec, end_sec, end_usec)){
				end_usec = System.nanoTime() / 1000;
				end_sec = end_usec / 1000;
			}
			
			SystemClock.sleep(pause / 1000);
			
			Log.i("CPU.java","pause time (ms) = "+pause);
			
			if((CPU.INIT_BATT_LEVEL != (int)Battery.getBatteryLevel()) && (CPU.INIT_BATT_LEVEL < (int)Battery.getBatteryLevel()))
			{
				CPU.INIT_BATT_LEVEL = (int)Battery.getBatteryLevel();
				break;
			}
				
		}
	}
	
	static boolean wakeUp(long s_sec, long s_usec, long e_sec, long e_usec){
		
		if(e_sec > s_sec) return true;
		
		if(e_sec == s_sec && e_usec > s_usec)return true;
		
		return false;
	}
	
	/*data0 =  cpu0.split(" ");
	data1 =  cpu1.split(" ");
	
	double total_cur = 0;
	double total_prev = 0;
	double idle_cur = 0;
	double idle_prev = 0;
	
	for(int i=1; i<=7; i++)
	{	
		total_cur += Double.parseDouble(data1[i]);
		total_prev += Double.parseDouble(data0[i]);
	}
	
	idle_cur = Double.parseDouble(data1[4]);
	idle_prev = Double.parseDouble(data0[4]);*/
	
	static double cpu_prev_total_util = 0;
	static double cpu_prev_idle = 0;
	static double cpu_cur_total_util = 0;
	static double cpu_cur_idle = 0;
	
	public static String parseCPU(String proc)
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
         
		return String.valueOf(String.format("%.2f", diff_util));
				
	}
	
	
}
