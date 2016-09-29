//
//UZModule
//
//Modified by magic 16/3/11.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzUIPullRefresh;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.uzmap.pkg.uzcore.UZCoreUtil;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class MTCusHeadView extends CusHeadView {
	private final static int PADDING = 7;
	private final static int TRANSFORM_DURATION = 100;
	private final static int LOAD_DURATION = 50;
	private UZModuleContext mModuleContext;
	private JsParamsUtil mJsParamsUtil;
	private ImageView mImageView;
	private int mImgViewSize;
	private Bitmap mPullImg;
	private AnimationDrawable mTransformAnim;
	private AnimationDrawable mLoadAnim;
	private int mLastState;

	public MTCusHeadView(Context context) {
		super(context);
		addImageView(context);
	}

	@Override
	protected void onSetRefreshInfo(UZModuleContext moduleContext) {
		mModuleContext = moduleContext;
		mJsParamsUtil = JsParamsUtil.getInstance();
		initStyles();
	}

	@Override
	protected void onRefresh() {
		stopAnim(mTransformAnim);
		imgRefreshStyle();
		startAnim(mLoadAnim);
	}

	@Override
	protected void onScrollY(int scrollY) {
		int absScrollY = Math.abs(scrollY);
		if (absScrollY < mImgViewSize) {
			setImageViewH(absScrollY);
		}
	}

	@Override
	protected void onStateChange(int state) {
		if (state == STATE_NORMAL) {
			onStateNormal();
		} else {
			onStateTransform();
		}
		mLastState = state;
	}

	private void addImageView(Context context) {
		initImgView(context);
		addView(mImageView);
	}

	private void initImgView(Context context) {
		mImageView = new ImageView(context);
		initLayoutParams();
		initPadding();
	}

	private void initLayoutParams() {
		mImgViewSize = UZUtility.dipToPix(UzUIPullRefresh.REFRSH_HEIGHT);
		LayoutParams layoutParams = new LayoutParams(mImgViewSize, 0);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mImageView.setLayoutParams(layoutParams);
	}

	private void initPadding() {
		int padding = UZCoreUtil.dipToPix(PADDING);
		mImageView.setPadding(padding, padding, padding, padding);
	}

	private void initStyles() {
		initBgColor();
		initPullImg();
		initTransformAnim();
		initLoadAnim();
	}

	private void initBgColor() {
		setBackgroundColor(mJsParamsUtil.bgColor(mModuleContext));
	}

	private void initPullImg() {
		mPullImg = mJsParamsUtil.pullImg(mModuleContext);
		if (mPullImg == null) {
			int drawableId = UZResourcesIDFinder.getResDrawableID("pull_image");
			mPullImg = BitmapFactory.decodeResource(getResources(), drawableId);
		}
		mImageView.setImageBitmap(mPullImg);
	}

	@SuppressWarnings("deprecation")
	private void initTransformAnim() {
		List<Bitmap> imgs = mJsParamsUtil.transformImgs(mModuleContext);
		if (imgs == null) {
			imgs = new ArrayList<Bitmap>();
			for (int i = 1; i <= 5; i++) {
				int drawableId = UZResourcesIDFinder
						.getResDrawableID("pull_end_image_frame_0" + i);
				imgs.add(BitmapFactory.decodeResource(getResources(),
						drawableId));
			}
		}
		mTransformAnim = new AnimationDrawable();
		mTransformAnim.setOneShot(true);
		if (imgs != null) {
			for (Bitmap img : imgs) {
				mTransformAnim.addFrame(new BitmapDrawable(img),
						TRANSFORM_DURATION);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void initLoadAnim() {
		List<Bitmap> imgs = mJsParamsUtil.loadImgs(mModuleContext);
		if (imgs == null) {
			imgs = new ArrayList<Bitmap>();
			for (int i = 1; i <= 8; i++) {
				int drawableId = UZResourcesIDFinder
						.getResDrawableID("refreshing_image_frame_0" + i);
				imgs.add(BitmapFactory.decodeResource(getResources(),
						drawableId));
			}
		}
		mLoadAnim = new AnimationDrawable();
		if (imgs != null) {
			for (Bitmap img : imgs) {
				mLoadAnim.addFrame(new BitmapDrawable(img), LOAD_DURATION);
			}
		}
		mLoadAnim.setOneShot(false);
	}

	private void setImageViewH(int height) {
		LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
		layoutParams.height = height;
		mImageView.setLayoutParams(layoutParams);
	}

	private void imgRefreshStyle() {
		mImageView.setImageDrawable(mLoadAnim);
		setImageViewH(mImgViewSize);
	}

	private void onStateNormal() {
		stopAnim(mLoadAnim);
		mImageView.setImageBitmap(mPullImg);
	}

	private void onStateTransform() {
		if (mLastState == STATE_NORMAL) {
			mImageView.setImageDrawable(mTransformAnim);
			startAnim(mTransformAnim);
		}
	}
}
