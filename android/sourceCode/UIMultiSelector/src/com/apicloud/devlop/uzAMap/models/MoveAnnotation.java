//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap.models;

import android.graphics.Bitmap;
import com.amap.api.maps.model.Marker;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class MoveAnnotation {
	private int id;
	private Marker marker;
	private double lat;
	private double lon;
	private Bitmap icon;
	private boolean draggable;
	private UZModuleContext moduleContext;

	public MoveAnnotation() {
	}

	public MoveAnnotation(int id, Marker marker, double lat, double lon,
			Bitmap icon, boolean draggable, UZModuleContext moduleContext) {
		this.id = id;
		this.marker = marker;
		this.lat = lat;
		this.lon = lon;
		this.icon = icon;
		this.draggable = draggable;
		this.moduleContext = moduleContext;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
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

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}

	public UZModuleContext getModuleContext() {
		return moduleContext;
	}

	public void setModuleContext(UZModuleContext moduleContext) {
		this.moduleContext = moduleContext;
	}

}
