package com.example.trainapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;

class HwTrainForExternal extends AsyncTask<Integer, String, Integer>
{
	    public int currentStep = 0;
	    public int totalStep = 0;
		public double position = 0;
		int countTime = 0;
		
		double test = 0;
		public String hwName = "";
	
		boolean isLoopEnd;
		boolean isBreak = false;
		boolean isMainBreak = false;
		boolean isStartTrain = false;
		boolean isCpuCheck = false;
		boolean isScreenCheck = false;
		boolean isGPSCheck = false;
		
		int currentUtil = 0;
		int currentFreq = 0;
		
		int delay = 30;
		int offset = 100;
		
		public String setUtil = "-1";
		
		public HwTrainForExternal(String data, String hw)
		{
			setUtil = data;
			hwName = hw;
		}
		
		public void killProcess() throws InterruptedException
		{
			CPU.killTrainApp();
		}
		
		@Override
    	protected void onPreExecute()
    	{   
			if(hwName == "wifi"){
				
				WiFi.createTestFile();
				
			}
    	}

		public void CPUTrain()
		{
			Screen.SetBrightness(255);
			
			String governor = "powersave";
	    	int freqs[] = {100000, 200000,400000,800000,1000000};
	    	int utils[] = {0}; //,10,20,30,40,50,60,70,80,90,100};
	    	
	    	utils[0] = Integer.parseInt(setUtil);
	    	
	    	totalStep = utils.length * freqs.length;

		    //////////////////////////Train CPU ////////////////////////////
	    	
	    	for(int u=0; u<utils.length; u++)
	    	{
	    		
	    		CPU.setUtil(utils[u]);
	    		
	    		currentUtil = utils[u];
	    		  		
	    		for(int f=0; f<freqs.length; f++)
	    		{
	    			++currentStep;
	    			currentFreq = freqs[f]/1000;	
	    			isLoopEnd = false;
	    			
	    			hwName = "cpu_"+ currentUtil +"_"+ currentFreq;
	    			CPU.setData(governor , ""+freqs[f]);
	    			
	    			
	    			//Break 30 seconds
		    		SystemClock.sleep(delay*1000);
		    		Screen.SetBrightness(5);
		    		
		    		isStartTrain = true;
		    		
		    		while(!isLoopEnd)
	    	    	{
	    	    			    	    		
	    	    		if(position == offset) //Math.floor(0.99f * offset))
	    	    		{
	    	    			position = 0;
	    	    			isLoopEnd = true;
	    	    		}
	    	    		
	    	    		SystemClock.sleep(1000);
	    	    		++position;
	    	    	}
	    	    	
	    	    	Screen.SetBrightness(255);
	    		}
	    		
	    		//save file
	    		isBreak = true;
	    		isMainBreak = true;
	    		Screen.SetBrightness(255);
	    	
	    		CPU.killTrainApp();
	    	}
	    	
	    	currentStep = 0;
	    	
	    	CPU.setData(governor , "1000000");	
	    	isMainBreak = true;
	    	
	    	//End CPU training
		}
		
		int brightData = 0;
		int trainDuration = 100; //seconds
		public void ScreenTrain()
		{
		
				 ////////////////////////// Train AMOLED ////////////////////////////
				 //String[] colors = {"#000000", "#FF0000", "#00FF00", "#0000FF", "#FFFFFF"};
				
			     //for(int c=0; c<colors.length; c++)
				 //{
				 
				 int[] brightness = {0,25,50,75,100,125,150,175,200,225,255};
				 
				 this.totalStep = brightness.length;
				 SystemClock.sleep(30000);
				 for(int b=0; b<=255; b+= 25)
				 { 
					 SystemClock.sleep(30000);
					 hwName = "screen_"+b;
		    		 Screen.SetBrightness(b);
		    		 brightData = b;
		    		 
		    		 while(trainDuration > 0)
		    		 {		
		    			 SystemClock.sleep(1000);
		    			 --trainDuration;
		    		 }
		    		 
		    		 trainDuration = 30;
		    		 ++currentStep;
		    		 Screen.SetBrightness(0);
				 }
		    		
				 Screen.SetBrightness(255);
		    	 isBreak = true;
		    	 isMainBreak = true;
			 
		}
		
		public void GPSTrain()
		{
				//new GPS().startGPS();	
				hwName = "gps";
		}
		
		public void BluetoothTrain(){
			
			BT.init();
			
			//Each run 7 times
			int numOfTest = 2;
			
			isStartTrain = true;
			hwName = "bluetooth_base";
			
			//1. Train base power, 0 bright, airplane
			for(int i = 0; i < numOfTest; i++)
			{
				SystemClock.sleep(30000);
				
				//BT.testBase();
				Screen.SetBrightness(0);
				
				totalStep = 20;
				
				while(currentStep < totalStep )
	    		{		
	    			 SystemClock.sleep(1000);
	    			 ++currentStep;
	    		}
	    		 
				//isStartTrain=false;
				//isBreak = true;
				currentStep = 0;
	    		Screen.SetBrightness(255);
			}
			
			isBreak = true;
			isMainBreak = true;
			
			//2. Train on -> non-discoverable mode
			//BT.testNonDiscovery();
			
			//3. Train on -> discoverable mode
			//4. Train send file
			//5. Train recv file
		}
		
		public void wifiTrain(){
		
			int numOfTest = 7;
			
			WiFi.initToServer();
			
			for(int i=1; i<=numOfTest; i++){
				
				
				Screen.SetBrightness(0);
				
				
				Util.delay(10000);
				
				
				isStartTrain = true;
				
				
				hwName = "wifi"+i;
				
				
				WiFi.sendFileToServer();
				
				
				isBreak = true;
				
				
				Screen.SetBrightness(255);
				
				
				Util.delay(30000);
			}
			
			WiFi.closeToServer();
			
			isMainBreak = true;
		}
		
		@Override
		protected Integer doInBackground(Integer... arg0) 
		{			   
			if(hwName.equals("cpu"))
			{
				this.CPUTrain();
			}
			else if(hwName.equals("screen"))
			{
				this.ScreenTrain();
			}
			else if(hwName.equals("gps"))
			{
				this.GPSTrain();
			}
			else if(hwName.equals("bluetooth")){
				this.BluetoothTrain();
			}
			else if(hwName.equals("wifi")){
				this.wifiTrain();
			}
			else {
				
			}
			
			SystemClock.sleep(1000);
			 
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