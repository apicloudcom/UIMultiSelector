//
//UZModule
//
//Modified by magic 16/3/11.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzUIPullRefresh;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import com.uzmap.pkg.uzcore.uzmodule.RefreshHeader;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class UzUIPullRefresh extends RefreshHeader {
	public final static int REFRSH_HEIGHT = 64;
	private MTCusHeadView mCusHeadView;

	@Override
	public View onCreateView(Context context) {
		if (null == mCusHeadView) {
			mCusHeadView = new MTCusHeadView(context);
		}
		return mCusHeadView;
	}

	@Override
	public int getRefreshingThreshold(Context context) {
		Resources res = context.getResources();
		float density = res.getDisplayMetrics().density;
		int threshold = (int) (density * REFRSH_HEIGHT);
		return threshold;
	}

	@Override
	public int getViewHeight(Context context) {
		Resources res = context.getResources();
		int screenHeight = res.getDisplayMetrics().heightPixels;
		return (screenHeight / 3) * 2;
	}

	@Override
	public void onDestroyView() {
	}

	@Override
	public void onForceRefresh() {
	}

	@Override
	public void onRefresh() {
		mCusHeadView.onRefresh();
	}

	@Override
	public void onRelease() {
		mCusHeadView.onStateChange(0);
	}

	@Override
	public void onScrollY(int curScrollY) {
		mCusHeadView.onScrollY(curScrollY);
	}

	@Override
	public void onSetRefreshInfo(UZModuleContext moduleContext) {
		mCusHeadView.onSetRefreshInfo(moduleContext);
	}

	@Override
	public void onSetVisibility(int visibility) {

	}

	@Override
	public void onStateChange(int state) {
		mCusHeadView.onStateChange(state);
		Log.e("pullRefresh", "onStateChange:" + state);
	}

}
