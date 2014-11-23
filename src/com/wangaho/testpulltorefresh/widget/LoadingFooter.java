
package com.wangaho.testpulltorefresh.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wangaho.testpulltorefresh.R;

public class LoadingFooter {
    protected View mLoadingFooter;

    protected State mState = State.Idle;

    private ImageView mProgress;

    private AnimationDrawable animationDrawable;  

    public static enum State {
        Idle, TheEnd, Loading
    }

    public LoadingFooter(Context context) {
        mLoadingFooter = LayoutInflater.from(context).inflate(R.layout.loading_footer, null);
        mLoadingFooter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        mProgress = (ImageView) mLoadingFooter.findViewById(R.id.progressBar);
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

    @SuppressLint("NewApi")
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
                break;
            case TheEnd:
                mProgress.setVisibility(View.GONE);
                animationDrawable = (AnimationDrawable) mProgress.getDrawable();  
                animationDrawable.stop();  
                break;
            default:
                mLoadingFooter.setVisibility(View.GONE);
                break;
        }
    }
}
