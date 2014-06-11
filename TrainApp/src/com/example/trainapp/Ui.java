package com.example.trainapp;

import android.widget.Button;
import android.widget.TextView;

public class Ui {

	public MainActivity act;
	public TextView cpuUtilTxt;
    public TextView cpuFreqTxt;
    public TextView battTxt;
    public TextView governTxt;
    public TextView statusTxt;
    public TextView brightTxt;
    public TextView gpsTxt;
    public TextView sampleTxt;
    public TextView cpuStatusTxt;
    public TextView screenStatusTxt;
    public TextView bluetoothTxt;
    
    public boolean isCpuChk = false;
    public boolean isScreenChk = false;
    public boolean isGPSChk = false;
    public boolean isBtChk = false;
    
    public Button button;
    
    public void Ui(){}
    
    public void init(){
    	
    	cpuUtilTxt = (TextView)act.findViewById(R.id.cpuUtil);
		cpuFreqTxt = (TextView)act.findViewById(R.id.freq);
		battTxt = (TextView)act.findViewById(R.id.batt);
		governTxt = (TextView)act.findViewById(R.id.governor);
		statusTxt = (TextView)act.findViewById(R.id.status);
		brightTxt = (TextView)act.findViewById(R.id.bright);
		gpsTxt = (TextView)act.findViewById(R.id.gpsStatus);
		sampleTxt = (TextView)act.findViewById(R.id.sample);
		cpuStatusTxt = (TextView)act.findViewById(R.id.cpuStatus);
		screenStatusTxt = (TextView)act.findViewById(R.id.screenStatus);
		bluetoothTxt = (TextView)act.findViewById(R.id.bluetoothStatus);
		
		button = (Button)act.findViewById(R.id.button);
    }
    
	public void showData(){
		
	 	statusTxt.setText("Status = SOD processing..");
    	sampleTxt.setText("# sample = " + Config.sample);
    	governTxt.setText("CPU governor = "+ FileMgr.governData);
    	cpuUtilTxt.setText("CPU util = " + FileMgr.cpuUtilData + " % ");
    	cpuFreqTxt.setText("CPU freq = "+ FileMgr.cpuFreqData + " MHz ");
    	brightTxt.setText("Brightness level = "+ FileMgr.brightData);
    	battTxt.setText("Battery capacity "+Battery.getBatteryLevel());
    	gpsTxt.setText("GPS Location = "+ FileMgr.voltData);
	    	
	}
}
