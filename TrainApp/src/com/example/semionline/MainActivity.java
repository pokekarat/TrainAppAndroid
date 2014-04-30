package com.example.semionline;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    TextView cpuUtilTxt;
    TextView cpuFreqTxt;
    TextView battTxt;
    TextView governTxt;
    TextView statusTxt;
    TextView brightTxt;
    TextView gpsTxt;
    TextView sampleTxt;
    
    TextView realUtilTxt1;
    TextView realFreqTxt1;
    TextView realUtilTxt2;
    TextView realFreqTxt2;
    TextView realUtilTxt3;
    TextView realFreqTxt3;
    TextView realUtilTxt4;
    TextView realFreqTxt4;
    
    LinearLayout li;
    
    boolean processing; 
    Button button;
    Intent batteryIntent;
    int minBatt = 20;
    boolean enable = false;
    Process process;
    String cpuGovern = "";
    String governor = "powersave";
    int bLevel = 255;
    
    LocationManager locMgr;
	GPS locListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		li = (LinearLayout)findViewById(R.id.LinearLayout1);
		cpuUtilTxt = (TextView)findViewById(R.id.cpuUtil);
		cpuFreqTxt = (TextView)findViewById(R.id.freq);
		battTxt = (TextView)findViewById(R.id.batt);
		governTxt = (TextView)findViewById(R.id.governor);
		statusTxt = (TextView)findViewById(R.id.status);
		brightTxt = (TextView)findViewById(R.id.bright);
		gpsTxt = (TextView)findViewById(R.id.gps);
		sampleTxt = (TextView)findViewById(R.id.sample);
		
		realUtilTxt1 = (TextView)findViewById(R.id.realUtil1);
		realFreqTxt1 = (TextView)findViewById(R.id.realFreq1);
		realUtilTxt2 = (TextView)findViewById(R.id.realUtil2);
		realFreqTxt2 = (TextView)findViewById(R.id.realFreq2);
		realUtilTxt3 = (TextView)findViewById(R.id.realUtil3);
		realFreqTxt3 = (TextView)findViewById(R.id.realFreq3);
		realUtilTxt4 = (TextView)findViewById(R.id.realUtil4);
		realFreqTxt4 = (TextView)findViewById(R.id.realFreq4);
		
		button = (Button)findViewById(R.id.button);
		button.setOnClickListener(new doButtonClick());
				
		Battery.main = MainActivity.this;
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		li.setBackgroundColor(Color.parseColor("#FFFFFF"));
		AMOLED.li = li;
		
		/*Audio.Setup();
		
		locMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locListener = new GPS();
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, locListener);*/
	}
	
	@SuppressLint("NewApi")
	class doButtonClick implements OnClickListener
	{
	
		ControlTask controlTask;
		
		public void onClick(View v)
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

     				dos.writeBytes("echo 'powersave' > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor" + "\n");
     				dos.writeBytes("echo 100000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq" + "\n");
     				dos.writeBytes("echo 100000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq" + "\n");
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
     	    			
			if(!processing)
			{
				controlTask = new ControlTask();
				controlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, minBatt);
				
			} else {
				
				controlTask.cancel(true);
			
			}
		}
	}
	
	class ControlTask extends AsyncTask<Integer, Integer , Integer>
	{
		
    	@Override
    	protected void onPreExecute()
    	{    		 
    		processing=true;
    	    button.setText("STOP");
    	    Battery.INIT_BATT_LEVEL = Battery.getBatteryLevel();
    	    
    	}
    	
    	HwTrain ht;
    
    	double realCPUutil = 0; int numCPUutil = 0;
    	double realCPUfreq = 0; int numCPUfreq = 0;
        @Override
    	protected Integer doInBackground(Integer... arg0)
    	{
        	ht = new HwTrain();
        	
        	long startTime = System.nanoTime();
        	Log.i("MainActivity.java [doInBackground]","startTime" + startTime);
        	
    	    while(Sample.sample <= 400)
    		{
    	    	if(Sample.sample == 0){
    	    		ht.execute(startTime);
    	    	}
    	    	
    	    	publishProgress(Sample.sample);
    	    	SystemClock.sleep(1000); //Nexus S battery update rate = 1 Hz    	    	
    	    	++Sample.sample;
    		}
    	    
    	    return 0;
    	}
       
       /* Thread mThread;
        @Override
    	protected Integer doInBackground(Integer... arg0) 
    	{
    		
    	    while(Sample.sample < 100)
    		{
    	    	    	    	
    	    	Battery.CURRENT_BATT_LEVEL = Battery.getBatteryLevel();
    	    	
    	    	//Trigger change
    	    	if(Battery.INIT_BATT_LEVEL - Battery.CURRENT_BATT_LEVEL == 1){
    	    		
    	    		Battery.INIT_BATT_LEVEL = Battery.CURRENT_BATT_LEVEL;
    	    		
    	    		
    	    	
    	    		mThread =  new Thread(){
    	    	        @Override
    	    	        public void run(){
    	    	            // Perform thread commands...
    	    	        	for (int i=0; i < 5000; i++)
    	    	        	{
    	    	        		// do something...
    	    	        		try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
    	    	        	}
    	    	          }
    	    	        };

    	    	    // Start the thread.
    	    	    mThread.start(); 
    	    	
    	    	}
    	    	
    	    	publishProgress(Sample.sample);
    	    	++Sample.sample;
    	    	SystemClock.sleep(1000); //Nexus S battery update rate = 1 Hz
    	    }
    	    
    	    return 0;
    	}*/
    	
    	
    	@Override
    	protected void onProgressUpdate(Integer... arg1)
    	{    		
    	       	RandomAccessFile cpuUtilFile = null;
    	    	RandomAccessFile cpuFreqFile = null;
    	    	RandomAccessFile brightFile = null;
    	    	RandomAccessFile governFile = null;
    	    	
    	    	String cpuUtil = "";
    	    	String cpuFreq = "";
    	    	String brightData = "";
    	    	String governData = "";
    	    	
    	    	try {
				
    	    		String cpu_util = "/proc/stat";
					String cpu_freq = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq";
					String bPath = "/sys/class/backlight/s5p_bl/brightness";
					String gPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
					
					cpuUtilFile = new RandomAccessFile(cpu_util, "r");
					cpuUtilFile.readLine();
					cpuUtil = CPU.parseCPU(cpuUtilFile.readLine());
										
					cpuFreqFile = new RandomAccessFile(cpu_freq, "r");
					cpuFreq = cpuFreqFile.readLine();
					
					brightFile = new RandomAccessFile(bPath, "r");
					brightData = brightFile.readLine();
					
					governFile = new RandomAccessFile(gPath, "r");
					governData = governFile.readLine();
										
					/*if(CPU.isStartTrain)
					{
						CPU.realCPUutil += (Double.parseDouble(cpuUtil));
						CPU.realCPUfreq += (Double.parseDouble(cpuFreq)/1000);
						realUtilTxt.setText(CPU.realCPUutil+"");
						realFreqTxt.setText(CPU.realCPUfreq+"");
					}*/
					
					
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
    	           	    	
    	    	sampleTxt.setText("# sample = " + Sample.sample);
    	    	governTxt.setText("Govern name = "+ governData);
    	    	cpuUtilTxt.setText("CPU util = " + cpuUtil + " % ");
    	    	int retFreq = (Integer.parseInt(cpuFreq)/1000);
    	    	cpuFreqTxt.setText("CPU freq = "+ retFreq + " MHz ");
    	    	battTxt.setText("Batt level = "+ Battery.CURRENT_BATT_LEVEL);
    	    	//brightTxt.setText("Sample data = "+ saveSample.toString());
    	    	
    	    	int offset = arg1[0]/100;
    	    	
    	    	if(arg1[0] >= ((offset*100)+40) && arg1[0] < ((offset*100)+60)){
    	    		++this.numCPUutil;
    	    		this.realCPUutil += Double.parseDouble(cpuUtil);
    	    	}
    	    	
    	    	if(arg1[0]==((offset*100)+60)){
    	    		
    	    		if(offset == 0){
    	    			realUtilTxt1.setText(String.format("%.2f", (realCPUutil/this.numCPUutil)));
    	    			realFreqTxt1.setText(""+retFreq);
    	    		}else if(offset == 1){
    	    			realUtilTxt2.setText(String.format("%.2f", (realCPUutil/this.numCPUutil)));
    	    			realFreqTxt2.setText(""+retFreq);
    	    		}else if(offset == 2){
    	    			realUtilTxt3.setText(String.format("%.2f", (realCPUutil/this.numCPUutil)));
    	    			realFreqTxt3.setText(""+retFreq);
    	    		}else if(offset == 3){
    	    			realUtilTxt4.setText(String.format("%.2f", (realCPUutil/this.numCPUutil)));
    	    			realFreqTxt4.setText(""+retFreq);
    	    		}
    	    		
    	    		this.numCPUutil = 0;
    	    		this.realCPUutil = 0;
    	    	}
    	    	
    	    	if(arg1[0] > ((offset*100)+60) && arg1[0] < ((offset*100)+90)){
    	    		++this.numCPUutil;
    	    		this.realCPUutil += Double.parseDouble(cpuUtil);
    	    	}
    	    	
    	    	if(arg1[0]==((offset*100)+90)){
    	    		if(offset == 0){
    	    			realUtilTxt1.append(" "+String.format("%.2f", (realCPUutil/this.numCPUutil)));
    	    			//realFreqTxt1.setText(""+retFreq);
    	    		}else if(offset == 1){
    	    			realUtilTxt2.append(" "+String.format("%.2f", (realCPUutil/this.numCPUutil)));
    	    			//realFreqTxt2.setText(""+retFreq);
    	    		}else if(offset == 2){
    	    			realUtilTxt3.append(" "+String.format("%.2f", (realCPUutil/this.numCPUutil)));
    	    			//realFreqTxt3.setText(""+retFreq);
    	    		}else if(offset == 3){
    	    			realUtilTxt4.append(" "+String.format("%.2f", (realCPUutil/this.numCPUutil)));
    	    			//realFreqTxt4.setText(""+retFreq);
    	    		}
    	    		this.numCPUutil = 0;
    	    		this.realCPUutil = 0;
    	    	}
    	}
    	
    	
    	@Override
    	protected void onPostExecute(Integer result){
    	    //result comes from return value of doInBackground
    	    //runs on UI thread, not called if task cancelled
    	    cpuUtilTxt.setText("Processed finished!");
    	    processing=false;
    	    button.setText("GO");
    	    //finish();
    	}
    	
    	@Override
        protected void onCancelled() {
            //run on UI thread if task is cancelled
            processing=false;
            button.setText("GO");
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
	
   /* @Override
    public void onPause() {
       super.onPause();
       this.finish();
    }*/

}

