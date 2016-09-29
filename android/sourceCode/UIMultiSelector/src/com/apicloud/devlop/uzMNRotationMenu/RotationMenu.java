package com.apicloud.devlop.uzMNRotationMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class RotationMenu extends View implements ImageLoaderInterface {
	private UZModuleContext mModuleContext;
	private JsParamsUtil mJsParamsUtil;
	private UzMNRotationMenu mModule;
	private Context mContext;
	private Matrix mMatrix;
	private Paint mPaint;
	private int mWidth;
	private int mHeight;
	private List<String> mItems;
	private List<Bitmap> mItemsBitmap;
	private double mRadius;
	private double mImgWidth;
	private double mImgHeight;
	private int mCorner;
	private boolean mIsColor;
	private boolean mIsShowIndicator;
	private int mBgColor;
	private int mIndicatorColor;
	private int mIndicatorAvtiveColor;
	private Bitmap mBgBitmap;
	private Bitmap mPlaceholder;
	private double mStartRadian = -Math.PI / 2;
	private double mEndRadian;
	private double mUnitAngle;
	private double mUnitRadian;
	private boolean mIsAngleCorrecting = false;
	private int mCurrentPostion;
	private double mCurrentRidian;
	private float mLastX;
	private float mLastY;
	private float mTmpAngle;
	private long mDownTime;
	private double mLastOffset;
	private int mDefaultIndex = 0;
	private Rect mBgSrc;
	private RectF mBgDes;
	private AutoFlingRunnable mAutoFlingRunnable;
	private RotateDesAngelRunnable mRotateDesAngelRunnable;
	private RotateNextRunnable mRotateNextRunnable;
	private int mItemsLength;
	private int mIndicatorX;
	private int mMaxIndicatorSize = 10;
	private int mIndicatorSize;
	private Timer mTimer;

	public RotationMenu(Context context) {
		super(context);
		mContext = context;
	}

	public RotationMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void init(UZModuleContext moduleContext, UzMNRotationMenu module) {
		mModuleContext = moduleContext;
		mJsParamsUtil = JsParamsUtil.getInstance();
		mModule = module;
		mMatrix = new Matrix();
		mPaint = new Paint();
		mTimer = new Timer();
		mWidth = UZUtility.dipToPix(mJsParamsUtil.w(mModuleContext,
				(Activity) mContext));
		mHeight = UZUtility.dipToPix(mJsParamsUtil.h(mModuleContext,
				(Activity) mContext));
		mImgWidth = UZUtility.dipToPix(mJsParamsUtil.imgWidth(moduleContext,
				(Activity) mContext));
		mImgHeight = UZUtility.dipToPix(mJsParamsUtil.imgHeight(moduleContext,
				(Activity) mContext));
		mRadius = UZUtility.dipToPix(mJsParamsUtil.r(mModuleContext,
				(Activity) mContext)) + mImgHeight;
		mUnitRadian = Math.asin(mImgWidth / 2 / (mRadius - mImgHeight)) * 2;
		mUnitAngle = 180 * mUnitRadian / Math.PI;
		mCorner = mJsParamsUtil.corner(mModuleContext);
		initItems();
		mDefaultIndex = mJsParamsUtil.index(mModuleContext);
		if (mDefaultIndex == -1 && mItems != null) {
			mDefaultIndex = mItems.size() / 2;
		}
		mCurrentPostion = mDefaultIndex;
		mStartRadian = -mUnitRadian * mDefaultIndex;
		if (mItems != null)
			mEndRadian = -mUnitRadian * (mItems.size() - 1);
		initPlacehoder();
		mIsColor = mJsParamsUtil.isColor(mModuleContext);
		if (mIsColor) {
			mBgColor = mJsParamsUtil.bgColor(mModuleContext);
		} else {
			mBgBitmap = mJsParamsUtil.bg(mModuleContext, mModule);
			mBgSrc = new Rect(0, 0, mBgBitmap.getWidth(), mBgBitmap.getHeight());
			mBgDes = new RectF(0, 0, mWidth, mHeight);
		}
		callBack("show", mDefaultIndex);
		mIsShowIndicator = mJsParamsUtil.isIndicator(mModuleContext);
		if (mIsShowIndicator) {
			mIndicatorX = mWidth / 6;
			mIndicatorSize = mWidth / (6 * mItemsLength - 3);
			if (mIndicatorSize > mMaxIndicatorSize) {
				mIndicatorSize = mMaxIndicatorSize;
				mIndicatorX = mWidth / 2 - (2 * mItemsLength - 1)
						* mIndicatorSize;
			}
			mIndicatorColor = mJsParamsUtil.indicatorColor(mModuleContext);
			mIndicatorAvtiveColor = mJsParamsUtil
					.indicatorActiveColor(mModuleContext);
		}
		boolean isAuto = mModuleContext.optBoolean("auto", false);
		if (isAuto) {
			int interval = mModuleContext.optInt("interval", 3000);
			mTimer.schedule(mTimerTask, 0, interval);
		}
	}

	public void setIndex(int index) {
		mStartRadian = -mUnitRadian * index;
		invalidate();
	}

	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			boolean isFling = false;
			if (mAutoFlingRunnable != null) {
				isFling = mAutoFlingRunnable.isFling;
			}
			if (!mIsAngleCorrecting && !isFling
					&& mCurrentPostion < mItemsLength - 1) {
				if (mRotateNextRunnable != null) {
					mRotateNextRunnable.stopRotate();
				}
				double desRaidan = mStartRadian + mUnitRadian
						* (mCurrentPostion + 1);
				post(mRotateNextRunnable = new RotateNextRunnable(desRaidan));
			}
		}
	};

	private void initItems() {
		mItems = mJsParamsUtil.items(mModuleContext);
		if (mItems != null) {
			mItemsBitmap = new ArrayList<Bitmap>();
			int i = 0;
			for (String item : mItems) {
				if (item.startsWith("http")) {
					mItemsBitmap.add(null);
					new ImageLoader().loadImage(this, i, item);
				} else {
					Bitmap bitmap = mJsParamsUtil.getBitmap(mModule
							.makeRealPath(item));
					Bitmap tmpBitmap = Bitmap.createScaledBitmap(bitmap,
							(int) mImgWidth, (int) mImgHeight, true);
					bitmap.recycle();
					mItemsBitmap.add(createRoundConerImage(tmpBitmap, mCorner));
				}
				i++;
			}
			mItemsLength = mItems.size();
		}
	}

	private void initPlacehoder() {
		mPlaceholder = mJsParamsUtil.placeholder(mModuleContext, mModule);
		if (mPlaceholder != null) {
			mPlaceholder = Bitmap.createScaledBitmap(mPlaceholder,
					(int) mImgWidth, (int) mImgHeight, true);
			mPlaceholder = createRoundConerImage(mPlaceholder, mCorner);
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mIsColor) {
			canvas.drawColor(mBgColor);
		} else {
			canvas.drawBitmap(mBgBitmap, mBgSrc, mBgDes, mPaint);
		}
		float startAngle = (float) (180 * mStartRadian / Math.PI);
		double offset = Math.PI;
		if (mItems == null)
			return;
		for (int i = 0; i < mItemsLength; i++) {
			mMatrix.reset();
			float curAngle = startAngle + (float) mUnitAngle * i;
			mMatrix.postRotate(curAngle);
			double curRadian = mStartRadian + mUnitRadian * i;
			if (Math.abs(curRadian) <= offset && !mIsAngleCorrecting) {
				offset = Math.abs(curRadian);
				mCurrentPostion = i;
				mCurrentRidian = curRadian;
			}
			if (Math.abs(curRadian) - Math.PI / 2 - mUnitRadian / 2 >= 0)
				continue;
			if (curAngle < 0) {
				float x = calculateX2(-curRadian);
				float y = calculateY2(-curRadian);
				mMatrix.postTranslate(x, y);
			} else {
				float x = calculateX1(curRadian);
				float y = calculateY1(curRadian);
				mMatrix.postTranslate(x, y);
			}
			Bitmap bitmap = mItemsBitmap.get(i);
			if (bitmap == null) {
				if (mPlaceholder != null)
					canvas.drawBitmap(mPlaceholder, mMatrix, null);
			} else {
				canvas.drawBitmap(bitmap, mMatrix, null);
			}
		}
		if (mIsShowIndicator) {
			for (int i = 0; i < mItemsLength; i++) {
				if (i == mCurrentPostion) {
					mPaint.setColor(mIndicatorAvtiveColor);
				} else {
					mPaint.setColor(mIndicatorColor);
				}
				canvas.drawCircle(mIndicatorX + mIndicatorSize * 4 * i,
						mHeight - 20, mIndicatorSize, mPaint);
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mRotateNextRunnable != null) {
			mRotateNextRunnable.stopRotate();
		}
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			mDownTime = System.currentTimeMillis();
			mTmpAngle = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			double angleOffset = calculateAngleOffset(mLastX, mLastY, x, y);
			if (Math.abs(angleOffset) > mUnitRadian) {
				angleOffset = 0;
			}
			mStartRadian += angleOffset;
			if (mStartRadian > 0) {
				mStartRadian = 0;
				invalidate();
				return true;
			} else if (mStartRadian < mEndRadian) {
				mStartRadian = mEndRadian;
				invalidate();
				return true;
			}
			if (mLastOffset * angleOffset < 0) {
				mTmpAngle = 0;
				mDownTime = System.currentTimeMillis();
			} else {
				mTmpAngle += angleOffset;
			}
			mLastOffset = angleOffset;
			invalidate();
			mLastX = x;
			mLastY = y;
			break;
		case MotionEvent.ACTION_UP:
			long currentTime = System.currentTimeMillis();
			float anglePerSecond = mTmpAngle * 1000 / (currentTime - mDownTime);
			if (Math.abs(anglePerSecond) > Math.PI / 20) {
				if (mAutoFlingRunnable != null) {
					mAutoFlingRunnable.stopFling();
				}
				post(mAutoFlingRunnable = new AutoFlingRunnable(anglePerSecond));
			} else {
				if (mRotateDesAngelRunnable != null) {
					mRotateDesAngelRunnable.stopRotate();
				}
				// post(mRotateDesAngelRunnable = new RotateDesAngelRunnable(
				// mCurrentRidian));
				callBack("click", calculateClickPos(x, y));
			}
			break;
		}
		return true;
	}

	private double calculateAngleOffset(float x1, float y1, float x2, float y2) {
		double angle1 = Math.atan((y1 - mRadius) / (x1 - mWidth / 2));
		double angle2 = Math.atan((y2 - mRadius) / (x2 - mWidth / 2));
		if (angle1 < 0 && angle2 > 0) {
			return -(Math.PI - angle2 + angle1);
		}
		if (angle1 > 0 && angle2 < 0) {
			return Math.PI - angle1 + angle2;
		}
		return angle2 - angle1;
	}

	private int calculateClickPos(float x, float y) {
		double clickAngle = Math.atan((y - mRadius) / (x - mWidth / 2));
		if (clickAngle < 0) {
			clickAngle = Math.PI / 2 + clickAngle;
		} else {
			clickAngle = -(Math.PI / 2 - clickAngle);
		}
		int length = mItems.size();
		for (int i = 0; i < length; i++) {
			double preAngle = (mStartRadian + mUnitRadian * i) - mUnitRadian
					/ 2;
			double nextAngle = (mStartRadian + mUnitRadian * i) + mUnitRadian
					/ 2;
			if (clickAngle >= preAngle && clickAngle <= nextAngle) {
				return i;
			}
		}
		return 0;
	}

	private float calculateX2(double angle) {
		double x = mWidth / 2 - mRadius * Math.sin(angle);
		return (float) (x - (mImgWidth / 2) * Math.cos(angle));
	}

	private float calculateY2(double angle) {
		double y = mRadius - mRadius * Math.cos(angle);
		return (float) (y + (mImgWidth / 2) * Math.sin(angle));
	}

	private float calculateX1(double angle) {
		double x = mWidth / 2 + mRadius * Math.sin(angle);
		return (float) (x - (mImgWidth / 2) * Math.cos(angle));
	}

	private float calculateY1(double angle) {
		double y = mRadius - mRadius * Math.cos(angle);
		return (float) (y - (mImgWidth / 2) * Math.sin(angle));
	}

	private Bitmap createRoundConerImage(Bitmap source, int corner) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rect, corner, corner, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		source.recycle();
		return target;
	}

	@Override
	public void onLoaderFinish(int index, Bitmap bitmap) {
		if (bitmap != null) {
			Bitmap tmpBitmap = Bitmap.createScaledBitmap(bitmap,
					(int) mImgWidth, (int) mImgHeight, true);
			bitmap.recycle();
			mItemsBitmap.set(index, createRoundConerImage(tmpBitmap, mCorner));
			invalidate();
		}
	}

	private class AutoFlingRunnable implements Runnable {

		private boolean isFling = false;
		private float angelPerSecond;

		public AutoFlingRunnable(float velocity) {
			this.angelPerSecond = velocity;
			isFling = true;
		}

		public void stopFling() {
			isFling = false;
		}

		public void run() {
			if (!isFling) {
				return;
			}
			if (mRotateDesAngelRunnable != null) {
				mRotateDesAngelRunnable.stopRotate();
			}
			if (Math.abs(angelPerSecond) < Math.PI / 10) {
				post(mRotateDesAngelRunnable = new RotateDesAngelRunnable(
						mCurrentRidian));
				isFling = false;
				return;
			}
			mStartRadian += (angelPerSecond / 30);
			if (mStartRadian > 0) {
				mStartRadian = 0;
				callBack("scroll", 0);
				invalidate();
				isFling = false;
				return;
			} else if (mStartRadian < mEndRadian) {
				mStartRadian = mEndRadian;
				invalidate();
				callBack("scroll", mItems.size() - 1);
				isFling = false;
				return;
			}
			angelPerSecond /= 1.0666F;
			postDelayed(this, 30);
			invalidate();
		}
	}

	private class RotateDesAngelRunnable implements Runnable {
		private boolean isStop = false;
		private double desRadian;
		private double unitRaian = Math.PI / 180;

		public RotateDesAngelRunnable(double desRadian) {
			this.desRadian = desRadian;
		}

		public void stopRotate() {
			isStop = true;
		}

		public void run() {
			mIsAngleCorrecting = true;
			if (isStop) {
				mIsAngleCorrecting = false;
				return;
			}
			if (Math.abs(desRadian) <= unitRaian) {
				callBack("scroll", mCurrentPostion);
				mIsAngleCorrecting = false;
				return;
			}
			if (desRadian > 0) {
				desRadian -= unitRaian;
				mStartRadian -= unitRaian;
			} else {
				desRadian += unitRaian;
				mStartRadian += unitRaian;
			}
			invalidate();
			postDelayed(this, 10);
		}
	}

	private class RotateNextRunnable implements Runnable {
		private boolean isStop = false;
		private double desRadian;
		private double unitRaian = Math.PI / 360;

		public RotateNextRunnable(double desRadian) {
			this.desRadian = desRadian;
		}

		public void stopRotate() {
			isStop = true;
		}

		public void run() {
			if (isStop) {
				return;
			}
			if (Math.abs(desRadian) <= unitRaian) {
				mStartRadian += desRadian - unitRaian;
				invalidate();
				callBack("scroll", mCurrentPostion);
				return;
			}
			if (desRadian > 0) {
				desRadian -= unitRaian;
				mStartRadian -= unitRaian;
			} else {
				desRadian += unitRaian;
				mStartRadian += unitRaian;
			}
			invalidate();
			postDelayed(this, 10);
		}
	}

	private void callBack(String eventType, int index) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("eventType", eventType);
			ret.put("index", index);
			mModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
