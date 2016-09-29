package com.apicloud.devlop.FNImageClip;
import android.widget.FrameLayout;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class UzImgClip extends UZModule {
	private ImgClipOpen mImgClipOpen;

	public UzImgClip(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_open(UZModuleContext moduleContext) {
		if (mImgClipOpen == null) {
			mImgClipOpen = new ImgClipOpen(mContext, moduleContext, this);
			mImgClipOpen.open();
		}
	}

	public void jsmethod_save(UZModuleContext moduleContext) {
		new ImgClipSave().save(mImgClipOpen, mContext, moduleContext, this);
	}

	public void jsmethod_reset(UZModuleContext moduleContext) {
		new ImgClipReset().reset(mImgClipOpen);
	}

	public void jsmethod_close(UZModuleContext moduleContext) {
		if (mImgClipOpen != null) {
			FrameLayout clipLayout = mImgClipOpen.getmClipLayout();
			mImgClipOpen = null;
			if (clipLayout != null) {
				removeViewFromCurWindow(clipLayout);
			}
		}
	}
}
