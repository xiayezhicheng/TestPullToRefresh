package com.wangaho.testpulltorefresh.ui;

import com.android.volley.Request;
import com.wangaho.testpulltorefresh.data.RequestManager;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

	protected void executeRequest(Request request) {
	        RequestManager.addRequest(request, this);
    }
	
    @Override
    public void onStop() {
        super.onStop();
        RequestManager.cancelAll(this);
    }
   
}
