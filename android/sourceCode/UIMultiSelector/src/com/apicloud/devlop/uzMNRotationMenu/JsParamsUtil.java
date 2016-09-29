//
//UZModule
//
//Modified by magic 16/3/11.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzMNRotationMenu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

	private JSONObject mRect;
	private JSONObject mStyles;
	private JSONObject mImage;

	public int x(UZModuleContext moduleContext) {
		if (mRect == null) {
			mRect = moduleContext.optJSONObject("rect");
		}
		if (mRect != null) {
			return mRect.optInt("x", 0);
		}
		return 0;
	}

	public int y(UZModuleContext moduleContext) {
		if (mRect == null) {
			mRect = moduleContext.optJSONObject("rect");
		}
		if (mRect != null) {
			return mRect.optInt("y", 0);
		}
		return 0;
	}

	public int w(UZModuleContext moduleContext, Activity context) {
		if (mRect == null) {
			mRect = moduleContext.optJSONObject("rect");
		}
		if (mRect != null) {
			return mRect.optInt("w", getDpScreenWidth(context));
		}
		return getDpScreenWidth(context);
	}

	public int h(UZModuleContext moduleContext, Activity context) {
		if (mRect == null) {
			mRect = moduleContext.optJSONObject("rect");
		}
		if (mRect != null) {
			return mRect.optInt("h", 180);
		}
		return 180;
	}

	public List<String> items(UZModuleContext moduleContext) {
		JSONArray items = moduleContext.optJSONArray("items");
		if (items != null) {
			List<String> itemsList = new ArrayList<String>();
			for (int i = 0; i < items.length(); i++) {
				itemsList.add(items.optJSONObject(i).optString("url"));
			}
			return itemsList;
		}
		return null;
	}

	public int r(UZModuleContext moduleContext, Activity context) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mStyles != null) {
			return mStyles.optInt("r", 190);
		}
		return 190;
	}

	public Bitmap placeholder(UZModuleContext moduleContext,
			UzMNRotationMenu module) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mImage == null) {
			if (mStyles != null) {
				mImage = mStyles.optJSONObject("image");
			}
		}
		if (mImage != null) {
			String placeholder = mImage.optString("placeholder");
			return getBitmap(module.makeRealPath(placeholder));
		}
		return null;
	}

	public boolean isColor(UZModuleContext moduleContext) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mStyles != null) {
			String bg = mStyles.optString("bg");
			if (bg != null && bg.contains("://")) {
				return false;
			}
		}
		return true;
	}

	public int bgColor(UZModuleContext moduleContext) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mStyles != null) {
			String bg = mStyles.optString("bg", "#fff");
			return UZUtility.parseCssColor(bg);
		}
		return Color.WHITE;
	}

	public Bitmap bg(UZModuleContext moduleContext, UzMNRotationMenu module) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mStyles != null) {
			String bg = mStyles.optString("bg");
			return getBitmap(module.makeRealPath(bg));
		}
		return null;
	}

	public boolean isIndicator(UZModuleContext moduleContext) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mStyles != null) {
			return !mStyles.isNull("indicator");
		}
		return false;
	}

	public int indicatorColor(UZModuleContext moduleContext) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mStyles != null) {
			JSONObject indicator = mStyles.optJSONObject("indicator");
			if (indicator != null) {
				return UZUtility.parseCssColor(indicator
						.optString("bg", "#eee"));
			}
		}
		return UZUtility.parseCssColor("#eee");
	}

	public int indicatorActiveColor(UZModuleContext moduleContext) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mStyles != null) {
			JSONObject indicator = mStyles.optJSONObject("indicator");
			if (indicator != null) {
				return UZUtility.parseCssColor(indicator.optString("active",
						"#eee"));
			}
		}
		return UZUtility.parseCssColor("#eee");
	}

	public int corner(UZModuleContext moduleContext) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mImage == null) {
			if (mStyles != null) {
				mImage = mStyles.optJSONObject("image");
			}
		}
		if (mImage != null) {
			return UZUtility.dipToPix(mImage.optInt("corner", 2));
		}
		return UZUtility.dipToPix(2);
	}

	public int imgWidth(UZModuleContext moduleContext, Activity context) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mImage == null) {
			if (mStyles != null) {
				mImage = mStyles.optJSONObject("image");
			}
		}
		if (mImage != null) {
			return mImage.optInt("w", 80);
		}
		return 80;
	}

	public int imgHeight(UZModuleContext moduleContext, Activity context) {
		if (mStyles == null) {
			mStyles = moduleContext.optJSONObject("styles");
		}
		if (mImage == null) {
			if (mStyles != null) {
				mImage = mStyles.optJSONObject("image");
			}
		}
		if (mImage != null) {
			return mImage.optInt("h", 108);
		}
		return 108;
	}

	public int index(UZModuleContext moduleContext) {
		return moduleContext.optInt("index", -1);
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
		return metric.widthPixels;
	}

	public int getScreenHeight(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	public int getDpScreenWidth(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return UZCoreUtil.pixToDip(metric.widthPixels);
	}

	public int getDpScreenHeight(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return UZCoreUtil.pixToDip(metric.heightPixels);
	}
}
