//
//UZModule
//
//Modified by magic 16/3/11.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzUIPullRefresh;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.RelativeLayout;

public abstract class CusHeadView extends RelativeLayout {

	protected final static int STATE_NORMAL = 0;
	protected final static int STATE_TRANSFORM = 1;

	public CusHeadView(Context context) {
		super(context);
	}

	protected abstract void onSetRefreshInfo(UZModuleContext moduleContext);
	
	protected abstract void onRefresh();

	protected abstract void onScrollY(int scrollY);

	protected abstract void onStateChange(int state);
	
	protected void stopAnim(AnimationDrawable anim) {
		if (anim.isRunning()) {
			anim.stop();
		}
	}

	protected void startAnim(AnimationDrawable anim) {
		if (!anim.isRunning()) {
			anim.start();
		}
	}
}
