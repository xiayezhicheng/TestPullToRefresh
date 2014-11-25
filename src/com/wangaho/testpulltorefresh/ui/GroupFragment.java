package com.wangaho.testpulltorefresh.ui;

import java.util.LinkedList;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wangaho.testpulltorefresh.R;
import com.wangaho.testpulltorefresh.adapter.GroupAdapter;
import com.wangaho.testpulltorefresh.bean.Group;
import com.wangaho.testpulltorefresh.utils.CommonUtils;
import com.wangaho.testpulltorefresh.vendor.API;
import com.wangaho.testpulltorefresh.widget.ListViewUtils;
import com.wangaho.testpulltorefresh.widget.LoadingFooter;
import com.wangaho.testpulltorefresh.widget.LoadingFooter.onClickLoadListener;
import com.wangaho.testpulltorefresh.widget.PullRefreshLayout;
import com.wangaho.testpulltorefresh.widget.LoadingFooter.State;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class GroupFragment extends BaseFragment {

	private Context context;
	private PullRefreshLayout layout;
	private LoadingFooter mLoadingFooter;
	private ListView mListView;
	private LinkedList<Group> data;
	private GroupAdapter adapter;
	private View emptyView;
	private View invalidateNetView;
	private int mPage = 0;
	private int mCount = 8;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		View view = inflater.from(context).inflate(R.layout.activity_list, null);

		//空内容展示
		emptyView = view.findViewById(R.id.empty_group);
		emptyView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				layout.setRefreshing(true);
				loadFirstPage();
			}
		});
		
		//无网络连接展示
		invalidateNetView = view.findViewById(R.id.invalidatenet_group);
		invalidateNetView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				layout.setRefreshing(true);
				loadFirstPage();
			}
		});
		
		mListView = (ListView) view.findViewById(R.id.recyclerView);
		mLoadingFooter = new LoadingFooter(context);
		mLoadingFooter.setOnClickLoadListener(new onClickLoadListener() {
			
			@Override
			public void onClick() {
				loadNextPage();
			}
		});
		mListView.addFooterView(mLoadingFooter.getView());
		
		data = new LinkedList<Group>();
		adapter = new GroupAdapter(context, data);
		mListView.setAdapter(adapter);
		
		layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
		layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadFirstPage();
			}
		});

		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (mLoadingFooter.getState() == LoadingFooter.State.Idle) {
					if (firstVisibleItem + visibleItemCount >= totalItemCount
							&& totalItemCount != 0
							&& totalItemCount != mListView.getHeaderViewsCount()+mListView.getFooterViewsCount()
							&& adapter.getCount() > 0) {
						loadNextPage();
					}
				}else if (mLoadingFooter.getState() == LoadingFooter.State.InvalidateNet) {
					if (!mLoadingFooter.getView().isShown()) {
						mLoadingFooter.setState(LoadingFooter.State.Idle);
					}
				}

			}
		});
		
		layout.setRefreshing(true);
		loadFirstPage();
		
		return view;
	}


	private void loadData(final int page) {
		final boolean isRefreshFromTop = page == 0;
		if (!layout.isRefreshing() && isRefreshFromTop) {
			layout.setRefreshing(true);
		}
		
		//添加数据
		StringRequest request = new StringRequest(String.format(API.GROUP, page,mCount), 
				new Response.Listener<String>() {

					@Override
					public void onResponse(final String response) {
						CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, LinkedList<Group>>(){

							@Override
							protected LinkedList<Group> doInBackground(Object... params) {

								LinkedList<Group> tempList = new LinkedList<Group>();
								Gson gson = new Gson();
								JsonParser parser = new JsonParser();
								JsonArray jarray = parser.parse(response).getAsJsonArray();
								for(JsonElement obj : jarray){
									Group group = gson.fromJson(obj, Group.class);
									tempList.add(group);
								}
								
								return tempList;
							}

							@Override
							protected void onPostExecute(LinkedList<Group> result) {
								super.onPostExecute(result);
								if (isRefreshFromTop) {
									data.clear();
									layout.setRefreshing(false);
									if(result.size()==0){
										emptyView.setVisibility(View.VISIBLE);
									}
								}
								if(result.size()<mCount){
									mLoadingFooter.setState(LoadingFooter.State.TheEnd);
								}else{
									mLoadingFooter.setState(LoadingFooter.State.Idle);
								}
								mPage = page;
								data.addAll(result);
								adapter.notifyDataSetChanged();
							}
						});
					}
			},new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					
					if (isRefreshFromTop) {
						layout.setRefreshing(false);
					} else {
						mLoadingFooter.setState(LoadingFooter.State.Idle);
					}
					if (!CommonUtils.isNetworkAvailable(context)) {
						if(isRefreshFromTop){
							if (data.isEmpty()) {
								invalidateNetView.setVisibility(View.VISIBLE);
							}else {
								Toast.makeText(context, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
							}
						}else{
							mLoadingFooter.setState(LoadingFooter.State.InvalidateNet);
						}
					
					}
					
				}
			});
		
		executeRequest(request);
		
	}

	private void loadNextPage() {
		mLoadingFooter.setState(LoadingFooter.State.Loading);
		loadData(mPage+1);
		Log.i("最后load", "你点击了，你懂么");
	}

	private void loadFirstPage() {
		if (emptyView.getVisibility()==View.VISIBLE) {
			emptyView.setVisibility(View.GONE);
		}
		if (invalidateNetView.getVisibility()==View.VISIBLE) {
			invalidateNetView.setVisibility(View.GONE);
		}
		mPage = 0;
		loadData(mPage);
		
	}

	public void loadFirstPageAndScrollToTop() {
		ListViewUtils.smoothScrollListViewToTop(mListView);
		loadFirstPage();
	}

}
