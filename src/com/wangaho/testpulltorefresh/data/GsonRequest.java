package com.wangaho.testpulltorefresh.data;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public  class GsonRequest<T> extends Request<T>{

	private final Gson mGson = new Gson();
	private final Class<T> mClazz;
	private final Listener<T> mListener;
	private final Map<String, String> mHeaders;

	public GsonRequest(String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
		this(Method.GET, url, clazz, null, listener, errorListener);
	}

	public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
			Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mClazz = clazz;
		this.mHeaders = headers;
		this.mListener = listener;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return super.getParams();
	}
	
	//将最终的数据进行回调
	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	//先是将服务器响应的数据解析出来，然后通过调用Gson的fromJson方法将数据组装成对象
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(mGson.fromJson(json, mClazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}
