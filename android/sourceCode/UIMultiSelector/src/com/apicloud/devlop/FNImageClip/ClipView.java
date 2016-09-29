package com.apicloud.devlop.FNImageClip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ClipView extends View {

	private Paint mPaint;
	private float mLeft;
	private float mRight;
	private float mTop;
	private float mBottom;
	private int mBgColor;
	private int mBorderColor;
	private int mBorderWidth;

	private boolean isInleft = false;
	private boolean isInright = false;
	private boolean isIntop = false;
	private boolean isInbottom = false;
	private boolean isInRect = false;
	private boolean isInCircle = false;
	private boolean isFirstDrawCircle = true;

	private float mPointX;
	private float mPointY;
	private float mR;

	private String mMode;

	private boolean isCircleClip = false;

	public ClipView(Context context) {
		super(context);
		mPaint = new Paint();
		setDrawingCacheEnabled(true);
	}

	public ClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		setDrawingCacheEnabled(true);
	}

	public void initParams(ClipRect clipRect, int bgColor, int borderColor,
			int borderWidth, String mode, boolean isCircle) {
		this.mLeft = clipRect.getLeft();
		this.mRight = clipRect.getRight();
		this.mTop = clipRect.getTop();
		this.mBottom = clipRect.getBottom();
		this.mBgColor = bgColor;
		this.mBorderColor = borderColor;
		this.mBorderWidth = borderWidth;
		this.mMode = mode;
		this.isCircleClip = isCircle;
	}

	public void restRect(ClipRect clipRect) {
		this.mLeft = clipRect.getLeft();
		this.mRight = clipRect.getRight();
		this.mTop = clipRect.getTop();
		this.mBottom = clipRect.getBottom();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isCircleClip) {
			drawCircle(canvas);
		} else {
			drawClipRect(canvas);
			drawBorder(canvas);
			drawBorderLines(canvas);
		}
	}

	private void drawCircle(Canvas canvas) {
		Path path = new Path();
		if (isFirstDrawCircle) {
			float r = (mRight - mLeft) / 2;
			mR = r;
			mPointX = getWidth() / 2;
			mPointY = getHeight() / 2;
			isFirstDrawCircle = false;
		}
		path.addCircle(mPointX, mPointY, mR, Direction.CW);
		canvas.clipPath(path, Op.DIFFERENCE);
		mPaint.setColor(mBgColor);
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
		mPaint.setColor(mBorderColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(mBorderWidth);
		canvas.drawCircle(mPointX, mPointY, mR + 1, mPaint);
		mLeft = mPointX - mR;
		mRight = mPointX + mR;
		mTop = mPointY - mR;
		mBottom = mPointY + mR;
	}

	private void drawClipRect(Canvas canvas) {
		mPaint.setColor(Color.TRANSPARENT);
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(mLeft, mTop, mRight, mBottom, mPaint);
		mPaint.setColor(mBgColor);
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, mTop, mLeft, mBottom, mPaint);
		canvas.drawRect(mRight, mTop, getWidth(), mBottom, mPaint);
		canvas.drawRect(0, 0, getWidth(), mTop, mPaint);
		canvas.drawRect(0, mBottom, getWidth(), getHeight(), mPaint);
	}

	private void drawBorder(Canvas canvas) {
		mPaint.setColor(mBorderColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(mBorderWidth);
		canvas.drawRect(mLeft, mTop, mRight, mBottom, mPaint);
	}

	private void drawBorderLines(Canvas canvas) {
		float horizontalAY = mTop - (mTop - mBottom) / 3;
		float horizontalBY = mTop - 2 * (mTop - mBottom) / 3;
		float verticalAX = mLeft + (mRight - mLeft) / 3;
		float verticalBX = mLeft + 2 * (mRight - mLeft) / 3;
		canvas.drawLine(mLeft, horizontalAY, mRight, horizontalAY, mPaint);
		canvas.drawLine(mLeft, horizontalBY, mRight, horizontalBY, mPaint);
		canvas.drawLine(verticalAX, mTop, verticalAX, mBottom, mPaint);
		canvas.drawLine(verticalBX, mTop, verticalBX, mBottom, mPaint);
	}

	private boolean isInLeft(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (x >= mLeft - 50 & x <= mLeft + 50 & y >= mTop - 50
				& y <= mBottom + 50) {
			return true;
		}
		return false;
	}

	private boolean isInRight(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (x >= mRight - 50 & x <= mRight + 50 & y >= mTop - 50
				& y <= mBottom + 50) {
			return true;
		}
		return false;
	}

	private boolean isInTop(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (x >= mLeft - 50 & x <= mRight + 50 & y >= mTop - 50
				& y <= mTop + 50) {
			return true;
		}
		return false;
	}

	private boolean isInBottom(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (x >= mLeft - 50 & x <= mRight + 50 & y >= mBottom - 50
				& y <= mBottom + 50) {
			return true;
		}
		return false;
	}

	private boolean isMoveClipRect(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (x >= mLeft + 50 && x <= mRight - 50 && y >= mTop + 50
				&& y <= mBottom - 50) {
			return true;
		}
		return false;
	}

	private boolean isInCircle(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (Math.pow(x - mPointX, 2) - Math.pow(y - mPointY, 2) <= Math.pow(mR,
				2)) {
			return true;
		}
		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean isCurentEvent = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isInLeft(event) || isInRight(event) || isInTop(event)
					|| isInBottom(event) || isMoveClipRect(event)) {
				isCurentEvent = true;
			}
			if (mMode.equals("image")) {
				isCurentEvent = false;
			}
			if (!isCircleClip) {
				if (isInLeft(event)) {
					isInleft = true;
				}
				if (isInTop(event)) {
					isIntop = true;
				}
				if (isInRight(event)) {
					isInright = true;
				}
				if (isInBottom(event)) {
					isInbottom = true;
				}
				if (isMoveClipRect(event)) {
					isInRect = true;
				}
			} else {
				if (isInCircle(event)) {
					isInCircle = true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isInleft) {
				if (!mMode.equals("image")) {
					mLeft = event.getX();
					if (mLeft < 0) {
						mLeft = 0;
					}
					if (mLeft >= mRight - 50) {
						mLeft = mRight - 50;
					}
					invalidate();
				} else {
					return false;
				}
			}
			if (isIntop) {
				if (!mMode.equals("image")) {
					mTop = event.getY();
					if (mTop < 0) {
						mTop = 0;
					}
					if (mTop >= mBottom - 50) {
						mTop = mBottom - 50;
					}
					invalidate();
				} else {
					return false;
				}
			}
			if (isInright) {
				if (!mMode.equals("image")) {
					mRight = event.getX();
					if (mRight > getWidth()) {
						mRight = getWidth();
					}
					if (mLeft >= mRight - 50) {
						mRight = mLeft + 50;
					}
					invalidate();
				} else {
					return false;
				}
			}
			if (isInbottom) {
				if (!mMode.equals("image")) {
					mBottom = event.getY();
					if (mBottom > getHeight()) {
						mBottom = getHeight();
					}
					if (mTop >= mBottom - 50) {
						mBottom = mTop + 50;
					}
					invalidate();
				} else {
					return false;
				}
			}
			if (isInRect) {
				if (!mMode.equals("image")) {
					float hroL = (mRight - mLeft) / 2;
					float verL = (mBottom - mTop) / 2;
					float left = event.getX() - hroL;
					float right = event.getX() + hroL;
					float top = event.getY() - verL;
					float bottom = event.getY() + verL;
					if (left >= 0 && right <= getWidth()) {
						mLeft = left;
						mRight = right;
					}
					if (top >= 0 && bottom <= getHeight()) {
						mBottom = bottom;
						mTop = top;
					}
					invalidate();
				} else {
					return false;
				}
			}
			if (isInCircle) {
				if (!mMode.equals("image")) {
					mPointX = event.getX();
					mPointY = event.getY();
					invalidate();
				} else {
					return false;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			isInleft = false;
			isInright = false;
			isIntop = false;
			isInbottom = false;
			isInRect = false;
			break;
		}

		return isCurentEvent;
	}

	public float getmLeft() {
		return mLeft;
	}

	public void setmLeft(float mLeft) {
		this.mLeft = mLeft;
	}

	public float getmRight() {
		return mRight;
	}

	public void setmRight(float mRight) {
		this.mRight = mRight;
	}

	public float getmTop() {
		return mTop;
	}

	public void setmTop(float mTop) {
		this.mTop = mTop;
	}

	public float getmBottom() {
		return mBottom;
	}

	public void setmBottom(float mBottom) {
		this.mBottom = mBottom;
	}

}
