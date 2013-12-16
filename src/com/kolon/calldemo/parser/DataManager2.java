package com.kolon.calldemo.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

public class DataManager2 {
	final private static String TAG ="MKWAY";
	private final Context mCtx;
	
	public DataManager2(Context ctx) {
		this.mCtx = ctx;
	}
	
	public ArrayList extractData(InputStream sResponseData) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;
		try {
			saxparser = factory.newSAXParser();
			XMLReader xmlReader    = saxparser.getXMLReader();
	        ParserHandler handler  = new ParserHandler(mCtx);
	        xmlReader.setContentHandler(handler);
	        xmlReader.parse(new InputSource(sResponseData));
	        return handler.arrData;
	        
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	
		return null;
	}


}
