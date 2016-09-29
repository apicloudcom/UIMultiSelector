//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MapShowUser implements AMapLocationListener {
	private Context mContext;
	private Marker mLocMarker;
	private AMapLocationClient mLocationClient;
	private AMapLocationClientOption mLocationOption;

	public void showUserLocation(AMap aMap, UZModuleContext moduleContext,
			Context context) {
		mContext = context;
		boolean isShow = moduleContext.optBoolean("isShow", true);
		if (aMap != null) {
			init();
			if (mLocMarker != null) {
				mLocMarker.remove();
			}
			if (isShow) {
				showUserLocation(aMap, context);
			}
		}
	}

	public void setTrackingMode(AMap aMap, UZModuleContext moduleContext) {
		if (aMap != null) {
			String trackingMode = moduleContext.optString("trackingMode",
					Constans.LOCATION_TYPE_NONE);
			if (trackingMode.equalsIgnoreCase(Constans.LOCATION_TYPE_FOLLOW)) {
				aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
			} else if (trackingMode
					.equalsIgnoreCase(Constans.LOCATION_TYPE_COMPASS)) {
				aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
			} else {
				aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
			}
		}
	}

	public void showUserLocationOpen(MapOpen mMap, Context context) {
		mContext = context;
		if (mMap != null) {
			AMap aMap = mMap.getMapView().getMap();
			init();
			addLocMarker(aMap);
			aMap.setMyLocationStyle(createLocationStyle(context));
		}
	}
	
	private void init() {
		mLocationClient = new AMapLocationClient(mContext);
		mLocationOption = new AMapLocationClientOption();
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		mLocationClient.setLocationListener(this);
		mLocationClient.startLocation();
	}

	public void showUserLocation(AMap aMap, Context context) {
		mContext = context;
		if (aMap != null) {
			init();
			addLocMarker(aMap);
			aMap.setMyLocationStyle(createLocationStyle(context));
		}
	}

	private void addLocMarker(AMap aMap) {
		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(BitmapDescriptorFactory.fromResource(UZResourcesIDFinder
				.getResDrawableID("mo_amap_point1")));
		giflist.add(BitmapDescriptorFactory.fromResource(UZResourcesIDFinder
				.getResDrawableID("mo_amap_point2")));
		giflist.add(BitmapDescriptorFactory.fromResource(UZResourcesIDFinder
				.getResDrawableID("mo_amap_point3")));
		giflist.add(BitmapDescriptorFactory.fromResource(UZResourcesIDFinder
				.getResDrawableID("mo_amap_point4")));
		giflist.add(BitmapDescriptorFactory.fromResource(UZResourcesIDFinder
				.getResDrawableID("mo_amap_point5")));
		giflist.add(BitmapDescriptorFactory.fromResource(UZResourcesIDFinder
				.getResDrawableID("mo_amap_point6")));
		mLocMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.icons(giflist).period(50));
	}

	private MyLocationStyle createLocationStyle(Context context) {
		BitmapDescriptor bitmapDescriptor = null;
		int pointId = -1;
		pointId = UZResourcesIDFinder.getResDrawableID("mo_amap_loc_icon");
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				pointId);
		bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(bitmapDescriptor);
		return myLocationStyle;
	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (aLocation != null) {
			if (mLocMarker != null) {
				mLocMarker.setPosition(new LatLng(aLocation.getLatitude(),
						aLocation.getLongitude()));
			}
		}
	}

}
