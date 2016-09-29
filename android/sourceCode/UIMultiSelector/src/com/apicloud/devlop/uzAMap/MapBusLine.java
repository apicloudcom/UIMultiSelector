//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import com.amap.api.maps.AMap;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineQuery.SearchType;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusLineSearch.OnBusLineSearchListener;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.SuggestionCity;
import com.apicloud.devlop.uzAMap.models.CustomBusLineOverLay;
import com.apicloud.devlop.uzAMap.utils.JsParamsUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MapBusLine implements OnBusLineSearchListener {
	private Context mContext;
	private UZModuleContext mModuleContext;
	private BusLineResult mBusLineResult;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, CustomBusLineOverLay> mBuslineMap = new HashMap<Integer, CustomBusLineOverLay>();

	public MapBusLine(Context context) {
		this.mContext = context;
	}

	public void searchBusLine(UZModuleContext moduleContext) {
		mModuleContext = moduleContext;
		String lineName = moduleContext.optString("line");
		String cityName = moduleContext.optString("city");
		int offset = moduleContext.optInt("offset", 20);
		int page = moduleContext.optInt("page", 1);
		page = page - 1 < 0 ? 0 : page - 1;
		BusLineQuery busLineQuery = new BusLineQuery(lineName,
				SearchType.BY_LINE_NAME, cityName);
		busLineQuery.setPageSize(offset);
		busLineQuery.setPageNumber(page);
		BusLineSearch busLineSearch = new BusLineSearch(mContext, busLineQuery);
		busLineSearch.setOnBusLineSearchListener(this);
		busLineSearch.searchBusLineAsyn();
	}

	public void drawBusLine(UZModuleContext moduleContext, AMap aMap) {
		int id = moduleContext.optInt("id");
		int index = moduleContext.optInt("index");
		List<BusLineItem> paths = mBusLineResult.getBusLines();
		if (paths != null) {
			if (index < paths.size()) {
				BusLineItem busPath = paths.get(index);
				if (busPath != null) {
					CustomBusLineOverLay busLineOverlay = getOverlay(
							moduleContext, aMap, busPath);
					busLineOverlay.removeFromMap();
					busLineOverlay.addToMap();
					mBuslineMap.put(id, busLineOverlay);
					if (moduleContext.optBoolean("autoresizing", true)) {
						busLineOverlay.zoomToSpan();
					}
				}
			}
		}
	}

	public void removeRoute(UZModuleContext moduleContext, AMap aMap) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		List<Integer> ids = jsParamsUtil.removeRouteIds(moduleContext);
		if (mBuslineMap == null)
			return;
		if (ids != null) {
			for (int id : ids) {
				CustomBusLineOverLay routeOverlay = mBuslineMap.get(id);
				if (routeOverlay != null) {
					routeOverlay.removeFromMap();
				}
			}
		}
	}

	private CustomBusLineOverLay getOverlay(UZModuleContext moduleContext,
			AMap aMap, BusLineItem busPath) {
		CustomBusLineOverLay busLineOverlay = new CustomBusLineOverLay(
				mContext, aMap, busPath);
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		busLineOverlay.setColor(jsParamsUtil.busColor(moduleContext));
		busLineOverlay.setLineWidth(jsParamsUtil.busWidth(moduleContext));
		busLineOverlay.setBusPointImgPath(jsParamsUtil.iconPath(moduleContext,
				"bus"));
		busLineOverlay.setStartPointImgPath(jsParamsUtil.iconPath(
				moduleContext, "start"));
		busLineOverlay.setEndPointImgPath(jsParamsUtil.iconPath(moduleContext,
				"end"));
		return busLineOverlay;
	}

	@Override
	public void onBusLineSearched(BusLineResult result, int rCode) {
		if (rCode == 1000) {
			if (result != null && result.getQuery() != null) {
				if (result.getQuery().getCategory() == SearchType.BY_LINE_NAME) {
					if (result.getPageCount() > 0) {
						mBusLineResult = result;
						routeCallBack(mModuleContext, busLineJson(result));
						return;
					}
				}
			}
		}
		failCallBack(mModuleContext);
	}

	@SuppressWarnings("deprecation")
	private JSONObject busLineJson(BusLineResult result) {
		JSONObject ret = new JSONObject();
		List<BusLineItem> busLines = result.getBusLines();
		try {
			if (busLines != null) {
				ret.put("status", true);
				JSONArray busLinesJson = new JSONArray();
				ret.put("buslines", busLinesJson);
				for (BusLineItem busLine : busLines) {
					JSONObject busLineJson = new JSONObject();
					busLinesJson.put(busLineJson);
					busLineJson.put("uid", busLine.getBusLineId());
					busLineJson.put("type", busLine.getBusLineType());
					busLineJson.put("name", busLine.getBusLineName());
					busLineJson.put("startStop",
							busLine.getOriginatingStation());
					busLineJson.put("endStop", busLine.getTerminalStation());
					Date startDate = busLine.getFirstBusTime();
					if (startDate != null)
						busLineJson.put("startTime", startDate.getHours() + ":"
								+ startDate.getMinutes());

					Date endDate = busLine.getLastBusTime();
					if (endDate != null)
						busLineJson.put("endTime", endDate.getHours() + ":"
								+ endDate.getMinutes());
					busLineJson.put("company", busLine.getBusCompany());
					busLineJson.put("distance", busLine.getDistance());
					busLineJson.put("basicPrice", busLine.getBasicPrice());
					busLineJson.put("totalPrice", busLine.getTotalPrice());
					List<BusStationItem> stations = busLine.getBusStations();
					if (stations != null) {
						JSONArray busStops = new JSONArray();
						busLineJson.put("busStops", busStops);
						for (BusStationItem station : stations) {
							JSONObject busStopJson = new JSONObject();
							busStops.put(busStopJson);
							busStopJson.put("uid", station.getBusStationId());
							busStopJson
									.put("name", station.getBusStationName());
							LatLonPoint latLonPoint = station.getLatLonPoint();
							if (latLonPoint != null) {
								busStopJson.put("lat",
										latLonPoint.getLatitude());
								busStopJson.put("lon",
										latLonPoint.getLongitude());
							}
						}
					}
				}
			}
			JSONObject suggestion = new JSONObject();
			ret.put("suggestion", suggestion);
			List<SuggestionCity> sugestionCity = result
					.getSearchSuggestionCities();
			if (sugestionCity != null) {
				JSONArray cityNames = new JSONArray();
				for (SuggestionCity city : sugestionCity) {
					cityNames.put(city.getCityName());
				}
				suggestion.put("cities", cityNames);
			}
			List<String> keywords = result.getSearchSuggestionKeywords();
			if (keywords != null) {
				JSONArray keywordsJson = new JSONArray();
				for (String keyword : keywords) {
					keywordsJson.put(keyword);
				}
				suggestion.put("keywords", keywordsJson);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
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
}
