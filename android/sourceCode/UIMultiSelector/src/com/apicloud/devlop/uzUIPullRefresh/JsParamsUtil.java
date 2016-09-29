//
//UZModule
//
//Modified by magic 16/3/11.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzUIPullRefresh;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import com.uzmap.pkg.uzcore.UZCoreUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class JsParamsUtil {
	private static JsParamsUtil instance;

	public static JsParamsUtil getInstance() {
		if (instance == null) {
			instance = new JsParamsUtil();
		}
		return instance;
	}

	public int bgColor(UZModuleContext moduleContext) {
		return UZUtility.parseCssColor(moduleContext.optString("bgColor",
				"#C0C0C0"));
	}

	public Bitmap pullImg(UZModuleContext moduleContext) {
		JSONObject img = moduleContext.optJSONObject("image");
		if (img != null) {
			String pullImgPath = img.optString("pull");
			if (pullImgPath != null) {
				String path = moduleContext.makeRealPath(pullImgPath);
				return getBitmap(path);
			}
		}
		return null;
	}

	public List<Bitmap> transformImgs(UZModuleContext moduleContext) {
		JSONObject img = moduleContext.optJSONObject("image");
		if (img != null) {
			JSONArray transform = img.optJSONArray("transform");
			if (transform != null && transform.length() > 0) {
				List<Bitmap> transformImgs = new ArrayList<Bitmap>();
				int length = transform.length();
				for (int i = 0; i < length; i++) {
					transformImgs.add(getBitmap(moduleContext
							.makeRealPath(transform.optString(i))));
				}
				return transformImgs;
			}
		}
		return null;
	}
	
	public List<Bitmap> loadImgs(UZModuleContext moduleContext) {
		JSONObject img = moduleContext.optJSONObject("image");
		if (img != null) {
			JSONArray load = img.optJSONArray("load");
			if (load != null && load.length() > 0) {
				List<Bitmap> transformImgs = new ArrayList<Bitmap>();
				int length = load.length();
				for (int i = 0; i < length; i++) {
					transformImgs.add(getBitmap(moduleContext
							.makeRealPath(load.optString(i))));
				}
				return transformImgs;
			}
		}
		return null;
	}

	public Bitmap getBitmap(String path) {
		Bitmap bitmap = null;
		InputStream input = null;
		try {
			input = UZUtility.guessInputStream(path);
			bitmap = BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public int getScreenWidth(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return UZCoreUtil.pixToDip(metric.widthPixels);
	}

	public int getScreenHeight(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return UZCoreUtil.pixToDip(metric.heightPixels);
	}
}
