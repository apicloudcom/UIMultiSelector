package com.apicloud.devlop.uzMNRotationMenu;

import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class UzMNRotationMenu extends UZModule {
	private RotationMenu mRotationMenu;

	public UzMNRotationMenu(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_open(UZModuleContext moduleContext) {
		if (mRotationMenu == null) {
			mRotationMenu = new RotationMenu(mContext);
			mRotationMenu.init(moduleContext, this);
			JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
			int x = jsParamsUtil.x(moduleContext);
			int y = jsParamsUtil.y(moduleContext);
			int w = jsParamsUtil.w(moduleContext, mContext);
			int h = jsParamsUtil.h(moduleContext, mContext);
			boolean fixed = moduleContext.optBoolean("fixed", true);
			String fixedOn = moduleContext.optString("fixedOn");
			LayoutParams layoutParams = new LayoutParams(w, h);
			layoutParams.setMargins(x, y, 0, 0);
			insertViewToCurWindow(mRotationMenu, layoutParams, fixedOn, fixed);
		}
	}

	public void jsmethod_setIndex(UZModuleContext moduleContext) {
		if (mRotationMenu != null) {
			int index = moduleContext.optInt("index");
			mRotationMenu.setIndex(index);
		}
	}

	public void jsmethod_show(UZModuleContext moduleContext) {
		if (mRotationMenu != null) {
			mRotationMenu.setVisibility(View.VISIBLE);
		}
	}

	public void jsmethod_hide(UZModuleContext moduleContext) {
		if (mRotationMenu != null) {
			mRotationMenu.setVisibility(View.GONE);
		}
	}

	public void jsmethod_close(UZModuleContext moduleContext) {
		if (mRotationMenu != null) {
			removeViewFromCurWindow(mRotationMenu);
			mRotationMenu = null;
		}
	}

	public void jsmethod_clearCache(UZModuleContext moduleContext) {
	}
}
