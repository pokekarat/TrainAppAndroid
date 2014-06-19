package com.example.trainapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FileMgr {

	public static void saveSDCard(String fileName, String input){
		
		// write on SD card file data in the text box
		try {
			
			File root = Environment.getExternalStorageDirectory();
			String folder = "/sdcard/semionline"; //root.getAbsolutePath()+"/semionline";
			
			File dir = new File(folder);
			
			if(!dir.isDirectory())
				dir.mkdir();
			
			File file = new File(dir, fileName+".txt");
			
			file.setExecutable(true);
		    file.setReadable(true);
		    file.setWritable(true);
			
		    FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(input);
			myOutWriter.close();
			fOut.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private static double cpuUtil = 0;
	public static String cpuUtilData = "";
	public static double cpuFreqData = 0;
	public static double brightData = 0;
	public static String governData = "";
	public static double voltData = 0;
	public static double tempData = 0;
	public static String status = "";
	public static String memUse = "";
	public static String cacheUse = "";
	public static int txPacket = 0;
	public static int rxPacket = 0;
	
	public static void updateResults(){
		
		RandomAccessFile cpuUtilFile = null;
    	RandomAccessFile cpuFreqFile = null;
    	RandomAccessFile brightFile = null;
    	RandomAccessFile governFile = null;
    	RandomAccessFile voltFile = null;
    	RandomAccessFile tempFile = null;
    	
    	
    	try {
		
    		//Nexus s
    		String cpuUtilPath = "/proc/stat";
			String cpuFrePath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
			String bPath = "/sys/class/backlight/s5p_bl/brightness";
			String gPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
			String vPath = "/sys/class/power_supply/battery/voltage_now";
			String tPath = "/sys/class/power_supply/battery/temp";
			String mPath = "/proc/meminfo";
			String txPath = "/sys/class/net/wlan0/statistics/tx_packets";
			String rxPath = "/sys/class/net/wlan0/statistics/rx_packets";
			
			cpuUtilFile = new RandomAccessFile(cpuUtilPath, "r");
			cpuUtilFile.readLine();
			cpuUtil = CPU.parseCPU(cpuUtilFile.readLine());
			cpuUtilData = String.format("%.2f",cpuUtil);
			
			memUse = CPU.parseMemUse(mPath);
			cacheUse = CPU.parseCacheUse(mPath);
								
			cpuFreqFile = new RandomAccessFile(cpuFrePath, "r");
			cpuFreqData = Double.parseDouble(cpuFreqFile.readLine())/1000;
			
			brightFile = new RandomAccessFile(bPath, "r");
			brightData = Double.parseDouble(brightFile.readLine());
			
			governFile = new RandomAccessFile(gPath, "r");
			governData = governFile.readLine();
			
			voltFile = new RandomAccessFile(vPath, "r");
			voltData = Double.parseDouble(voltFile.readLine())/1000000;
			
			tempFile = new RandomAccessFile(tPath, "r");
			tempData = Double.parseDouble(tempFile.readLine())/10;
			
			txPacket = WiFi.TxPacket(txPath);
			
			rxPacket = WiFi.RxPacket(rxPath);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally { 
            try { 
           
                if (cpuUtilFile != null)
                	cpuUtilFile.close();
                
                if (cpuFreqFile != null)
                	cpuFreqFile.close();
                
                if (brightFile != null)
                	brightFile.close();
                
                if (governFile != null)
                	governFile.close();
                 
            } catch (IOException ex) {
                ex.printStackTrace();
            } 
        } 
    	
	}
	
	public static String readOneLine(String path){

		String result = "";
		
		try 
		{
		
			RandomAccessFile file = new RandomAccessFile(path, "r");		
			result = file.readLine();
			file.close();
			
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
