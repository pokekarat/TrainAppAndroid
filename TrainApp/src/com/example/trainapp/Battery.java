package com.example.trainapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Battery {
	
	public static MainActivity main;
	
	public static float getBatteryLevel() 
	    {  
			Intent batteryIntent = main.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
	        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	        // Error checking that probably isn't needed but I added just in case.
	        if(level == -1 || scale == -1) {
	            return 50.0f;
	        }

	        return ((float)level / (float)scale) * 100.0f; 
	    }
}
