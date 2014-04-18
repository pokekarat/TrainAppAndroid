package com.example.trainapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    TextView tv;
    TextView freq;
    TextView batt;
    TextView govern;
    TextView time;
    
    int count;            //number of times process has run, used for feedback
    boolean processing; 
    Button button;
    Intent batteryIntent;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView)findViewById(R.id.text);
		freq = (TextView)findViewById(R.id.freq);
		batt = (TextView)findViewById(R.id.batt);
		govern = (TextView)findViewById(R.id.governor);
		time = (TextView)findViewById(R.id.count);
		
		button = (Button)findViewById(R.id.button);
		button.setOnClickListener(new doButtonClick());
		
		Battery.main = MainActivity.this;
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	}
	
	@SuppressLint("NewApi")
	class doButtonClick implements OnClickListener
	{
	
		ThisTakesAWhile ttaw;
		
		public void onClick(View v)
		{
						
			if(!processing)
			{
				ttaw = new ThisTakesAWhile();
				//ttaw.execute(80);// loop five times
				ttaw.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 70);
				
			} else {
				
				ttaw.cancel(true);
			
			}
		}
	}
	
	class SecondTask extends AsyncTask<Integer, Integer, Integer>{
		
		int numcycles;
		
		@Override
		protected Integer doInBackground(Integer... arg0) 
		{	
			 numcycles=arg0[0];
			 int i = 90;
	    
			 while((int)Battery.getBatteryLevel() > numcycles)
	    	 {
				publishProgress(i);
    	    	CPU.TrainUtil(i);
    	    	i-=10;
	    	 }
			 
			 return 1;
		}
		
		@Override
    	protected void onProgressUpdate(Integer... arg1)
    	{
			time.setText("Expected cpu util = "+ arg1[0]);
    	
    	}
	}
	
	class ThisTakesAWhile extends AsyncTask<Integer, Integer, Integer>{
		
		
		int numcycles;	//total number of times to execute process
		
    	@Override
    	protected void onPreExecute(){    		
    	    //Executes in UI thread before task begins
    	    //Can be used to set things up in UI such as showing progress bar
    	    count=0;	//count number of cycles
    	    processing=true;
    	    tv.setText("Processing, please wait.");
    	    freq.setText("Current CPU frequence.");
    	    
    	    button.setText("STOP");
    	    batt.setText("Batt level = "+ Battery.getBatteryLevel());
    	    
    	    CPU.INIT_BATT_LEVEL = (int)Battery.getBatteryLevel();
    	    CPU.Initial(200000, "powersave");
    	}
    	
    	@Override
    	protected Integer doInBackground(Integer... arg0) {
    	    //Runs in a background thread
    	    //Used to run code that could block the UI
    	    //numcycles=arg0[0];	//Run arg0 times
    	    //Need to check isCancelled to see if cancel was called
    	   /* while(count < numcycles && !isCancelled()) {
    	    	//wait one second (simulate a long process)
		    	SystemClock.sleep(1000);
		    	//count cycles
		    	count++;
		    	//signal to the UI (via onProgressUpdate)
		    	//class arg1 determines type of data sent
		    	publishProgress(count);
    	    } */
    	    
    	    //Train
    	   
    	    new SecondTask().execute(70);
    	    
    	    int i=0;
    	    while(i<1000){
    	    	publishProgress(i);
    	    	++i;
    	    	SystemClock.sleep(1000);
    	    }
    	    
    	   
    	    
    	    //return value sent to UI via onPostExecute
    	    //class arg2 determines result type sent
    	    return count;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer... arg1)
    	{
    	    //called when background task calls publishProgress
    	    //in doInBackground
    	    if(isCancelled()) 
    	    {
    	    
    	    	tv.setText("Cancelled! Completed " + arg1[0] + " processes.");
    	    
    	    } else {
    	    	
    	    	RandomAccessFile cpuUtilFile = null;
    	    	RandomAccessFile cpuFreqFile = null;
    	    	RandomAccessFile cpuGovernFile = null;
    	    	
    	    	String cpuUtil = "";
    	    	String cpuFreq = "";
    	    	String cpuGovern = "";
				
    	    	try {
				
    	    		String cpu_util = "/proc/stat";
					String cpu_freq = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
					String cpu_govern = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
					
					cpuUtilFile = new RandomAccessFile(cpu_util, "r");
					cpuUtilFile.readLine();
					cpuUtil = CPU.parseCPU(cpuUtilFile.readLine());
					
					cpuFreqFile = new RandomAccessFile(cpu_freq, "r");
					cpuFreq = cpuFreqFile.readLine();
					
					cpuGovernFile = new RandomAccessFile(cpu_govern, "r");
					cpuGovern = cpuGovernFile.readLine();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	         
    	    	tv.setText("CPU util = " + cpuUtil + " % ");
    	    	freq.setText("CPU freq = "+ (Integer.parseInt(cpuFreq)/1000) + " MHz ");
    	    	batt.setText("Batt level = "+ Battery.getBatteryLevel());
    	    	govern.setText("CPU governor = "+ cpuGovern);
    	    	
    	    }
    	}
    	
    	@Override
    	protected void onPostExecute(Integer result){
    	    //result comes from return value of doInBackground
    	    //runs on UI thread, not called if task cancelled
    	    tv.setText("Processed " + result + ", finished!");
    	    processing=false;
    	    button.setText("GO");
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
	
	 
    @Override
    public void onBackPressed() {
       
    }

}
