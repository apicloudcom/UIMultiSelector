package com.apicloud.devlop.FNImageClip;

import java.lang.reflect.Method;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class ScreenShot {

	@SuppressWarnings("rawtypes")
	public static Bitmap createSnapshot(View target) {
		Bitmap.Config quality = Bitmap.Config.RGB_565;
		int backgroundColor = Color.TRANSPARENT;
		Class[] paramTypes = { Bitmap.Config.class, int.class, boolean.class };
		try {
			Method createSnapshot = View.class.getDeclaredMethod(
					"createSnapshot", paramTypes);
			createSnapshot.setAccessible(true);
			Bitmap bitmap = (Bitmap) createSnapshot.invoke(target, quality,
					backgroundColor, false);
			if (null != bitmap) {
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		int width = target.getRight() - target.getLeft();
		int height = target.getBottom() - target.getTop();
		final float scale = 1.0f;
		width = (int) ((width * scale) + 0.5f);
		height = (int) ((height * scale) + 0.5f);
		Resources resources = target.getResources();
		Bitmap bitmap = null;
		try {

			bitmap = Bitmap.createBitmap(width > 0 ? width : 1,
					height > 0 ? height : 1, quality);
			if (resources.getDisplayMetrics() != null) {
				bitmap.setDensity(resources.getDisplayMetrics().densityDpi);
			}
		} catch (Exception e) {

		}
		if (bitmap == null) {
			return null;
		}
		if (resources != null) {
			bitmap.setDensity(resources.getDisplayMetrics().densityDpi);
		}
		Canvas canvas = new Canvas(bitmap);
		if ((backgroundColor & 0xff000000) != 0) {
			bitmap.eraseColor(backgroundColor);
		}
		target.computeScroll();
		final int restoreCount = canvas.save();
		canvas.scale(scale, scale);
		canvas.translate(-target.getScrollX(), -target.getScrollY());
		target.draw(canvas);
		canvas.restoreToCount(restoreCount);
		canvas.setBitmap(null);
		return bitmap;
	}
}