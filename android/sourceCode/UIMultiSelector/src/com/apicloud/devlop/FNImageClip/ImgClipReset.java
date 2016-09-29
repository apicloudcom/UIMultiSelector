package com.apicloud.devlop.FNImageClip;

import android.widget.ImageView;

public class ImgClipReset {

	public void reset(ImgClipOpen imgClipOpen) {
		if (imgClipOpen != null) {
			ClipView clipView = imgClipOpen.getmClipView();
			ImageView imageView = imgClipOpen.getmImageView();
			ClipRect clipRect = imgClipOpen.getmClipRect();
			if (clipView != null && imageView != null && clipRect != null) {
				clipView.restRect(clipRect);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
		}
	}

}
