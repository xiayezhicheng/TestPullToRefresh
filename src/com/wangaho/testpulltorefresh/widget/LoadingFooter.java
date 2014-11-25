
package com.wangaho.testpulltorefresh.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangaho.testpulltorefresh.R;

public class LoadingFooter {
    protected View mLoadingFooter;

    protected State mState = State.Idle;

    private ImageView mProgress;
    
    protected TextView mLoadingText;

    private AnimationDrawable animationDrawable;  
    
    public static enum State {
        Idle, TheEnd, Loading, InvalidateNet
    }

    public LoadingFooter(Context context) {
        mLoadingFooter = LayoutInflater.from(context).inflate(R.layout.loading_footer, null);
        mProgress = (ImageView) mLoadingFooter.findViewById(R.id.progressBar);
        mLoadingText = (TextView) mLoadingFooter.findViewById(R.id.txt_invalidatenet);
        mLoadingText.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		mOnClickLoadListener.onClick();
        	}
        });
        setState(State.Idle);
    }

    public View getView() {
        return mLoadingFooter;
    }

    public State getState() {
        return mState;
    }

    public void setState(final State state, long delay) {
        mLoadingFooter.postDelayed(new Runnable() {

            @Override
            public void run() {
                setState(state);
            }
        }, delay);
    }

	public void setState(State status) {
        if (mState == status) {
            return;
        }
        mState = status;

        mLoadingFooter.setVisibility(View.VISIBLE);

        
        switch (status) {
            case Loading:
                mProgress.setVisibility(View.VISIBLE);
                animationDrawable = (AnimationDrawable) mProgress.getDrawable();  
                animationDrawable.start();
                mLoadingText.setVisibility(View.GONE);
                break;
            case TheEnd:
                mProgress.setVisibility(View.GONE);
                animationDrawable = (AnimationDrawable) mProgress.getDrawable();  
                animationDrawable.stop();  
                mLoadingText.setVisibility(View.GONE);
                break;
            case InvalidateNet:
            	mProgress.setVisibility(View.GONE);
            	animationDrawable = (AnimationDrawable) mProgress.getDrawable();  
            	animationDrawable.stop();  
            	mLoadingText.setText("网络连接异常,点击重试");
            	mLoadingText.setVisibility(View.VISIBLE);
            	break;
            default:
                mLoadingFooter.setVisibility(View.GONE);
                break;
        }
    }
	
	private onClickLoadListener mOnClickLoadListener;
	
	/**
	 * 设置点击监听事件
	 * @author wanghao
	 * @since 2014-11-25 下午4:50:37
	 * @param listener
	 * @return void
	 */
	public void setOnClickLoadListener(onClickLoadListener listener){
		mOnClickLoadListener = listener;
	}
	
	/**
	 * 自定义点击事件的回调接口
	 * @author wanghao
	 * @since 2014-11-25 下午4:49:58
	 */
	public interface onClickLoadListener{
		public void onClick();
	}
}
