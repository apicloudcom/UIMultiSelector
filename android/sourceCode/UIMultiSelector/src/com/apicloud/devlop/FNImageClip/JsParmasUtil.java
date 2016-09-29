package com.apicloud.devlop.FNImageClip;

import java.io.IOException;
import java.io.InputStream;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import com.uzmap.pkg.uzcore.UZCoreUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class JsParmasUtil {
	private static JsParmasUtil instance;

	public static JsParmasUtil getInstance() {
		if (instance == null) {
			instance = new JsParmasUtil();
		}
		return instance;
	}

	public int x(UZModuleContext moduleContext) {
		JSONObject rect = moduleContext.optJSONObject("rect");
		if (rect != null) {
			return rect.optInt("x", 0);
		}
		return 0;
	}

	public int y(UZModuleContext moduleContext) {
		JSONObject rect = moduleContext.optJSONObject("rect");
		if (rect != null) {
			return rect.optInt("y", 0);
		}
		return 0;
	}

	public int w(UZModuleContext moduleContext, Context context) {
		JSONObject rect = moduleContext.optJSONObject("rect");
		int defaultWidth = getScreenWidth((Activity) context);
		if (rect != null && x(moduleContext) + rect.optInt("w") > defaultWidth) {
			return defaultWidth - x(moduleContext);
		}
		return rect.optInt("w", defaultWidth);
	}

	public int h(UZModuleContext moduleContext, Context context) {
		JSONObject rect = moduleContext.optJSONObject("rect");
		int defaultHeight = getScreenHeight((Activity) context);
		int y = y(moduleContext);
		if (rect != null && y + rect.optInt("h") > defaultHeight) {
			return defaultHeight - y;
		}
		return rect.optInt("h", defaultHeight);
	}

	public int layerColor(UZModuleContext moduleContext) {
		int defaultColor = UZUtility.parseCssColor("#888");
		JSONObject style = moduleContext.optJSONObject("style");
		if (style != null) {
			int layerColor = UZUtility.parseCssColor(style.optString("mask",
					"#888"));
			return layerColor;
		}
		return defaultColor;
	}

	public int borderColor(UZModuleContext moduleContext) {
		int defaultColor = UZUtility.parseCssColor("#00000000");
		JSONObject style = moduleContext.optJSONObject("style");
		if (style != null) {
			JSONObject clip = style.optJSONObject("clip");
			if (clip != null) {
				int borderColor = UZUtility.parseCssColor(clip.optString(
						"borderColor", "#00000000"));
				return borderColor;
			}
		}
		return defaultColor;
	}

	public int borderWidth(UZModuleContext moduleContext) {
		int defaultBorderWidth = 0;
		JSONObject style = moduleContext.optJSONObject("style");
		if (style != null) {
			JSONObject clip = style.optJSONObject("clip");
			if (clip != null) {
				int borderWidth = clip.optInt("borderWidth", 0);
				return borderWidth;
			}
		}
		return defaultBorderWidth;
	}

	public boolean isSaveToAlbum(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("copyToAlbum", false);
	}

	public Bitmap img(UZModuleContext moduleContext, UZModule uzModule) {
		String imgStr = moduleContext.optString("srcPath");
		String path = uzModule.makeRealPath(imgStr);
		return getBitmap(path);
	}

	public boolean isColor(UZModuleContext moduleContext) {
		String bgStr = moduleContext.optString("bg");
		if (bgStr.contains("://")) {
			return false;
		} else {
			return true;
		}
	}

	public int bgColor(UZModuleContext moduleContext) {
		String bgStr = moduleContext.optString("bg", "#000000");
		return UZUtility.parseCssColor(bgStr);
	}

	public Bitmap bg(UZModuleContext moduleContext, UZModule uzModule) {
		String bgStr = moduleContext.optString("bg");
		bgStr = uzModule.makeRealPath(bgStr);
		return getBitmap(bgStr);
	}

	public String mode(UZModuleContext moduleContext) {
		return moduleContext.optString("mode","all");
	}

	public boolean scalable(UZModuleContext moduleContext) {
		JSONObject style = moduleContext.optJSONObject("style");
		if (style != null) {
			JSONObject clip = style.optJSONObject("clip");
			if (clip != null) {
				return clip.optBoolean("scalable", false);
			}
		}
		return false;
	}

	public boolean isCircle(UZModuleContext moduleContext) {
		JSONObject style = moduleContext.optJSONObject("style");
		if (style != null) {
			JSONObject clip = style.optJSONObject("clip");
			if (clip != null) {
				String appearance = clip.optString("appearance", "rectangle");
				if (appearance.equals("rectangle"))
					return false;
				else
					return true;
			}
		}
		return false;
	}

	public ClipRect clipRect(UZModuleContext moduleContext,
			ClipRect defaultClipRect) {
		int defaultLeft = defaultClipRect.getLeft();
		int defaultRight = defaultClipRect.getRight();
		int defaultTop = defaultClipRect.getTop();
		int defaultBottom = defaultClipRect.getBottom();
		ClipRect clipRect = null;
		JSONObject style = moduleContext.optJSONObject("style");
		if (style != null) {
			JSONObject clip = style.optJSONObject("clip");
			if (clip == null || clip.isNull("x") || clip.isNull("y")
					|| clip.isNull("w") || clip.isNull("h")) {
				clipRect = new ClipRect(defaultLeft, defaultTop, defaultRight,
						defaultBottom);
			} else {
				int left = clip.optInt("x");
				left = UZUtility.dipToPix(left);
				int top = clip.optInt("y");
				top = UZUtility.dipToPix(top);
				int right = left + UZUtility.dipToPix(clip.optInt("w"));
				int bottom = top + UZUtility.dipToPix(clip.optInt("h"));
				clipRect = new ClipRect(left, top, right, bottom);
			}
		} else {
			clipRect = new ClipRect(defaultLeft, defaultTop, defaultRight,
					defaultBottom);
		}
		return clipRect;
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

	private int getScreenWidth(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return UZCoreUtil.pixToDip(metric.widthPixels);
	}

	private int getScreenHeight(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		Rect frame = new Rect();
		act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return UZCoreUtil.pixToDip(metric.heightPixels)
				- UZCoreUtil.pixToDip(statusBarHeight);
	}
}
