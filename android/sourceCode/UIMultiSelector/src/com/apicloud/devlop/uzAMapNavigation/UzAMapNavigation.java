package com.apicloud.devlop.uzAMapNavigation;

import android.content.Intent;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class UzAMapNavigation extends UZModule {

	public static UZModuleContext mNaviModuleContext;
	public static UzAMapNavigation mModule;
	public static NaviActivity NAVI_ACTIVITY;

	public UzAMapNavigation(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_start(UZModuleContext moduleContext) {
		mModule = this;
		mNaviModuleContext = moduleContext;
		Intent intent = new Intent(mContext, NaviActivity.class);
		mContext.startActivity(intent);
	}

	public void jsmethod_close(UZModuleContext moduleContext) {
		if (NAVI_ACTIVITY != null) {
			NAVI_ACTIVITY.finish();
		}
	}
}
