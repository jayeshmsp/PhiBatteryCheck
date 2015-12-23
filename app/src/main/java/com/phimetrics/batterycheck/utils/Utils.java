package com.phimetrics.batterycheck.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

	public static boolean checkLocationServiceEnabled(final Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			return false;
		}
		return true;
	}
	
	public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
	    ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info==null || !info.isConnected())
            return "NOT CONNECTED"; //not connected
        if(info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI";
        if(info.getType() == ConnectivityManager.TYPE_MOBILE){
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                	return "2G (GPRS)";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                	return "2G (EDGE)";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                	return "2G (CDMA)";
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                	return "2G (1xRTT)";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                	return "2G (IDEN)";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                	return "3G (UMTS)";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                	return "3G (EVDO_0)";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                	return "3G (EVDO_A)";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                	return "3G (HSDPA)";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                	return "3G (HSUPA)";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                	return "3G (HSPA)";
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                	return "3G (EVDO_B)";
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                	return "3G (EVDO_B)";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G (HSPAP)";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G (LTE)";
                default:
                    return "Unknown";
            }
        }
        return "?";
    }

    public static String getCallStatus(Context mContext) {
        TelephonyManager teleMan =
                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = teleMan.getNetworkType();
        switch (networkType) {
        case TelephonyManager.NETWORK_TYPE_GPRS:
            return "2G (GPRS)";
        case TelephonyManager.NETWORK_TYPE_EDGE:
            return "2G (EDGE)";
        case TelephonyManager.NETWORK_TYPE_CDMA:
            return "2G (CDMA)";
        case TelephonyManager.NETWORK_TYPE_1xRTT:
            return "2G (1xRTT)";
        case TelephonyManager.NETWORK_TYPE_IDEN:
            return "2G (IDEN)";
        case TelephonyManager.NETWORK_TYPE_UMTS:
            return "3G (UMTS)";
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
            return "3G (EVDO_0)";
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
            return "3G (EVDO_A)";
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            return "3G (HSDPA)";
        case TelephonyManager.NETWORK_TYPE_HSUPA:
            return "3G (HSUPA)";
        case TelephonyManager.NETWORK_TYPE_HSPA:
            return "3G (HSPA)";
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            return "3G (EVDO_B)";
        case TelephonyManager.NETWORK_TYPE_EHRPD:
            return "3G (EVDO_B)";
        case TelephonyManager.NETWORK_TYPE_HSPAP:
            return "3G (HSPAP)";
        case TelephonyManager.NETWORK_TYPE_LTE:
            return "4G (LTE)";
        default:
            return "UNKNOWN";
        }
    }


    public static int getRSSI(Context mContext) {
        //TelephonyManager telephonyManager =
         //       (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //CellInfoCdma cellinfogsm = (CellInfoCdma)telephonyManager.getAllCellInfo().get(0);
        //CellSignalStrengthWcdma cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();

       // return cellinfogsm.getCellSignalStrength().getDbm();
        return 0;
    }


    public static boolean copyFileFromPath(Context context, String filePath, String outPutFilePath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            inputStream = new FileInputStream(filePath);
            outputStream = new FileOutputStream(outPutFilePath);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read( buffer, 0, buffer.length ) ) >= 0) {
                outputStream.write( buffer, 0, buffer.length );
            }
            inputStream.close();
            outputStream.close();
        } catch ( Exception e ){
            Log.e("log_tag", "Exception occurred " + e.getMessage());
        } finally{
        	
        }
        return true;
    }

}
