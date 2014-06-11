package com.example.trainapp;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

class HwTrainForSOD extends AsyncTask<Long, String, Integer>
{
				
		public void killProcess() throws InterruptedException{
			CPU.killTrainApp();
		}
		
		@Override
    	protected void onPreExecute()
    	{   
			
    	}
		
		public void SODTrain(){
			
		}
		
		@Override
		protected Integer doInBackground(Long... arg0) 
		{	
			   
			String governor = "powersave";
	    	String freqs[] = {"200000","400000","800000","1000000"};
	    	int util[] = {0,100};
	    	
	    	int fInx = 0;
	    	int uInx = 0;
	    	
				
			 while(true)
			 {
			    	
				 int index = Config.sample % 1020;
		    	 if(index == 0 )
		    	 {
		    		 Screen.SetBrightness(0);
		    		 CPU.setData("powersave" , "200000");
		    		 
		    	 }else if(index == 60){
		    		 
		    		 //AMOLED.SetBrightness(255);
		    		 CPU.setData("powersave" , freqs[fInx / 4]);
		    		 CPU.setUtil(util[uInx]);
		    		 
		    	 }else if(index == 960){
		    		 
		    		 CPU.killTrainApp();
		    		 //AMOLED.SetBrightness(2);
		    		 CPU.setData("powersave" , "200000");
		    		
		    	
		    	 }else if(index == 1019){
		    		 //AMOLED.SetBrightness(255);
		    		 ++fInx;
		    		 uInx = fInx / 4;
		    		 
		    		 if(fInx == 8 && uInx == 2)
		    		 {
		    			 break;
		    		 }
		    	 }
		    	 
		    	 SystemClock.sleep(1000);
		    	
		    }
				
			
			 
			 return 1;
		}
		
		@Override
    	protected void onProgressUpdate(String... arg1)
    	{
			Screen.li.setBackgroundColor(Color.parseColor(arg1[0]));
    	}
		
		int resultFromCpuTask = 0;   
		@Override
    	protected void onPostExecute(Integer result)
		{
			resultFromCpuTask = result;
    	}
		
		@Override
        protected void onCancelled() {
			//time.setText("Status = This task is cancelled");
			int x = 0;
        }
	}