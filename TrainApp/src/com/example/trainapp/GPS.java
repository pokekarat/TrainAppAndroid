package com.example.trainapp;

import java.util.LinkedList;
import java.util.List;

import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GPS implements GpsStatus.Listener, LocationListener {

	public static LocationManager locationManager;
	public String locateStr = "GPS";
	public String numSat = "0";
	public String gpsStatus = "No status";
	public boolean isStartOnce = true;	
	public boolean gpsEnabled;
	public boolean gpsFix;
	private static final long DURATION_TO_FIX_LOST_MS = 10000;
	private double latitude;
	private double longitude;
	private int satellitesTotal;
	private int satellitesUsed;
	private float accuracy;
	
	// the last location time is needed to determine if a fix has been lost
	private long locationTime = 0;
	private List<Float> rollingAverageData = new LinkedList<Float>();
	
	

	public GPS(LocationManager lm){
		locationManager = lm;
	}

	@Override
	public void onGpsStatusChanged(int changeType) {
		if (locationManager != null) {

			// status changed so ask what the change was
			GpsStatus status = locationManager.getGpsStatus(null);
			
			switch(changeType) {
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					gpsEnabled = true;
					gpsFix = true;
					gpsStatus = "First Fix";
					break;
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					gpsEnabled = true;
					// if it has been more then 10 seconds since the last update, consider the fix lost
					gpsFix = System.currentTimeMillis() - locationTime < DURATION_TO_FIX_LOST_MS;
					gpsStatus = "Satellite is fixed";
					break;
				case GpsStatus.GPS_EVENT_STARTED: // GPS turned on
					gpsEnabled = true;
					gpsFix = false;
					gpsStatus = "GPS start";
					break;
				case GpsStatus.GPS_EVENT_STOPPED: // GPS turned off
					gpsEnabled = false;
					gpsFix = false;
					gpsStatus = "GPS stop";
					break;
				default:
					//Log.w(TAG, "unknown GpsStatus event type. "+changeType);
					gpsStatus = "Unknown status";
					return;
			}

			// number of satellites, not useful, but cool
			int newSatTotal = 0;
			int newSatUsed = 0;
			for(GpsSatellite sat : status.getSatellites()) 
			{
				newSatTotal++;
				if (sat.usedInFix()) {
					newSatUsed++;
				}
			}
			
			satellitesTotal = newSatTotal;
			satellitesUsed = newSatUsed;
			
			numSat = satellitesUsed + "/" + satellitesTotal;
		}
	}

	@Override
	public void onLocationChanged(Location location) 
	{

		locationTime = location.getTime();
		latitude = location.getLatitude();
		longitude = location.getLongitude();

		if (location.hasAccuracy()) {
			// rolling average of accuracy so "Signal Quality" is not erratic
			updateRollingAverage(location.getAccuracy());
		}

		locateStr = locationTime + ":" + latitude + ":" + longitude;
		//updateView();
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		/* dont need this info */
	}

	@Override
	public void onProviderEnabled(String provider) {
		/* dont need this info */
	}

	@Override
	public void onProviderDisabled(String provider) {
		/* dont need this info */
	}

	private void updateRollingAverage(float value)
	{
		// does a simple rolling average
		rollingAverageData.add(value);
		if (rollingAverageData.size() > 10) {
			rollingAverageData.remove(0);
		}

		float average = 0.0f;
		for(Float number : rollingAverageData) {
			average += number;
		}
		average = average / rollingAverageData.size();

		accuracy = average;
	}
}
