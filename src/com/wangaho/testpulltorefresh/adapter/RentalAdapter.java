package com.wangaho.testpulltorefresh.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wangaho.testpulltorefresh.R;
import com.wangaho.testpulltorefresh.bean.Rental;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RentalAdapter extends BaseListAdapter<Rental>{
	
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	private DisplayImageOptions options;
	
	public RentalAdapter(Context context, List<Rental> list) {
		super(context, list);
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
			convertView = mInflater.inflate(R.layout.list_rental, null);
		}
		
		ImageView  img_rental = ViewHolder.get(convertView, R.id.img_rental);
		TextView txt_title_rental = ViewHolder.get(convertView, R.id.txt_title_rental);
		TextView txt_content_rental = ViewHolder.get(convertView, R.id.txt_content_rental);
		TextView txt_type_rental = ViewHolder.get(convertView, R.id.txt_type_rental);
		TextView txt_area_rental = ViewHolder.get(convertView, R.id.txt_area_rental);
		TextView txt_rental_price = ViewHolder.get(convertView, R.id.txt_rental_price);
	
		ImageLoader.getInstance().displayImage(getList().get(position).getThumb(), img_rental, options);
		txt_title_rental.setText(getList().get(position).getTitle());
		txt_content_rental.setText(getList().get(position).getAddress());
		txt_type_rental.setText(getList().get(position).getRoom()
				+ "室" + getList().get(position).getHall() + "厅"
				+ getList().get(position).getToilet() + "卫");
		txt_area_rental.setText("/"+getList().get(position).getHouseearm()+"平米");
		txt_rental_price.setText(getList().get(position).getPrice()+"元/月");
		
		return convertView;
	}

}
