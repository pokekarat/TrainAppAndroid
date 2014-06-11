package com.example.trainapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Battery {
	
	public static MainActivity main;
	public static int INIT_BATT_LEVEL;
	public static int CURRENT_BATT_LEVEL;
	
	public static int getBatteryLevel() 
	{  
		
			Intent batteryIntent = main.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
	        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	        // Error checking that probably isn't needed but I added just in case.
	        if(level == -1 || scale == -1) {
	            return 50;
	        }

	        return (int)(((float)level / (float)scale) * 100.0f); 
	}
	
	public static boolean isChange = false;
	public static boolean Wait(){
		while(true)
		{
			int batt_curr = (int)Battery.getBatteryLevel();
			if((CURRENT_BATT_LEVEL - batt_curr) == 1)
			{
				CURRENT_BATT_LEVEL = batt_curr;
				break;
			}
		}
		isChange = true;
		return true;
	}
}
