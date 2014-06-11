package com.example.trainapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BT {
	
	public static MainActivity main;
	private static final int REQUEST_ENABLE_BT = 1;
	public static BluetoothAdapter myBluetoothAdapter;
	
	public static void init(){
		
		 // take an instance of BluetoothAdapter - Bluetooth radio
	      myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	
	public static void testBase(){
		Screen.SetBrightness(10);
	}
	
	//bluetooth on
	public static void testNonDiscovery(){
		
		if (!myBluetoothAdapter.isEnabled()) 
	      {
	         Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	         main.startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

	      }
	     
		
	}
}
