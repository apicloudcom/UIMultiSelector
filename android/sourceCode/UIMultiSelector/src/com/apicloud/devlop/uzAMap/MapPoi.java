//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.apicloud.devlop.uzAMap.utils.JsParamsUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MapPoi implements OnPoiSearchListener {
	private UZModuleContext mModuleContext;
	private Context mContext;

	public MapPoi(UZModuleContext moduleContext, Context context) {
		this.mModuleContext = moduleContext;
		this.mContext = context;
	}

	public void searchInCity(UZModuleContext moduleContext) {
		mModuleContext = moduleContext;
		String city = moduleContext.optString("city");
		String keyword = moduleContext.optString("keyword");
		int offset = moduleContext.optInt("offset", 20);
		int page = moduleContext.optInt("page", 1);
		page = page - 1 < 0 ? 0 : page - 1;
		Query query = new Query(keyword, "", city);
		query.setPageSize(offset);
		query.setPageNum(page);
		PoiSearch poiSearch = new PoiSearch(mContext, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	public void searchNearby(UZModuleContext moduleContext) {
		mModuleContext = moduleContext;
		String keyWord = moduleContext.optString("keyword", "");
		double lon = moduleContext.optDouble("lon");
		double lat = moduleContext.optDouble("lat");
		int radius = moduleContext.optInt("radius", 3000);
		int page = moduleContext.optInt("page", 1);
		page = page - 1 < 0 ? 0 : page - 1;
		int pageSize = moduleContext.optInt("offset", 20);
		SearchBound searchBound = new SearchBound(new LatLonPoint(lat, lon),
				radius);
		Query query = new Query(keyWord, "", "");
		query.setPageSize(pageSize);
		query.setPageNum(page);
		PoiSearch poiSearch = new PoiSearch(mContext, query);
		poiSearch.setBound(searchBound);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	public void searchBounds(UZModuleContext moduleContext) {
		mModuleContext = moduleContext;
		String keyWord = moduleContext.optString("keyword", "");
		int page = moduleContext.optInt("page", 1);
		page = page - 1 < 0 ? 0 : page - 1;
		int pageSize = moduleContext.optInt("offset", 20);
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		SearchBound searchBound = new SearchBound(
				jsParamsUtil.boundsPoints(moduleContext));
		Query query = new Query(keyWord, "", "");
		query.setPageSize(pageSize);
		query.setPageNum(page);
		PoiSearch poiSearch = new PoiSearch(mContext, query);
		poiSearch.setBound(searchBound);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	public void autoComplete(final UZModuleContext moduleContext) {
		String city = moduleContext.optString("city", "");
		String keyword = moduleContext.optString("keyword");
		Inputtips inputTips = new Inputtips(mContext, new InputtipsListener() {

			@Override
			public void onGetInputtips(List<Tip> tipList, int rCode) {
				if (rCode == 1000) {
					moduleContext.success(autoCompleteJson(tipList), false);
				} else {
					failCallBack(moduleContext);
				}
			}
		});
		try {
			inputTips.requestInputtips(keyword, city);
		} catch (AMapException e) {
			e.printStackTrace();
			failCallBack(moduleContext);
		}
	}

	private JSONObject autoCompleteJson(List<Tip> tipList) {
		if (tipList == null) {
			return null;
		}
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			JSONArray tips = new JSONArray();
			ret.put("tips", tips);
			Tip tip = null;
			for (int i = 0; i < tipList.size(); i++) {
				JSONObject tipJson = new JSONObject();
				tips.put(tipJson);
				tip = tipList.get(i);
				tipJson.put("name", tip.getName());
				tipJson.put("adcode", tip.getAdcode());
				tipJson.put("district", tip.getDistrict());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		if (rCode == 1000) {
			if (result != null && result.getQuery() != null) {
				callBack(mModuleContext, callBackJson(result));
				return;
			}
		}
		failCallBack(mModuleContext);
	}

	private JSONObject callBackJson(PoiResult result) {
		JSONObject ret = new JSONObject();
		ArrayList<PoiItem> poiItems = result.getPois();
		try {
			if (poiItems != null) {
				ret.put("status", true);
				JSONArray pois = new JSONArray();
				ret.put("pois", pois);
				for (PoiItem poiItem : poiItems) {
					JSONObject poi = new JSONObject();
					pois.put(poi);
					poi.put("uid", poiItem.getPoiId());
					poi.put("name", poiItem.getTitle());
					poi.put("type", poiItem.getPoiId());
					poi.put("address", poiItem.getSnippet());
					poi.put("tel", poiItem.getTel());
					poi.put("distance", poiItem.getDistance());
					LatLonPoint latLonPoint = poiItem.getLatLonPoint();
					if (latLonPoint != null) {
						poi.put("lat", latLonPoint.getLatitude());
						poi.put("lon", latLonPoint.getLongitude());
					}
				}
			}

			JSONObject suggestion = new JSONObject();
			ret.put("suggestion", suggestion);
			List<SuggestionCity> sugestionCity = result
					.getSearchSuggestionCitys();
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

	private void callBack(UZModuleContext moduleContext, JSONObject ret) {
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

	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {

	}
}
