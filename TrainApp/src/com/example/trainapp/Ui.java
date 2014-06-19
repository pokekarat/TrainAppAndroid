package com.example.trainapp;

import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class Ui {

	public TextView cpuUtilTxt;
    public TextView cpuFreqTxt;
    public TextView battTxt;
    public TextView battVoltTxt;
    public TextView battTempTxt;
    public TextView wifiSpeedTxt;
    public TextView wifiStateTxt;
    
    public TextView governTxt;
    public TextView statusTxt;
    public TextView brightTxt;
    public TextView gpsTxt;
    public TextView sampleTxt;
    public TextView cpuStatusTxt;
    public TextView screenStatusTxt;
    public TextView bluetoothTxt;
    public TextView wifiTxt;
    
    public boolean isCpuChk = false;
    public boolean isScreenChk = false;
    public boolean isGPSChk = false;
    public boolean isBtChk = false;
    
    public Button button;
    
    public CheckBox cpu_cb;
    public CheckBox screen_cb;
    public CheckBox gps_cb;
    public CheckBox bt_cb;
    public CheckBox wifi_cb;
    
	public String hwTarget = "";
	LocationManager locMgr;
	GPS locListener;
	public LocationManager locateMgr;
	public GPS gps;
	
	public Spinner spinner1;
    
	MainActivity _act;
	
    public void init(MainActivity act){
    	
    	_act = act;
    	
    	cpuUtilTxt = (TextView)act.findViewById(R.id.cpuUtil);
		cpuFreqTxt = (TextView)act.findViewById(R.id.freq);
		governTxt = (TextView)act.findViewById(R.id.governor);
		
		statusTxt = (TextView)act.findViewById(R.id.status);
		
		brightTxt = (TextView)act.findViewById(R.id.bright);
		
		battTxt = (TextView)act.findViewById(R.id.battCap);
		battVoltTxt = (TextView)act.findViewById(R.id.battVolt);
		battTempTxt = (TextView)act.findViewById(R.id.battTemp);
		
		sampleTxt = (TextView)act.findViewById(R.id.sample);
		
		cpuStatusTxt = (TextView)act.findViewById(R.id.cpuStatus);
		screenStatusTxt = (TextView)act.findViewById(R.id.screenStatus);
		gpsTxt = (TextView)act.findViewById(R.id.gpsStatus);
		bluetoothTxt = (TextView)act.findViewById(R.id.bluetoothStatus);
		
		wifiTxt = (TextView)act.findViewById(R.id.wifiStatus);
		//wifiSpeedTxt = (TextView)act.findViewById(R.id.wifiSpeedTxt);
		wifiStateTxt = (TextView)act.findViewById(R.id.wifiStateTxt);
		
		
		button = (Button)act.findViewById(R.id.button);
		
		cpu_cb = (CheckBox)act.findViewById(R.id.cpu_cb);
		screen_cb = (CheckBox)act.findViewById(R.id.screen_cb);
		gps_cb = (CheckBox)act.findViewById(R.id.gps_cb);
		bt_cb = (CheckBox)act.findViewById(R.id.bluetooth_cb);
		wifi_cb = (CheckBox)act.findViewById(R.id.wifi_cb);

		spinner1 = (Spinner) act.findViewById(R.id.spinner1);
    }
    
	public void showData(){
		
	 	statusTxt.setText("Status = SOD processing..");
    	sampleTxt.setText("# sample =  " + Config.sample);
    	governTxt.setText("CPU governor =  "+ FileMgr.governData);
    	cpuUtilTxt.setText("CPU util =  " + FileMgr.cpuUtilData + " % ");
    	cpuFreqTxt.setText("CPU freq =  "+ FileMgr.cpuFreqData + " MHz ");
    	brightTxt.setText("Brightness level =  "+ FileMgr.brightData);
    	gpsTxt.setText("GPS Location =  0");
    	
    	battTxt.setText("Battery capacity =  "+ Battery.getBatteryLevel());
    	battVoltTxt.setText("Battery volt =  "+ FileMgr.voltData);
    	battTempTxt.setText("Battery temp =  "+ FileMgr.tempData);
    	
    	int speed = WiFi.wifiMgr.getConnectionInfo().getLinkSpeed();
    	int strength = WifiManager.calculateSignalLevel(WiFi.wifiMgr.getConnectionInfo().getRssi(),5);
		String units = WifiInfo.LINK_SPEED_UNITS;
    	String ssid = WiFi.wifiMgr.getConnectionInfo().getSSID();
    	//wifiSpeedTxt.setText("Wifi speed = " + );
    	wifiStateTxt.setText(String.format("%s \nat %s%s. \nStrength %s/5", ssid,speed,units,strength));
	    	
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
		
		gps_cb.setOnClickListener(new OnClickListener() 
		{
			
			public void onClick(View v){
				if(((CheckBox) v).isChecked()){
					
					locateMgr = (LocationManager)_act.getSystemService(Context.LOCATION_SERVICE);
					
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
		
		wifi_cb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v){
				if(((CheckBox) v).isChecked()){
					hwTarget = "wifi";
					FileMgr.status = "WiFi is checked.";
				}
			}
		});
	}
	
	
}
