package com.phimetrics.batterycheck.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.phimetrics.batterycheck.GPSDataApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Komal <komalj@birdseyetech.com>.
 */
public class PhoneStateServiceReceiver extends BroadcastReceiver {
    public static final SimpleDateFormat start_time = new SimpleDateFormat("hh:mm:ss");
    public static final SimpleDateFormat end_time = new SimpleDateFormat("hh:mm:ss");

    private static String starttime, endtime;
    private static Date converStartDate, converEndDate;
    private static final String TAG = "BrodCastTag";
    private static boolean callEnded = false;
    public static String callType;
    String text = "";
    public boolean flagState = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (flagState) {
            MyPhoneStateListener phoneListener = new MyPhoneStateListener();
            TelephonyManager telephony = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            flagState = false;
        }
    }

    public class MyPhoneStateListener extends PhoneStateListener {


        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.v("log_tag", "State changed: Idle " + state + " " + incomingNumber);
                    Log.e("Call Ended", "-->" + callEnded);

                    if (callEnded) {
                        callEnded = false;
                        //Store total_time somewhere or pass it to an Activity using intent
                        //you will be here at **STEP 4**
                        //you should stop service again over here

                        Log.d(" if Idle", "1111");
                        Log.e("callEnded", "callEnded");

                        if (!callType.equalsIgnoreCase("Miss Call")) {


                            Calendar c = Calendar.getInstance();
                            endtime = end_time.format(c.getTime());
                            Log.d("Cut the call", "1111" + " " + endtime);
                            converEndDate = new Date();
                            try {
                                converEndDate = end_time.parse(endtime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long diff = ((converEndDate.getTime() - converStartDate.getTime()));
                            long second = (diff / 1000);
                            long minute = (diff / (60 * 1000));
                            long hour = (diff / (60 * 60 * 1000));
                            //long second = diff / (60 * 1000);

                            if (second >= 60) {
                                minute = second / 60;
                                second = second - minute * 60;
                            }
                            if (minute >= 60) {
                                hour = minute / 60;
                                minute = minute - hour * 60;
                            }

                            String timeDetail = "";
                            timeDetail = String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second);
                            Log.e("TimeDetail", "-->" + timeDetail);
                            text = incomingNumber + "," + starttime + "," + endtime + "," + timeDetail + "," + callType + ","
                                    + System.getProperty("line.separator");

                            Log.v("log_tag", "text::" + text);

                        } else {

                            //String text = "";
                            text = incomingNumber + "," + starttime + "," + callType + ","
                                    + System.getProperty("line.separator");

                        }
                        FileOutputStream fos;
                        try {
                            Log.e("File Write", "File Write");
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
                        flagState = true;
                    } else {
                        //you will be here at **STEP 1**
                        //stop your service over here,
                        //i.e. stopService (new Intent(`your_context`,`CallService.class`));
                        //NOTE: `your_context` with appropriate context and `CallService.class` with appropriate class name of your service which you want to stop.

                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.v("log_tag", "State changed: OffHook " + state + " " + incomingNumber);
                    //you will be here at **STEP 3**
                    // you will be here when you cut call

                    if (callType == null) {
                        callType = "Outgoing";
                        callEnded = true;
                    } else {
                        callType = "Incoming";
                    }



                /*if (callType != null && callType.equalsIgnoreCase("Miss Call"))
                {
                    callType = "Outgoing";
                }else {
                    callType = "Incoming";
                }*/

                    // Start your new activity
                    Log.e("Call Received", "Call Received");
                    Calendar c = Calendar.getInstance();
                    starttime = start_time.format(c.getTime());
                    Log.d("Running call", "333" + " " + starttime);
                    converStartDate = new Date();
                    try {
                        converStartDate = start_time.parse(starttime);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.v("log_tag", "State changed: Ringing " + state + " " + incomingNumber);
                    //you will be here at **STEP 2**
                    callType = "Miss Call";
                    callEnded = true;
                    Calendar c1 = Calendar.getInstance();
                    starttime = start_time.format(c1.getTime());
                    Log.d("Ringing", "4444" + callType);
                    break;


                default:
                    break;
            }
        }
    }


    private File createFolderAndFile() throws IOException {
        if (!new File(GPSDataApplication.folderPath).exists()) {
            new File(GPSDataApplication.folderPath).mkdirs();
        }
        File f = new File(GPSDataApplication.folderPath + GPSDataApplication.callDetails);
        if (!f.exists()) {
            f.createNewFile();
        }
        return f;
    }
}