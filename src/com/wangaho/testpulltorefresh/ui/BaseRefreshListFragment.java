package com.wangaho.testpulltorefresh.ui;

import java.util.LinkedList;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.wangaho.testpulltorefresh.R;
import com.wangaho.testpulltorefresh.adapter.BaseListAdapter;
import com.wangaho.testpulltorefresh.data.GsonRequest;
import com.wangaho.testpulltorefresh.utils.CommonUtils;
import com.wangaho.testpulltorefresh.widget.ListViewUtils;
import com.wangaho.testpulltorefresh.widget.LoadingFooter;
import com.wangaho.testpulltorefresh.widget.PullRefreshLayout;
import com.wangaho.testpulltorefresh.widget.LoadingFooter.onClickLoadListener;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


public abstract class BaseRefreshListFragment<T> extends BaseFragment{

	private Context context;
	protected PullRefreshLayout layout;
	private LoadingFooter mLoadingFooter;
	private ListView mListView;
	protected LinkedList<T> data;
	private BaseListAdapter<T> adapter;
	private View emptyView;
	private View invalidateNetView;
	private int mPage = 0;
	private int mCount = 10;
	
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
		
		data = new LinkedList<T>();
		adapter = getAdapter();
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
		
		return view;
	}

	private void loadData(final int page) {
		final boolean isRefreshFromTop = page == 0;
		if (!layout.isRefreshing() && isRefreshFromTop) {
			layout.setRefreshing(true);
		}
		
		GsonRequest<T[]> request = new GsonRequest<T[]>(String.format(getUrlString(), page,mCount), getDataType(),
				new Listener<T[]>() {

					@Override
					public void onResponse(final T[] response) {

						CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, LinkedList<T>>(){

							@Override
							protected LinkedList<T> doInBackground(Object... params) {
								LinkedList<T> temp = new LinkedList<T>();
								for (T object:response) {
									temp.add(object);
								}
								return temp;
							}

							@Override
							protected void onPostExecute(LinkedList<T> result) {
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
				}, new ErrorListener() {

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
				}
		);
		
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		executeRequest(request);
		
	}

	private void loadNextPage() {
		mLoadingFooter.setState(LoadingFooter.State.Loading);
		loadData(mPage+1);
	}

	protected void loadFirstPage() {
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
	
	protected abstract BaseListAdapter<T> getAdapter();
	
	protected abstract Class<T[]> getDataType();
	
	protected abstract String getUrlString();
}
