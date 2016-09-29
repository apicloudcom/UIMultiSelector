package com.apicloud.devlop.FNImageClip;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class TounchListener implements OnTouchListener {
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private ImageView mImgView;
	private PointF mStartPoint = new PointF();
	private Matrix mMatrix = new Matrix();
	private Matrix mCurrentMaritx = new Matrix();
	private int mMode = 0;// 用于标记模式
	private float mStartDis = 0;
	private long mSingleClickTime;
	private long mCurrentClickTime;

	public TounchListener(ImageView mImgView) {
		this.mImgView = mImgView;
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			onDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			onMove(event);
			break;
		case MotionEvent.ACTION_UP:
			mMode = 0;
			break;
		// 有手指离开屏幕，但屏幕还有触点(手指)
		case MotionEvent.ACTION_POINTER_UP:
			mMode = 0;
			break;
		// 当屏幕上已经有触点（手指）,再有一个手指压下屏幕
		case MotionEvent.ACTION_POINTER_DOWN:
			onPointerDown(event);
			break;
		}
		mImgView.setImageMatrix(mMatrix);
		return true;
	}

	private void onDown(MotionEvent event) {
		if (isDoubleClick()) {
			onDoubleClick();
		}
		mMode = DRAG;
		mImgView.setScaleType(ImageView.ScaleType.MATRIX);
		mCurrentMaritx.set(mImgView.getImageMatrix());// 记录ImageView当前的移动位置
		mStartPoint.set(event.getX(), event.getY());// 开始点
		onMove(event);
	}

	private void onMove(MotionEvent event) {
		if (mMode == DRAG) {// 图片拖动事件
			float dx = event.getX() - mStartPoint.x;// x轴移动距离
			float dy = event.getY() - mStartPoint.y;
			mMatrix.set(mCurrentMaritx);// 在当前的位置基础上移动
			mMatrix.postTranslate(dx, dy);

		} else if (mMode == ZOOM) {// 图片放大事件
			float endDis = distance(event);// 结束距离
			if (endDis > 10f) {
				float scale = endDis / mStartDis;// 放大倍数
				mMatrix.set(mCurrentMaritx);
				float currentX = mImgView.getWidth() / 2;
				float currentY = mImgView.getHeight() / 2;
				mMatrix.postScale(scale, scale, currentX, currentY);
			}
		}
	}

	private void onPointerDown(MotionEvent event) {
		mMode = ZOOM;
		mStartDis = distance(event);
		if (mStartDis > 10f) {// 避免手指上有两个茧
			mCurrentMaritx.set(mImgView.getImageMatrix());// 记录当前的缩放倍数
		}
	}

	private void onDoubleClick() {
		float scale = 2;// 放大倍数
		mMatrix.set(mCurrentMaritx);
		float currentX = mImgView.getWidth() / 2;
		float currentY = mImgView.getHeight() / 2;
		mMatrix.postScale(scale, scale, currentX, currentY);
		mImgView.setImageMatrix(mMatrix);
	}

	private boolean isDoubleClick() {
		mCurrentClickTime = System.currentTimeMillis();
		if (mCurrentClickTime - mSingleClickTime < 300) {
			mSingleClickTime = 0;
			return true;
		}
		mSingleClickTime = mCurrentClickTime;
		return false;
	}

	/**
	 * 两点之间的距离
	 * 
	 * @param event
	 * @return
	 */
	@SuppressLint("FloatMath")
	private static float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		return FloatMath.sqrt(dx * dx + dy * dy);
	}
}
