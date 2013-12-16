package com.kolon.calldemo.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class DataManager {
	final private static String TAG ="[DataManager]";
	
	public static String m_strTable = null;
	private ArrayList <String> arrDataInfo = new ArrayList<String>();
	private ArrayList<HashMap<String, String>> arrColumnInfo = new ArrayList<HashMap<String, String>>();
	public  ArrayList<HashMap<String, String>> arrData = new ArrayList<HashMap<String, String>>();
	
	private static HashMap<String, String> mapColumnInfo = new HashMap<String, String>();
	
	public boolean extractData(String sResponseData) {
		try {
			// DOM Factory
			DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
			// DOM Builder
			DocumentBuilder	db = factory.newDocumentBuilder();
			// DOM
			String str = sResponseData.substring(1);
			Document dom = db.parse(new InputSource(new StringReader(str)));
			// root
			Element root = dom.getDocumentElement();
			NodeList nl = root.getElementsByTagName("DocumentElement");
			if(nl == null)
			{
				Log.e(TAG, "XML Error");
				return false;
			}
			
			getTableInfo(root);
			extractColumnInfo(root);
			extractTableDataset(root);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	
	public void getTableInfo(Element root) {
		// get a list
		NodeList nl = root.getElementsByTagName("table");
		Element e1 = (Element)nl.item(0);
		m_strTable = e1.getAttribute("name");
	}
	
	public void extractColumnInfo(Element root) {
		NodeList nl = root.getElementsByTagName("table");
		Element e1 = (Element)nl.item(0);
		String sTable = e1.getAttribute("name");
		
		NodeList items = e1.getElementsByTagName("column");
		
		for(int i = 0 ; i < items.getLength() ; i++) {
			String sName = "", sType = "", sLength = "", sNull = "", sCaption = "", sDescription = "";
			mapColumnInfo = new HashMap<String, String>();
			
			Element e2 = (Element)items.item(i);
			
			// Name
			if(e2.getAttribute("name").length() > 0) {
				sName = e2.getAttribute("name");
			}
			mapColumnInfo.put("0", sName);
			
			// type
			if(e2.getAttribute("type").length() > 0) {
				sType = e2.getAttribute("type");
			}
			mapColumnInfo.put("1", sType);
			
			// length
			if(e2.getAttribute("length").length() > 0) {
				sLength = e2.getAttribute("length");
			}
			mapColumnInfo.put("2", sLength);
			
			// null
			if(e2.getAttribute("null").length() > 0) {
				sNull = e2.getAttribute("null");
			}
			mapColumnInfo.put("3", sNull);
			
			// caption
			if(e2.getAttribute("caption").length() > 0) {
				sCaption = e2.getAttribute("caption");
			}
			mapColumnInfo.put("4", sCaption);
			
			// description
			if(e2.getAttribute("description").length() > 0) {
				sDescription = e2.getAttribute("description");
			}
			mapColumnInfo.put("5", sDescription);
			
			arrColumnInfo.add(mapColumnInfo);
		}
	}
	
	public void extractTableDataset(Element root) {
			
		int nCol = arrColumnInfo.size();
		
		dataSetInfo(root);

		for(int iud = 0 ; iud < arrDataInfo.size(); iud++) {
			String sDataTag = arrDataInfo.get(iud);
			NodeList nl = root.getElementsByTagName(sDataTag);
			Element e1 = (Element)nl.item(0);
			NodeList items = e1.getElementsByTagName("r");
			
			for(int i = 0 ; i < items.getLength() ; i++) {
				HashMap<String, String> mapData = new HashMap<String, String>();
				for(int j = 0 ; j < nCol ; j++) {
					String sData = "";
					String sIndex = arrColumnInfo.get(j).get("0");
					String sTag = "c" + j;
					
					Element e2 = (Element)items.item(i);
					
					if(e2.getAttribute(sTag).length() > 0) {
						sData = e2.getAttribute(sTag);
					}
					mapData.put(sIndex, e2.getAttribute(sTag));
				}
				arrData.add(mapData);
				mapData = null;
			}			
		}
	}
	
	private void dataSetInfo(Element root) {
		NodeList nl = root.getElementsByTagName("dataset");
		if(nl != null) {
			arrDataInfo.add("dataset");
		} else {
			arrDataInfo.add("insert");
			arrDataInfo.add("update");
			arrDataInfo.add("delete");
		}
	}
}
