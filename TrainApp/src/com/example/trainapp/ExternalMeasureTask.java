package com.example.trainapp;

import android.os.AsyncTask;
import android.os.SystemClock;

class ExternalMeasureTask extends AsyncTask<Integer, Integer , Integer>
{
	
	public String setExternal = "";
	public String hwTargetName = "";
	public boolean isTrainStop;
	HwTrainForExternal ht;
	Ui view;
	
	public ExternalMeasureTask(String s, Ui v)
	{
		setExternal = s;
		view = v;
		hwTargetName = v.hwTarget;
	}
	
	@Override
	protected void onPreExecute()
	{    		 
		FileMgr.status = "External measure working";
		Config.processing=true;
	    view.button.setText("STOP");
	    Battery.INIT_BATT_LEVEL = Battery.getBatteryLevel();
	    Config.sample = 0;
	    ht = new HwTrainForExternal(this.setExternal, hwTargetName);
	}
	
    @Override
	protected Integer doInBackground(Integer... arg0)
	{	
    	while(true)
    	{
    		
    		if(Config.sample == 30)
    			ht.execute(Config.sample);
    		
    		this.publishProgress(Config.sample);
    		
    		SystemClock.sleep(1000);
    		      		
    		++Config.sample;
    		
    		if(this.isTrainStop)
    			break;
    	}
    	
	    return 0;
	}
    
    Double voltSaveIn = 0.0;
    Double voltSaveOut = 0.0;
    Double avgVoltIn = 0.0;
    Double avgVoltOut = 0.0;
    String result = "";
    int startPoint = 0;
    
	@Override
	protected void onProgressUpdate(Integer... arg1)
	{    
		
		FileMgr.processResults();
		
		view.showData();
	    		
    	if(ht.hwName.contains("cpu"))
    	{
    		
    		if(ht.isStartTrain)
    		{
	    		
	    		view.cpuStatusTxt.setText("Start @sample= "+startPoint+"\n["+ht.currentStep+"/"+ht.totalStep+"]["+ht.position+"/"+ht.offset+"]\n[util = "+ ht.currentUtil + ", freq = "+ht.currentFreq+"]" );
	    		result += Config.sample+" [" + FileMgr.cpuUtilData + "," + FileMgr.cpuFreqData + ",("+ht.currentUtil+","+ht.currentFreq+")] [" + FileMgr.brightData + "]\n";
	    	
    	    	if(ht.isBreak)
    	    	{
    	    		view.cpuStatusTxt.setText("Saving CPU training.");
    	    		FileMgr.saveSDCard(ht.hwName, result);
    	    		result = "";
    	    		ht.isBreak = false;
    	    		
    	    	}
    	    	
    	    	if(ht.isMainBreak)
    	    		this.isTrainStop = true;
    		}
    		else
    		{
	    		view.cpuStatusTxt.setText("Not sample yet \n ["+ht.currentStep+"/"+ht.totalStep+"]["+ht.position+"/"+ht.offset+"]\n[util = "+ ht.currentUtil + ", freq = "+ht.currentFreq+"]" );
	    		startPoint = Config.sample;
    		}	
    	}
    	else if(ht.hwName.contains("screen"))
    	{
    		view.screenStatusTxt.setText("["+ht.currentStep+"/"+ht.totalStep+"] [" + FileMgr.cpuUtilData + "," + FileMgr.cpuFreqData + "] [" + FileMgr.brightData + ",("+ht.brightData+")] ");
    		
    		result += Config.sample+" [" + FileMgr.cpuUtilData + "," + FileMgr.cpuFreqData + "] [" + FileMgr.brightData + ",("+ht.brightData+")] ";
	    	
    		if(ht.isStartTrain){
    			result += "*\n";
    		}else{
    			result += "\n";
    		}
    	
	    	if(ht.isBreak)
	    	{
	    		view.screenStatusTxt.setText("Finish LCD training.");
	    		FileMgr.saveSDCard(ht.hwName, result);
	    		this.isTrainStop = true;
	    		result = "";
	    	}
	    	
	    	if(ht.isMainBreak)
	    		this.isTrainStop = true;
    	}
    	else if (ht.hwName.contains("gps"))
    	{
    		view.gpsTxt.setText(view.gps.gpsStatus + " " +view.gps.numSat+" "+view.gps.locateStr);
    	
    	}
    	else if (ht.hwName.contains("bluetooth"))
    	{
    		view.bluetoothTxt.setText("Waiting for testing...");
    		
    		if(ht.isStartTrain){
    			
    			String s = "sample="+Config.sample + " step="+ht.currentStep+"/"+ht.totalStep + " cpu="+FileMgr.cpuUtil+" freq="+ FileMgr.cpuFreqData +" bright="+FileMgr.brightData + " voltage="+FileMgr.voltData + " temp="+FileMgr.tempData + " cap="+Battery.getBatteryLevel() + "\n";
    			result += s;
    			view.bluetoothTxt.setText(s);
    			
    		}
    		
    		if(ht.isBreak){
    			
    			view.bluetoothTxt.setText("Finish testing...");
    			FileMgr.saveSDCard(ht.hwName, result);
	    		result = "";
	    		ht.isBreak = false;
	    		
    		}
    		
    		if(ht.isMainBreak)
	    		this.isTrainStop = true;
    	}
    	else if(ht.hwName.contains("")){
    		
    		view.statusTxt.setText("No hw being trained");
    	}
	    	
	}
	
	
	@Override
	protected void onPostExecute(Integer result){
	    //result comes from return value of doInBackground
	    //runs on UI thread, not called if task cancelled
	    view.cpuUtilTxt.setText("Processed finished!");
	    Config.processing=false;
	    view.button.setText("GO");
	    //finish();
	}
	
	@Override
    protected void onCancelled() {
        //run on UI thread if task is cancelled
        Config.processing=false;
        view.button.setText("GO");
    }
}
