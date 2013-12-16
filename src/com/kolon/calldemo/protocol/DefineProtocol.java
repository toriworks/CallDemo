package com.kolon.calldemo.protocol;

public class DefineProtocol {
	
	final public static String Commercial_url = "http://203.225.255.146:8081/";
	final public static String url_service_search = "business/app/usersearch.do";
	
	final public static String url_image_main ="http://gw.kolon.com";
    final public static String url_image_search ="/KolonAppUserImage.asp?";
	/*
	final public static String Commercial_url = "http://203.225.3.52:8000";
	final public static String url_service_search = "/search/user_search.aspx";
    */
	// Action(XML)
	public static String REQ_LOGIN_ACTION = "ProcessLogin";
	public static String REQ_SYNC_ACTION = "Sync";
	public static String REQ_RESET_ACTION = "Reset";
	public static String REQ_RETRIEVE_ACTION = "Retrieve";
	public static String REQ_REQUEST_ACTION = "Request";
	public static String REQ_APPROVE_ACTION = "Approve";
	public static String REQ_REGISTER_ACTION = "Register";
	public static String REQ_CRBCDELETE = "CRBCdelete";
	public static String REQ_CRBCINSERT = "CRBCinsert";

	// Request XML
	public static String REQ_ACTION_ID = "action";
	public static String REQ_XMLPARAM = "xmlparam";
	public static String REQ_CONDITION = "condition";

	public static String urlURL;
	public static String urlSearch;
	public static String urlImageSearch;

	public static final int HTTP_TIMEOUT = 3 * 1000;

	public static void init_url() {
		urlURL = Commercial_url;
		urlSearch = Commercial_url + url_service_search;
		urlImageSearch = url_image_main + url_image_search;
	}
}
