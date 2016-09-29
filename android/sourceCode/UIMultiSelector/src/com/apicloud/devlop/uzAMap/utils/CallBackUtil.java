//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap.utils;

import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Point;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class CallBackUtil {
	public static void openCallBack(UZModuleContext moduleContext) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void locationCallBack(UZModuleContext moduleContext,
			AMapLocation aLocation, float heading, boolean status) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", status);
			if (status) {
				ret.put("lon", aLocation.getLongitude());
				ret.put("lat", aLocation.getLatitude());
				long timestamp = System.currentTimeMillis();
				ret.put("timestamp", timestamp);
				ret.put("heading", heading);
			}
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getDistanceCallBack(UZModuleContext moduleContext,
			float distance) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			ret.put("distance", distance);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getCenterCallBack(UZModuleContext moduleContext,
			LatLng latLng) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			if (latLng != null) {
				ret.put("lat", latLng.latitude);
				ret.put("lon", latLng.longitude);
			}
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getZoomLevelCallBack(UZModuleContext moduleContext,
			float level) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			ret.put("level", level);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getRotateCallBack(UZModuleContext moduleContext,
			float rotation) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			ret.put("rotation", rotation);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getOverlookCallBack(UZModuleContext moduleContext,
			float overlook) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			ret.put("overlook", overlook);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getRegionCallBack(UZModuleContext moduleContext,
			LatLngBounds latLngBounds) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			ret.put("lbLat", latLngBounds.southwest.latitude);
			ret.put("lbLon", latLngBounds.southwest.longitude);
			ret.put("rtLat", latLngBounds.northeast.latitude);
			ret.put("rtLon", latLngBounds.northeast.longitude);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void isPolygonContantPointCallBack(
			UZModuleContext moduleContext, boolean status) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", status);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void interconvertCoords(UZModuleContext moduleContext,
			LatLng latLng, Point point) {
		JSONObject ret = new JSONObject();
		try {
			if (latLng != null) {
				ret.put("status", true);
				ret.put("lat", latLng.latitude);
				ret.put("lon", latLng.longitude);
			} else if (point != null) {
				ret.put("status", true);
				ret.put("x", point.x);
				ret.put("y", point.y);
			} else {
				ret.put("status", false);
			}
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void viewChangeCallBack(UZModuleContext moduleContext,
			CameraPosition cameraPosition) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			ret.put("lat", cameraPosition.target.latitude);
			ret.put("lon", cameraPosition.target.longitude);
			ret.put("zoom", cameraPosition.zoom);
			ret.put("rotate", cameraPosition.bearing);
			ret.put("overlook", cameraPosition.tilt);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void clickCallBack(UZModuleContext moduleContext,
			LatLng latLng) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			ret.put("lat", latLng.latitude);
			ret.put("lon", latLng.longitude);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void markerClickCallBack(UZModuleContext moduleContext, int id) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("id", id);
			ret.put("eventType", "click");
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void infoWindowClickCallBack(UZModuleContext moduleContext,
			int id, String clickType) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("id", id);
			ret.put("eventType", clickType);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void markerDragCallBack(UZModuleContext moduleContext,
			int id, String dragState) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("id", id);
			ret.put("eventType", "drag");
			ret.put("dragState", "dragState");
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getMarkerCoordsCallBack(UZModuleContext moduleContext,
			double lat, double lon) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("lat", lat);
			ret.put("lon", lon);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void annotationExistCallBack(UZModuleContext moduleContext,
			boolean status) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", status);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
