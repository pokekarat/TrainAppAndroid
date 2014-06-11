package com.example.trainapp;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Environment;

public class Audio {
	
	public static final String MUSIC_DIR = "/music/Kalimba.mp3";
	public static MediaPlayer mediaPlayer;
	
	public static void Setup(){
		
		String musicDir = Environment.getExternalStorageDirectory().getAbsolutePath() + MUSIC_DIR;
		
		mediaPlayer = new MediaPlayer();
		
		try {
			
			mediaPlayer.setDataSource(musicDir);
			mediaPlayer.prepare();
			mediaPlayer.setLooping(true);
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void PlayMusic(){
		mediaPlayer.start();
		mediaPlayer.setLooping(true);
	}
	
	public static void StopMusic(){
		mediaPlayer.stop();
	}
}
