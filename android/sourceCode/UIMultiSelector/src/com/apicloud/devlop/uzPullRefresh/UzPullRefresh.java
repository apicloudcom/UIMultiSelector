package com.apicloud.devlop.uzPullRefresh;

import android.view.View;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class UzPullRefresh extends UZModule {

	public UzPullRefresh(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_open(UZModuleContext moduleContext) {
		View view = new View(mContext);
	}

}
