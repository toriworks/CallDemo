package com.kolon.calldemo.protocol;

import java.util.HashMap;

import android.util.Log;

public class HttpRequestVO {
	private String url;
	private String m_sNameValue = "";
	private HashMap<String, String> headerInfo;
	private HashMap<String, String> paramInfo;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public HashMap<String, String> getHeaderInfo() {
		return headerInfo;
	}
	public void setHeaderInfo(HashMap<String, String> headerInfo) {
		this.headerInfo = headerInfo;
	}
	public HashMap<String, String> getParamInfo() {
		return paramInfo;
	}
	public void setParamInfo(HashMap<String, String> paramInfo) {
		this.paramInfo = paramInfo;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HttpRequestVO [url=");
		builder.append(url);
		builder.append(", headerInfo=");
		builder.append(headerInfo);
		builder.append(", paramInfo=");
		builder.append(paramInfo);
		builder.append("]");
		return builder.toString();
	}
	
	public void addNameValue(String sName, String sValue) {
		 String sTemp = " _le_NameValue_se_ " +
		    "_le_Value_se_"+ sValue + "_le_/Value_se_ " +
		    "_le_Name_se_" + sName + "_le_/Name_se_ " +
		    "_le_/NameValue_se_ ";
		 
		 m_sNameValue += sTemp;
		 return;
	 }
	 
	 public String getPreTail() {
		 return "  _le_/items_se_ " +
		    "_le_/HttpParameterCollection_se_";
	 }
	 
	 public String getParameter() {
		 String strParamter = null;
		 
		 String sBody = "_le_HttpParameterCollection xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"_se_ _le_items_se_ ";
		 strParamter = sBody + m_sNameValue + getPreTail();
		 
		 Log.d("Parameter","getParameter = " + strParamter);
		 return strParamter;
	 }
}
