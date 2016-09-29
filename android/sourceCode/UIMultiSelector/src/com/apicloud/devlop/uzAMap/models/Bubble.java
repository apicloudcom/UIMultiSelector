//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap.models;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import android.graphics.Bitmap;

public class Bubble {
	private int id;
	private Bitmap bgImg;
	private String title;
	private String subTitle;
	private String iconPath;
	private int titleSize;
	private int subTitleSize;
	private String illusAlign;
	private int titleColor;
	private int subTitleColor;
	private UZModuleContext moduleContext;

	public Bubble() {
	}

	public Bubble(int id, Bitmap bgImg, String title, String subTitle,
			String iconPath, int titleSize, int subTitleSize,
			String illusAlign, int titleColor, int subTitleColor,
			UZModuleContext moduleContext) {
		this.id = id;
		this.bgImg = bgImg;
		this.title = title;
		this.subTitle = subTitle;
		this.iconPath = iconPath;
		this.titleSize = titleSize;
		this.subTitleSize = subTitleSize;
		this.illusAlign = illusAlign;
		this.titleColor = titleColor;
		this.subTitleColor = subTitleColor;
		this.moduleContext = moduleContext;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bitmap getBgImg() {
		return bgImg;
	}

	public void setBgImg(Bitmap bgImg) {
		this.bgImg = bgImg;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public int getTitleSize() {
		return titleSize;
	}

	public void setTitleSize(int titleSize) {
		this.titleSize = titleSize;
	}

	public int getSubTitleSize() {
		return subTitleSize;
	}

	public void setSubTitleSize(int subTitleSize) {
		this.subTitleSize = subTitleSize;
	}

	public String getIllusAlign() {
		return illusAlign;
	}

	public void setIllusAlign(String illusAlign) {
		this.illusAlign = illusAlign;
	}

	public int getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
	}

	public int getSubTitleColor() {
		return subTitleColor;
	}

	public void setSubTitleColor(int subTitleColor) {
		this.subTitleColor = subTitleColor;
	}

	public UZModuleContext getModuleContext() {
		return moduleContext;
	}

	public void setModuleContext(UZModuleContext moduleContext) {
		this.moduleContext = moduleContext;
	}
}
