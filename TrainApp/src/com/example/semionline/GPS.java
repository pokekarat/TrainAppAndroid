package com.example.semionline;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GPS implements LocationListener {

	public LocationManager locateMgr;
	public String locateStr = "GPS";
	
	/*
	public GPS(LocationManager obj){
		
		locateMgr = obj;
		boolean enabled = locateMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if(!enabled){

			locateMgr.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                     3000,   // 3 sec
                     10, // 10 meters 
                     this);
		}
	}
	*/

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		String str = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();
		locateStr = str;
		Log.i("GPS.java [onLocationChanged]","location "+ str);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		locateStr = "enabled";
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		locateStr = "disabled";
	}
}
