package com.kolon.calldemo.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

public class NetworkAsyncTask2 extends
		ProcessDialogAsyncTask<HttpRequestVO, Void, InputStream> {
	protected HttpRequestVO requestVo;
	protected Map<String, List<String>> headers;
	public static String mCookie;
	public static final String COOKIE = "Cookie";
//	public static final String GET_COOKIE = "Set-Cookie";
	public static final String GET_COOKIE = "set-cookie";	// 폰마다 헤더값이 틀리므로 꼭 소문자로 작성
	
	public String multiFileName;	// 업로드 할 파일명

	public static final int SEND_GET = 0;
	public static final int SEND_POST = 1;
	public static final int SEND_MULTIPART_POST = 2;

	private int SEND_METHOD;

	public Handler handler;

	public NetworkAsyncTask2(Context context) {
		super(context);

		this.SEND_METHOD = SEND_POST;
	}

	public NetworkAsyncTask2(Context context, int method) {
		super(context);

		this.SEND_METHOD = method;
	}

	@Override
	protected void onPreExecute() {
		handler = new Handler();
		super.onPreExecute();
	}

	@Override
	protected InputStream doInBackground(HttpRequestVO... params) {
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
							in = requestor.sendPost2();
							break;
						case SEND_MULTIPART_POST:
							in = requestor.sendMultipartPost();
							break;
						default:
							in = requestor.sendGet();
							break;
						}
//						System.out.println("header : "+headers);
					} catch (IOException e) {
						e.printStackTrace();
//						onNetworkError();
					} catch (Exception e) {
						e.printStackTrace();
//						onNetworkError();
					}
				}
			}
		}
		return in;
	}

	@Override
	protected void onPostExecute(InputStream result) {
		super.onPostExecute(result);
		if (result != null) {
//			System.out.println("result not null");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					result));

			String line;
			StringBuilder strBuilder = new StringBuilder();
			try {
				while ((line = reader.readLine()) != null) {
					strBuilder.append(line);
					//System.out.println(line);
				}
				reader.close();
			} catch (IOException e) {
				//e.printStackTrace();
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
			finally {
				try {
					reader.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}

			System.out.println("res : "+strBuilder.toString());
			onPostExecute(strBuilder.toString());
		} else {
			onNetworkError();
//			System.out.println("result null");
		}
	}
	
	protected void onPostExecute(String result) {
//		System.out.println("str "+result.length());
	}

	protected void onNetworkError() {
		if (handler != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getContext(), "Network Data를 일시적으로 가져올 수 없습니다.\n다시 시도해 주세요.", 500)
							.show();
				}
			});
		}
	}
}
