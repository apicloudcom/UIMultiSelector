package com.apicloud.devlop.FNImageClip;

import org.json.JSONException;
import org.json.JSONObject;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ClipLayout extends FrameLayout{

	private UZModuleContext mModuleContext;
	
	public ClipLayout(Context context) {
		super(context);
	}
	
	public ClipLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setModuleContext(UZModuleContext moduleContext) {
		this.mModuleContext = moduleContext;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		openCallBack(mModuleContext);
	}
	
	public void openCallBack(UZModuleContext moduleContext) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
