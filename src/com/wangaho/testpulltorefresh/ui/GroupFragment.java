package com.wangaho.testpulltorefresh.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangaho.testpulltorefresh.adapter.BaseListAdapter;
import com.wangaho.testpulltorefresh.adapter.GroupAdapter;
import com.wangaho.testpulltorefresh.bean.Group;
import com.wangaho.testpulltorefresh.vendor.API;

public class GroupFragment extends BaseRefreshListFragment<Group> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState);
		layout.setRefreshing(true);
		loadFirstPage();
		return contentView;
	}

	@Override
	protected BaseListAdapter<Group> getAdapter() {
		return new GroupAdapter(getActivity(), data);
	}

	@Override
	protected Class<Group[]> getDataType() {
		return Group[].class;
	}

	@Override
	protected String getUrlString() {
		return API.GROUP;
	}

}
