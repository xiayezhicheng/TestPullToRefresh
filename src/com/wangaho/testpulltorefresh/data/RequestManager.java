package com.wangaho.testpulltorefresh.data;


import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.wangaho.testpulltorefresh.config.CustomApplcation;


public class RequestManager {
	
	  	public static RequestQueue mRequestQueue = newRequestQueue();

	    private RequestManager(){

	    }

	    private static Cache openCache() {
	        return new DiskBasedCache(CacheUtils.getExternalCacheDir(CustomApplcation.getContext()),
	                10 * 1024 * 1024);
	    }

	    private static RequestQueue newRequestQueue() {
	        RequestQueue requestQueue = new RequestQueue(openCache(), new BasicNetwork(new HurlStack()));
	        requestQueue.start();
	        return requestQueue;
	    }

	    public static void addRequest(Request request, Object tag) {
	        if (tag != null) {
	            request.setTag(tag);
	        }
	        mRequestQueue.add(request);
	    }
	    
	    public static RequestQueue getRequestQueue() {
	        if (mRequestQueue != null) {
	            return mRequestQueue;
	        } else {
	            throw new IllegalStateException("RequestQueue not initialized");
	        }
	    }
	    public static void cancelAll(Object tag) {
	        mRequestQueue.cancelAll(tag);
	    }

}
