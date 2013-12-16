package com.kolon.calldemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {
	private Context mContext = null;
	private String TAG = "CallCatcher";

	public MyPhoneStateListener(Context context) {
		mContext = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			Log.i(TAG,
					"MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_IDLE "
							+ incomingNumber);
			if(isServiceRunningCheck() == true) {
				Intent intent1 = new Intent(mContext, SearchResultService.class);
				mContext.stopService(intent1);
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.i(TAG,
					"MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_OFFHOOK "
							+ incomingNumber);
			if(isServiceRunningCheck() == true) {
				Intent intent2 = new Intent(mContext, SearchResultService.class);
				mContext.stopService(intent2);
			}
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			Log.i(TAG,
					"MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_RINGING "
							+ incomingNumber);
			if(isServiceRunningCheck() == false) {
				Intent intent = new Intent(mContext, SearchResultService.class);
				intent.putExtra("tel", incomingNumber);
				mContext.startService(intent);
			}
			break;
		default:
			Log.i(TAG,
					"MyPhoneStateListener->onCallStateChanged() -> default -> "
							+ Integer.toString(state));
			break;
		}
	}

	@Override
	public void onServiceStateChanged(ServiceState serviceState) {
		switch (serviceState.getState()) {
		case ServiceState.STATE_IN_SERVICE:
			Log.i(TAG,
					"MyPhoneStateListener->onServiceStateChanged() -> STATE_IN_SERVICE");
			serviceState.setState(ServiceState.STATE_IN_SERVICE);
			break;
		case ServiceState.STATE_OUT_OF_SERVICE:
			Log.i(TAG,
					"MyPhoneStateListener->onServiceStateChanged() -> STATE_OUT_OF_SERVICE");
			serviceState.setState(ServiceState.STATE_OUT_OF_SERVICE);
			break;
		case ServiceState.STATE_EMERGENCY_ONLY:
			Log.i(TAG,
					"MyPhoneStateListener->onServiceStateChanged() -> STATE_EMERGENCY_ONLY");
			serviceState.setState(ServiceState.STATE_EMERGENCY_ONLY);
			break;
		case ServiceState.STATE_POWER_OFF:
			Log.i(TAG,
					"MyPhoneStateListener->onServiceStateChanged() -> STATE_POWER_OFF");
			serviceState.setState(ServiceState.STATE_POWER_OFF);
			break;
		default:
			Log.i(TAG,
					"MyPhoneStateListener->onServiceStateChanged() -> default -> "
							+ Integer.toString(serviceState.getState()));
			break;
		}
	}
	
	public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
        	String str = service.service.getClassName();
        	Log.d(TAG, str);
            if ("com.kolon.calldemo.SearchResultService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
}
}
