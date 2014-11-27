package com.wangaho.testpulltorefresh.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangaho.testpulltorefresh.adapter.BaseListAdapter;
import com.wangaho.testpulltorefresh.adapter.RentalAdapter;
import com.wangaho.testpulltorefresh.bean.Rental;
import com.wangaho.testpulltorefresh.vendor.API;

public class RentalFragment extends BaseRefreshListFragment<Rental>{

	private boolean isVisible ;
	private boolean isInit ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState);
		isInit = true;
		onFirstLoad();
		return contentView;
	}

	@Override
	protected BaseListAdapter<Rental> getAdapter() {
		return new RentalAdapter(getActivity(), data);
	}

	@Override
	protected Class<Rental[]> getDataType() {
		return Rental[].class;
	}

	@Override
	protected String getUrlString() {
		return API.RENTAL;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		isVisible = !hidden;
		//显示该fragment之前载入activity的时候就已经初始化完成
		onFirstLoad();
	}
	
	public void onFirstLoad() {
		//延迟加载(懒加载)
		if (data != null && data.size() == 0) {
			if(isVisible&&isInit){
				layout.setRefreshing(true);
				loadFirstPage();
			}
		}
	}
}
