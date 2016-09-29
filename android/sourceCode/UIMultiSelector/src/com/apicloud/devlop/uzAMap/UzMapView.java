//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewParent;
import com.amap.api.maps.MapView;

@SuppressLint("ClickableViewAccessibility")
public class UzMapView extends MapView {

	public UzMapView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			requestParentDisallowInterceptTouchEvent(true);
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void requestParentDisallowInterceptTouchEvent(
			boolean disallowIntercept) {
		final ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(disallowIntercept);
		}
	}
}
