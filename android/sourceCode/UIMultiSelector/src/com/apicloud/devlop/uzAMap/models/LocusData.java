package com.apicloud.devlop.uzAMap.models;

public class LocusData {
	double longtitude;
	double latitude;
	int rgba;
	public LocusData(double longtitude, double latitude, int rgba) {
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.rgba = rgba;
	}
	public double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getRgba() {
		return rgba;
	}
	public void setRgba(int rgba) {
		this.rgba = rgba;
	}
}
