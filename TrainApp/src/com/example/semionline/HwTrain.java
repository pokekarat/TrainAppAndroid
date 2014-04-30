package com.example.semionline;

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

class HwTrain extends AsyncTask<Long, String, Integer>
	{
		
		public void killProcess() throws InterruptedException{
			CPU.KillTrainApp();
		}
		
		@Override
    	protected void onPreExecute()
    	{   
			
    	}
		
		@Override
		protected Integer doInBackground(Long... arg0) 
		{	
			   
			    //////////////////////////Train CPU ////////////////////////////
    	    	/*String governor = "powersave";
    	    	String freqs[] = {"200000","400000","800000","1000000"};

    	    	int offset = 100;
    	    	
    	    	for(int i=0; i<freqs.length; i++)
    	    	{
	    	    	while(true)
	    	    	{
	    	    		if(Sample.sample == (20 + (i*offset)))
	    	    		{
	    	    			AMOLED.SetBrightness(0);
	    	    		}
	    	    		else if(Sample.sample == (25+ (i*offset)))
				    	{
	    	    			AMOLED.SetBrightness(255);
				    		
				    	}
	    	    		else if(Sample.sample == (30 + (i*offset)))
	    	    		{
	    	    			CPU.SetData(governor , freqs[i]);
				    		
	    	    		}
				    	else if(Sample.sample == (60+ (i*offset)))
				    	{
				    		//CPU.KillTrainApp();
				    		//CPU.SetData(governor, freqs[i]);
				    		CPU.StartTrainUtil(100);
				    	}
				    	else if(Sample.sample == (90+ (i*offset)))
				    	{	
				    		    		
				    		//kill max u and set min f
				    		CPU.KillTrainApp();
				    		
				    		//CPU.SetData(governor , minFreq);
				    	}
				    	else if(Sample.sample == (95+ (i*offset))){
				    		AMOLED.SetBrightness(0);
				    		
				    	}
				    	else if(Sample.sample == (100+ (i*offset))){
				    		AMOLED.SetBrightness(255);
				    		break;
				    	}
					    
					    //while((System.nanoTime() - startTime) < 1000000000);
					    SystemClock.sleep(1000);
	    	    	}
    	    	}*/
				 
				 ////////////////////////// Train AMOLED ////////////////////////////
				 String[] colors = {"#000000", "#FF0000", "#00FF00", "#0000FF", "#FFFFFF"};
				 
				 
				 for(int j=0; j<colors.length; j++)
				 {
					 //set color
					 publishProgress(colors[j]);
					 
					 for(int i=5; i<=255; i+= 25)
					 {
						 AMOLED.SetBrightness(i);
						 SystemClock.sleep(1000);
					 }
				 }
				 			 
				 //////////////////////////Train AUDIO ////////////////////////////
				 /*
				 Audio.PlayMusic();
				 Battery.Wait();
				 Audio.StopMusic();
				 */
				 
				 //////////////////////////Train GPS ////////////////////////////
				 /*int loop = 0;
				 while(loop<50)
				 {		
					 publishProgress(locListener.locateStr);
					 ++loop;
					 SystemClock.sleep(5000);
				 }*/
			 
			 return 1;
		}
		
		@Override
    	protected void onProgressUpdate(String... arg1)
    	{
			AMOLED.li.setBackgroundColor(Color.parseColor(arg1[0]));
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