//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.services.core.LatLonPoint;
import com.apicloud.devlop.uzAMap.UzAMap;
import com.apicloud.devlop.uzAMap.models.Annotation;
import com.apicloud.devlop.uzAMap.models.Bubble;
import com.apicloud.devlop.uzAMap.models.LocusData;
import com.apicloud.devlop.uzAMap.models.MoveAnnotation;
import com.uzmap.pkg.uzcore.UZCoreUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
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

	public String apiKey(UZModuleContext moduleContext, UZModule module) {
		if (!moduleContext.isNull("apiKey")) {
			return moduleContext.optString("apiKey");
		} else {
			return module.getFeatureValue("aMap", "android_api_key");
		}
	}

	public int x(UZModuleContext moduleContext) {
		JSONObject rect = moduleContext.optJSONObject("rect");
		if (!moduleContext.isNull("rect")) {
			return rect.optInt("x", 0);
		}
		return 0;
	}

	public int y(UZModuleContext moduleContext) {
		JSONObject rect = moduleContext.optJSONObject("rect");
		if (!moduleContext.isNull("rect")) {
			return rect.optInt("y", 0);
		}
		return 0;
	}

	public int w(UZModuleContext moduleContext, Context context) {
		int defaultValue = getScreenWidth((Activity) context);
		JSONObject rect = moduleContext.optJSONObject("rect");
		if (!moduleContext.isNull("rect")) {
			return rect.optInt("w", defaultValue);
		}
		return defaultValue;
	}

	public int h(UZModuleContext moduleContext, Context context) {
		int defaultValue = getScreenHeight((Activity) context);
		JSONObject rect = moduleContext.optJSONObject("rect");
		if (!moduleContext.isNull("rect")) {
			return rect.optInt("h", defaultValue);
		}
		return defaultValue;
	}

	public double openCenterLat(UZModuleContext moduleContext) {
		JSONObject center = moduleContext.optJSONObject("center");
		if (center != null) {
			return center.optDouble("lat");
		}
		return 0;
	}

	public double openCenterLon(UZModuleContext moduleContext) {
		JSONObject center = moduleContext.optJSONObject("center");
		if (center != null) {
			return center.optDouble("lon");
		}
		return 0;
	}

	public double centerLat(UZModuleContext moduleContext) {
		JSONObject center = moduleContext.optJSONObject("coords");
		if (center != null) {
			return center.optDouble("lat");
		}
		return 0;
	}

	public double centerLon(UZModuleContext moduleContext) {
		JSONObject center = moduleContext.optJSONObject("coords");
		if (center != null) {
			return center.optDouble("lon");
		}
		return 0;
	}

	public double zoomLevel(UZModuleContext moduleContext) {
		return moduleContext.optDouble("zoomLevel", 10);
	}

	public double level(UZModuleContext moduleContext) {
		return moduleContext.optDouble("level", 10);
	}

	public boolean showUserLocation(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("showUserLocation", true);
	}

	public boolean autoStop(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("autoStop", true);
	}

	public String city(UZModuleContext moduleContext) {
		return moduleContext.optString("city");
	}

	public String line(UZModuleContext moduleContext) {
		return moduleContext.optString("line");
	}

	public String address(UZModuleContext moduleContext) {
		return moduleContext.optString("address");
	}

	public float lat(UZModuleContext moduleContext) {
		return (float) moduleContext.optDouble("lat");
	}

	public float lon(UZModuleContext moduleContext) {
		return (float) moduleContext.optDouble("lon");
	}

	public String mcode(UZModuleContext moduleContext) {
		return moduleContext.optString("mcode");
	}

	public float lat(UZModuleContext moduleContext, String parent) {
		JSONObject parentObj = moduleContext.optJSONObject(parent);
		if (parentObj != null) {
			return (float) parentObj.optDouble("lat");
		}
		return 0;
	}

	public float lon(UZModuleContext moduleContext, String parent) {
		JSONObject parentObj = moduleContext.optJSONObject(parent);
		if (parentObj != null) {
			return (float) parentObj.optDouble("lon");
		}
		return 0;
	}

	public boolean isGpsCoord(UZModuleContext moduleContext) {
		String type = moduleContext.optString("type", "common");
		if (type.equals("gps")) {
			return true;
		}
		return false;
	}

	public boolean isShow(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("isShow", true);
	}

	public String trackingMode(UZModuleContext moduleContext) {
		return moduleContext.optString("trackingMode", "none");
	}

	public String mapType(UZModuleContext moduleContext) {
		return moduleContext.optString("type", "standard");
	}

	public boolean zoomEnable(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("zoomEnable", true);
	}

	public boolean scrollEnable(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("scrollEnable", true);
	}

	public int rotateDegree(UZModuleContext moduleContext) {
		return moduleContext.optInt("degree", 0);
	}

	public int overlookDegree(UZModuleContext moduleContext) {
		return moduleContext.optInt("degree", 0);
	}

	public LatLngBounds latLngBounds(UZModuleContext moduleContext) {
		double lbLon = moduleContext.optDouble("lbLon");
		double lbLat = moduleContext.optDouble("lbLat");
		double rtLon = moduleContext.optDouble("rtLon");
		double rtLat = moduleContext.optDouble("rtLat");
		LatLng lbLatLng = new LatLng(lbLat, lbLon);
		LatLng rtLatLng = new LatLng(rtLat, rtLon);
		return new LatLngBounds(lbLatLng, rtLatLng);
	}

	public boolean latLngBoundsAnimation(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("animation", true);
	}

	public List<LatLng> polygonPoints(UZModuleContext moduleContext) {
		JSONArray point = moduleContext.optJSONArray("points");
		if (point != null) {
			List<LatLng> points = new ArrayList<LatLng>();
			JSONObject jsonObject = null;
			for (int i = 0; i < point.length(); i++) {
				jsonObject = point.optJSONObject(i);
				points.add(new LatLng(jsonObject.optDouble("lat"), jsonObject
						.optDouble("lon")));
			}
			return points;
		}
		return null;
	}

	public List<LatLonPoint> boundsPoints(UZModuleContext moduleContext) {
		JSONArray point = moduleContext.optJSONArray("points");
		if (point != null) {
			List<LatLonPoint> points = new ArrayList<LatLonPoint>();
			JSONObject jsonObject = null;
			for (int i = 0; i < point.length(); i++) {
				jsonObject = point.optJSONObject(i);
				points.add(new LatLonPoint(jsonObject.optDouble("lat"),
						jsonObject.optDouble("lon")));
			}
			return points;
		}
		return null;
	}

	public String eventName(UZModuleContext moduleContext) {
		return moduleContext.optString("name");
	}

	public List<Annotation> annotations(UZModuleContext moduleContext,
			UzAMap aMap) {
		JSONArray annotations = moduleContext.optJSONArray("annotations");
		if (annotations != null) {
			List<Annotation> annotationList = new ArrayList<Annotation>();
			JSONObject jsonObject = null;
			JSONArray icons = null;
			JSONArray allIcons = null;
			allIcons = moduleContext.optJSONArray("icons");
			List<Bitmap> allIconList = new ArrayList<Bitmap>();
			List<String> allIconPathList = new ArrayList<String>();
			if (allIcons != null) {
				String iconPath = null;
				for (int j = 0; j < allIcons.length(); j++) {
					iconPath = allIcons.optString(j);
					allIconList.add(getBitmap(aMap.makeRealPath(iconPath)));
					allIconPathList.add(aMap.makeRealPath(iconPath));
				}
			}
			boolean allDraggable = moduleContext.optBoolean("draggable", false);
			for (int i = 0; i < annotations.length(); i++) {
				Annotation annotation = new Annotation();
				jsonObject = annotations.optJSONObject(i);
				int id = jsonObject.optInt("id");
				double lat = jsonObject.optDouble("lat");
				double lon = jsonObject.optDouble("lon");
				boolean draggable = jsonObject.optBoolean("draggable", false);
				icons = jsonObject.optJSONArray("icons");
				if (icons != null) {
					List<Bitmap> iconList = new ArrayList<Bitmap>();
					List<String> iconPathList = new ArrayList<String>();
					String iconPath = null;
					for (int j = 0; j < icons.length(); j++) {
						iconPath = icons.optString(j);
						iconList.add(getBitmap(aMap.makeRealPath(iconPath)));
						iconPathList.add(aMap.makeRealPath(iconPath));
					}
					annotation.setIcons(iconList);
					annotation.setIconsPath(iconPathList);
				} else {
					annotation.setIcons(allIconList);
					annotation.setIconsPath(allIconPathList);
				}
				annotation.setId(id);
				annotation.setLat(lat);
				annotation.setLon(lon);
				annotation.setDraggable(draggable);
				if (jsonObject.isNull("draggable")) {
					annotation.setDraggable(allDraggable);
				}
				double timeInterval = moduleContext
						.optDouble("timeInterval", 3);
				annotation.setTimeInterval(timeInterval);
				annotation.setModuleContext(moduleContext);
				annotationList.add(annotation);
			}
			return annotationList;
		}
		return null;
	}

	public List<MoveAnnotation> moveAnnotations(UZModuleContext moduleContext,
			UzAMap aMap) {
		List<MoveAnnotation> moveAnnotations = new ArrayList<MoveAnnotation>();
		JSONArray array = moduleContext.optJSONArray("annotations");
		if (array != null) {
			JSONObject object = null;
			MoveAnnotation annotation = null;
			for (int i = 0; i < array.length(); i++) {
				object = array.optJSONObject(i);
				int id = object.optInt("id");
				double lat = object.optDouble("lat");
				double lon = object.optDouble("lon");
				String iconPath = object.optString("icon");
				Bitmap icon = getBitmap(aMap.makeRealPath(iconPath));
				boolean draggable = object.optBoolean("draggable", false);
				annotation = new MoveAnnotation(id, null, lat, lon, icon,
						draggable, moduleContext);
				moveAnnotations.add(annotation);
			}
			return moveAnnotations;
		}
		return null;
	}

	public Bubble bubble(UZModuleContext moduleContext, UzAMap aMap) {
		int id = moduleContext.optInt("id");
		String bgImgStr = moduleContext.optString("bgImg");
		Bitmap bgImg = getBitmap(aMap.makeRealPath(bgImgStr));
		JSONObject content = moduleContext.optJSONObject("content");
		String title = null;
		String subTitle = null;
		String iconPath = null;
		if (content != null) {
			title = content.optString("title");
			subTitle = content.optString("subTitle");
			iconPath = content.optString("illus");
		}
		JSONObject styles = moduleContext.optJSONObject("styles");
		String titleColorStr = null;
		int titleSize = 16;
		String subTitleColorStr = null;
		int subTitleSize = 14;
		String illusAlign = null;
		if (content != null) {
			titleColorStr = styles.optString("titleColor");
			subTitleColorStr = styles.optString("subTitleColor");
			titleSize = styles.optInt("titleSize", 16);
			subTitleSize = styles.optInt("subTitleSize", 14);
			illusAlign = styles.optString("illusAlign", "left");
		}
		int titleColor = UZUtility.parseCssColor(titleColorStr);
		int subTitleColor = UZUtility.parseCssColor(subTitleColorStr);
		return new Bubble(id, bgImg, title, subTitle, iconPath, titleSize,
				subTitleSize, illusAlign, titleColor, subTitleColor,
				moduleContext);
	}

	public int bubbleId(UZModuleContext moduleContext) {
		return moduleContext.optInt("id");
	}

	public String bubbleTitle(UZModuleContext moduleContext) {
		JSONObject content = moduleContext.optJSONObject("content");
		if (content != null) {
			return content.optString("title");
		}
		return null;
	}

	public String bubbleSubTitle(UZModuleContext moduleContext) {
		JSONObject content = moduleContext.optJSONObject("content");
		if (content != null) {
			return content.optString("subTitle");
		}
		return null;
	}

	public String bubbleIllusPath(UZModuleContext moduleContext) {
		JSONObject content = moduleContext.optJSONObject("content");
		if (content != null) {
			return content.optString("illus");
		}
		return null;
	}

	public int bubbleTitleColor(UZModuleContext moduleContext) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return UZUtility.parseCssColor(styles.optString("titleColor",
					"#000"));
		}
		return UZUtility.parseCssColor("#000");
	}

	public int bubbleTitleSize(UZModuleContext moduleContext) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return styles.optInt("titleSize", 16);
		}
		return 16;
	}

	public int bubbleSubTitleColor(UZModuleContext moduleContext) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return UZUtility.parseCssColor(styles.optString("subTitleColor",
					"#000"));
		}
		return UZUtility.parseCssColor("#000");
	}

	public int bubbleSubTitleSize(UZModuleContext moduleContext) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return styles.optInt("subTitleSize", 16);
		}
		return 16;
	}

	public String bubbleIconAlign(UZModuleContext moduleContext) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return styles.optString("illusAlign", "left");
		}
		return "left";
	}

	public boolean isBillboardNetIcon(UZModuleContext moduleContext) {
		JSONObject content = moduleContext.optJSONObject("content");
		if (content != null) {
			String url = content.optString("illus");
			if (url != null && url.startsWith("http")) {
				return true;
			}
		}
		return false;
	}

	public List<Integer> removeOverlayIds(UZModuleContext moduleContext) {
		List<Integer> list = new ArrayList<Integer>();
		JSONArray ids = moduleContext.optJSONArray("ids");
		if (ids != null && ids.length() > 0) {
			for (int i = 0; i < ids.length(); i++) {
				list.add(ids.optInt(i));
			}
			return list;
		}
		return null;
	}

	public int overlayColor(UZModuleContext moduleContext) {
		String defaultValue = "#000";
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return UZUtility.parseCssColor(styles.optString("borderColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int overlayFillColor(UZModuleContext moduleContext) {
		String defaultValue = "#000";
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return UZUtility.parseCssColor(styles.optString("fillColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public boolean lineDash(UZModuleContext moduleContext) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return styles.optBoolean("lineDash", false);
		}
		return false;
	}

	public int overlayWidth(UZModuleContext moduleContext) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			return styles.optInt("borderWidth", 2);
		}
		return 2;
	}

	public Bitmap overlayImg(UZModuleContext moduleContext, UZModule module) {
		String imgPath = moduleContext.optString("imgPath");
		return getBitmap(module.makeRealPath(imgPath));
	}

	public Bitmap dashImg(UZModuleContext moduleContext, UZModule module) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			String imgPath = styles.optString("dashImg");
			return getBitmap(module.makeRealPath(imgPath));
		}
		return null;
	}

	public float overlayImgOpacity(UZModuleContext moduleContext) {
		return (float) moduleContext.optDouble("opacity", 1);
	}

	public String routeType(UZModuleContext moduleContext) {
		return moduleContext.optString("type");
	}

	public String routePolicy(UZModuleContext moduleContext) {
		return moduleContext.optString("policy", "ebus_time_first");
	}

	public String routeCity(UZModuleContext moduleContext, String type) {
		if (!moduleContext.isNull(type)) {
			JSONObject point = moduleContext.optJSONObject(type);
			if (!point.isNull("city")) {
				return point.optString("city");
			}
		}
		return null;
	}

	public Bitmap nodeIcon(UZModuleContext moduleContext, UZModule module,
			String name) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (!moduleContext.isNull("styles")) {
			JSONObject start = styles.optJSONObject(name);
			if (start != null) {
				return getBitmap(module.makeRealPath(start.optString("icon")));
			}
		}
		return null;
	}

	public int busColor(UZModuleContext moduleContext) {
		String defaultValue = "#00BFFF";
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("busLine");
			if (parent != null) {
				return UZUtility.parseCssColor(parent.optString("color",
						defaultValue));
			}
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int driveColor(UZModuleContext moduleContext) {
		String defaultValue = "#0000EE";
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("driveLine");
			if (parent != null) {
				return UZUtility.parseCssColor(parent.optString("color",
						defaultValue));
			}
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int walkColor(UZModuleContext moduleContext) {
		String defaultValue = "#698B22";
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("walkLine");
			if (parent != null) {
				return UZUtility.parseCssColor(parent.optString("color",
						defaultValue));
			}
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int busWidth(UZModuleContext moduleContext) {
		int defaultValue = 4;
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("busLine");
			if (parent != null) {
				return UZUtility.dipToPix(parent.optInt("width", defaultValue));
			}
		}
		return UZUtility.dipToPix(defaultValue);
	}

	public int driveWidth(UZModuleContext moduleContext) {
		int defaultValue = 5;
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("driveLine");
			if (parent != null) {
				return UZUtility.dipToPix(parent.optInt("width", defaultValue));
			}
		}
		return UZUtility.dipToPix(defaultValue);
	}

	public int walkWidth(UZModuleContext moduleContext) {
		int defaultValue = 3;
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("walkLine");
			if (parent != null) {
				return UZUtility.dipToPix(parent.optInt("width", defaultValue));
			}
		}
		return UZUtility.dipToPix(defaultValue);
	}

	public String iconPath(UZModuleContext moduleContext, String iconTypeName) {
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("icons");
			if (parent != null) {
				return parent.optString(iconTypeName);
			}
		}
		return "";
	}

	public int buslineWidth(UZModuleContext moduleContext) {
		int defaultValue = 4;
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("line");
			if (parent != null) {
				return UZUtility.dipToPix(parent.optInt("width", defaultValue));
			}
		}
		return UZUtility.dipToPix(defaultValue);
	}

	public int buslineColor(UZModuleContext moduleContext) {
		String defaultValue = "#00BFFF";
		JSONObject styles = moduleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject parent = styles.optJSONObject("line");
			if (parent != null) {
				return UZUtility.parseCssColor(parent.optString("color",
						defaultValue));
			}
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public boolean autoresizing(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("autoresizing", true);
	}

	public List<Integer> removeRouteIds(UZModuleContext moduleContext) {
		JSONArray ids = moduleContext.optJSONArray("ids");
		if (ids != null) {
			List<Integer> idList = new ArrayList<Integer>();
			for (int i = 0; i < ids.length(); i++) {
				idList.add(ids.optInt(i));
			}
			return idList;
		}
		return null;
	}

	public List<LocusData> locusDatas(UzAMap aMap, UZModuleContext moduleContext) {
		List<LocusData> locusDatas = new ArrayList<LocusData>();
		String path = moduleContext.optString("locusData");
		if (path != null) {
			try {
				InputStream is = UZUtility.guessInputStream(aMap
						.makeRealPath(path));
				StringBuffer sb = new StringBuffer();
				BufferedReader in = new BufferedReader(
						new InputStreamReader(is));
				String temp = null;
				while (true) {
					temp = in.readLine();
					if (temp == null)
						break;
					String tmp = new String(temp.getBytes(), "utf-8");
					sb.append(tmp);
				}
				is.close();
				in.close();
				JSONArray json = new JSONArray(sb.toString());
				JSONObject obj = null;
				for (int i = 0; i < json.length(); i++) {
					obj = json.optJSONObject(i);
					double lon = Double.valueOf(obj.optString("longtitude"));
					double lat = Double.valueOf(obj.optString("latitude"));
					int rgba = UZUtility.parseCssColor(obj.optString("rgba"));
					locusDatas.add(new LocusData(lon, lat, rgba));
				}
				return locusDatas;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
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
