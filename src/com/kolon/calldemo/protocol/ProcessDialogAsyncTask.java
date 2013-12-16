package com.kolon.calldemo.protocol;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class ProcessDialogAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {
	protected boolean dontDismiss = false;
	protected ProgressDialog waitDialog = null;
	protected Context context;
	private int messageResourceId;

	// 다이얼로그 보여줄지 여부
	public boolean isWaitDialog = true;

	public ProcessDialogAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		try {
			if (isWaitDialog) {
				waitDialog = new ProgressDialog(context);
				waitDialog.setMessage("Loading...");
				waitDialog.setCancelable(false);
				waitDialog.setIndeterminate(true);
				waitDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		onFinish();
	}

	protected void onFinish() {
		try {
			if (waitDialog != null && !dontDismiss)
				waitDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Context getContext() {
		return context;
	}
}
