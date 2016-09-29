//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.apicloud.devlop.uzAMap.utils.CallBackUtil;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MapLocation implements AMapLocationListener, SensorEventListener {
	private Context mContext;
	private UZModuleContext mModuleContext;
	private int mAccuracy;
	private float mMinDistance;
	private boolean mAutoStop;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private AMapLocationClient mLocationClient;
	private AMapLocationClientOption mLocationOption;

	public void getLocation(UZModuleContext moduleContext, Context context) {
		mContext = context;
		initSensor();
		UzMapView mMapView = new UzMapView(mContext);
		mMapView.onCreate(null);
		mModuleContext = moduleContext;
		mAccuracy = moduleContext.optInt("accuracy", 10);
		mMinDistance = (float) moduleContext.optDouble("filter", 1.0);
		mAutoStop = moduleContext.optBoolean("autoStop", true);
		init();
	}

	public void stopLocation() {
		if (mLocationClient != null) {
			mLocationClient.stopLocation();
		}
	}

	@SuppressWarnings("deprecation")
	private void initSensor() {
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void init() {
		mLocationClient = new AMapLocationClient(mContext);
		mLocationOption = new AMapLocationClientOption();
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		mLocationClient.setLocationListener(this);
		mLocationClient.startLocation();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ORIENTATION: {
			float x = event.values[0];
			x += getScreenRotationOnPhone(mContext);
			x %= 360.0F;
			if (x > 180.0F)
				x -= 360.0F;
			else if (x < -180.0F)
				x += 360.0F;
			mAngle = x;
			if (mAngle < 0) {
				mAngle = 360 + mAngle;
			}
		}
		}
	}

	private float mAngle;

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public static int getScreenRotationOnPhone(Context context) {
		final Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			return 0;

		case Surface.ROTATION_90:
			return 90;

		case Surface.ROTATION_180:
			return 180;

		case Surface.ROTATION_270:
			return -90;
		}
		return 0;
	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		boolean status = false;
		if (aLocation != null) {
			aLocation.setAccuracy(mAccuracy);
			status = true;
		}
		CallBackUtil
				.locationCallBack(mModuleContext, aLocation, mAngle, status);
		if (mAutoStop) {
			stopLocation();
		}
	}
}
