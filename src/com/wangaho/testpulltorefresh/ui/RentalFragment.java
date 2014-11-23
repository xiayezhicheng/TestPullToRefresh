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
import com.wangaho.testpulltorefresh.adapter.RentalAdapter;
import com.wangaho.testpulltorefresh.bean.Group;
import com.wangaho.testpulltorefresh.bean.Rental;
import com.wangaho.testpulltorefresh.utils.CommonUtils;
import com.wangaho.testpulltorefresh.vendor.API;
import com.wangaho.testpulltorefresh.widget.ListViewUtils;
import com.wangaho.testpulltorefresh.widget.LoadingFooter;
import com.wangaho.testpulltorefresh.widget.PullRefreshLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class RentalFragment extends BaseFragment{

	private Context context;
	private PullRefreshLayout layout;
	private LoadingFooter mLoadingFooter;
	private ListView mListView;
	private LinkedList<Rental> data;
	private RentalAdapter adapter;
	private int mPage = 0;
	private int mCount = 5;
	private boolean isVisible ;
	private boolean isInit ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		View view = inflater.from(context).inflate(R.layout.activity_list, null);

		mListView = (ListView) view.findViewById(R.id.recyclerView);
		mLoadingFooter = new LoadingFooter(context);
		mListView.addFooterView(mLoadingFooter.getView());

		data = new LinkedList<Rental>();
		adapter = new RentalAdapter(context, data);
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

				if (mLoadingFooter.getState() == LoadingFooter.State.Loading
						|| mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
					return;
				}

				if (firstVisibleItem + visibleItemCount >= totalItemCount
						&& totalItemCount != 0
						&& totalItemCount != mListView.getHeaderViewsCount()+mListView.getFooterViewsCount()
						&& adapter.getCount() > 0) {
					loadNextPage();
				}
			}
		});
		Log.i("onCreateView", "rental");
		isInit = true;
		onFirstLoad();
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		isVisible = !hidden;
		//显示该fragment之前载入activity的时候就已经初始化完成
		onFirstLoad();
		Log.i("onHiddenChanged", "rental");
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

	private void loadData(final int page) {
		final boolean isRefreshFromTop = page == 0;
		if (!layout.isRefreshing() && isRefreshFromTop) {
			layout.setRefreshing(true);
		}
		//添加数据
		StringRequest request = new StringRequest(String.format(API.RENTAL, page,mCount), 
				new Response.Listener<String>() {

					@Override
					public void onResponse(final String response) {
						CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, LinkedList<Rental>>(){

							@Override
							protected LinkedList<Rental> doInBackground(Object... params) {

								LinkedList<Rental> tempList = new LinkedList<Rental>();
								Gson gson = new Gson();
								JsonParser parser = new JsonParser();
								JsonArray jarray = parser.parse(response).getAsJsonArray();
								for(JsonElement obj : jarray){
									Rental rental = gson.fromJson(obj, Rental.class);
									tempList.add(rental);
								}
								
								if(tempList.size()>0){
					    			// 下拉刷新的数据去重复处理
					    			if(isRefreshFromTop){
					    				data.clear();
					    			}
					    			data.addAll(tempList);
					    			return data;
					    		}else {
									return null;
								}
							}

							@Override
							protected void onPostExecute(LinkedList<Rental> result) {
								super.onPostExecute(result);
								
								if (isRefreshFromTop) {
									layout.setRefreshing(false);
								}
								if(result==null||result.size()<mCount){
									mLoadingFooter.setState(LoadingFooter.State.TheEnd);
								}else{
									mLoadingFooter.setState(LoadingFooter.State.Idle);
									adapter.notifyDataSetChanged();
								}
							}
						});
					}
			},new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					
					if (CommonUtils.isNetworkAvailable(context)) {
						if (isRefreshFromTop) {
							layout.setRefreshing(false);
						} else {
							mLoadingFooter.setState(LoadingFooter.State.Idle,2400);
						}
					}else {
						Toast.makeText(context, "网络无连接，请连接网络", Toast.LENGTH_SHORT).show();
					}
					
				}
			});
		
		executeRequest(request);
		
	}

	private void loadNextPage() {
		mLoadingFooter.setState(LoadingFooter.State.Loading);
		loadData(++mPage);
	}

	private void loadFirstPage() {
		mPage = 0;
		loadData(mPage);
	}

	public void loadFirstPageAndScrollToTop() {
		ListViewUtils.smoothScrollListViewToTop(mListView);
		loadFirstPage();
	}

}
