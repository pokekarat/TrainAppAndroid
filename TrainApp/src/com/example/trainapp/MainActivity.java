package com.example.trainapp;



import java.io.DataOutputStream;
import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView.OnItemSelectedListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity 
{

    Intent batteryIntent;
    int minBatt = 20;
    boolean enable = false;
    Process process;
    String cpuGovern = "";
    String governor = "powersave";
    int bLevel = 255;	
	long trainMode = 0;
	private String extraValue = "0";
	
    public Ui ui;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ui = new Ui();
		ui.init(MainActivity.this);
		ui.button.setOnClickListener(new doButtonClick());
		ui.button.setEnabled(true);
		ui.setCB();		
		ui.spinner1.setOnItemSelectedListener(new CustomOnItemSelectedLister());
		
		WiFi.init(MainActivity.this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Battery.main = MainActivity.this;
		
		/*
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		if (bundle != null) 
		{
		        extraValue = bundle.getString("extraKey");
		        
		        if(extraValue.equals("lcd"))
		        {
		        	screen_cb.setChecked(true);	
		        }    	
		}*/
		    
	}
		
	public class CustomOnItemSelectedLister implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			
			trainMode = parent.getItemIdAtPosition(position);
			FileMgr.status = "Train mode.";
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}

	}
	
	@SuppressLint("NewApi")
	class doButtonClick implements OnClickListener
	{
		public void onClick(View v)
		{
			if(!Config.processing)
				startProcess();
		
				
		}
	}
	
	public void startProcess()
	{
		
		try 
 		{
 			process = Runtime.getRuntime().exec("su");
 			DataOutputStream dos = new DataOutputStream(process.getOutputStream());
 			
 			try {
 				
 				dos.writeBytes("chmod 777 /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq" + "\n");
 				dos.writeBytes("chmod 777 /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq" + "\n");
 				dos.writeBytes("chmod 777 /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq" + "\n");
 				dos.writeBytes("chmod 777 /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor" + "\n");
 				dos.writeBytes("chmod 777 /sys/class/backlight/s5p_bl/brightness"+"\n");

 				String governor = "ondemand";
 				dos.writeBytes("echo '"+governor+"' > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor" + "\n");
 				//dos.writeBytes("echo 800000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq" + "\n");
 				//dos.writeBytes("echo 800000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq" + "\n");
 				dos.writeBytes("echo 255 > /sys/class/backlight/s5p_bl/brightness"+"\n");
 				dos.writeBytes("exit\n");
 				dos.flush();
 				dos.close();
 				process.waitFor();
			 
 			} catch (IOException e) {
 				
 				e.printStackTrace();
 			}
 			catch (InterruptedException e) {
				
				e.printStackTrace();
			}
 		} 
 		catch (IOException e) {
 			
 			e.printStackTrace();
 		}
 	    			
		if(!Config.processing)
		{
			
			int choice = (int)trainMode;
			
			switch(choice)
			{
				case 0:
				
				ExternalMeasureTask externalTask = new ExternalMeasureTask(this.extraValue, ui);
				externalTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, minBatt);
				
				break;
				
				case 1:

				SODTask sodTask = new SODTask(ui);
				sodTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, minBatt);
				
				break;
				
				default: break;
			}
		} 
		else 
		{			
			//controlTask.cancel(true);
		}
	}
}

