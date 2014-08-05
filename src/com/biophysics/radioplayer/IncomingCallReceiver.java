package com.biophysics.radioplayer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class IncomingCallReceiver extends BroadcastReceiver implements PlayerCallback {

    @Override
    public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
           
            if(null == bundle)
                    return;
           
//            Log.i("IncomingCallReceiver",bundle.toString());
           
            String state = bundle.getString(TelephonyManager.EXTRA_STATE);
                           
//            Log.i("IncomingCallReceiver","State: "+ state);
           
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
            {
                   // String phonenumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                                               
//                    Log.i("IncomingCallReceiver","Incomng Number: " + phonenumber);
                   
                  //  String info = "Detect Calls sample application\nIncoming number: " + phonenumber;
                   
              //      Toast.makeText(context, info, Toast.LENGTH_LONG).show();
                    System.exit(0);

            }
    }

	public void playerStarted() {
		// TODO Auto-generated method stub		
	}

	public void playerPCMFeedBuffer(boolean isPlaying, int audioBufferSizeMs,
			int audioBufferCapacityMs) {
		// TODO Auto-generated method stub
		
	}

	public void playerStopped(int perf) {
		// TODO Auto-generated method stub
		
	}

	public void playerException(Throwable t) {
		// TODO Auto-generated method stub
		
	}

}

