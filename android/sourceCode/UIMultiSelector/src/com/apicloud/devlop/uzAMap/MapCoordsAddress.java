//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MapCoordsAddress implements OnGeocodeSearchListener {

	private UZModuleContext mModuleContext;

	public void getLocationFromName(UZModuleContext moduleContext,
			Context context) {
		mModuleContext = moduleContext;
		GeocodeSearch geocoderSearch = new GeocodeSearch(context);
		if (!moduleContext.isNull("city") && !moduleContext.isNull("address")) {
			String city = moduleContext.optString("city");
			String address = moduleContext.optString("address");
			GeocodeQuery query = new GeocodeQuery(address, city);
			geocoderSearch.setOnGeocodeSearchListener(this);
			geocoderSearch.getFromLocationNameAsyn(query);
		} else {
			geocoderErrBack(moduleContext, 4);
		}
	}

	public void getNameFromLocation(UZModuleContext moduleContext,
			Context context) {
		mModuleContext = moduleContext;
		GeocodeSearch geocoderSearch = new GeocodeSearch(context);
		if (!moduleContext.isNull("lon") && !moduleContext.isNull("lat")) {
			double lon = moduleContext.optDouble("lon");
			double lat = moduleContext.optDouble("lat");
			RegeocodeQuery query = new RegeocodeQuery(
					new LatLonPoint(lat, lon), 200, GeocodeSearch.AMAP);
			geocoderSearch.setOnGeocodeSearchListener(this);
			geocoderSearch.getFromLocationAsyn(query);
		} else {
			geocoderErrBack(moduleContext, 4);
		}
	}

	private void geocoderErrBack(UZModuleContext moduleContext, int code) {
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			ret.put("status", false);
			err.put("code", code);
			moduleContext.error(ret, err, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int code) {
		if (code == 1000) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				geocodeSearchedBack(address, true, 0);
			} else {
				geocodeSearchedBack(null, false, -1);
			}
		} else {
			geocodeSearchedBack(null, false, code);
		}
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int code) {
		if (code == 1000) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				regeocodeSearchedBack(result, true, code);
			} else {
				regeocodeSearchedBack(null, false, -1);
			}
		} else {
			regeocodeSearchedBack(null, false, code);
		}
	}

	private void geocodeSearchedBack(GeocodeAddress address, boolean status,
			int code) {
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		if (status) {
			try {
				ret.put("status", true);
				ret.put("lon", address.getLatLonPoint().getLongitude());
				ret.put("lat", address.getLatLonPoint().getLatitude());
				mModuleContext.success(ret, false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				ret.put("status", false);
				err.put("code", code);
				mModuleContext.error(ret, err, false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void regeocodeSearchedBack(RegeocodeResult result, boolean status,
			int code) {
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		if (status) {
			RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
			try {
				ret.put("status", true);
				ret.put("address", regeocodeAddress.getFormatAddress());
				ret.put("city", regeocodeAddress.getCity());
				ret.put("state", regeocodeAddress.getProvince());
				ret.put("district", regeocodeAddress.getDistrict());
				ret.put("street", regeocodeAddress.getStreetNumber()
						.getStreet());
				ret.put("number", regeocodeAddress.getStreetNumber()
						.getNumber());
				List<RegeocodeRoad> roadList = regeocodeAddress.getRoads();
				if (roadList != null && roadList.size() > 0) {
					ret.put("thoroughfare", roadList.get(0).getName());
				}
				ret.put("township", regeocodeAddress.getTownship());
				mModuleContext.success(ret, false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				ret.put("status", false);
				err.put("code", code);
				mModuleContext.error(ret, err, false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
