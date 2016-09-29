//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import java.util.List;

import android.graphics.Point;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyTrafficStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.apicloud.devlop.uzAMap.utils.CallBackUtil;
import com.apicloud.devlop.uzAMap.utils.JsParamsUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MapSimple {

	public void getDistance(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		double startLon = jsParamsUtil.lon(moduleContext, "start");
		double startLat = jsParamsUtil.lat(moduleContext, "start");
		double endLon = jsParamsUtil.lon(moduleContext, "end");
		double endLat = jsParamsUtil.lat(moduleContext, "end");
		float distance = AMapUtils.calculateLineDistance(new LatLng(startLat,
				startLon), new LatLng(endLat, endLon));
		CallBackUtil.getDistanceCallBack(moduleContext, distance);
	}

	public void getCenter(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			LatLng latLng = aMap.getCameraPosition().target;
			CallBackUtil.getCenterCallBack(moduleContext, latLng);
		}
	}

	public void panBy(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			boolean isAnimated = moduleContext.optBoolean("animation");
			changeCamera(aMap, getScrollCameraUpdate(moduleContext),
					isAnimated, null);
		}
	}

	public void setCenter(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			boolean isAnimated = moduleContext.optBoolean("animation", true);
			changeCamera(aMap, getCenterCameraUpdate(aMap, moduleContext),
					isAnimated, null);
		}
	}

	public void setCenterOpen(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			changeCamera(aMap, getOpenCenterCameraUpdate(moduleContext), false,
					null);
		}
	}

	public void setZoomLevel(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			float zoomLevel = (float) moduleContext.optDouble("level", 10);
			boolean isAnimated = moduleContext.optBoolean("animation", true);
			changeCamera(aMap, CameraUpdateFactory.zoomTo(zoomLevel),
					isAnimated, null);
		}
	}

	public void getZoomLevel(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			float zoomLevel = aMap.getCameraPosition().zoom;
			CallBackUtil.getZoomLevelCallBack(moduleContext, zoomLevel);
		}
	}

	public void setMapAttr(UZModuleContext moduleContext, AMap aMap) {
		String type = moduleContext.optString("type", Constans.MAP_TYPE_NORMAL);
		boolean isTrafficon = moduleContext.optBoolean("trafficOn", false);
		initMyTrafficStyle(aMap);
		setType(type, aMap, isTrafficon);
		setZoomEnable(moduleContext, aMap);
		setScrollEnable(moduleContext, aMap);
		setRotateEnable(moduleContext, aMap);
		setTiltEnable(moduleContext, aMap);
	}

	public void setRotation(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			boolean isAnimated = moduleContext.optBoolean("animation");
			int duration = (int) (moduleContext.optDouble("duration") * 1000);
			changeCamera(aMap, getRotateCameraUpdate(moduleContext),
					isAnimated, duration, null);
		}
	}

	public void getRotation(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			float rotation = aMap.getCameraPosition().bearing;
			CallBackUtil.getRotateCallBack(moduleContext, rotation);
		}
	}

	public void setOverlook(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			boolean isAnimated = moduleContext.optBoolean("animation");
			int duration = (int) (moduleContext.optDouble("duration") * 1000);
			changeCamera(aMap, getOverLookCameraUpdate(moduleContext),
					isAnimated, duration, null);
		}
	}

	public void getOverlook(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			float tilt = aMap.getCameraPosition().tilt;
			CallBackUtil.getOverlookCallBack(moduleContext, tilt);
		}
	}

	public void setRegion(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			boolean isAnimated = moduleContext.optBoolean("animation");
			changeCamera(aMap, getRegionCameraUpdate(moduleContext),
					isAnimated, null);
		}
	}

	public void getRegion(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			LatLngBounds latLngBounds = aMap.getProjection().getVisibleRegion().latLngBounds;
			CallBackUtil.getRegionCallBack(moduleContext, latLngBounds);
		}
	}

	private CameraUpdate getCenterCameraUpdate(AMap aMap,
			UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		double lat = jsParamsUtil.centerLat(moduleContext);
		double lon = jsParamsUtil.centerLon(moduleContext);
		return CameraUpdateFactory.newCameraPosition(new CameraPosition(
				new LatLng(lat, lon), aMap.getCameraPosition().zoom, 0, 0));
	}

	private CameraUpdate getOpenCenterCameraUpdate(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		double lat = jsParamsUtil.openCenterLat(moduleContext);
		double lon = jsParamsUtil.openCenterLon(moduleContext);
		float zoomLevel = (float) moduleContext.optDouble("zoomLevel", 10);
		return CameraUpdateFactory.newCameraPosition(new CameraPosition(
				new LatLng(lat, lon), zoomLevel, 0, 0));
	}

	private CameraUpdate getRegionCameraUpdate(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		return CameraUpdateFactory.newLatLngBounds(
				jsParamsUtil.latLngBounds(moduleContext), 0);
	}

	private CameraUpdate getOverLookCameraUpdate(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		int degree = jsParamsUtil.overlookDegree(moduleContext);
		return CameraUpdateFactory.changeTilt(degree);
	}

	private CameraUpdate getRotateCameraUpdate(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		int degree = jsParamsUtil.rotateDegree(moduleContext);
		return CameraUpdateFactory.changeBearing(degree);
	}

	private void setZoomEnable(UZModuleContext moduleContext, AMap aMap) {
		boolean zoomEnable = moduleContext.optBoolean("zoomEnable", true);
		if (aMap != null) {
			aMap.getUiSettings().setZoomGesturesEnabled(zoomEnable);
		}
	}

	private void setScrollEnable(UZModuleContext moduleContext, AMap aMap) {
		boolean scrollEnable = moduleContext.optBoolean("scrollEnable", true);
		if (aMap != null) {
			aMap.getUiSettings().setScrollGesturesEnabled(scrollEnable);
		}
	}

	private void setRotateEnable(UZModuleContext moduleContext, AMap aMap) {
		boolean rotateEnable = moduleContext.optBoolean("rotateEnabled", true);
		if (aMap != null) {
			aMap.getUiSettings().setRotateGesturesEnabled(rotateEnable);
		}
	}

	private void setTiltEnable(UZModuleContext moduleContext, AMap aMap) {
		boolean overlookEnabled = moduleContext.optBoolean("overlookEnabled",
				true);
		if (aMap != null) {
			aMap.getUiSettings().setTiltGesturesEnabled(overlookEnabled);
		}
	}

	private CameraUpdate getScrollCameraUpdate(UZModuleContext moduleContext) {
		int x = moduleContext.optInt("x");
		int y = moduleContext.optInt("y");
		return CameraUpdateFactory.scrollBy(x, y);
	}

	private void changeCamera(AMap aMap, CameraUpdate update,
			boolean isAnimated, CancelableCallback callback) {
		if (isAnimated) {
			aMap.animateCamera(update, 300, callback);
		} else {
			aMap.moveCamera(update);
		}
	}

	private void changeCamera(AMap aMap, CameraUpdate update,
			boolean isAnimated, long duration, CancelableCallback callback) {
		if (isAnimated) {
			aMap.animateCamera(update, duration, callback);
		} else {
			aMap.moveCamera(update);
		}
	}

	private void initMyTrafficStyle(AMap aMap) {
		MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
		myTrafficStyle.setSeriousCongestedColor(0xff92000a);
		myTrafficStyle.setCongestedColor(0xffea0312);
		myTrafficStyle.setSlowColor(0xffff7508);
		myTrafficStyle.setSmoothColor(0xff00a209);
		if (aMap != null) {
			aMap.setMyTrafficStyle(myTrafficStyle);
		}
	}

	private void setType(String type, AMap aMap, boolean isTrafficon) {
		if (aMap != null) {
			if (type.equals(Constans.MAP_TYPE_NORMAL)) {
				aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
			} else if (type.equals(Constans.MAP_TYPE_SATELLITE)) {
				aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
			} else if (type.equals(Constans.MAP_TYPE_NIGHT)) {
				aMap.setMapType(AMap.MAP_TYPE_NIGHT);
			}
			aMap.setTrafficEnabled(isTrafficon);
		}
	}

	public void setScaleBar(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			boolean isShow = moduleContext.optBoolean("show", false);
			aMap.getUiSettings().setScaleControlsEnabled(isShow);
		}
	}

	public void setCompass(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			boolean isShow = moduleContext.optBoolean("show", false);
			aMap.getUiSettings().setCompassEnabled(isShow);
		}
	}

	public void setLogo(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			String position = moduleContext.optString("position", "right");
			if (position.equals("left")) {
				aMap.getUiSettings().setLogoPosition(
						AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
			} else if (position.equals("center")) {
				aMap.getUiSettings().setLogoPosition(
						AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
			} else {
				aMap.getUiSettings().setLogoPosition(
						AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
			}
		}
	}

	public void isPolygonContantPoint(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			PolygonOptions polygonOptions = new PolygonOptions();
			JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
			List<LatLng> points = jsParamsUtil.polygonPoints(moduleContext);
			double lon = jsParamsUtil.lon(moduleContext, "point");
			double lat = jsParamsUtil.lat(moduleContext, "point");
			LatLng latLng = new LatLng(lat, lon);
			if (points != null && points.size() > 0) {
				for (LatLng point : points) {
					polygonOptions.add(point);
				}
				Polygon polygon = aMap.addPolygon(polygonOptions);
				if (polygon.contains(latLng)) {
					polygon.remove();
					CallBackUtil.isPolygonContantPointCallBack(moduleContext,
							true);
					return;
				} else {
					polygon.remove();
				}
			}
			CallBackUtil.isPolygonContantPointCallBack(moduleContext, false);
		}
	}

	public void interconvertCoords(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			double x = moduleContext.optDouble("x");
			double y = moduleContext.optDouble("y");
			double lat = moduleContext.optDouble("lat");
			double lon = moduleContext.optDouble("lon");
			if (!moduleContext.isNull("x") && !moduleContext.isNull("y")) {
				LatLng latLng = aMap.getProjection().fromScreenLocation(
						new Point((int) x, (int) y));
				CallBackUtil.interconvertCoords(moduleContext, latLng, null);
			} else if (!moduleContext.isNull("lat")
					&& !moduleContext.isNull("lon")) {
				Point point = aMap.getProjection().toScreenLocation(
						new LatLng(lat, lon));
				CallBackUtil.interconvertCoords(moduleContext, null, point);
			}

		}
	}

	public void addEventListener(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			String name = moduleContext.optString("name");
			if (name.equals("viewChange")) {
				aMap.setOnCameraChangeListener(getCameraChangeListener(moduleContext));
			} else if (name.equals("longPress")) {
				aMap.setOnMapLongClickListener(getOnMapLongClickListener(moduleContext));
			} else if (name.equals("click")) {
				aMap.setOnMapClickListener(getOnMapClickListener(moduleContext));
			}
		}
	}

	public void removeEventListener(UZModuleContext moduleContext, AMap aMap) {
		if (aMap != null) {
			String name = moduleContext.optString("name");
			if (name.equals("viewChange")) {
				aMap.setOnCameraChangeListener(null);
			} else if (name.equals("longPress")) {
				aMap.setOnMapLongClickListener(null);
			} else if (name.equals("click")) {
				aMap.setOnMapClickListener(null);
			}
		}
	}

	private OnCameraChangeListener getCameraChangeListener(
			final UZModuleContext moduleContext) {
		return new OnCameraChangeListener() {
			@Override
			public void onCameraChangeFinish(CameraPosition cameraPosition) {
				CallBackUtil.viewChangeCallBack(moduleContext, cameraPosition);
			}

			@Override
			public void onCameraChange(CameraPosition arg0) {
			}
		};
	}

	private OnMapLongClickListener getOnMapLongClickListener(
			final UZModuleContext moduleContext) {
		return new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng latLng) {
				CallBackUtil.clickCallBack(moduleContext, latLng);
			}
		};
	}

	private OnMapClickListener getOnMapClickListener(
			final UZModuleContext moduleContext) {
		return new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				CallBackUtil.clickCallBack(moduleContext, latLng);
			}
		};
	}
}
