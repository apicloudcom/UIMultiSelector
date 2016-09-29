//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzUIMultiSelector;

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

	public int h(UZModuleContext moduleContext) {
		if (!moduleContext.isNull("rect")) {
			JSONObject rect = moduleContext.optJSONObject("rect");
			return rect.optInt("h", 244);
		}
		return 244;
	}

	public String title(UZModuleContext moduleContext) {
		if (!moduleContext.isNull("text")) {
			JSONObject rect = moduleContext.optJSONObject("text");
			return rect.optString("title", "");
		}
		return "";
	}

	public String leftBtn(UZModuleContext moduleContext) {
		if (!moduleContext.isNull("text")) {
			JSONObject rect = moduleContext.optJSONObject("text");
			return rect.optString("leftBtn", "取消");
		}
		return "取消";
	}

	public String rightBtn(UZModuleContext moduleContext) {
		if (!moduleContext.isNull("text")) {
			JSONObject rect = moduleContext.optJSONObject("text");
			return rect.optString("rightBtn", "完成");
		}
		return "完成";
	}

	public String selectAll(UZModuleContext moduleContext) {
		if (!moduleContext.isNull("text")) {
			JSONObject rect = moduleContext.optJSONObject("text");
			return rect.optString("selectAll", "全选");
		}
		return "全选";
	}

	public boolean singleSelection(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("singleSelection", false);
	}

	public JSONObject styles(UZModuleContext moduleContext) {
		return moduleContext.optJSONObject("styles");
	}

	public String bg(JSONObject styles) {
		if (styles != null)
			return styles.optString("bg");
		return null;
	}

	public int bgColor(String bg) {
		if (bg != null)
			return UZUtility.parseCssColor(bg);
		return UZUtility.parseCssColor("#ddd");
	}

	public Bitmap bgBitmap(String bg, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(bg));
	}

	public String mask(JSONObject styles) {
		if (styles != null)
			return styles.optString("mask");
		return null;
	}

	public int maskColor(String mask) {
		if (mask != null)
			return UZUtility.parseCssColor(mask);
		return UZUtility.parseCssColor("rgba(0,0,0,0)");
	}

	public Bitmap maskBitmap(String mask, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(mask));
	}

	public JSONObject title(JSONObject styles) {
		if (styles != null)
			return styles.optJSONObject("title");
		return null;
	}

	public String titleBg(JSONObject title) {
		if (title != null)
			return title.optString("bg");
		return null;
	}

	public int titleBgColor(String title) {
		if (title != null)
			return UZUtility.parseCssColor(title);
		return UZUtility.parseCssColor("#ddd");
	}

	public Bitmap titleBitmap(String title, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(title));
	}

	public int titleColor(JSONObject title) {
		if (title != null) {
			return UZUtility.parseCssColor(title.optString("color", "#444"));
		}
		return UZUtility.parseCssColor("#444");
	}

	public int titleSize(JSONObject title) {
		if (title != null) {
			return title.optInt("size", 16);
		}
		return 16;
	}

	public int titleH(JSONObject title) {
		if (title != null) {
			return title.optInt("h", 44);
		}
		return 44;
	}

	public JSONObject leftButton(JSONObject styles) {
		if (styles != null)
			return styles.optJSONObject("leftButton");
		return null;
	}

	public String leftButtonBg(JSONObject leftButton) {
		if (leftButton != null)
			return leftButton.optString("bg");
		return null;
	}

	public int leftButtonBgColor(String leftButton) {
		if (leftButton != null)
			return UZUtility.parseCssColor(leftButton);
		return UZUtility.parseCssColor("#f00");
	}

	public Bitmap leftButtonBitmap(String leftButton, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(leftButton));
	}

	public int leftButtonColor(JSONObject leftButton) {
		if (leftButton != null) {
			return UZUtility.parseCssColor(leftButton
					.optString("color", "#fff"));
		}
		return UZUtility.parseCssColor("#fff");
	}

	public int leftButtonSize(JSONObject leftButton) {
		if (leftButton != null) {
			return leftButton.optInt("size", 14);
		}
		return 14;
	}

	public int leftButtonW(JSONObject leftButton) {
		if (leftButton != null) {
			return leftButton.optInt("w", 85);
		}
		return 85;
	}

	public int leftButtonH(JSONObject leftButton) {
		if (leftButton != null) {
			return leftButton.optInt("h", 35);
		}
		return 35;
	}

	public int leftButtonMarginT(JSONObject leftButton) {
		if (leftButton != null) {
			return leftButton.optInt("marginT", 5);
		}
		return 5;
	}

	public int leftButtonMarginL(JSONObject leftButton) {
		if (leftButton != null) {
			return leftButton.optInt("marginL", 8);
		}
		return 8;
	}

	public JSONObject rightButton(JSONObject styles) {
		if (styles != null)
			return styles.optJSONObject("rightButton");
		return null;
	}

	public String rightButtonBg(JSONObject rightButton) {
		if (rightButton != null)
			return rightButton.optString("bg");
		return null;
	}

	public int rightButtonBgColor(String rightButton) {
		if (rightButton != null)
			return UZUtility.parseCssColor(rightButton);
		return UZUtility.parseCssColor("#0f0");
	}

	public Bitmap rightButtonBitmap(String rightButton, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(rightButton));
	}

	public int rightButtonColor(JSONObject rightButton) {
		if (rightButton != null) {
			return UZUtility.parseCssColor(rightButton.optString("color",
					"#fff"));
		}
		return UZUtility.parseCssColor("#fff");
	}

	public int rightButtonSize(JSONObject rightButton) {
		if (rightButton != null) {
			return rightButton.optInt("size", 14);
		}
		return 14;
	}

	public int rightButtonW(JSONObject rightButton) {
		if (rightButton != null) {
			return rightButton.optInt("w", 85);
		}
		return 85;
	}

	public int rightButtonH(JSONObject rightButton) {
		if (rightButton != null) {
			return rightButton.optInt("h", 35);
		}
		return 35;
	}

	public int rightButtonMarginT(JSONObject rightButton) {
		if (rightButton != null) {
			return rightButton.optInt("marginT", 5);
		}
		return 5;
	}

	public int rightButtonMarginR(JSONObject rightButton) {
		if (rightButton != null) {
			return rightButton.optInt("marginR", 8);
		}
		return 8;
	}

	public JSONObject item(JSONObject styles) {
		if (styles != null)
			return styles.optJSONObject("item");
		return null;
	}

	public int itemH(JSONObject item) {
		if (item != null) {
			return item.optInt("h", 35);
		}
		return 35;
	}

	public String itemBg(JSONObject item) {
		if (item != null)
			return item.optString("bg");
		return null;
	}

	public int itemBgColor(String item) {
		if (item != null)
			return UZUtility.parseCssColor(item);
		return UZUtility.parseCssColor("#fff");
	}

	public Bitmap itemBitmap(String item, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(item));
	}

	public String itemBgActive(JSONObject item) {
		if (item != null) {
			if (item.isNull("bgActive")) {
				return itemBg(item);
			}
			return item.optString("bgActive");
		}
		return null;
	}

	public int itemBgActiveColor(String item) {
		if (item != null)
			return UZUtility.parseCssColor(item);
		return UZUtility.parseCssColor("#fff");
	}

	public Bitmap itemActiveBitmap(String item, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(item));
	}

	public String itemBgHighlight(JSONObject item) {
		if (item != null) {
			if (item.isNull("bgHighlight")) {
				return itemBg(item);
			}
			return item.optString("bgHighlight");
		}
		return null;
	}

	public int itemBgHighlightColor(String item) {
		if (item != null)
			return UZUtility.parseCssColor(item);
		return UZUtility.parseCssColor("#fff");
	}

	public Bitmap itemHighlightBitmap(String item, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(item));
	}

	public int itemTxtColor(JSONObject item) {
		if (item != null)
			return UZUtility.parseCssColor(item.optString("color", "#444"));
		return UZUtility.parseCssColor("#444");
	}

	public int itemTxtActiveColor(JSONObject item) {
		if (item != null) {
			if (item.isNull("active")) {
				return itemTxtColor(item);
			}
			return UZUtility.parseCssColor(item.optString("active", "#444"));
		}
		return UZUtility.parseCssColor("#444");
	}

	public int itemTxtHighlightColor(JSONObject item) {
		if (item != null) {
			if (item.isNull("highlight")) {
				return itemTxtColor(item);
			}
			return UZUtility.parseCssColor(item.optString("highlight", "#444"));
		}
		return UZUtility.parseCssColor("#444");
	}

	public int itemTxtSize(JSONObject item) {
		if (item != null) {
			return item.optInt("size", 14);
		}
		return 14;
	}

	public int itemLineColor(JSONObject item) {
		if (item != null) {
			return UZUtility.parseCssColor(item.optString("lineColor",
					"rgba(0,0,0,0)"));
		}
		return UZUtility.parseCssColor("rgba(0,0,0,0)");
	}

	public String itemTxtAlign(JSONObject item) {
		if (item != null) {
			return item.optString("textAlign", "left");
		}
		return "left";
	}

	public JSONObject icon(JSONObject styles) {
		if (styles != null)
			return styles.optJSONObject("icon");
		return null;
	}

	public int iconW(JSONObject icon) {
		if (icon != null) {
			return icon.optInt("w", 20);
		}
		return 20;
	}

	public int iconH(JSONObject icon) {
		if (icon != null) {
			return icon.optInt("w", icon.optInt("w", 20));
		}
		return 20;
	}

	public int iconMarginT(JSONObject icon) {
		if (icon != null) {
			return icon.optInt("marginT", -1);
		}
		return -1;
	}

	public int iconMarginH(JSONObject icon) {
		if (icon != null) {
			return icon.optInt("marginH", 8);
		}
		return 8;
	}

	public String iconBg(JSONObject icon) {
		if (icon != null)
			return icon.optString("bg");
		return null;
	}

	public int iconBgColor(String icon) {
		if (icon != null)
			return UZUtility.parseCssColor(icon);
		return UZUtility.parseCssColor("rgba(0,0,0,0)");
	}

	public Bitmap iconBitmap(String icon, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(icon));
	}

	public String iconBgActive(JSONObject icon) {
		if (icon != null) {
			if (icon.isNull("bgActive")) {
				return iconBg(icon);
			}
			return icon.optString("bgActive");
		}
		return null;
	}

	public int iconBgActiveColor(String icon) {
		if (icon != null)
			return UZUtility.parseCssColor(icon);
		return UZUtility.parseCssColor("rgba(0,0,0,0)");
	}

	public Bitmap iconActiveBitmap(String icon, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(icon));
	}

	public String iconBgHighlight(JSONObject icon) {
		if (icon != null) {
			if (icon.isNull("bgHighlight")) {
				return itemBg(icon);
			}
			return icon.optString("bgHighlight");
		}
		return null;
	}

	public int iconBgHighlightColor(String icon) {
		if (icon != null)
			return UZUtility.parseCssColor(icon);
		return UZUtility.parseCssColor("rgba(0,0,0,0)");
	}

	public Bitmap iconHighlightBitmap(String icon, UzUIMultiSelector module) {
		return getBitmap(module.makeRealPath(icon));
	}

	public String iconAlign(JSONObject icon) {
		if (icon != null)
			return icon.optString("align", "left");
		return "left";
	}

	public boolean animation(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("animation", true);
	}

	public List<Item> items(UZModuleContext moduleContext) {
		if (!moduleContext.isNull("items")) {
			JSONArray items = moduleContext.optJSONArray("items");
			if (items != null && items.length() > 0) {
				List<Item> itemList = new ArrayList<Item>();
				itemList.add(new Item(selectAll(moduleContext), "normal"));
				JSONObject item = null;
				for (int i = 0; i < items.length(); i++) {
					item = items.optJSONObject(i);
					itemList.add(new Item(item.optString("text"), item
							.optString("status", "normal")));
				}
				return itemList;
			}
		}
		return null;
	}

	public JSONArray itemsJson(UZModuleContext moduleContext) {
		if (!moduleContext.isNull("items")) {
			return moduleContext.optJSONArray("items");
		}
		return null;
	}

	class Item {
		String text;
		String status;

		public Item(String text, String status) {
			super();
			this.text = text;
			this.status = status;
		}
	}

	public boolean isBitmap(String param) {
		if (param != null && param.contains("://")) {
			return true;
		}
		return false;
	}

	public int max(UZModuleContext moduleContext) {
		return moduleContext.optInt("max", 0);
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
