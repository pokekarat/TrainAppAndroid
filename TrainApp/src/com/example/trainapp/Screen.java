package com.example.trainapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.util.Log;
import android.widget.LinearLayout;

public class Screen {
	
	public static LinearLayout li;

	public static void SetBrightness(int bLevel){
		
		
    	RandomAccessFile bFile = null;
    		
    	String bPath = "/sys/class/backlight/s5p_bl/brightness";
		
		
    	try {
		
			bFile = new RandomAccessFile(bPath, "rw");
			bFile.writeBytes(bLevel + "\n");
			
			Log.i("AMOLED.java [SetBrightness]","write bright level = "+bLevel);
		
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
            try { 
           
                if (bFile != null)
                	bFile.close();
                 
            } catch (IOException ex) {
                ex.printStackTrace();
            } 
        } 
	}
	
	
}
