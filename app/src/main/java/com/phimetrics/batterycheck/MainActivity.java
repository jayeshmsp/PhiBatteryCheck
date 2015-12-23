package com.phimetrics.batterycheck;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.phimetrics.batterycheck.service.GetLocationService;
import com.phimetrics.gps.R;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends Activity {

//	private TextView tvService;
//	private Button bStartService;
	
	private Button bExportFile;
	private Button bClearData;
	public static final int FILE_EXPORT = 1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

	//	tvService = (TextView) findViewById(R.id.text);
	//	bStartService = (Button) findViewById(R.id.startservice);
		bExportFile = (Button) findViewById(R.id.exportdata);
		bClearData = (Button) findViewById(R.id.cleardata);
		
		setServiceTextButtons();
		
		/*bStartService.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), GetLocationService.class);
				getApplicationContext().startService(intent);
			}
		});*/

		bExportFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(new File(GPSDataApplication.outputFilePath).exists()) {
					Uri u1 = Uri.fromFile(new File(GPSDataApplication.outputFilePath));
					Intent sendIntent = new Intent(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_SUBJECT, "PhiBatteryCheck Data as on " + GPSDataApplication.dateFormat.format(Calendar.getInstance().getTime()));
					sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
					sendIntent.setType("text/richtext");
					startActivityForResult(sendIntent, FILE_EXPORT);
				} else {
					Toast.makeText(MainActivity.this, "Data cannot be exported right now.. Please try again later.", Toast.LENGTH_SHORT).show();
				}

				/*final Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
				sendIntent.setType("text/richtext");

				ArrayList<Uri> uris = new ArrayList<Uri>();

				String[] filePaths = new String[] {GPSDataApplication.outputFilePath, GPSDataApplication.callDetails};
				for (String file : filePaths) {
					File fileIn = new File(file);
					Uri u = Uri.fromFile(fileIn);
					uris.add(u);
				}

				sendIntent.putExtra(Intent.EXTRA_SUBJECT, "PhiBatteryCheck Data as on" + GPSDataApplication.dateFormat.format(Calendar.getInstance().getTime()));
				sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
				startActivityForResult(sendIntent, FILE_EXPORT);*/
			}
		});

		bClearData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				File file = new File(GPSDataApplication.outputFilePath);
				if(file.exists()) {
					file.delete();
				}

				File receive_Call_File = new File(GPSDataApplication.outputCallDetailFilePath);
				if (receive_Call_File.exists()) {
					receive_Call_File.delete();
				}
			}
		});
		
		
		Intent intent = new Intent(getApplicationContext(), GetLocationService.class);
		getApplicationContext().startService(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FILE_EXPORT && resultCode == RESULT_OK) {
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		IntentFilter serviceStateFilter = new IntentFilter(GPSDataApplication.SERVICE_STATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStateChange, serviceStateFilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceStateChange);
	}
	
	private BroadcastReceiver serviceStateChange = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	setServiceTextButtons();
	    }
	};
	
	private void setServiceTextButtons() {
		/*if(Utils.isServiceRunning(GetLocationService.class, getApplicationContext())) {
			tvService.setText("GPS Service is running");
			bStartService.setEnabled(false);
		} else {
			tvService.setText("GPS Service is not running");
			bStartService.setEnabled(true);
		}*/
	}
	

	
	
	
	
}
