//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.apicloud.devlop.uzAMap.models.MoveOverlay;

public class MapAnimationOverlay {

	private static final int TIME_INTERVAL = 100;
	private Timer mTimer;
	private TimerTask mTask;
	private List<MoveOverlay> mMoveOverlayList;

	private boolean mIsStop;

	public void startMove() {
		mIsStop = false;
		if (mTimer == null) {
			mTimer = new Timer();
			mTask = new TimerTask() {
				@Override
				public void run() {
					moveMarker();
				}
			};
			mTimer.schedule(mTask, 0, TIME_INTERVAL);
		}
	}

	public void stop() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}
		mIsStop = true;
	}

	public void addMoveOverlay(MoveOverlay moveOverlay) {
		if (mMoveOverlayList == null) {
			mMoveOverlayList = new ArrayList<MoveOverlay>();
		}
		Marker marker = moveOverlay.getMaker();
		float currAngle = marker.getRotateAngle();
		float nextAngle = (float) getAngle(marker.getPosition(),
				moveOverlay.getEndPoint());
		if (nextAngle < 0) {
			nextAngle = 360 + nextAngle;
		}
		float unitAngle = (nextAngle - currAngle) / 3;
		moveOverlay.setUnitAngle(unitAngle);
		mMoveOverlayList.add(moveOverlay);
	}

	public synchronized void moveMarker() {
		if (mMoveOverlayList != null) {
			for (int i = mMoveOverlayList.size() - 1; i >= 0; i--) {
				MoveOverlay moveOverlay = mMoveOverlayList.get(i);
				if (moveOverlay != null) {
					Marker marker = moveOverlay.getMaker();
					if (marker != null) {
						if (moveOverlay.getCurrRotateTime() < 300) {
							rotate(moveOverlay);
						} else {
							move(moveOverlay);
						}
					}
				}
			}
		}
	}

	private void rotate(MoveOverlay moveOverlay) {
		Marker marker = moveOverlay.getMaker();
		if (marker != null && !mIsStop) {
			marker.setRotateAngle(marker.getRotateAngle()
					+ moveOverlay.getUnitAngle());
		}
		moveOverlay.setCurrRotateTime(moveOverlay.getCurrRotateTime()
				+ TIME_INTERVAL);
	}

	private void move(MoveOverlay moveOverlay) {
		Marker marker = moveOverlay.getMaker();
		if (moveOverlay.getCurrTime() < moveOverlay.getDuration() * 1000) {
			LatLng latLng = null;
			double time = moveOverlay.getDuration() * 10;
			double distanceX = moveOverlay.getEndPoint().longitude
					- moveOverlay.getStartPoint().longitude;
			double unitDistanceX = distanceX / time;

			double distanceY = moveOverlay.getEndPoint().latitude
					- moveOverlay.getStartPoint().latitude;
			double unitDistanceY = distanceY / time;

			latLng = new LatLng(marker.getPosition().latitude + unitDistanceY,
					marker.getPosition().longitude + unitDistanceX);
			if (marker != null && !mIsStop)
				marker.setPosition(latLng);
			moveOverlay.setCurrTime(moveOverlay.getCurrTime() + 100);
		} else {
			JSONObject ret = new JSONObject();
			try {
				ret.put("status", true);
				ret.put("id", moveOverlay.getId());
				moveOverlay.getModuleContext().success(ret, false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mMoveOverlayList.remove(moveOverlay);
		}
	}

	private double getAngle(LatLng fromPoint, LatLng toPoint) {
		double slope = getSlope(fromPoint, toPoint);
		if (slope == Double.MAX_VALUE) {
			if (toPoint.latitude > fromPoint.latitude) {
				return 0;
			} else {
				return 180;
			}
		}
		float deltAngle = 0;
		if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
			deltAngle = 180;
		}
		double radio = Math.atan(slope);
		double angle = 180 * (radio / Math.PI) + deltAngle - 90;
		return angle;
	}

	private double getSlope(LatLng fromPoint, LatLng toPoint) {
		if (toPoint.longitude == fromPoint.longitude) {
			return Double.MAX_VALUE;
		}
		double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
		return slope;

	}
}
