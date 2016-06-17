package com.jacky.app.pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigManager {

	private static final String CONFIG = "config.ini";
	private static final String DEFAULT = "website";

	public ConfigManager() {
		init();
	}
	
	private JSONObject mRootNode;
	private LinkedList<String> qudaoList;
	
	public File getLastApkPath() {
		String s = mRootNode.optString("lastapk");
		if(s == null) return null;
		return new File(s);
	}
	
	public void saveLastApkPath(String path) {
		try {
			mRootNode.put("lastapk", path);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getList() {
		if(qudaoList == null) {
			qudaoList = new LinkedList<>();
			JSONArray a = mRootNode.optJSONArray("channel");	
			if(a == null) {
				qudaoList.add(DEFAULT);
			} else {
				int size = a.length();
				for(int i = 0;i < size;i++ ) {
					qudaoList.add(a.optString(i));
				}
			}
		}
		return qudaoList;
	}

	public SignInfo getSignInfo() {
		JSONObject obj = mRootNode.optJSONObject("sign");
		if(obj == null) return null;
		
		SignInfo info = new SignInfo();
		info.path = obj.optString("path");
		info.pwd = obj.optString("pwd");
		info.alias = obj.optString("alias");
		info.apwd = obj.optString("apwd");
		return info;
	}
	
	public void saveSignInfo(String path, String pwd, String alias, String apwd) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("path", path);
			obj.put("pwd", pwd);
			obj.put("alias", alias);
			obj.put("apwd", apwd);
			mRootNode.put("sign", obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		File file = new File(CONFIG);
		if(!file.exists()) {
			mRootNode = new JSONObject();
			return;
		}
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			if(null == line) {
				mRootNode = new JSONObject();
			} else {
				mRootNode = new JSONObject(line);
			}
		} catch (IOException|JSONException e) {
			e.printStackTrace();
			mRootNode = new JSONObject();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} //end try...catch...
	}
	
	public void saveToConfigFile() {
		File file = new File(CONFIG);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		try {
			mRootNode.put("channel", new JSONArray(qudaoList));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		BufferedWriter writer = null;
		
		try {
			writer= new BufferedWriter(new FileWriter(file));
			writer.write(mRootNode.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}//end try...catch...
	}
	
	public void add(String qudao) {
		qudaoList.add(qudao);
	}
	
	public void remove(String qudao) {
		qudaoList.remove(qudao);
	}
	
	public void remove(int index) {
		qudaoList.remove(index);
	}
}
