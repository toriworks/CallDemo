package com.kolon.calldemo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.kolon.calldemo.protocol.DefineProtocol;
import com.kolon.calldemo.protocol.HttpRequestVO;
import com.kolon.calldemo.protocol.NetworkAsyncTask;

public class SearchResultService extends Service {
	private View view;
	public static LinearLayout llSearchResultAll;
	public static ViewAnimator mTopBarSwitcher;
	private LinearLayout llSearchPart;
	private LinearLayout llSearchMsg;
	private LinearLayout llResultPart;
	private TextView tvSearchMsg;
	private TextView tvCompanyNm;
	private TextView tvOrgunit;
	private TextView tvName;
	private TextView tvJobtitle;
	private String telNumber;
	public static Bitmap imgBitmap = null;

	@Override
	public IBinder onBind(Intent arg0) { return null; }

	@Override
	public void onCreate() {
		super.onCreate();

		DefineProtocol.init_url();
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		String strTelNumber = null;
		telNumber=(String) intent.getExtras().get("tel");

		createUI();

		if(telNumber.length() > 0) {
			TextView tvTelNumber = (TextView)view.findViewById(R.id.id_search_tel_no);
			strTelNumber = PhoneNumberUtils.formatNumber(telNumber);
			tvTelNumber.setText(strTelNumber);

			setViewSearching("전화정보 검색중...");
			Request(telNumber);
		}

		return 1; 
	}

	private void createUI() {
		view = ((LayoutInflater)getSystemService("layout_inflater")).inflate(R.layout.search_result_activity, null);
		llSearchResultAll = (LinearLayout)view.findViewById(R.id.id_ll_search_result_all);
		llSearchPart = ((LinearLayout)view.findViewById(R.id.id_ll_search_part));
		tvSearchMsg = ((TextView)view.findViewById(R.id.id_search_ing_msg));
		llSearchMsg = ((LinearLayout)view.findViewById(R.id.id_ll_search_msg));
		llResultPart = ((LinearLayout)view.findViewById(R.id.id_ll_result_part));

		tvCompanyNm = ((TextView)view.findViewById(R.id.id_tv_result_company));
		tvOrgunit = ((TextView)view.findViewById(R.id.id_tv_result_orgunit));
		tvName = ((TextView)view.findViewById(R.id.id_tv_result_name));
		tvJobtitle = ((TextView)view.findViewById(R.id.id_tv_result_jobtitle));

		WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		LayoutParams.gravity = Gravity.TOP;
		LayoutParams.width = WindowManager.LayoutParams.FILL_PARENT;
		LayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		LayoutParams.alpha = 0.8F;
		((WindowManager)getSystemService("window")).addView(view, LayoutParams);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(view != null) {
			((WindowManager) getSystemService("window")).removeView(view);
			view = null;
		}
		
		if(imgBitmap != null) {
			imgBitmap.recycle();
			imgBitmap = null;
		}
	}

	private void Request(String strTel) {
		HttpRequestVO requestVO = new HttpRequestVO();
		HashMap<String, String> paramInfo = new HashMap<String, String>();

		requestVO.setUrl(DefineProtocol.urlSearch);
		requestVO.addNameValue(DefineProtocol.REQ_CONDITION, strTel);

		paramInfo.put(DefineProtocol.REQ_XMLPARAM, requestVO.getParameter());

		requestVO.setParamInfo(paramInfo);

		new RequestAsyncTask(getBaseContext()).execute(requestVO);
	}


	class RequestAsyncTask extends NetworkAsyncTask {

		public RequestAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {
			super.isWaitDialog = false;
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			super.onPostExecute(result);

			if(result.size() == 0) {
				setResultNotSearchView();
				return;
			}

			setView(result);
		}

		@Override
		protected void onNetworkError() {
			super.onNetworkError();
		}

		void setResultNotSearchView() {
			llSearchPart.setVisibility(View.GONE);
			llSearchMsg.setVisibility(View.VISIBLE);
		}

		void setView(ArrayList<HashMap<String, String>> result) {
			if(view == null)
				return;
			
			llResultPart.setVisibility(View.VISIBLE);
			llSearchPart.setVisibility(View.GONE);
			tvCompanyNm.setText(result.get(0).get("ORGNAME"));
			tvOrgunit.setText(result.get(0).get("ORGUNIT"));
			tvName.setText(result.get(0).get("NAME"));
			tvJobtitle.setText(result.get(0).get("JOBTITLE"));
			
			String strUrl = "http://gw.kolon.com/KolonAppUserImage.asp?" + "userid=" + result.get(0).get("IKENID");
			new DownloadPhotoTask().execute(strUrl);
		}
	}

	////////////////////////
	public Bitmap GetImageFromURL(String url) throws Exception {
		HttpGet httpRequest = null;

		URL url1 = null;
		try {
			url1 = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpRequest = new HttpGet(url1.toURI());
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = (HttpResponse)httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		InputStream instream = bufHttpEntity.getContent();
		Bitmap bm = BitmapFactory.decodeStream(instream);

		ByteArrayOutputStream  byteArray = new ByteArrayOutputStream();
		bm.compress(CompressFormat.PNG, 100, byteArray);
		return bm;
	}
	
	@SuppressLint("NewApi")
	class DownloadPhotoTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... params) {
			String strUrl = params[0];
			try {
				imgBitmap = GetImageFromURL(strUrl);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return imgBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			ImageView imgView = (ImageView)view.findViewById(R.id.id_iv_result_image);
			if (imgBitmap != null) {
				imgView.setImageBitmap(imgBitmap);
			}
		}
	}

	private void setViewSearching(String str) {
		llSearchPart.setVisibility(View.VISIBLE);
		llSearchMsg.setVisibility(View.GONE);
		tvSearchMsg.setText(str);
	}
}
