//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap.models;

import java.io.IOException;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.uzmap.pkg.uzkit.UZUtility;

public class CustomDriveRoute extends DrivingRouteOverlay {
	private int busColor;
	private int driveColor;
	private int walkColor;
	private float lineWidth;
	private String startPointImgPath;
	private String endPointImgPath;
	private String busPointImgPath;
	private String walkPointImgPath;
	private String drivePointImgPath;

	public CustomDriveRoute(Context context, AMap aMap, DrivePath drivePath,
			LatLonPoint start, LatLonPoint end) {
		super(context, aMap, drivePath, start, end);
	}

	public void setBusColor(int color) {
		this.busColor = color;
	}

	public void setDriveColor(int driveColor) {
		this.driveColor = driveColor;
	}

	public void setWalkColor(int walkColor) {
		this.walkColor = walkColor;
	}

	public void setBusPointImgPath(String busPointImgPath) {
		this.busPointImgPath = busPointImgPath;
	}

	public void setWalkPointImgPath(String walkPointImgPath) {
		this.walkPointImgPath = walkPointImgPath;
	}

	public void setDrivePointImgPath(String drivePointImgPath) {
		this.drivePointImgPath = drivePointImgPath;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	@Override
	protected float getRouteWidth() {
		return lineWidth;
	}

	@Override
	protected int getBusColor() {
		return busColor;
	}

	@Override
	protected int getDriveColor() {
		return driveColor;
	}

	@Override
	protected int getWalkColor() {
		return walkColor;
	}

	public void setStartPointImgPath(String startPointImgPath) {
		this.startPointImgPath = startPointImgPath;
	}

	public void setEndPointImgPath(String endPointImgPath) {
		this.endPointImgPath = endPointImgPath;
	}

	@Override
	protected BitmapDescriptor getEndBitmapDescriptor() {
		BitmapDescriptor bitmapDescriptor = null;
		try {
			bitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeStream(UZUtility
							.guessInputStream(endPointImgPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bitmapDescriptor != null) {
			return bitmapDescriptor;
		}
		return super.getEndBitmapDescriptor();
	}

	@Override
	protected BitmapDescriptor getStartBitmapDescriptor() {
		BitmapDescriptor bitmapDescriptor = null;
		try {
			bitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeStream(UZUtility
							.guessInputStream(startPointImgPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bitmapDescriptor != null) {
			return bitmapDescriptor;
		}
		return super.getStartBitmapDescriptor();
	}

	@Override
	protected BitmapDescriptor getBusBitmapDescriptor() {
		BitmapDescriptor bitmapDescriptor = null;
		try {
			bitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeStream(UZUtility
							.guessInputStream(busPointImgPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bitmapDescriptor != null) {
			return bitmapDescriptor;
		}
		return super.getBusBitmapDescriptor();
	}

	@Override
	protected BitmapDescriptor getDriveBitmapDescriptor() {
		BitmapDescriptor bitmapDescriptor = null;
		try {
			bitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeStream(UZUtility
							.guessInputStream(drivePointImgPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bitmapDescriptor != null) {
			return bitmapDescriptor;
		}
		return super.getDriveBitmapDescriptor();
	}

	@Override
	protected BitmapDescriptor getWalkBitmapDescriptor() {
		BitmapDescriptor bitmapDescriptor = null;
		try {
			bitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeStream(UZUtility
							.guessInputStream(walkPointImgPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bitmapDescriptor != null) {
			return bitmapDescriptor;
		}
		return super.getWalkBitmapDescriptor();
	}
}