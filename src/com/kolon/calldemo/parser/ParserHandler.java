package com.kolon.calldemo.parser;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class ParserHandler extends DefaultHandler {

	final private static String TAG ="MemberSearch";
	private Context mCtx;

	private ArrayList <String> arrDataInfo = new ArrayList<String>();
	private ArrayList<HashMap<String, String>> arrColumnInfo = new ArrayList<HashMap<String, String>>();
	public  ArrayList<HashMap<String, String>> arrData = new ArrayList<HashMap<String, String>>();

	public ParserHandler(Context ctx) {
		this.mCtx = ctx;
	}

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void endDocument() throws SAXException {

	}

	@Override
	public void startElement(String url, String lName, String ele,
			Attributes attributes) throws SAXException {

		if(attributes.getLength() <= 0) {
			return;
		}

		String[] attrQNames = new String[attributes.getLength()];
		String[] attrValues = new String[attributes.getLength()]; 

		for(int i=0;i<attributes.getLength();i++){
			attrQNames[i]=attributes.getQName(i).trim();
			attrValues[i]=attributes.getValue(i).trim();
		}

		setData(lName, attrQNames, attrValues);		 
	}

	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException {

	}

	private void setData(String lName, String[] attrQNames, String[] attrValues) {
		if(lName.equalsIgnoreCase("column")) {
			extractColumnInfo(attrQNames, attrValues);
		} else if(lName.equalsIgnoreCase("r")) {
			extractTableDataset(attrQNames, attrValues);
		}
	}

	private void extractColumnInfo(String[] attrQNames, String[] attrValues) {
		if(attrQNames.length <= 0)
			return;

		HashMap<String, String> mapColumnInfo = new HashMap<String, String>();

		for(int i = 0 ; i < attrQNames.length ; i++) {
			mapColumnInfo.put(String.valueOf(i) , attrValues[i].toString());
		}

		arrColumnInfo.add(mapColumnInfo);
		mapColumnInfo = null;
	}

	private void extractTableDataset(String[] attrQNames, String[] attrValues) {
		if(attrQNames.length <= 0)
			return;

		HashMap<String, String> mapData = new HashMap<String, String>();

		for(int i = 0 ; i < attrQNames.length ; i++) {
			String sIndex = arrColumnInfo.get(i).get("0");
			mapData.put(sIndex, attrValues[i].toString());
		}

		arrData.add(mapData);
		mapData = null;
	}
}
