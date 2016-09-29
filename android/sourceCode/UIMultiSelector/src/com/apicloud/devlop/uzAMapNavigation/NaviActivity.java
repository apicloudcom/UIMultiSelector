package com.apicloud.devlop.uzAMapNavigation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class NaviActivity extends Activity implements AMapNaviListener,
		AMapNaviViewListener {
	private UZModuleContext mModuleContext;
	private AMapNaviView mNaviView;
	private AMapNavi mAMapNavi;
	private TTSController mTTSController;
	private NaviLatLng mStartLatlng;
	private NaviLatLng mEndLatlng;
	private List<NaviLatLng> mStartList = new ArrayList<NaviLatLng>();
	private List<NaviLatLng> mEndList = new ArrayList<NaviLatLng>();
	private List<NaviLatLng> mWayPointList = new ArrayList<NaviLatLng>();
	private AMapNaviViewOptions mViewOptions = new AMapNaviViewOptions();
	private Bitmap mStartImg;
	private Bitmap mEndImg;
	private Bitmap mWayImg;
	private Bitmap mCameraImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutID());
		UzAMapNavigation.NAVI_ACTIVITY = this;
		mModuleContext = UzAMapNavigation.mNaviModuleContext;
		initTTEScontroller();
		initImgs();
		initPreference();
		initNaviView(savedInstanceState);
		initNavi();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mNaviView.onResume();
		initParams();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mNaviView.onPause();
		mTTSController.stopSpeaking();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mNaviView.onDestroy();
		mAMapNavi.destroy();
		mTTSController.destroy();
		callBack("naviClose");
	}

	private void initParams() {
		JSONObject start = mModuleContext.optJSONObject("start");
		if (start != null) {
			double startLat = start.optDouble("lat");
			double startLon = start.optDouble("lon");
			mStartLatlng = new NaviLatLng(startLat, startLon);
			mStartList.add(mStartLatlng);
		}

		JSONObject end = mModuleContext.optJSONObject("end");
		if (end != null) {
			double endLat = end.optDouble("lat");
			double endLon = end.optDouble("lon");
			mEndLatlng = new NaviLatLng(endLat, endLon);
			mEndList.add(mEndLatlng);
		}

		JSONArray wayPoint = mModuleContext.optJSONArray("wayPoint");
		if (wayPoint != null && wayPoint.length() > 0) {
			JSONObject point = null;
			for (int i = 0; i < wayPoint.length(); i++) {
				point = wayPoint.optJSONObject(i);
				mWayPointList.add(new NaviLatLng(point.optDouble("lat"), point
						.optDouble("lon")));
			}
		}
	}

	private void initNavi() {
		mAMapNavi = AMapNavi.getInstance(getApplicationContext());
		mAMapNavi.setAMapNaviListener(this);
		mAMapNavi.setAMapNaviListener(mTTSController);
		mAMapNavi.setEmulatorNaviSpeed(120);
	}

	private void initNaviView(Bundle savedInstanceState) {
		mNaviView = (AMapNaviView) findViewById(getNaviViewID());
		mViewOptions.setAutoDrawRoute(false);
		mNaviView.setViewOptions(mViewOptions);
		mNaviView.onCreate(savedInstanceState);
		mNaviView.setAMapNaviViewListener(this);
	}

	private int getLayoutID() {
		return UZResourcesIDFinder.getResLayoutID("mo_gaode_navi");
	}

	private int getNaviViewID() {
		return UZResourcesIDFinder.getResIdID("navi_view");
	}

	private void initTTEScontroller() {
		mTTSController = TTSController.getInstance(getApplicationContext());
		mTTSController.init();
		mTTSController.startSpeaking();
	}

	@Override
	public void onInitNaviFailure() {
		failCallBack(-1, "naviFai");
	}

	private void callBack(String msg) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("eventType", msg);
			if (msg.equals("calculateSuc")) {
				AMapNaviPath naviPath = mAMapNavi.getNaviPath();
				JSONObject routeInfo = new JSONObject();
				routeInfo.put("length", naviPath.getAllLength());
				routeInfo.put("time", naviPath.getAllTime());
				routeInfo.put("tollCost", naviPath.getTollCost());
				routeInfo.put("segmentCount", naviPath.getStepsCount());
				ret.put("routeInfo", routeInfo);
			}
			mModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void failCallBack(int errCode, String msg) {
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			ret.put("eventType", msg);
			err.put("code", errCode);
			mModuleContext.error(ret, err, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initImgs() {
		JSONObject styles = mModuleContext.optJSONObject("styles");
		if (styles != null) {
			JSONObject imageJson = styles.optJSONObject("image");
			if (imageJson != null) {
				String start = imageJson.optString("start");
				String startPath = UzAMapNavigation.mModule.makeRealPath(start);
				mStartImg = getBitmap(startPath);
				String end = imageJson.optString("end");
				String endPath = UzAMapNavigation.mModule.makeRealPath(end);
				mEndImg = getBitmap(endPath);
				String way = imageJson.optString("way");
				String wayPath = UzAMapNavigation.mModule.makeRealPath(way);
				mWayImg = getBitmap(wayPath);
				String camera = imageJson.optString("camera");
				String cameraPath = UzAMapNavigation.mModule
						.makeRealPath(camera);
				mCameraImg = getBitmap(cameraPath);
				if (mCameraImg != null)
					mViewOptions.setMonitorCameraBitmap(mCameraImg);
			}
		}
	}

	private void initPreference() {
		JSONObject styles = mModuleContext.optJSONObject("styles");
		boolean night = false;
		boolean compass = false;
		boolean crossImg = false;
		int degree = 30;
		boolean yawReCal = false;
		boolean jamReCal = false;
		boolean alwaysBright = false;
		if (styles != null) {
			JSONObject preference = styles.optJSONObject("preference");
			if (preference != null) {
				night = preference.optBoolean("night", false);
				compass = preference.optBoolean("compass", false);
				crossImg = preference.optBoolean("crossImg", false);
				degree = preference.optInt("degree", 30);
				yawReCal = preference.optBoolean("yawReCal", false);
				jamReCal = preference.optBoolean("jamReCal", false);
				alwaysBright = preference.optBoolean("alwaysBright", false);
			}
		}
		mViewOptions.setNaviNight(night);
		mViewOptions.setCompassEnabled(compass);
		mViewOptions.setCrossDisplayShow(crossImg);
		mViewOptions.setTilt(degree);
		mViewOptions.setReCalculateRouteForYaw(yawReCal);
		mViewOptions.setReCalculateRouteForTrafficJam(jamReCal);
		mViewOptions.setScreenAlwaysBright(alwaysBright);
	}

	private Bitmap getBitmap(String path) {
		Bitmap bitmap = null;
		InputStream input = null;
		try {
			input = UZUtility.guessInputStream(path);
			bitmap = BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	@Override
	public void onInitNaviSuccess() {
		String calculateMode = mModuleContext.optString("type", "drive");
		boolean status = false;
		if (calculateMode.equals("drive")) {
			status = mAMapNavi.calculateDriveRoute(mStartList, mEndList,
					mWayPointList, getStrategy());
		} else {
			status = mAMapNavi.calculateWalkRoute(mStartLatlng, mEndLatlng);
		}
		if (!status) {
			failCallBack(-1, "naviFai");
		}
	}

	private int getStrategy() {
		String strategyStr = mModuleContext.optString("strategy", "fast");
		if (strategyStr.equals("fast")) {
			return 0;
		} else if (strategyStr.equals("fee")) {
			return 1;
		} else if (strategyStr.equals("distance")) {
			return 2;
		} else if (strategyStr.equals("highway")) {
			return 3;
		} else if (strategyStr.equals("jam")) {
			return 4;
		} else if (strategyStr.equals("feeJam")) {
			return 12;
		} else if (strategyStr.equals("multipleRoutes")) {
			return 13;
		}
		return 0;
	}

	@Override
	public void onCalculateRouteSuccess() {
		callBack("calculateSuc");
		customPointImg();
		String mode = mModuleContext.optString("mode", "GPS");
		boolean status = false;
		if (mode.equals("GPS")) {
			status = mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
		} else {
			status = mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);
		}
		if (!status) {
			failCallBack(-1, "calculateFai");
		}
	}

	private void customPointImg() {
		RouteOverLay routeOverlay = new RouteOverLay(mNaviView.getMap(),
				mAMapNavi.getNaviPath(), this);
		if (mStartImg != null)
			routeOverlay.setStartPointBitmap(mStartImg);
		if (mEndImg != null)
			routeOverlay.setEndPointBitmap(mEndImg);
		if (mWayImg != null)
			routeOverlay.setWayPointBitmap(mWayImg);
		routeOverlay.setWidth(20);
		int length = mWayPointList.size() + 1;
		int color[] = new int[length];
		for (int i = 0; i < length; i++) {
			color[i] = UZUtility.parseCssColor("#0066FF");
		}
		routeOverlay
				.addToMap(color, mAMapNavi.getNaviPath().getWayPointIndex());
	}

	@Override
	public void onCalculateRouteFailure(int errCode) {
		failCallBack(errCode, "calculateFai");
	}

	@Override
	public void onLockMap(boolean arg0) {

	}

	@Override
	public boolean onNaviBackClick() {
		return false;
	}

	@Override
	public void onNaviCancel() {
		callBack("naviClose");
		finish();
	}

	@Override
	public void onNaviMapMode(int arg0) {

	}

	@Override
	public void onNaviSetting() {

	}

	@Override
	public void onNaviTurnClick() {

	}

	@Override
	public void onNextRoadClick() {

	}

	@Override
	public void onScanViewButtonClick() {

	}

	@Override
	public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {

	}

	@Override
	public void hideCross() {

	}

	@Override
	public void hideLaneInfo() {

	}

	@Override
	public void onArriveDestination() {
		callBack("naviEnd");
	}

	@Override
	public void onArrivedWayPoint(int arg0) {

	}

	@Override
	public void onEndEmulatorNavi() {
		callBack("naviEnd");
	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {

	}

	@Override
	@Deprecated
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {

	}

	@Override
	public void onReCalculateRouteForYaw() {

	}

	@Override
	public void onStartNavi(int arg0) {
		callBack("naviStart");
	}

	@Override
	public void onTrafficStatusUpdate() {

	}

	@Override
	public void showCross(AMapNaviCross arg0) {

	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {

	}

}
