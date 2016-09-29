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
import android.content.Context;
import android.util.Log;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MapOffline implements OfflineMapDownloadListener {
	private boolean mIsDownload;
	private OfflineMapManager mAMapManager;
	private Map<String, UZModuleContext> mModuleContextMap = new HashMap<String, UZModuleContext>();

	public void getProvinces(UZModuleContext moduleContext, Context context) {
		OfflineMapManager aMapManager = new OfflineMapManager(context, this);
		ArrayList<OfflineMapProvince> provinces = aMapManager
				.getOfflineMapProvinceList();
		try {
			getProvincesCallBack(moduleContext, provinces);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void getAllCities(UZModuleContext moduleContext, Context context) {
		OfflineMapManager aMapManager = new OfflineMapManager(context, this);
		ArrayList<OfflineMapCity> cities = aMapManager.getOfflineMapCityList();
		try {
			getCitiesCallBack(moduleContext, cities);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void downloadRegion(UZModuleContext moduleContext, Context context) {
		if (mAMapManager == null) {
			mAMapManager = new OfflineMapManager(context, this);
		}
		mAMapManager.restart();
		String adcode = moduleContext.optString("adcode");
		if (adcode != null) {
			mModuleContextMap.put(adcode, moduleContext);
			ArrayList<OfflineMapCity> cities = mAMapManager
					.getOfflineMapCityList();
			if (cities != null) {
				for (OfflineMapCity city : cities) {
					if (adcode.equals(city.getCode())) {
						try {
							mAMapManager.downloadByCityCode(adcode);
							mIsDownload = true;
						} catch (AMapException e) {
							e.printStackTrace();
						}
					}
				}
			}

			ArrayList<OfflineMapProvince> provinces = mAMapManager
					.getOfflineMapProvinceList();
			for (OfflineMapProvince province : provinces) {
				if (adcode.equals(province.getProvinceCode())) {
					try {
						mAMapManager.downloadByProvinceName(province
								.getProvinceName());
						mIsDownload = true;
					} catch (AMapException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void cancelAllDownload() {
		if (mAMapManager != null) {
			mAMapManager.stop();
		}
	}

	public void cancelAllDownload(UZModuleContext moduleContext, Context context) {
		if (mAMapManager != null) {
			mAMapManager.stop();
			mIsDownload = false;
		}
	}

	public void pauseDownload(UZModuleContext moduleContext, Context context) {
		if (mAMapManager != null) {
			mAMapManager.pause();
			mIsDownload = false;
		}
	}

	public void clearDisk() {
		if (mAMapManager != null) {
			List<OfflineMapCity> cities = mAMapManager
					.getDownloadOfflineMapCityList();
			if (cities != null) {
				for (OfflineMapCity city : cities) {
					mAMapManager.remove(city.getCity());
				}
			}
			ArrayList<OfflineMapProvince> provinces = mAMapManager
					.getDownloadOfflineMapProvinceList();
			if (provinces != null) {
				for (OfflineMapProvince province : provinces) {
					mAMapManager.remove(province.getProvinceName());
				}
			}
		}
	}

	public void isDownloading(UZModuleContext moduleContext, Context context) {
		try {
			isDownloadingCallBack(moduleContext, mIsDownload);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// if (mAMapManager != null) {
		// String adcode = moduleContext.optString("adcode");
		// if (adcode != null) {
		// ArrayList<OfflineMapCity> cities = mAMapManager
		// .getDownloadingCityList();
		// boolean isDownloading = false;
		// if (cities != null) {
		// for (OfflineMapCity city : cities) {
		// if (adcode.equals(city.getAdcode())) {
		// try {
		// isDownloadingCallBack(moduleContext, true);
		// isDownloading = true;
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		// ArrayList<OfflineMapProvince> provinces = mAMapManager
		// .getDownloadingProvinceList();
		// if (provinces != null) {
		// for (OfflineMapProvince province : provinces) {
		// if (adcode.equals(province.getProvinceCode())) {
		// try {
		// isDownloadingCallBack(moduleContext, true);
		// isDownloading = true;
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		// if (!isDownloading) {
		// try {
		// isDownloadingCallBack(moduleContext, false);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// } else {
		// try {
		// isDownloadingCallBack(moduleContext, false);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	public void onDownload(int status, int completeCode, String name) {
		OfflineMapCity city = mAMapManager.getItemByCityName(name);
		OfflineMapProvince province = mAMapManager.getItemByProvinceName(name);
		if (city != null) {
			System.out.println(status);
			long expectedSize = city.getSize();
			long receivedSize = expectedSize * completeCode / 100;
			UZModuleContext moduleContext = mModuleContextMap.get(city
					.getCode());
			if (moduleContext != null) {
				downloadCallBack(moduleContext, status, expectedSize,
						receivedSize);
			}
		} else if (province != null) {
			long expectedSize = province.getSize();
			long receivedSize = expectedSize * completeCode / 100;
			UZModuleContext moduleContext = mModuleContextMap.get(province
					.getProvinceCode());
			if (moduleContext != null) {
				downloadCallBack(moduleContext, status, expectedSize,
						receivedSize);
			}
		}
	}

	private void downloadCallBack(UZModuleContext moduleContext, int status,
			long expectedSize, long receivedSize) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", tranStatus(status));
			JSONObject info = new JSONObject();
			info.put("expectedSize", expectedSize);
			info.put("receivedSize", receivedSize);
			ret.put("info", info);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private int tranStatus(int status) {
		switch (status) {
		case -1:
			return 7;
		case 0:
			return 2;
		case 1:
			return 5;
		case 2:
			return 2;
		case 3:
			return 4;
		case 4:
			return 6;
		case 5:
			return 4;
		}
		return status;
	}

	private void getProvincesCallBack(UZModuleContext moduleContext,
			ArrayList<OfflineMapProvince> provinces) throws JSONException {
		JSONObject ret = new JSONObject();
		if (provinces != null) {
			ret.put("status", true);
			JSONArray provincesJson = new JSONArray();
			ret.put("provinces", provincesJson);
			for (OfflineMapProvince province : provinces) {
				JSONObject provinceJson = new JSONObject();
				provincesJson.put(provinceJson);
				provinceJson.put("name", province.getProvinceName());
				provinceJson.put("adcode", province.getProvinceCode());
				provinceJson.put("jianpin", province.getJianpin());
				provinceJson.put("pinyin", province.getPinyin());
				provinceJson.put("size", province.getSize());
				provinceJson.put("status", province.getState());
			}
		} else {
			ret.put("status", false);
		}
		moduleContext.success(ret, false);
	}

	private void getCitiesCallBack(UZModuleContext moduleContext,
			ArrayList<OfflineMapCity> cities) throws JSONException {
		JSONObject ret = new JSONObject();
		if (cities != null) {
			ret.put("status", true);
			JSONArray citiesJson = new JSONArray();
			ret.put("cities", citiesJson);
			for (OfflineMapCity city : cities) {
				JSONObject cityJson = new JSONObject();
				citiesJson.put(cityJson);
				cityJson.put("name", city.getCity());
				cityJson.put("adcode", city.getAdcode());
				cityJson.put("cityCode", city.getCode());
				cityJson.put("jianpin", city.getJianpin());
				cityJson.put("pinyin", city.getPinyin());
				cityJson.put("size", city.getSize());
				cityJson.put("downloadSize", city.getcompleteCode());
				cityJson.put("status", city.getState());
			}
		} else {
			ret.put("status", false);
		}
		moduleContext.success(ret, false);
	}

	private void isDownloadingCallBack(UZModuleContext moduleContext,
			boolean status) throws JSONException {
		JSONObject ret = new JSONObject();
		ret.put("status", status);
		moduleContext.success(ret, false);
	}

	@Override
	public void onCheckUpdate(boolean arg0, String arg1) {
		Log.i("amap-demo", "onCheckUpdate " + arg1 + " : " + arg0);
	}

	@Override
	public void onRemove(boolean arg0, String arg1, String arg2) {

	}
}
