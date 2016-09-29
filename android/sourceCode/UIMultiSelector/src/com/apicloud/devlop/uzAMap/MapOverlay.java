//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.apicloud.devlop.uzAMap.models.LocusData;
import com.apicloud.devlop.uzAMap.utils.JsParamsUtil;
import com.google.android.gms.internal.js;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

@SuppressLint("UseSparseArrays")
public class MapOverlay {
	private UzAMap mUzAMap;
	private AMap mAMap;
	private Map<Integer, Polyline> mLineMap = new HashMap<Integer, Polyline>();
	private Map<Integer, Polygon> mGonMap = new HashMap<Integer, Polygon>();
	private Map<Integer, Circle> mCircleMap = new HashMap<Integer, Circle>();
	private Map<Integer, GroundOverlay> mGroundMap = new HashMap<Integer, GroundOverlay>();

	public MapOverlay(UzAMap uzAMap, AMap aMap) {
		this.mUzAMap = uzAMap;
		this.mAMap = aMap;
	}

	public void addLine(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		PolylineOptions polylineOptions = new PolylineOptions();
		JSONObject styles = moduleContext.optJSONObject("styles");
		boolean lineDash = false;
		int borderColor = UZUtility.parseCssColor("#000");
		double borderWidth = 2;
		if (styles != null) {
			lineDash = styles.optBoolean("lineDash", false);
			borderColor = UZUtility.parseCssColor(styles.optString(
					"borderColor", "#000"));
			borderWidth = styles.optDouble("borderWidth", 2);
			String strokeImgPath = moduleContext.optString("strokeImg");
			JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
			Bitmap strokeImg = jsParamsUtil.getBitmap(mUzAMap
					.makeRealPath(strokeImgPath));
			polylineOptions.setCustomTexture(BitmapDescriptorFactory
					.fromBitmap(strokeImg));
		}
		polylineOptions.width((float) borderWidth).color(borderColor);
		polylineOptions.setDottedLine(lineDash);
		if (!moduleContext.isNull("points")) {
			JSONArray pointArray = moduleContext.optJSONArray("points");
			if (pointArray.length() > 0) {
				double lon = 0;
				double lat = 0;
				JSONObject tmp = null;
				LatLng latLng = null;
				for (int i = 0; i < pointArray.length(); i++) {
					tmp = pointArray.optJSONObject(i);
					lon = tmp.optDouble("lon");
					lat = tmp.optDouble("lat");
					latLng = new LatLng(lat, lon);
					polylineOptions.add(latLng);
				}
			}
			if (mAMap != null) {
				Polyline polyline = mAMap.addPolyline(polylineOptions);
				mLineMap.put(id, polyline);
			}
		}
	}

