package com.wangaho.testpulltorefresh.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

public class CommonUtils {
	 @SuppressLint("NewApi")
	public static <Params, Progress, Result> void executeAsyncTask(
	            AsyncTask<Params, Progress, Result> task, Params... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

	/** 检查是否有网络 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	/** 检查是否是WIFI */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/** 检查是否是移动网络 */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	private static NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/** 检查SD卡是否存在 */
	public static boolean checkSdCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	/*
	 
	 *  把中文字符串转换为十六进制Unicode编码字符串
	 
	 */
	 
	    public static String stringToUnicode(String s) {
	        String str = "";
	        for (int i = 0; i < s.length(); i++) {
	            int ch = (int) s.charAt(i);
	            if (ch > 255)
	                str += "\\u" + Integer.toHexString(ch);
	            else
	                str += "\\" + Integer.toHexString(ch);
	        }
	        return str;
	    }
	 
	     
	 
	/*
	 
	 *  把十六进制Unicode编码字符串转换为中文字符串
	 
	 */
	 
	    public static String unicodeToString(String str) {
	 
	        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
	 
	        Matcher matcher = pattern.matcher(str);
	 
	        char ch;
	 
	        while (matcher.find()) {
	 
	            ch = (char) Integer.parseInt(matcher.group(2), 16);
	 
	            str = str.replace(matcher.group(1), ch + "");    
	 
	        }
	 
	        return str;
	 
	    }
}
