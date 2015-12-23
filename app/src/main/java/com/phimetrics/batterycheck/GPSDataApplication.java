package com.phimetrics.batterycheck;

import android.app.Application;
import android.os.Environment;

import com.phimetrics.batterycheck.service.GetLocationService;

import java.io.File;
import java.text.SimpleDateFormat;

public class GPSDataApplication extends Application {
	
	public static final String SERVICE_STATE = "serviceState";
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SS z");
	
	public GetLocationService gpsServiceIntent;
	
	public static String folderName = "GPSData";
	public static String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +  GPSDataApplication.folderName + File.separator;
	public static String callDetails = "CallDetails.csv";
	public static String fileName = "GPSData.csv";
	public static String copyfileName = "ExportGPSData.csv";
	public static String outputFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +  GPSDataApplication.folderName + File.separator + fileName;
	public static String outputCallDetailFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +  GPSDataApplication.folderName + File.separator + callDetails;
	public static String copyFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +  GPSDataApplication.folderName + File.separator + copyfileName;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	
	
	

	
}
