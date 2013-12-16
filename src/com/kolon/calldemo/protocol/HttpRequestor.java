package com.kolon.calldemo.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public final class HttpRequestor {
	private Context mCtx;
	private static final int HTTP_TIMEOUT = 30 * 1000;
	
	public static final String CRLF = "\r\n";
	public Map<String, List<String>> headerFields; // 응답 Header
	public static String mCookie; 
	/**
	 * 연결할 URL
	 */
	private URL targetURL;

	/**
	 * 파라미터 목록을 저장하고 있다. 파라미터 이름과 값이 차례대로 저장된다.
	 */
	private HashMap<String, String> headerInfo;
	private ArrayList<Object> list;

	/**
	 * HttpRequest를 생성한다.
	 * 
	 * @param target
	 *            HTTP 메시지를 전송할 대상 URL
	 */
	public HttpRequestor(URL target) {
		this(target, 20);
	}

	public HttpRequestor(URL target, HashMap<String, String> headerInfo) {
		this(target, 20);

		this.headerInfo = headerInfo;
	}

	/**
	 * HttpRequest를 생성한다.
	 * 
	 * @param target
	 *            HTTP 메시지를 전송할 대상 URL
	 */
	public HttpRequestor(URL target, int initialCapicity) {
		this.targetURL = target;
		this.list = new ArrayList<Object>(initialCapicity);
	}

	public Map<String, List<String>> getHeaderFields() {
		return headerFields;
	}

	public void setContext(Context mCtx) {
		this.mCtx = mCtx;
	}
	/**
	 * 파라미터를 추가한다.
	 * 
	 * @param parameterName
	 *            파라미터 이름
	 * @param parameterValue
	 *            파라미터 값
	 * @exception IllegalArgumentException
	 *                parameterValue가 null일 경우
	 */
	public void addParameter(String parameterName, String parameterValue) {
		if (parameterValue == null)
			throw new IllegalArgumentException("parameterValue can't be null!");

		list.add(parameterName);
		list.add(parameterValue);
	}

	/**
	 * 파일 파라미터를 추가한다. 만약 parameterValue가 null이면(즉, 전송할 파일을 지정하지 않는다면 서버에 전송되는
	 * filename 은 "" 이 된다.
	 * 
	 * @param parameterName
	 *            파라미터 이름
	 * @param parameterValue
	 *            전송할 파일
	 * @exception IllegalArgumentException
	 *                parameterValue가 null일 경우
	 */
	public void addFile(String parameterName, File parameterValue) {
		// paramterValue가 null일 경우 NullFile을 삽입한다.
		if (parameterValue == null) {
			list.add(parameterName);
			list.add(new NullFile());
		} else {
			list.add(parameterName);
			list.add(parameterValue);
		}
	}

	/**
	 * 지금까지 지정한 파라미터를 모두 삭제한다.
	 */
	public void clearParameters() {
		list.clear();
	}
	
	public void getCookie(HttpURLConnection conn) {
		if(mCtx != null) {
			CookieSyncManager.createInstance(mCtx);
			String getCookie = CookieManager.getInstance().getCookie(conn.getURL().toString());
			if(getCookie!=null) {
				conn.setRequestProperty("cookie", getCookie);	// 변수는 꼭 소문자여야 함
			}
		}
	}
	
	public void setCookie(HttpURLConnection conn) {
		if(mCtx != null) {
			CookieSyncManager.createInstance(mCtx);
			String setCooKie = conn.getHeaderField("set-cookie");

			if(setCooKie != null) {
				String[] sCookieList = setCooKie.split(";");
				for(String sCookie : sCookieList) {
//					System.out.println("list : "+sCookie);
				}
				CookieManager.getInstance().setCookie(conn.getURL().toString(), setCooKie);
				if(setCooKie.startsWith("JSESSIONID")) {
					mCookie = setCooKie;
				}
			}
		}
	}

	/**
	 * GET 방식으로 대상 URL에 파라미터를 전송한 후 응답을 InputStream으로 리턴한다.
	 * 
	 * @return InputStream
	 */
	public InputStream sendGet() throws IOException {
		String paramString = null;
		if (list.size() > 0)
			paramString = "?" + encodeString(list);
		else
			paramString = "";

		HttpURLConnection conn = (HttpURLConnection) UrlConnectionFactory.getInstance(
				new URL(targetURL.toExternalForm() + paramString), headerInfo);
		conn.setDoOutput(true);
		getCookie(conn);
		InputStream in = conn.getInputStream();
		headerFields = conn.getHeaderFields();
		setCookie(conn);
		
		return in;
	}

	public InputStream sendPost() throws IOException {
		HttpPost request = new HttpPost(targetURL.toExternalForm());
		//HttpPost request = new HttpPost("http://www.moyacall.com/WHO/CallAndroid2");
		//HttpPost request = new HttpPost("http://m.kolon.com:8080/business/app/servAppVer.do?appNm=MembersSearch");
		
		ArrayList<NameValuePair> nameValue = new ArrayList<NameValuePair>();
		nameValue.add(new BasicNameValuePair((String)list.get(0), (String)list.get(1)));
		
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValue, "UTF-8");
        request.setEntity(formEntity);
        
		HttpClient client = new DefaultHttpClient();
        
		HttpParams params = client.getParams(); 
		params.setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
		HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
		
		HttpResponse response = client.execute(request);
		
		InputStream in = null;
		in = response.getEntity().getContent();
		
        return in;
	}

	public InputStream sendPost2() throws IOException {
		String paramString = null;
		if (list.size() > 0)
			paramString = encodeString(list);
		else
			paramString = "";

		HttpURLConnection conn = (HttpURLConnection) UrlConnectionFactory
				.getInstance(targetURL, headerInfo);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",
				Integer.toString(paramString.length()));
		// con.setConnectTimeout(YTNApp.CONNECTION_TIMEOUT);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		
		getCookie(conn);

		DataOutputStream out = null;
		try {
			out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(paramString);
			out.flush();
		} finally {
			if (out != null)
				out.close();
		}
		InputStream in = null;
		try {
			in = conn.getInputStream();
			headerFields = conn.getHeaderFields();
		} catch (Exception e) {
			in = conn.getErrorStream();
			BufferedReader re = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = re.readLine()) != null) {
//				System.out.println(line);
			}
		}
		setCookie(conn);
		return in;
	}
	
	/**
	 * multipart/form-data 인코딩을 사용하여 대상 URL에 데이터를 전송한 후에 응답을 InputStream으로 리턴한다.
	 * 
	 * @return InputStream
	 */
	public InputStream sendMultipartPost() throws IOException {
		HttpURLConnection conn = (HttpURLConnection) UrlConnectionFactory
				.getInstance(targetURL, headerInfo);

		// Delimeter 생성
		String delimeter = makeDelimeter();

		byte[] newLineBytes = CRLF.getBytes();
		byte[] delimeterBytes = delimeter.getBytes();
		byte[] dispositionBytes = "Content-Disposition: form-data; name="
				.getBytes();
		byte[] quotationBytes = "\"".getBytes();
		byte[] contentTypeBytes = "Content-Type: application/octet-stream"
				.getBytes();
		byte[] fileNameBytes = "; filename=".getBytes();
		byte[] twoDashBytes = "--".getBytes();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + delimeter);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		
		getCookie(conn);

		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(conn.getOutputStream());

			Object[] obj = new Object[list.size()];
			list.toArray(obj);

			for (int i = 0; i < obj.length; i += 2) {
				// Delimeter 전송
				out.write(twoDashBytes);
				out.write(delimeterBytes);
				out.write(newLineBytes);
				// 파라미터 이름 출력
				out.write(dispositionBytes);
				out.write(quotationBytes);
				out.write(((String) obj[i]).getBytes("utf-8"));
				out.write(quotationBytes);
				if (obj[i + 1] instanceof String) {
					// String 이라면
					out.write(newLineBytes);
					out.write(newLineBytes);
					// 값 출력
					out.write(((String) obj[i + 1]).getBytes("utf-8"));
					out.write(newLineBytes);
				} else {
					// 파라미터의 값이 File 이나 NullFile인 경우
					if (obj[i + 1] instanceof File) {
						File file = (File) obj[i + 1];
						// File이 존재하는 지 검사한다.
						out.write(fileNameBytes);
						out.write(quotationBytes);
						out.write(file.getAbsolutePath().getBytes("utf-8"));
						out.write(quotationBytes);
					} else {
						// NullFile 인 경우
						out.write(fileNameBytes);
						out.write(quotationBytes);
						out.write(quotationBytes);
					}
					out.write(newLineBytes);
					out.write(contentTypeBytes);
					out.write(newLineBytes);
					out.write(newLineBytes);
					// File 데이터를 전송한다.
					if (obj[i + 1] instanceof File) {
						File file = (File) obj[i + 1];
						// file에 있는 내용을 전송한다.
						BufferedInputStream is = null;
						try {
							is = new BufferedInputStream(new FileInputStream(
									file));
							byte[] fileBuffer = new byte[1024 * 8]; // 8k
							int len = -1;
							while ((len = is.read(fileBuffer)) != -1) {
								out.write(fileBuffer, 0, len);
							}
						} finally {
							if (is != null)
								try {
									is.close();
								} catch (IOException ex) {
								}
						}
					}
					out.write(newLineBytes);
				} // 파일 데이터의 전송 블럭 끝
				if (i + 2 == obj.length) {
					// 마지막 Delimeter 전송
					out.write(twoDashBytes);
					out.write(delimeterBytes);
					out.write(twoDashBytes);
					out.write(newLineBytes);
				}
			} // for 루프의 끝

			out.flush();
		} finally {
			if (out != null)
				out.close();
		}
		InputStream in = conn.getInputStream();
		headerFields = conn.getHeaderFields();
		setCookie(conn);
		return in;
	}

	/**
	 * 지정한 ArrayList에 저장되어 있는 파라미터&값 목록을 application/x-www-form-urlencoded MIME에
	 * 맞춰서 인코딩한다. 파라미터의 값의 타입이 File일 경우에는 그 파라미터를 무시하고 다음 파라미터를 처리한다.
	 * 
	 * @param parameters
	 *            파라미터 이름과 파라미터 값을 저장하고 있는 객체
	 * @return 인코딩된 String
	 */
	private static String encodeString(ArrayList<Object> parameters) {
		StringBuffer sb = new StringBuffer(256);

		Object[] obj = new Object[parameters.size()];
		parameters.toArray(obj);

		try {
			for (int i = 0; i < obj.length; i += 2) {
				if (obj[i + 1] instanceof File
						|| obj[i + 1] instanceof NullFile)
					continue;
				sb.append(URLEncoder.encode((String) obj[i], "utf-8"));
				sb.append('=');
				sb.append(URLEncoder.encode((String) obj[i + 1], "utf-8"));

				if (i + 2 < obj.length)
					sb.append('&');
			}
		} catch (Exception e) {
		}

		return sb.toString();
	}

	/**
	 * multipart/form-data 로 데이터를 전송할 때 사용되는 딜리미터를 생성한다.
	 * <p>
	 * 임의로 생성하지 않고 매번 같은 딜리미터를 생성한다.
	 */
	private static String makeDelimeter() {
		return "---------------------------7d115d2a20060c";
	}

	/**
	 * 전송할 파일을 지정하지 않은 경우에 사용되는 클래스
	 */
	private class NullFile {
		NullFile() {
		}

		public String toString() {
			return "";
		}
	}

	private static class UrlConnectionFactory {

		public static HttpURLConnection getInstance(URL url,
				HashMap<String, String> headerInfo) throws IOException {

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(HTTP_TIMEOUT);
			conn.setReadTimeout(HTTP_TIMEOUT);
			if (headerInfo != null) {
				Iterator<String> keys = headerInfo.keySet().iterator();
				String key;
				while (keys.hasNext()) {
					key = keys.next();
					conn.setRequestProperty(key, headerInfo.get(key));
				}
			}

			return conn;
		}
	}
}
