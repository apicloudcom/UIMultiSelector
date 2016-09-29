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
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import com.amap.api.maps.AMap;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.Doorway;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.apicloud.devlop.uzAMap.models.CustomBusRoute;
import com.apicloud.devlop.uzAMap.models.CustomDriveRoute;
import com.apicloud.devlop.uzAMap.models.CustomWalkRoute;
import com.apicloud.devlop.uzAMap.utils.JsParamsUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MapSearch implements OnRouteSearchListener {
	private Context mContext;
	private UZModuleContext mModuleContext;

	private static final String ROUTE_TYPE_DRIVE = "drive";
	private static final String ROUTE_TYPE_TRANSIT = "transit";
	private static final String ROUTE_TYPE_WALK = "walk";

	private static final String DRIVE_TIME_FIRST = "drive_time_first";// 速度优先（时间）
	private static final String DRIVE_FEE_FIRST = "drive_fee_first";// 费用优先（不走收费路段的最快道路）
	private static final String DRIVE_DIS_FIRST = "drive_dis_first";// 距离优先
	private static final String DRIVE_FAST_NO = "drive_fast_no";// 不走快速路
	private static final String DRIVE_JAM_NO = "drive_jam_no";// 结合实时交通（躲避拥堵）
	private static final String DRIVE_MULTI_STRATEGY = "drive_multi_strategy";// 多策略（同时使用速度优先、费用优先、距离优先三个策略）
	private static final String DRIVE_HIGHWAY_NO = "drive_highway_no";// 不走高速
	private static final String DRIVE_HIGHWAY_FEE_NO = "drive_highway_fee_no";// 不走高速且避免收费
	private static final String DRIVE_FEE_JAM = "drive_fee_jam";// 躲避收费和拥堵
	private static final String DRIVE_HIGHWAY_FEE_JAM = "drive_highway_fee_jam";// 不走高速且躲避收费和拥堵
	private static final String TRANSIT_TIME_FIRST = "transit_time_first";// 最快捷模式
	private static final String TRANSIT_FEE_FIRST = "transit_fee_first";// 最经济模式
	private static final String TRANSIT_TRANSFER_FIRST = "transit_transfer_first";// 最少换乘模式
	private static final String TRANSIT_WALK_FIRST = "transit_walk_first";// 最少步行模式
	private static final String TRANSIT_COMFORT_FIRST = "transit_comfort_first";// 最舒适模式
	private static final String TRANSIT_SUBWAY_NO = "transit_subway_no";// 不乘地铁模式

	private BusRouteResult mBusRouteResult;
	private DriveRouteResult mDriveRouteResult;
	private WalkRouteResult mWalkRouteResult;

	private String mSearchType;

	@SuppressLint("UseSparseArrays")
	private Map<Integer, CustomBusRoute> mBusRouteOverlayMap = new HashMap<Integer, CustomBusRoute>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, CustomDriveRoute> mDriveRouteOverlayMap = new HashMap<Integer, CustomDriveRoute>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, CustomWalkRoute> mWalkRouteOverlayMap = new HashMap<Integer, CustomWalkRoute>();

	public MapSearch(Context context) {
		this.mContext = context;
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		if (rCode == 1000) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				mBusRouteResult = result;
				JSONObject resultJson = busRouteSearchedJson(result);
				routeCallBack(mModuleContext, resultJson);
				return;
			}
		}
		failCallBack(mModuleContext);
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		if (rCode == 1000) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				mDriveRouteResult = result;
				JSONObject resultJson = driveRouteSearchedJson(result);
				routeCallBack(mModuleContext, resultJson);
				return;
			}
		}
		failCallBack(mModuleContext);
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		if (rCode == 1000) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				mWalkRouteResult = result;
				JSONObject resultJson = walkRouteSearchedJson(result);
				routeCallBack(mModuleContext, resultJson);
				return;
			}
		}
		failCallBack(mModuleContext);
	}

	private void routeCallBack(UZModuleContext moduleContext, JSONObject ret) {
		moduleContext.success(ret, false);
	}

	private void failCallBack(UZModuleContext moduleContext) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", false);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void searchRoute(UZModuleContext moduleContext) {
		mModuleContext = moduleContext;
		String type = moduleContext.optString("type", "transit");
		mSearchType = type;
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		double startLat = jsParamsUtil.lat(moduleContext, "start");
		double startLon = jsParamsUtil.lon(moduleContext, "start");
		double endLat = jsParamsUtil.lat(moduleContext, "end");
		double endLon = jsParamsUtil.lon(moduleContext, "end");
		String city = moduleContext.optString("city");
		String policyStr = getPolicyStr(moduleContext, type);
		LatLonPoint startPoint = new LatLonPoint(startLat, startLon);
		LatLonPoint endPoint = new LatLonPoint(endLat, endLon);
		List<LatLonPoint> wayPoints = passedByPoints(moduleContext);
		boolean nightflag = moduleContext.optBoolean("nightflag", false);
		searchRouteResult(startPoint, endPoint, type, policyStr, city,
				wayPoints, nightflag);
	}

	public void drawRoute(UZModuleContext moduleContext, AMap aMap,
			UZModule module) {
		if (mSearchType != null) {
			if (mSearchType.equals(ROUTE_TYPE_TRANSIT)) {
				if (mBusRouteResult != null) {
					drawBusRoute(moduleContext, aMap, module);
				}
			} else if (mSearchType.equals(ROUTE_TYPE_DRIVE)) {
				if (mDriveRouteResult != null) {
					drawDriveRoute(moduleContext, aMap, module);
				}
			} else if (mSearchType.equals(ROUTE_TYPE_WALK)) {
				if (mWalkRouteResult != null) {
					drawWalkRoute(moduleContext, aMap, module);
				}
			}
		}
	}

	public void removeRoute(UZModuleContext moduleContext, AMap aMap) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		List<Integer> ids = jsParamsUtil.removeRouteIds(moduleContext);
		if (ids != null) {
			for (int id : ids) {
				if (mBusRouteOverlayMap.containsKey(id)) {
					CustomBusRoute busRoute = mBusRouteOverlayMap.get(id);
					if (busRoute != null) {
						busRoute.removeFromMap();
					}
				}
				if (mDriveRouteOverlayMap.containsKey(id)) {
					CustomDriveRoute driveRoute = mDriveRouteOverlayMap.get(id);
					if (driveRoute != null) {
						driveRoute.removeFromMap();
					}
				}
				if (mWalkRouteOverlayMap.containsKey(id)) {
					CustomWalkRoute walkRoute = mWalkRouteOverlayMap.get(id);
					if (walkRoute != null) {
						walkRoute.removeFromMap();
					}
				}
			}
		}
	}

	private void drawBusRoute(UZModuleContext moduleContext, AMap aMap,
			UZModule module) {
		if (mContext == null) {
			return;
		}
		int id = moduleContext.optInt("id");
		int index = moduleContext.optInt("index");
		List<BusPath> paths = mBusRouteResult.getPaths();
		if (paths != null) {
			if (index < paths.size()) {
				BusPath busPath = paths.get(index);
				if (busPath != null) {
					CustomBusRoute customBusRoute = getCustomBusRoute(
							moduleContext, aMap, busPath, module);
					mBusRouteOverlayMap.put(id, customBusRoute);
					customBusRoute.removeFromMap();
					customBusRoute.addToMap();
					if (moduleContext.optBoolean("autoresizing", true)) {
						customBusRoute.zoomToSpan();
					}
				}
			}
		}
	}

	private void drawDriveRoute(UZModuleContext moduleContext, AMap aMap,
			UZModule module) {
		if (mContext == null) {
			return;
		}
		int id = moduleContext.optInt("id");
		int index = moduleContext.optInt("index");
		List<DrivePath> paths = mDriveRouteResult.getPaths();
		if (paths != null) {
			if (index < paths.size()) {
				DrivePath drivePath = paths.get(index);
				if (drivePath != null) {
					CustomDriveRoute customDriveRoute = getCustomDriveRoute(
							moduleContext, aMap, drivePath, module);
					mDriveRouteOverlayMap.put(id, customDriveRoute);
					customDriveRoute.removeFromMap();
					customDriveRoute.addToMap();
					if (moduleContext.optBoolean("autoresizing", true)) {
						customDriveRoute.zoomToSpan();
					}
				}
			}
		}
	}

	private void drawWalkRoute(UZModuleContext moduleContext, AMap aMap,
			UZModule module) {
		if (mContext == null) {
			return;
		}
		int id = moduleContext.optInt("id");
		int index = moduleContext.optInt("index");
		List<WalkPath> paths = mWalkRouteResult.getPaths();
		if (paths != null) {
			if (index < paths.size()) {
				WalkPath walkPath = paths.get(index);
				if (walkPath != null) {
					CustomWalkRoute customWalkRoute = getCustomWalkRoute(
							moduleContext, aMap, walkPath, module);
					mWalkRouteOverlayMap.put(id, customWalkRoute);
					customWalkRoute.removeFromMap();
					customWalkRoute.addToMap();
					if (moduleContext.optBoolean("autoresizing", true)) {
						customWalkRoute.zoomToSpan();
					}
				}
			}
		}
	}

	private CustomBusRoute getCustomBusRoute(UZModuleContext moduleContext,
			AMap aMap, BusPath busPath, UZModule module) {
		CustomBusRoute customBusRoute = new CustomBusRoute(mContext, aMap,
				busPath, mBusRouteResult.getStartPos(),
				mBusRouteResult.getTargetPos());
		customBusRoute.setNodeIconVisibility(true);
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		customBusRoute.setBusColor(jsParamsUtil.busColor(moduleContext));
		customBusRoute.setWalkColor(jsParamsUtil.walkColor(moduleContext));
		customBusRoute.setDriveColor(jsParamsUtil.driveColor(moduleContext));
		customBusRoute.setLineWidth(jsParamsUtil.busWidth(moduleContext));
		customBusRoute.setBusPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "bus")));
		customBusRoute.setWalkPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "man")));
		customBusRoute.setDrivePointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "car")));
		customBusRoute.setStartPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "start")));
		customBusRoute.setEndPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "end")));
		return customBusRoute;
	}

	private CustomDriveRoute getCustomDriveRoute(UZModuleContext moduleContext,
			AMap aMap, DrivePath drivePath, UZModule module) {
		CustomDriveRoute customDriveRoute = new CustomDriveRoute(mContext,
				aMap, drivePath, mDriveRouteResult.getStartPos(),
				mDriveRouteResult.getTargetPos());
		customDriveRoute.setNodeIconVisibility(true);
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		customDriveRoute.setBusColor(jsParamsUtil.busColor(moduleContext));
		customDriveRoute.setWalkColor(jsParamsUtil.walkColor(moduleContext));
		customDriveRoute.setDriveColor(jsParamsUtil.driveColor(moduleContext));
		customDriveRoute.setLineWidth(jsParamsUtil.busWidth(moduleContext));
		customDriveRoute.setBusPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "bus")));
		customDriveRoute.setWalkPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "man")));
		customDriveRoute.setDrivePointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "car")));
		customDriveRoute.setStartPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "start")));
		customDriveRoute.setEndPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "end")));
		return customDriveRoute;
	}

	private CustomWalkRoute getCustomWalkRoute(UZModuleContext moduleContext,
			AMap aMap, WalkPath walkPath, UZModule module) {
		CustomWalkRoute customWalkRoute = new CustomWalkRoute(mContext, aMap,
				walkPath, mWalkRouteResult.getStartPos(),
				mWalkRouteResult.getTargetPos());
		customWalkRoute.setNodeIconVisibility(true);
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		customWalkRoute.setBusColor(jsParamsUtil.busColor(moduleContext));
		customWalkRoute.setWalkColor(jsParamsUtil.walkColor(moduleContext));
		customWalkRoute.setDriveColor(jsParamsUtil.driveColor(moduleContext));
		customWalkRoute.setLineWidth(jsParamsUtil.busWidth(moduleContext));
		customWalkRoute.setBusPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "bus")));
		customWalkRoute.setWalkPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "man")));
		customWalkRoute.setDrivePointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "car")));
		customWalkRoute.setStartPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "start")));
		customWalkRoute.setEndPointImgPath(module.makeRealPath(jsParamsUtil
				.iconPath(moduleContext, "end")));
		return customWalkRoute;
	}

	private String getPolicyStr(UZModuleContext moduleContext, String type) {
		String policyStr = moduleContext.optString("strategy");
		if (type.equalsIgnoreCase(ROUTE_TYPE_TRANSIT)) {
			if (moduleContext.isNull("strategy")) {
				policyStr = DRIVE_TIME_FIRST;
			}
		} else if (type.equalsIgnoreCase(ROUTE_TYPE_DRIVE)) {
			if (moduleContext.isNull("strategy")) {
				policyStr = TRANSIT_TIME_FIRST;
			}
		}
		return policyStr;
	}

	private List<LatLonPoint> passedByPoints(UZModuleContext moduleContext) {
		JSONArray waypoints = moduleContext.optJSONArray("waypoints");
		if (waypoints != null) {
			List<LatLonPoint> points = new ArrayList<LatLonPoint>();
			JSONObject point = null;
			for (int i = 0; i < waypoints.length(); i++) {
				point = waypoints.optJSONObject(i);
				points.add(new LatLonPoint(point.optDouble("lat"), point
						.optDouble("lon")));
			}
			return points;
		}
		return null;
	}

	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint,
			String type, String policyStr, String city,
			List<LatLonPoint> wayPoints, boolean nightflag) {
		int policy = getPolicy(policyStr);
		RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint,
				endPoint);
		RouteSearch routeSearch = new RouteSearch(mContext);
		routeSearch.setRouteSearchListener(this);
		if (type.equalsIgnoreCase(ROUTE_TYPE_TRANSIT)) {
			BusRouteQuery query = new BusRouteQuery(fromAndTo, policy, city,
					nightflag ? 1 : 0);
			routeSearch.calculateBusRouteAsyn(query);
		} else if (type.equalsIgnoreCase(ROUTE_TYPE_DRIVE)) {
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, policy,
					wayPoints, null, "");
			routeSearch.calculateDriveRouteAsyn(query);
		} else if (type.equalsIgnoreCase(ROUTE_TYPE_WALK)) {
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, policy);
			routeSearch.calculateWalkRouteAsyn(query);
		}
	}

	private int getPolicy(String policyStr) {
		if (policyStr.equalsIgnoreCase(DRIVE_TIME_FIRST)) {
			return RouteSearch.DrivingDefault;
		} else if (policyStr.equalsIgnoreCase(DRIVE_FEE_FIRST)) {
			return RouteSearch.DrivingSaveMoney;
		} else if (policyStr.equalsIgnoreCase(DRIVE_DIS_FIRST)) {
			return RouteSearch.DrivingShortDistance;
		} else if (policyStr.equalsIgnoreCase(DRIVE_FAST_NO)) {
			return RouteSearch.DrivingNoExpressways;
		} else if (policyStr.equalsIgnoreCase(DRIVE_JAM_NO)) {
			return RouteSearch.DrivingAvoidCongestion;
		} else if (policyStr.equalsIgnoreCase(DRIVE_MULTI_STRATEGY)) {
			return RouteSearch.DrivingMultiStrategy;
		} else if (policyStr.equalsIgnoreCase(DRIVE_HIGHWAY_NO)) {
			return RouteSearch.DrivingNoHighWay;
		} else if (policyStr.equalsIgnoreCase(DRIVE_HIGHWAY_FEE_NO)) {
			return RouteSearch.DrivingNoHighWaySaveMoney;
		} else if (policyStr.equalsIgnoreCase(DRIVE_FEE_JAM)) {
			return RouteSearch.DrivingSaveMoneyAvoidCongestion;
		} else if (policyStr.equalsIgnoreCase(DRIVE_HIGHWAY_FEE_JAM)) {
			return RouteSearch.DrivingNoHighAvoidCongestionSaveMoney;
		} else if (policyStr.equalsIgnoreCase(TRANSIT_TIME_FIRST)) {
			return RouteSearch.BusDefault;
		} else if (policyStr.equalsIgnoreCase(TRANSIT_FEE_FIRST)) {
			return RouteSearch.BusSaveMoney;
		} else if (policyStr.equalsIgnoreCase(TRANSIT_TRANSFER_FIRST)) {
			return RouteSearch.BusLeaseChange;
		} else if (policyStr.equalsIgnoreCase(TRANSIT_WALK_FIRST)) {
			return RouteSearch.BusLeaseWalk;
		} else if (policyStr.equalsIgnoreCase(TRANSIT_COMFORT_FIRST)) {
			return RouteSearch.BusComfortable;
		} else if (policyStr.equalsIgnoreCase(TRANSIT_SUBWAY_NO)) {
			return RouteSearch.BusNoSubway;
		}
		return 0;
	}

	private JSONObject busRouteSearchedJson(BusRouteResult result) {
		JSONObject resultJson = new JSONObject();
		try {
			resultJson.put("status", true);
			resultJson.put("start", startPoint(result));
			resultJson.put("end", endPoint(result));
			resultJson.put("transits", busPlans(result));
			resultJson.put("taxiCost", result.getTaxiCost());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	private JSONObject startPoint(BusRouteResult result) throws JSONException {
		JSONObject startJson = new JSONObject();
		LatLonPoint start = result.getStartPos();
		startJson.put("lon", start.getLongitude());
		startJson.put("lat", start.getLatitude());
		return startJson;
	}

	private JSONObject startPoint(WalkRouteResult result) throws JSONException {
		JSONObject startJson = new JSONObject();
		LatLonPoint start = result.getStartPos();
		startJson.put("lon", start.getLongitude());
		startJson.put("lat", start.getLatitude());
		return startJson;
	}

	private JSONObject endPoint(BusRouteResult result) throws JSONException {
		JSONObject endJson = new JSONObject();
		LatLonPoint end = result.getTargetPos();
		endJson.put("lon", end.getLongitude());
		endJson.put("lat", end.getLatitude());
		return endJson;
	}

	private JSONObject endPoint(WalkRouteResult result) throws JSONException {
		JSONObject endJson = new JSONObject();
		LatLonPoint end = result.getTargetPos();
		endJson.put("lon", end.getLongitude());
		endJson.put("lat", end.getLatitude());
		return endJson;
	}

	private JSONArray busPlans(BusRouteResult result) throws JSONException {
		JSONArray plans = new JSONArray();
		List<BusPath> busPaths = result.getPaths();
		BusPath busPath = null;
		for (int i = 0; i < busPaths.size(); i++) {
			JSONObject plan = new JSONObject();
			plans.put(plan);
			busPath = busPaths.get(i);
			plan.put("cost", busPath.getCost());
			plan.put("duration", busPath.getDuration());
			plan.put("nightflag", busPath.isNightBus());
			plan.put("walkingDistance", busPath.getWalkDistance());
			plan.put("busDistance", busPath.getBusDistance());
			plan.put("segments", busSteps(busPath));
		}
		return plans;
	}

	private JSONArray busSteps(BusPath busPath) throws JSONException {
		JSONArray busStepsJson = new JSONArray();
		List<BusStep> busSteps = busPath.getSteps();
		if (busSteps != null) {
			for (int i = 0; i < busSteps.size(); i++) {
				busStepsJson.put(busStep(busSteps.get(i)));
			}
		}
		return busStepsJson;
	}

	private JSONObject busStep(BusStep busStep) throws JSONException {
		JSONObject busStepJson = new JSONObject();
		Doorway enter = busStep.getEntrance();
		Doorway exit = busStep.getExit();
		if (enter != null) {
			busStepJson.put("enterName", enter.getName());
			busStepJson.put("enterLoc", enterLoc(busStep));
		}
		if (exit != null) {
			busStepJson.put("exitName", exit.getName());
			busStepJson.put("exitLoc", exitLoc(busStep));
		}
		busStepJson.put("buslines", buslines(busStep));
		busStepJson.put("walking", walking(busStep));
		return busStepJson;
	}

	private JSONObject buslines(BusStep busStep) throws JSONException {
		RouteBusLineItem busLineItem = busStep.getBusLine();
		JSONObject buslines = new JSONObject();
		if (busLineItem != null) {
			buslines.put("type", busLineItem.getBusLineType());
			buslines.put("name", busLineItem.getBusLineName());
			buslines.put("uid", busLineItem.getBusLineId());
		}
		return buslines;
	}

	private JSONObject enterLoc(BusStep busStep) throws JSONException {
		Doorway enter = busStep.getEntrance();
		JSONObject enterLoc = new JSONObject();
		enterLoc.put("lon", enter.getLatLonPoint().getLongitude());
		enterLoc.put("lat", enter.getLatLonPoint().getLatitude());
		return enterLoc;
	}

	private JSONObject walking(BusStep busStep) throws JSONException {
		RouteBusWalkItem walk = busStep.getWalk();
		JSONObject walking = new JSONObject();
		if (walk != null) {
			walking.put("distance", walk.getDistance());
			walking.put("duration", walk.getDuration());
		}
		return walking;
	}

	private JSONObject exitLoc(BusStep busStep) throws JSONException {
		Doorway exit = busStep.getEntrance();
		JSONObject exitLoc = new JSONObject();
		if (exit != null) {
			LatLonPoint latLonPoint = exit.getLatLonPoint();
			if (latLonPoint != null) {
				exitLoc.put("lon", latLonPoint.getLongitude());
				exitLoc.put("lat", latLonPoint.getLatitude());
			}
		}
		return exitLoc;
	}

	private JSONObject driveRouteSearchedJson(DriveRouteResult result) {
		JSONObject resultJson = new JSONObject();
		try {
			resultJson.put("status", true);
			resultJson.put("start", startPoint(result));
			resultJson.put("end", endPoint(result));
			resultJson.put("paths", drivePlans(result));
			resultJson.put("taxiCost", result.getTaxiCost());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	private JSONObject walkRouteSearchedJson(WalkRouteResult result) {
		JSONObject resultJson = new JSONObject();
		try {
			resultJson.put("status", true);
			resultJson.put("start", startPoint(result));
			resultJson.put("end", endPoint(result));
			resultJson.put("paths", walkPlans(result));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	private JSONArray drivePlans(DriveRouteResult result) throws JSONException {
		List<DrivePath> drivePaths = result.getPaths();
		JSONArray plans = new JSONArray();
		DrivePath drivePath = null;
		for (int i = 0; i < drivePaths.size(); i++) {
			JSONObject plan = new JSONObject();
			plans.put(plan);
			drivePath = drivePaths.get(i);
			plan.put("tolls", drivePath.getTolls());
			plan.put("duration", drivePath.getDuration());
			plan.put("distance", drivePath.getDistance());
			plan.put("tollDistance", drivePath.getTollDistance());
			plan.put("strategy", drivePath.getStrategy());
			plan.put("steps", driveSteps(drivePath));
		}
		return plans;
	}

	private JSONArray walkPlans(WalkRouteResult result) throws JSONException {
		List<WalkPath> walkPaths = result.getPaths();
		JSONArray plans = new JSONArray();
		WalkPath walkPath = null;
		for (int i = 0; i < walkPaths.size(); i++) {
			JSONObject plan = new JSONObject();
			plans.put(plan);
			walkPath = walkPaths.get(i);
			plan.put("duration", walkPath.getDuration());
			plan.put("distance", walkPath.getDistance());
			plan.put("steps", walkSteps(walkPath));
		}
		return plans;
	}

	private JSONArray driveSteps(DrivePath drivePath) throws JSONException {
		JSONArray driveStepsJson = new JSONArray();
		List<DriveStep> driveSteps = drivePath.getSteps();
		if (driveSteps != null) {
			for (int i = 0; i < driveSteps.size(); i++) {
				driveStepsJson.put(driveStep(driveSteps.get(i)));
			}
		}
		return driveStepsJson;
	}

	private JSONArray walkSteps(WalkPath walkPath) throws JSONException {
		JSONArray walkStepsJson = new JSONArray();
		List<WalkStep> walkSteps = walkPath.getSteps();
		if (walkSteps != null) {
			for (int i = 0; i < walkSteps.size(); i++) {
				walkStepsJson.put(walkStep(walkSteps.get(i)));
			}
		}
		return walkStepsJson;
	}

	private JSONObject driveStep(DriveStep driveStep) throws JSONException {
		JSONObject busStepJson = new JSONObject();
		busStepJson.put("instruction", driveStep.getInstruction());
		busStepJson.put("orientation", driveStep.getOrientation());
		busStepJson.put("road", driveStep.getRoad());
		busStepJson.put("distance", driveStep.getDistance());
		busStepJson.put("duration", driveStep.getDuration());
		busStepJson.put("action", driveStep.getAction());
		busStepJson.put("assistantAction", driveStep.getAssistantAction());
		busStepJson.put("tolls", driveStep.getTolls());
		busStepJson.put("tollDistance", driveStep.getTollDistance());
		busStepJson.put("tollRoad", driveStep.getTollRoad());
		return busStepJson;
	}

	private JSONObject walkStep(WalkStep walkStep) throws JSONException {
		JSONObject busStepJson = new JSONObject();
		busStepJson.put("instruction", walkStep.getInstruction());
		busStepJson.put("orientation", walkStep.getOrientation());
		busStepJson.put("road", walkStep.getRoad());
		busStepJson.put("distance", walkStep.getDistance());
		busStepJson.put("duration", walkStep.getDuration());
		busStepJson.put("action", walkStep.getAction());
		busStepJson.put("assistantAction", walkStep.getAssistantAction());
		return busStepJson;
	}

	private JSONObject startPoint(DriveRouteResult result) throws JSONException {
		JSONObject startJson = new JSONObject();
		LatLonPoint start = result.getStartPos();
		startJson.put("lon", start.getLongitude());
		startJson.put("lat", start.getLatitude());
		return startJson;
	}

	private JSONObject endPoint(DriveRouteResult result) throws JSONException {
		JSONObject endJson = new JSONObject();
		LatLonPoint end = result.getTargetPos();
		endJson.put("lon", end.getLongitude());
		endJson.put("lat", end.getLatitude());
		return endJson;
	}
}
