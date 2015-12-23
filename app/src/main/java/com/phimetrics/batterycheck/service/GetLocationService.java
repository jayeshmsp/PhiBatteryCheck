package com.phimetrics.batterycheck.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.phimetrics.batterycheck.GPSDataApplication;
import com.phimetrics.batterycheck.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@SuppressLint("NewApi")
public class GetLocationService extends Service implements
		LocationListener,
		ConnectionCallbacks, OnConnectionFailedListener {
	private GoogleApiClient mLocationClient;
	private LocationRequest locationRequest = null;
	private static final int MIN_DISTANCE_REQUIRED_TO_GET_GPSLOCATION = 0;//mts
	private static final long MIN_MILLIS_REQUIRED_TO_GET_GPSLOCATION = 10*1000;
	private static boolean isGpsStatusNotified = false;
	private BroadcastReceiver gpsStatusChangeReceiver;
	private static final String TAG = "log_tag";
	private String batteryLevel = "";
	private TelephonyManager telManager;

	private String signalStrengthDbm = "";
	private int rssi;
	
	public GetLocationService(Context context) {
		initVariables(context);
        //mLocationClient = new LocationClient(context, this, this);
        initLocationClient();
	}

    private void initLocationClient() {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

	public GetLocationService() {
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("log_tag", "on create");
		initVariables(this);
		//mLocationClient = new LocationClient(this, this, this);
        initLocationClient();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("log_tag", "on start command");
		SignalStrengthListener signalStrengthListener = new SignalStrengthListener();
        telManager = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		initVariables(this);
        initLocationClient();
		//mLocationClient = new LocationClient(this, this, this);
		checkLocationServices();
		findRssiValue();
		return START_STICKY;
	}
	
	private class SignalStrengthListener extends PhoneStateListener {
      @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
    	 super.onSignalStrengthsChanged(signalStrength);
		 int signalStrengthAsu = signalStrength.getGsmSignalStrength();
         if(signalStrengthAsu >=0 && signalStrengthAsu <=31) {
        	 signalStrengthDbm = ((2 * signalStrengthAsu) - 113) + " dBm  " + signalStrengthAsu + " asu";  
         } else {
        	 signalStrengthDbm = signalStrengthAsu + " asu";
         }
      }

    };

	
	private void checkLocationServices() {
		boolean checkLocationServiceEnabled = Utils.checkLocationServiceEnabled(this);
		if(!checkLocationServiceEnabled) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Location Services not Active!");
			builder.setMessage("Please provide Location access. Either enable GPS or the Wireless networks.");
			builder.setPositiveButton("Settings",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogInterface, int i) {
							// Show location settings when the user acknowledges the
							// alert dialog
							dialogInterface.dismiss();
							Intent intent = new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
						}
					});
			builder.setNegativeButton("Close",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					});
			Dialog alertDialog = builder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();
		} else {
			startGPSLocationUpdates();
		}
	}

	public void startGPSLocationUpdates() {
		try {
			if (mLocationClient != null) {
				if (!mLocationClient.isConnected()
						&& !mLocationClient.isConnecting()) {
					mLocationClient.connect();
				}
			}
		} catch (Exception ex) {
			Log.v(TAG, ex.toString());
		}
	}

	public void stopGPSLocationUpdates() {
		if (mLocationClient != null) {
			if (mLocationClient.isConnected()) {
				try {
					//mLocationClient.removeLocationUpdates(this);
                    LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
					mLocationClient.disconnect();
				}catch(IllegalStateException e) {
					Log.v(TAG, e.toString());
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.mBatInfoReceiver);
	}

	@Override
	public void onLocationChanged(Location location) {
		if(location != null) {
			
			String networkOperator = telManager.getNetworkOperator();
			String networkString1 = "";
            String networkString2 = "";
            if(networkOperator != null && networkOperator.length() >= 3) {
                networkString1 = networkOperator.substring(0,3);
            }
            if(networkOperator != null && networkOperator.length() > 3) {
                networkString2 = networkOperator.substring(3);
            }
			String carrierName = telManager.getNetworkOperatorName();
			String text = "";
			text = location.getLatitude() + "," + location.getLongitude() + ","
                    + GPSDataApplication.dateFormat.format(new Date()) + ","
                    + batteryLevel + "," + signalStrengthDbm + ","
                    + Utils.getNetworkType(GetLocationService.this) + "," + networkString1
                    + "," + networkString2 + "," + carrierName + "," +
                    Utils.getCallStatus(GetLocationService.this) + "," + rssi + ","
                    + System.getProperty("line.separator");

//			List<NeighboringCellInfo> infos = telephonyManager.getNeighboringCellInfo();


			Log.v("log_tag", "text::" + text);
			FileOutputStream fos;
			try {
				File f = createFolderAndFile();
				fos = new FileOutputStream(f, true);
				fos.write(text.getBytes());
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				Log.v(TAG, e.toString());
			} catch (IOException e) {
				Log.v(TAG, e.toString());
			}
		}
	}
	
	private File createFolderAndFile() throws IOException {
		if(!new File(GPSDataApplication.folderPath).exists()) {
			new File(GPSDataApplication.folderPath).mkdirs();
		}
		File f = new File(GPSDataApplication.folderPath + GPSDataApplication.fileName);
		if(!f.exists()){
			f.createNewFile();
		}
		return f;
	}

	public float getBearing() {
		try {
			if (mLocationClient != null && mLocationClient.isConnected()) {
				return LocationServices.FusedLocationApi.getLastLocation(mLocationClient).getBearing();
			}
		} catch (Exception ex) {
			Log.v(TAG, ex.toString());
			return 0;
		}
		return 0;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onConnectionFailed(ConnectionResult arg0) {
		//Log.i(TAG, "client connection failed");
	}

	public void onConnected(Bundle connectionHint) {
		if (mLocationClient != null && mLocationClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mLocationClient, locationRequest, this);

			//mLocationClient.requestLocationUpdates(locationRequest, GetLocationService.this);
		}
		Intent serviceStateIntent = new Intent();
		serviceStateIntent.setAction(GPSDataApplication.SERVICE_STATE);
		LocalBroadcastManager.getInstance(this).sendBroadcast(serviceStateIntent);

		//Log.v("log_tag", "location client connected");
	}

    @Override
    public void onConnectionSuspended(int i) {

    }

	private void initVariables(Context context) {
		try {
			gpsStatusChangeReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					LocationManager manager = (LocationManager) context
							.getSystemService(Context.LOCATION_SERVICE);
					boolean status = (manager
							.isProviderEnabled(LocationManager.GPS_PROVIDER) && manager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

					if (!status) {
						isGpsStatusNotified = false;
						isGPSOn(GetLocationService.this);
					}
				}
			};
			this.getApplicationContext().registerReceiver(
					gpsStatusChangeReceiver,
					new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

		} catch (Exception e) {
			Log.v(TAG, e.toString());
		}

		locationRequest = LocationRequest
				.create()
				.setSmallestDisplacement(
						MIN_DISTANCE_REQUIRED_TO_GET_GPSLOCATION)
				.setInterval(MIN_MILLIS_REQUIRED_TO_GET_GPSLOCATION)
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);// 16ms = 60fps

	}

	public static void isGPSOn(Context context) {
		try {
			LocationManager manager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			boolean status = (manager
					.isProviderEnabled(LocationManager.GPS_PROVIDER) && manager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
			if (!isGpsStatusNotified && !status) {
				isGpsStatusNotified = true;
			}
		} catch (Exception e) {
			Log.v(TAG, e.toString());
		}
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		initVariables(this);
		//mLocationClient = new LocationClient(this, this, this);
        initLocationClient();
        checkLocationServices();
	}
	
	 private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
		    @Override
		    public void onReceive(Context ctxt, Intent intent) {
		      int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		      batteryLevel = String.valueOf(level) + "%";
		    }
	 };
	private void findRssiValue() {
		/*TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
		CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
		signnalStrength = cellSignalStrengthGsm.getDbm() + " dBm ";
*/
		WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		rssi = wifiManager.getConnectionInfo().getRssi();
		//Log.e("Wifi rssi", "RSSI" + rssi );
	}
}
