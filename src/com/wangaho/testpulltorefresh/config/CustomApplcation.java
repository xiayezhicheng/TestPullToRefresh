package com.wangaho.testpulltorefresh.config;

import java.io.InputStream;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import android.app.Application;
import android.content.Context;



/**
 * 自定义的Application
 * @author wanghao
 * @since 2014-11-17 下午12:15:34
 */
public class CustomApplcation extends Application {

	public static CustomApplcation mInstance;
	private static Context context;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		context = getApplicationContext();
		//Occasional EOFException when reading cached file
		//1. Try to load lots of previously cached images in a ListView by quickly scrolling back and forth
		//2. Some images will fail to load
		System.setProperty("http.keepAlive", "false");
		initImageLoader(context);
	}

	/**
	 * 初始化UIL
	 * @author wanghao
	 * @since 2014-11-17 下午1:37:16
	 * @param context
	 * @return void
	 */
	private void initImageLoader(Context context) {
		
		//磁盘缓存：如配置了diskCacheSize和diskCacheFileCount，就使用的是LruDiscCache，否则使用的是UnlimitedDiscCache
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
					.threadPoolSize(1)
					.threadPriority(Thread.NORM_PRIORITY-2)
					.threadPoolSize(3)
					.denyCacheImageMultipleSizesInMemory()// 拒绝缓存同一图片，有不同的大小  
					.memoryCache(new LruMemoryCache(10*1024*1024))
					.diskCacheFileNameGenerator(new Md5FileNameGenerator())
					.diskCacheSize(50*1024*1024)
					.tasksProcessingOrder(QueueProcessingType.LIFO)
					.imageDownloader(new BaseImageDownloader(context, 5*1000, 20*1000))
					.writeDebugLogs()//发布版本时删除
					.build();
		//用configuration初始化ImageLoader
		ImageLoader.getInstance().init(configuration);
		
	}

	public static CustomApplcation getInstance() {
		return mInstance;
	}
	
	public static Context getContext(){
		return context;
	}
	
}
