package com.example.trainapp;



import java.io.DataOutputStream;
import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {

    
    
    Spinner spinner1;
    CheckBox cpu_cb;
    CheckBox screen_cb;
    CheckBox gps_cb;
    CheckBox bt_cb;
    
   
   
    Intent batteryIntent;
    int minBatt = 20;
    boolean enable = false;
    Process process;
    String cpuGovern = "";
    String governor = "powersave";
    int bLevel = 255;
    
    LocationManager locMgr;
	GPS locListener;
	
	long trainMode = 0;
	
	
	public LocationManager locateMgr;
	public GPS gps;
	private String extraValue = "0";
	
	public String hwTarget = "";
	
    public Ui ui;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ctx = this;
		ui = new Ui();
		ui.act = MainActivity.this;
		ui.init();
		
		
		ui.button.setOnClickListener(new doButtonClick());
		ui.button.setEnabled(true);
		
		cpu_cb = (CheckBox)findViewById(R.id.cpu_cb);
		screen_cb = (CheckBox)findViewById(R.id.screen_cb);
		gps_cb = (CheckBox)findViewById(R.id.gps_cb);
		bt_cb = (CheckBox)findViewById(R.id.bluetooth_cb);
		
		setCB();
		
		Battery.main = MainActivity.this;
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(new CustomOnItemSelectedLister());
		
		Screen.SetBrightness(255);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		if (bundle != null) 
		{
		        extraValue = bundle.getString("extraKey");
		        
		        if(extraValue.equals("lcd"))
		        {
		        	screen_cb.setChecked(true);	
		        }
		        	
		}
		    
		//this.startProcess();
		//Audio.Setup();
		
	}
	
	public void setCB(){
		
		cpu_cb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v){
				if(((CheckBox) v).isChecked()){
					hwTarget = "cpu";
					FileMgr.status = "Cpu is checked";
				}
			}
		});
		
		screen_cb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v){
				if(((CheckBox) v).isChecked()){
					hwTarget = "screen";
					FileMgr.status = "Screen is checked.";
				}
			}
		});
		
		bt_cb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v){
				if(((CheckBox) v).isChecked()){
					hwTarget = "bluetooth";
					FileMgr.status = "BT is checked.";
				}
			}
		});
		
		gps_cb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v){
				if(((CheckBox) v).isChecked()){
					
					locateMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
					
					gps = new GPS(locateMgr);
					
					locateMgr.addGpsStatusListener(gps);
					
					locateMgr.requestLocationUpdates( LocationManager.GPS_PROVIDER,
			                     0,   // 3 sec
			                     0.0f, // 10 meters 
			                     gps);
						
					Log.i("GPS.java [startGPS]","is start");
					hwTarget = "gps";
					FileMgr.status = "GPS is checked.";
				}
			}
		});
	}
	
	public class CustomOnItemSelectedLister implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			trainMode = parent.getItemIdAtPosition(position);
			
			FileMgr.status = "Train mode.";
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}
	
	@SuppressLint("NewApi")
	class doButtonClick implements OnClickListener
	{
		public void onClick(View v)
		{
			startProcess();
		}
	}
	
	public void startProcess(){
		
		ExternalMeasureTask externalTask;
		SODTask sodTask;
		
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

 				dos.writeBytes("echo 'ondemand' > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor" + "\n");
 				dos.writeBytes("echo 200000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq" + "\n");
 				dos.writeBytes("echo 200000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq" + "\n");
 				//dos.writeBytes("echo 255 > /sys/class/backlight/s5p_bl/brightness"+"\n");
 				dos.writeBytes("exit\n");
 				dos.flush();
 				dos.close();
 				process.waitFor();
			 
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		} 
 		catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 	    			
		if(!Config.processing)
		{
			
			int choice = (int)trainMode;
			
			switch(choice)
			{
				case 0:
				externalTask = new ExternalMeasureTask(this.extraValue, ui, this.hwTarget);
				externalTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, minBatt);
				break;
				
				case 1:
				sodTask = new SODTask(ui);
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
}

