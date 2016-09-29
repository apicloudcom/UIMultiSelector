package com.apicloud.devlop.FNImageClip;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class ImgClipOpen {
	private JsParmasUtil mJsParmasUtil;
	private Activity mContext;
	private ClipLayout mClipLayout;
	private UZModuleContext mModuleContext;
	private UZModule mMoude;
	private ImageView mImageView;
	private Bitmap mImgBitmap;
	private ClipView mClipView;
	private ClipRect mClipRect;
	private int mX;
	private int mY;
	private int mW;
	private int mH;
	private boolean mIsCircle = false;
	private String mSavePath;

	public ImgClipOpen(Activity context, UZModuleContext moduleContext,
			UZModule moude) {
		mJsParmasUtil = JsParmasUtil.getInstance();
		mContext = context;
		mModuleContext = moduleContext;
		mMoude = moude;
	}

	public void open() {
		initClipLayout();
		initBackground(mModuleContext);
		initImageView(mModuleContext);
		initClipView(mModuleContext);
		LayoutParams layoutParams = createLayoutParams(mModuleContext);
		String fixedOn = mModuleContext.optString("fixedOn");
		mMoude.insertViewToCurWindow(mClipLayout, layoutParams, fixedOn, false);
	}

	public void openCallBack(UZModuleContext moduleContext) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initClipLayout() {
		int clipLayoutId = UZResourcesIDFinder.getResLayoutID("mo_fnimgclip");
		mClipLayout = (ClipLayout) mContext.getLayoutInflater().inflate(
				clipLayoutId, null);
		mClipLayout.setModuleContext(mModuleContext);
	}

	@SuppressLint("NewApi")
	private void initBackground(UZModuleContext moduleContext) {
		if (mJsParmasUtil.isColor(moduleContext)) {
			int bgColor = mJsParmasUtil.bgColor(moduleContext);
			mClipLayout.setBackgroundColor(bgColor);
		} else {
			Bitmap bgBitmap = mJsParmasUtil.bg(moduleContext, mMoude);
			BitmapDrawable bg = new BitmapDrawable(mContext.getResources(),
					bgBitmap);
			if (bgBitmap != null) {
				mClipLayout.setBackground(bg);
			}
		}
	}

	@SuppressLint("NewApi")
	private void initImageView(UZModuleContext moduleContext) {
		int imgViewId = UZResourcesIDFinder.getResIdID("imgView");
		mImageView = (ImageView) mClipLayout.findViewById(imgViewId);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		mImgBitmap = mJsParmasUtil.img(moduleContext, mMoude);
		mImageView.setImageBitmap(mImgBitmap);
		String mode = mJsParmasUtil.mode(moduleContext);
		if (!mode.equals("clip")) {
			TounchListener tounchListener = new TounchListener(mImageView);
			mImageView.setOnTouchListener(tounchListener);
		}
	}

	private void initClipView(UZModuleContext moduleContext) {
		int clipViewId = UZResourcesIDFinder.getResIdID("imgClipView");
		mClipView = (ClipView) mClipLayout.findViewById(clipViewId);
		mW = mJsParmasUtil.w(moduleContext, mContext);
		mH = mJsParmasUtil.h(moduleContext, mContext);
		int left = getClipRectLeft(moduleContext, mImgBitmap);
		left = UZUtility.dipToPix(left);
		int right = getClipRectRight(moduleContext, mImgBitmap);
		right = UZUtility.dipToPix(right);
		int top = getClipRectTop(moduleContext, mImgBitmap);
		top = UZUtility.dipToPix(top);
		int bottom = getClipRectBottom(moduleContext, mImgBitmap);
		bottom = UZUtility.dipToPix(bottom);
		int layerColor = mJsParmasUtil.layerColor(moduleContext);
		int borderColor = mJsParmasUtil.borderColor(moduleContext);
		int borderWidth = mJsParmasUtil.borderWidth(moduleContext);
		ClipRect clipRect = new ClipRect(left, top, right, bottom);
		mClipRect = mJsParmasUtil.clipRect(moduleContext, clipRect);
		String mode = mJsParmasUtil.mode(moduleContext);
		mIsCircle = mJsParmasUtil.isCircle(moduleContext);
		mSavePath = moduleContext.optString("destPath");
		mClipView.initParams(mClipRect, layerColor, borderColor, borderWidth,
				mode, mIsCircle);
	}

	private int getClipRectLeft(UZModuleContext moduleContext, Bitmap imgBitmap) {
		return (int) (1.0 * (mW - getImgViewWidth(moduleContext, imgBitmap)) / 2);
	}

	private int getClipRectRight(UZModuleContext moduleContext, Bitmap imgBitmap) {
		return getClipRectLeft(moduleContext, imgBitmap)
				+ getImgViewWidth(moduleContext, imgBitmap);
	}

	private int getClipRectTop(UZModuleContext moduleContext, Bitmap imgBitmap) {
		return (int) (1.0 * (mH - getImgViewHeight(moduleContext, imgBitmap)) / 2);
	}

	private int getClipRectBottom(UZModuleContext moduleContext,
			Bitmap imgBitmap) {
		return getClipRectTop(moduleContext, imgBitmap)
				+ getImgViewHeight(moduleContext, imgBitmap);
	}

	private int getImgViewWidth(UZModuleContext moduleContext, Bitmap imgBitmap) {
		int parentWidth = mJsParmasUtil.w(moduleContext, mContext);
		int parentHeight = mJsParmasUtil.h(moduleContext, mContext);
		int imgWidth = imgBitmap.getWidth();
		int imgHeight = imgBitmap.getHeight();
		if (imgWidth <= parentWidth && imgHeight <= parentHeight) {
			return parentWidth;
		} else if (imgWidth <= parentWidth && imgHeight > parentHeight) {
			return imgWidth;
		} else if (imgWidth > parentWidth && imgHeight <= parentHeight) {
			return parentWidth;
		} else if (imgWidth > parentWidth && imgHeight > parentHeight) {
			if (1.0 * imgWidth / imgHeight > 1.0 * parentWidth / parentHeight) {
				return parentWidth;
			} else {
				return (int) (imgWidth / (1.0 * imgHeight / parentHeight));
			}
		}
		return 0;
	}

	private int getImgViewHeight(UZModuleContext moduleContext, Bitmap imgBitmap) {
		int parentWidth = mJsParmasUtil.w(moduleContext, mContext);
		int parentHeight = mJsParmasUtil.h(moduleContext, mContext);
		int imgWidth = imgBitmap.getWidth();
		int imgHeight = imgBitmap.getHeight();
		if (imgWidth <= parentWidth && imgHeight <= parentHeight) {
			int height = (int) (1.0 * parentHeight * imgWidth / parentWidth);
			return height;
		} else if (imgWidth <= parentWidth && imgHeight > parentHeight) {
			return parentHeight;
		} else if (imgWidth > parentWidth && imgHeight <= parentHeight) {
			int height = (int) (1.0 * parentHeight * imgWidth / parentWidth);
			return height;
		} else if (imgWidth > parentWidth && imgHeight > parentHeight) {
			if (1.0 * imgWidth / imgHeight > 1.0 * parentWidth / parentHeight) {
				return (int) (imgHeight / (1.0 * imgWidth / parentWidth));
			} else {
				return parentHeight;
			}
		}
		return 0;
	}

	private LayoutParams createLayoutParams(UZModuleContext moduleContext) {
		LayoutParams layoutParams = new LayoutParams(mW, mH);
		mX = mJsParmasUtil.x(moduleContext);
		mY = mJsParmasUtil.y(moduleContext);
		layoutParams.setMargins(mX, mY, 0, 0);
		return layoutParams;
	}

	public JsParmasUtil getmJsParmasUtil() {
		return mJsParmasUtil;
	}

	public Activity getmContext() {
		return mContext;
	}

	public FrameLayout getmClipLayout() {
		return mClipLayout;
	}

	public UZModuleContext getmModuleContext() {
		return mModuleContext;
	}

	public UZModule getmMoude() {
		return mMoude;
	}

	public ImageView getmImageView() {
		return mImageView;
	}

	public Bitmap getmImgBitmap() {
		return mImgBitmap;
	}

	public ClipView getmClipView() {
		return mClipView;
	}

	public ClipRect getmClipRect() {
		return mClipRect;
	}

	public int getmX() {
		return mX;
	}

	public int getmY() {
		return mY;
	}

	public int getmW() {
		return mW;
	}

	public int getmH() {
		return mH;
	}

	public boolean isCircle() {
		return mIsCircle;
	}

	public String getSavePath() {
		return mSavePath;
	}
}
