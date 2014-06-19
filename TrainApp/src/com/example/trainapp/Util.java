package com.example.trainapp;

import android.os.SystemClock;

public class Util {

	public static boolean isInteger(String text) {
		  try {
			  
		    new Integer(text);
		    return true;
		  } catch (NumberFormatException e) {
		    return false;
		  }
	}
	
	public static void delay(int delayTime){
		
		SystemClock.sleep(delayTime);
	}
}