	public void addLocus(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		PolylineOptions polylineOptions = new PolylineOptions();
		double borderWidth = moduleContext.optDouble("borderWidth", 5);
		polylineOptions.width((float) borderWidth);
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		List<LocusData> locusDatas = jsParamsUtil.locusDatas(mUzAMap,
				moduleContext);
		List<Integer> colorList = new ArrayList<Integer>();
		if (locusDatas != null) {
			double lbLon = 0;
			double lbLat = 0;
			double rtLon = 0;
			double rtLat = 0;
			for (int i = 0; i < locusDatas.size(); i++) {
				LocusData ld = locusDatas.get(i);
				double lat = ld.getLatitude();
				double lon = ld.getLongtitude();
				if (i == 0) {
					lbLat = lat;
					lbLon = lon;
				}
				if (lat > lbLat) {
					rtLat = lat;
				} else {
					lbLat = lat;
				}
				if (lon > lbLon) {
					rtLon = lon;
				} else {
					lbLon = lon;
				}
				colorList.add(ld.getRgba());
				polylineOptions.add(new LatLng(ld.getLatitude(), ld
						.getLongtitude()));
			}
			LatLng lbLatLng = new LatLng(lbLat, lbLon);
			LatLng rtLatLng = new LatLng(rtLat, rtLon);
			polylineOptions.colorValues(colorList);
			if (mAMap != null) {
				Polyline polyline = mAMap.addPolyline(polylineOptions);
				mLineMap.put(id, polyline);
				if (moduleContext.optBoolean("autoresizing", true)) {
					mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
							new LatLngBounds(lbLatLng, rtLatLng), 0));
				}
			}
		}
	}

	public void addCircle(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		JSONObject styles = moduleContext.optJSONObject("styles");
		CircleOptions circleOptions = new CircleOptions();
		int borderColor = UZUtility.parseCssColor("#000");
		int fillColor = UZUtility.parseCssColor("rgba(125,125,125,0.8)");
		double borderWidth = 2;
		if (styles != null) {
			borderColor = UZUtility.parseCssColor(styles.optString(
					"borderColor", "#000"));
			fillColor = UZUtility.parseCssColor(styles.optString("fillColor",
					"rgba(125,125,125,0.8)"));
			borderWidth = styles.optDouble("borderWidth", 2);
			double radius = moduleContext.optDouble("radius");
			circleOptions.fillColor(fillColor).strokeColor(borderColor)
					.strokeWidth((float) borderWidth).radius(radius);
		}
		JSONObject center = moduleContext.optJSONObject("center");
		if (center != null) {
			double lat = center.optDouble("lat");
			double lon = center.optDouble("lon");
			circleOptions.center(new LatLng(lat, lon));
			if (mAMap != null) {
				Circle circle = mAMap.addCircle(circleOptions);
				mCircleMap.put(id, circle);
			}
		}

	}

	public void addPolygon(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		PolygonOptions polygonOptions = new PolygonOptions();
		JSONObject styles = moduleContext.optJSONObject("styles");
		int borderColor = UZUtility.parseCssColor("#000");
		int fillColor = UZUtility.parseCssColor("rgba(125,125,125,0.8)");
		double borderWidth = 2;
		if (styles != null) {
			borderColor = UZUtility.parseCssColor(styles.optString(
					"borderColor", "#000"));
			borderWidth = styles.optDouble("borderWidth", 2);
			fillColor = UZUtility.parseCssColor(styles.optString("fillColor",
					"rgba(125,125,125,0.8)"));
		}
		polygonOptions.strokeWidth((float) borderWidth)
				.strokeColor(borderColor).fillColor(fillColor);
		if (!moduleContext.isNull("points")) {
			JSONArray pointArray = moduleContext.optJSONArray("points");
			if (pointArray.length() > 0) {
				double lon = 0;
				double lat = 0;
				JSONObject tmp = null;
				LatLng latLng = null;
				for (int i = 0; i < pointArray.length(); i++) {
					tmp = pointArray.optJSONObject(i);
					lon = tmp.optDouble("lon");
					lat = tmp.optDouble("lat");
					latLng = new LatLng(lat, lon);
					polygonOptions.add(latLng);
				}
			}
			if (mAMap != null) {
				Polygon polygon = mAMap.addPolygon(polygonOptions);
				mGonMap.put(id, polygon);
			}
		}
	}

	public void addImg(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		if (!moduleContext.isNull("imgPath")) {
			String imgPath = moduleContext.optString("imgPath");
			JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
			Bitmap bitmap = jsParamsUtil.getBitmap(mUzAMap
					.makeRealPath(imgPath));
			if (bitmap != null) {
				double lbLon = moduleContext.optDouble("lbLon");
				double lbLat = moduleContext.optDouble("lbLat");
				LatLng lLatLng = new LatLng(lbLat, lbLon);
				double rtLon = moduleContext.optDouble("rtLon");
				double rtLat = moduleContext.optDouble("rtLat");
				LatLng rLatLng = new LatLng(rtLat, rtLon);
				LatLngBounds bounds = new LatLngBounds.Builder()
						.include(lLatLng).include(rLatLng).build();
				GroundOverlay groundoverlay = mAMap
						.addGroundOverlay(new GroundOverlayOptions()
								.anchor(0.5f, 0.5f)
								.transparency(0.1f)
								.image(BitmapDescriptorFactory
										.fromBitmap(bitmap))
								.positionFromBounds(bounds));
				mGroundMap.put(id, groundoverlay);
			}
		}
	}

	public void removeOverlay(UZModuleContext moduleContext) {
		JSONArray ids = moduleContext.optJSONArray("ids");
		if (ids != null) {
			for (int i = 0; i < ids.length(); i++) {
				int id = ids.optInt(i);
				Polyline polyline = mLineMap.get(id);
				if (polyline != null) {
					polyline.remove();
				}
				mLineMap.remove(id);
				Polygon polygon = mGonMap.get(id);
				if (polygon != null) {
					polygon.remove();
				}
				mGonMap.remove(id);
				Circle circle = mCircleMap.get(id);
				if (circle != null) {
					circle.remove();
				}
				mCircleMap.remove(id);
				GroundOverlay groundOverlay = mGroundMap.get(id);
				if (groundOverlay != null) {
					groundOverlay.remove();
				}
				mGroundMap.remove(id);
			}
		}
	}
}
