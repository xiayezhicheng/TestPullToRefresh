package com.wangaho.testpulltorefresh.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wangaho.testpulltorefresh.R;
import com.wangaho.testpulltorefresh.bean.Group;

public class GroupAdapter extends BaseListAdapter<Group>{

	private Drawable mDefaultImageDrawable;
	private DisplayImageOptions options;
	
	public GroupAdapter(Context context, List<Group> list) {
		super(context, list);
		mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
		options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.pic_loading_large)
					.showImageForEmptyUri(mDefaultImageDrawable)
					.showImageOnFail(mDefaultImageDrawable)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
					.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位 ,防止图片显示错位
					.build();
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.list_group, null);
		}
		
		ImageView   img_group = ViewHolder.get(convertView, R.id.img_group);
		TextView txt_title_group = ViewHolder.get(convertView, R.id.txt_title_group);
		TextView txt_content_group_people = ViewHolder.get(convertView, R.id.txt_content_group_people);
		TextView txt_group_address = ViewHolder.get(convertView, R.id.txt_group_address);
		TextView txt_group_price = ViewHolder.get(convertView, R.id.txt_group_price);

		ImageLoader.getInstance().displayImage(getList().get(position).getThumb(), img_group, options);
		txt_title_group.setText(getList().get(position).getTitle());
		txt_content_group_people.setText("已参加："+getList().get(position).getOrders()+"人");
		txt_group_address.setText(getList().get(position).getAddress());
		txt_group_price.setText(getList().get(position).getPrice()+"元/㎡");
		
		return convertView;
	}
}
