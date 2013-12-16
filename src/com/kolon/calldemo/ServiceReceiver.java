package com.kolon.calldemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ServiceReceiver extends BroadcastReceiver {
	private String TAG = "CallCatcher";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "ServiceReceiver->onReceive();");

		MyPhoneStateListener phoneListener = new MyPhoneStateListener(context);
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		telephony.listen(phoneListener, PhoneStateListener.LISTEN_SERVICE_STATE);
		telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		/*
		Bundle bundle = intent.getExtras();
		String number = bundle.getString("incoming_number");
	
		Intent myIntent = new Intent(context,AlwaysOnTopService.class);
		myIntent.putExtra("tel", number);
		context.startService(myIntent);
		
		Bundle bundle = intent.getExtras();
		String number = bundle.getString("incoming_number");

		Intent testActivityIntent = new Intent(context,
				CallCatcherActivity.class);
		testActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		testActivityIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
		testActivityIntent.putExtra("tel", number);
		context.startActivity(testActivityIntent);
*/
	}

}
