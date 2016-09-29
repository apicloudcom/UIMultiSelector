//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap.models;

import android.view.View;

import com.amap.api.maps.model.Marker;

public class Billboard {
	private int id;
	private double lat;
	private double lon;
	private boolean draggable;
	private Marker marker;
	private View view;

	public Billboard(int id, double lat, double lon, boolean draggable,
			Marker marker) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.draggable = draggable;
		this.marker = marker;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}
}
