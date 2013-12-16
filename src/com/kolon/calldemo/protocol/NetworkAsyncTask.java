package com.kolon.calldemo.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.kolon.calldemo.parser.DataManager2;

public class NetworkAsyncTask extends
		ProcessDialogAsyncTask<HttpRequestVO, InputStream, ArrayList<HashMap<String, String>>> {
	protected HttpRequestVO requestVo;
	protected Map<String, List<String>> headers;
	public static String mCookie;
	public static final String COOKIE = "Cookie";
	public static final String GET_COOKIE = "set-cookie";	// 폰마다 헤더값이 틀리므로 꼭 소문자로 작성
	
	public String multiFileName;	// 업로드 할 파일명

	public static final int SEND_GET = 0;
	public static final int SEND_POST = 1;
	public static final int SEND_MULTIPART_POST = 2;
	private int SEND_METHOD;
	
	protected Context context;
	public Handler handler;

	public NetworkAsyncTask(Context context) {
		super(context);
		this.context = context;
		this.SEND_METHOD = SEND_POST;
	}

	public NetworkAsyncTask(Context context, int method) {
		super(context);

		this.SEND_METHOD = method;
	}

	@Override
	protected void onPreExecute() {
		handler = new Handler();
		super.onPreExecute();
	}

	@Override
	protected ArrayList<HashMap<String, String>> doInBackground(HttpRequestVO... params) {
		InputStream in = null;
		if (params != null && params.length > 0) {
			requestVo = (HttpRequestVO) params[0];

			String url = requestVo.getUrl();
			if (url != null) {
				HttpRequestor requestor = null;
				try {
					HashMap<String, String> headerInfo = requestVo
							.getHeaderInfo();
					
					requestor = new HttpRequestor(new URL(url), headerInfo);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				HashMap<String, String> paramMap = requestVo.getParamInfo();
				if (requestor != null) {
					requestor.setContext(context);
					Iterator<String> keys = paramMap.keySet().iterator();
					String key;
					while (keys.hasNext()) {
						key = keys.next();
						requestor.addParameter(key, paramMap.get(key));
					}

					try {
						switch (SEND_METHOD) {
						case SEND_POST:
							in = requestor.sendPost();
							break;
						case SEND_MULTIPART_POST:
							//in = requestor.sendMultipartPost();
							break;
						default:
							//in = requestor.sendGet();
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
						onNetworkError();
					} catch (Exception e) {
						e.printStackTrace();
						onNetworkError();
					}
				}
			}
		}
		
		DataManager2 dataManager = new DataManager2(context);
		ArrayList<HashMap<String, String>> arrData = new ArrayList<HashMap<String, String>>();
		arrData = dataManager.extractData(in);
		
		return arrData;
	}

	@Override
	protected void onPostExecute(ArrayList<HashMap<String, String>> arrData) {
		super.onPostExecute(arrData);
	}

	protected void onNetworkError() {
		if (handler != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getContext(), "통신 중 장애가 발생하였습니다.\n다시 시도해 주세요.", 500).show();
				}
			});
		}
	}
}
