package com.phimetrics.batterycheck.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

		private static final String TAG = BootCompletedReceiver.class.getCanonicalName();

		@Override
	    public void onReceive(Context context, Intent intent) {
	    	Log.v("log_tag","ON BOOT COMPLETE");
	    	Intent intent1 = new Intent(context.getApplicationContext(), GetLocationService.class);
	    	context.getApplicationContext().startService(intent1);
	    }
}
