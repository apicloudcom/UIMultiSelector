//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzAMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.apicloud.devlop.uzAMap.models.Annotation;
import com.apicloud.devlop.uzAMap.models.Billboard;
import com.apicloud.devlop.uzAMap.models.Bubble;
import com.apicloud.devlop.uzAMap.models.MoveAnnotation;
import com.apicloud.devlop.uzAMap.utils.CallBackUtil;
import com.apicloud.devlop.uzAMap.utils.JsParamsUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.util.OtherUtils;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class MapAnnotations implements OnMarkerClickListener,
		OnMarkerDragListener, OnInfoWindowClickListener, InfoWindowAdapter {
	private UzAMap mUzAMap;
	private AMap mAMap;
	private Context mContext;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Marker> mMarkers = new HashMap<Integer, Marker>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Annotation> mAnnotations = new HashMap<Integer, Annotation>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Billboard> mBillboards = new HashMap<Integer, Billboard>();
	private Map<Marker, Annotation> mMarkerAnnoMap = new HashMap<Marker, Annotation>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, MoveAnnotation> mMoveMarkerMap = new HashMap<Integer, MoveAnnotation>();
	private Map<Marker, Bubble> mMarkerBubbleMap = new HashMap<Marker, Bubble>();
	private Map<Marker, MoveAnnotation> mMoveAnnoMap = new HashMap<Marker, MoveAnnotation>();

	public MapAnnotations(UzAMap uzAMap, AMap aMap, Context context) {
		this.mUzAMap = uzAMap;
		this.mAMap = aMap;
		this.mContext = context;
		this.mAMap.setOnMarkerDragListener(this);
		this.mAMap.setOnMarkerClickListener(this);
		this.mAMap.setInfoWindowAdapter(this);
		this.mAMap.setOnInfoWindowClickListener(this);
	}

	public void addAnnotations(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		List<Annotation> annotations = jsParamsUtil.annotations(moduleContext,
				mUzAMap);
		if (annotations != null && annotations.size() > 0) {
			for (Annotation annotation : annotations) {
				mAnnotations.put(annotation.getId(), annotation);
				Marker marker = mAMap.addMarker(createMarkerOptions(
						annotation.getLon(), annotation.getLat(),
						annotation.getIcons(), annotation.getIconsPath(),
						annotation.isDraggable(),
						(int) (annotation.getTimeInterval() * 50)));

				Marker oldMarker = mMarkers.get(annotation.getId());
				if (oldMarker != null) {
					oldMarker.remove();
				}
				mMarkers.put(annotation.getId(), marker);
				mMarkerAnnoMap.put(marker, annotation);
			}
		}
	}

	public void addMoveAnnotations(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		List<MoveAnnotation> annotations = jsParamsUtil.moveAnnotations(
				moduleContext, mUzAMap);
		if (annotations != null && annotations.size() > 0) {
			for (MoveAnnotation annotation : annotations) {
				mMoveMarkerMap.put(annotation.getId(), annotation);
				Marker marker = mAMap.addMarker(createMarkerOptions(
						annotation.getLon(), annotation.getLat(),
						annotation.getIcon(), annotation.isDraggable()));
				Marker oldMarker = mMarkers.get(annotation.getId());
				annotation.setMarker(marker);
				if (oldMarker != null) {
					oldMarker.remove();
				}
				mMarkers.put(annotation.getId(), marker);
				mMoveAnnoMap.put(marker, annotation);
			}
		}
	}

	public void removeAnnotations(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		List<Integer> ids = jsParamsUtil.removeOverlayIds(moduleContext);
		for (int id : ids) {
			Marker marker = mMarkers.get(id);
			if (marker != null) {
				marker.remove();
				mMarkers.remove(id);
			}
		}
	}

	public void getAnnotationCoords(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		Marker marker = mMarkers.get(id);
		if (marker != null) {
			CallBackUtil.getMarkerCoordsCallBack(moduleContext,
					marker.getPosition().latitude,
					marker.getPosition().longitude);
		}
	}

	public void setAnnotationCoords(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		Marker marker = mMarkers.get(id);
		if (marker != null) {
			double lat = moduleContext.optDouble("lat");
			double lon = moduleContext.optDouble("lon");
			marker.setPosition(new LatLng(lat, lon));
		}
	}

	public void annotationExist(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		Marker marker = mMarkers.get(id);
		if (marker != null) {
			CallBackUtil.annotationExistCallBack(moduleContext, true);
		} else {
			CallBackUtil.annotationExistCallBack(moduleContext, false);
		}
	}

	public void setBubble(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		Bubble bubble = jsParamsUtil.bubble(moduleContext, mUzAMap);
		Marker marker = mMarkers.get(bubble.getId());
		if (marker != null) {
			mMarkerBubbleMap.put(marker, bubble);
			marker.setTitle("");
		}
	}

	public void popupBubble(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		Marker marker = mMarkers.get(id);
		if (marker != null) {
			marker.showInfoWindow();
		}
	}
	
	public void closeBubble(UZModuleContext moduleContext) {
		int id = moduleContext.optInt("id");
		Marker marker = mMarkers.get(id);
		if (marker != null) {
			marker.hideInfoWindow();
		}
	}

	@SuppressWarnings("deprecation")
	public void addBillboard(UZModuleContext moduleContext) {
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		Bubble bubble = jsParamsUtil.bubble(moduleContext, mUzAMap);
		String iconPath = bubble.getIconPath();
		String illusAlign = bubble.getIllusAlign();
		int layoutId = UZResourcesIDFinder
				.getResLayoutID("mo_amap_bubble_left");
		if (illusAlign == null || !illusAlign.equals("left")) {
			layoutId = UZResourcesIDFinder
					.getResLayoutID("mo_amap_bubble_right");
		}
		View infoContent = View.inflate(mContext, layoutId, null);
		Bitmap bgImg = bubble.getBgImg();
		if (bgImg != null) {
			infoContent.setBackgroundDrawable(new BitmapDrawable(bgImg));
			infoContent.setLayoutParams(new LayoutParams(UZUtility
					.dipToPix(120), UZUtility.dipToPix(75)));
		} else {
			infoContent.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, UZUtility.dipToPix(75)));
		}
		TextView titleView = (TextView) infoContent
				.findViewById(UZResourcesIDFinder.getResIdID("title"));
		titleView.setText(bubble.getTitle());
		titleView.setTextColor(bubble.getTitleColor());
		titleView.setTextSize(bubble.getTitleSize());
		TextView subTitleView = (TextView) infoContent
				.findViewById(UZResourcesIDFinder.getResIdID("subTitle"));
		subTitleView.setText(bubble.getSubTitle());
		subTitleView.setTextColor(bubble.getSubTitleColor());
		subTitleView.setTextSize(bubble.getSubTitleSize());
		ImageView iconView = (ImageView) infoContent
				.findViewById(UZResourcesIDFinder.getResIdID("icon"));
		double lat = jsParamsUtil.lat(moduleContext, "coords");
		double lon = jsParamsUtil.lon(moduleContext, "coords");
		boolean draggable = moduleContext.optBoolean("draggable", false);
		Billboard billboard = new Billboard(bubble.getId(), lat, lon,
				draggable, null);
		mBillboards.put(bubble.getId(), billboard);
		if (iconPath != null && iconPath.startsWith("http")) {
			billboard.setView(infoContent);
			getImgShowUtil().display(iconView, bubble.getIconPath(),
					getLoadCallBack(bubble.getId()));
		} else {
			iconView.setBackgroundDrawable(new BitmapDrawable(jsParamsUtil
					.getBitmap(mUzAMap.makeRealPath(bubble.getIconPath()))));
			Marker marker = mAMap.addMarker(createBillboardOptions(lon, lat,
					infoContent, draggable));
			billboard.setMarker(marker);
			mMarkers.put(bubble.getId(), marker);
		}
	}

	private BitmapLoadCallBack<View> getLoadCallBack(final int id) {
		return new BitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View container, String uri,
					Bitmap bitmap, BitmapDisplayConfig displayConfig,
					BitmapLoadFrom from) {
				((ImageView) container).setImageBitmap(bitmap);
				Billboard billboard = mBillboards.get(id);
				if (billboard != null) {
					Marker marker = mAMap.addMarker(createBillboardOptions(
							billboard.getLon(), billboard.getLat(),
							billboard.getView(), billboard.isDraggable()));
					billboard.setMarker(marker);
					mMarkers.put(id, marker);
				}
			}

			@Override
			public void onLoading(View container, String uri,
					BitmapDisplayConfig config, long total, long current) {
			}

			@Override
			public void onLoadFailed(View container, String uri,
					Drawable failedDrawable) {
			}
		};
	}

	private MarkerOptions createBillboardOptions(double lon, double lat,
			View view, boolean draggable) {
		MarkerOptions markerOptions = new MarkerOptions();
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.fromView(view);
		markerOptions.anchor(0.5f, 0.5f).position(new LatLng(lat, lon))
				.draggable(draggable).icon(bitmapDescriptor);
		return markerOptions;
	}

	@SuppressWarnings("deprecation")
	private MarkerOptions createMarkerOptions(double lon, double lat,
			List<Bitmap> icons, List<String> iconsPath, boolean draggable,
			int period) {
		MarkerOptions markerOptions = new MarkerOptions();
		if (icons != null && icons.size() > 0) {
			ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
			BitmapDescriptor bitmapDescriptor = null;
			JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
			for (String icon : iconsPath) {
				if (icon != null) {
					bitmapDescriptor = BitmapDescriptorFactory
							.fromBitmap(jsParamsUtil.getBitmap(icon));
					giflist.add(bitmapDescriptor);
				}
			}
			markerOptions.icons(giflist);
		} else {
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED);
			markerOptions.icon(bitmapDescriptor);
		}
		markerOptions.anchor(0.5f, 0.5f).position(new LatLng(lat, lon))
				.draggable(draggable).period(period).perspective(true)
				.title(null).snippet(null);
		return markerOptions;
	}

	@SuppressWarnings("deprecation")
	private MarkerOptions createMarkerOptions(double lon, double lat,
			Bitmap icon, boolean draggable) {
		MarkerOptions markerOptions = new MarkerOptions();
		BitmapDescriptor bitmapDescriptor = null;
		if (icon != null) {
			bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon);
			markerOptions.icon(bitmapDescriptor);

		} else {
			bitmapDescriptor = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED);
			markerOptions.icon(bitmapDescriptor);
		}
		markerOptions.anchor(0.5f, 0.5f).position(new LatLng(lat, lon))
				.draggable(draggable).perspective(true).title(null)
				.snippet(null);
		return markerOptions;
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		Annotation annotation = mMarkerAnnoMap.get(marker);
		if (annotation != null)
			CallBackUtil.markerDragCallBack(annotation.getModuleContext(),
					annotation.getId(), "dragging");
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		Annotation annotation = mMarkerAnnoMap.get(marker);
		if (annotation != null)
			CallBackUtil.markerDragCallBack(annotation.getModuleContext(),
					annotation.getId(), "starting");
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		Annotation annotation = mMarkerAnnoMap.get(marker);
		if (annotation != null)
			CallBackUtil.markerDragCallBack(annotation.getModuleContext(),
					annotation.getId(), "dragging");
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		Annotation annotation = mMarkerAnnoMap.get(marker);
		if (annotation != null)
			CallBackUtil.markerClickCallBack(annotation.getModuleContext(),
					annotation.getId());
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getInfoWindow(Marker marker) {
		final Bubble bubble = mMarkerBubbleMap.get(marker);
		if (bubble == null)
			return null;
		String illusAlign = bubble.getIllusAlign();
		int layoutId = UZResourcesIDFinder
				.getResLayoutID("mo_amap_bubble_left");
		if (illusAlign == null || !illusAlign.equals("left")) {
			layoutId = UZResourcesIDFinder
					.getResLayoutID("mo_amap_bubble_right");
		}
		View infoContent = View.inflate(mContext, layoutId, null);
		Bitmap bgImg = bubble.getBgImg();
		if (bgImg != null) {
			infoContent.setBackgroundDrawable(new BitmapDrawable(bgImg));
		} else {
			infoContent.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, UZUtility.dipToPix(90)));
		}
		ImageView iconView = (ImageView) infoContent
				.findViewById(UZResourcesIDFinder.getResIdID("icon"));
		iconView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CallBackUtil.infoWindowClickCallBack(bubble.getModuleContext(),
						bubble.getId(), "clickIllus");
			}
		});
		if (bubble.getIconPath() != null
				&& bubble.getIconPath().startsWith("http")) {
			getImgShowUtil().display(iconView, bubble.getIconPath(),
					getLoadCallBack(bubble.getId()));
		} else {
			JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
			iconView.setBackgroundDrawable(new BitmapDrawable(jsParamsUtil
					.getBitmap(mUzAMap.makeRealPath(bubble.getIconPath()))));
		}
		if (bubble.getIconPath() == null || bubble.getIconPath().isEmpty()) {
			iconView.setVisibility(View.GONE);
		}

		TextView titleView = (TextView) infoContent
				.findViewById(UZResourcesIDFinder.getResIdID("title"));
		titleView.setText(bubble.getTitle());
		titleView.setTextColor(bubble.getTitleColor());
		titleView.setTextSize(bubble.getTitleSize());
		titleView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CallBackUtil.infoWindowClickCallBack(bubble.getModuleContext(),
						bubble.getId(), "clickContent");
			}
		});
		TextView subTitleView = (TextView) infoContent
				.findViewById(UZResourcesIDFinder.getResIdID("subTitle"));
		subTitleView.setText(bubble.getSubTitle());
		subTitleView.setTextColor(bubble.getSubTitleColor());
		subTitleView.setTextSize(bubble.getSubTitleSize());
		subTitleView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CallBackUtil.infoWindowClickCallBack(bubble.getModuleContext(),
						bubble.getId(), "clickContent");
			}
		});
		return infoContent;
	}

	private BitmapUtils getImgShowUtil() {
		BitmapUtils bitmapUtils = new BitmapUtils(mContext,
				OtherUtils.getDiskCacheDir(mContext, ""));
		bitmapUtils.configDiskCacheEnabled(true);
		bitmapUtils.configMemoryCacheEnabled(true);
		return bitmapUtils;
	}

	public Map<Integer, MoveAnnotation> getMoveMarkerMap() {
		return mMoveMarkerMap;
	}
}
