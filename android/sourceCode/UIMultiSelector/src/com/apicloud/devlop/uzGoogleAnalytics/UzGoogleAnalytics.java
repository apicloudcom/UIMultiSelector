package com.apicloud.devlop.uzGoogleAnalytics;

import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class UzGoogleAnalytics extends UZModule {

	private Tracker mTracker;

	public UzGoogleAnalytics(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_onPageStart(UZModuleContext moduleContext) {
		Tracker tracker = getTracker();
		tracker.send(MapBuilder.createEvent("page", "open",
				moduleContext.optString("pageName"), 0L).build());
	}

	public void jsmethod_onPageEnd(UZModuleContext moduleContext) {
		Tracker tracker = getTracker();
		tracker.send(MapBuilder.createEvent("page", "close",
				moduleContext.optString("pageName"), 0L).build());
	}

	public void jsmethod_onEvent(UZModuleContext moduleContext) {
		Tracker tracker = getTracker();
		tracker.send(MapBuilder.createEvent(
				moduleContext.optString("category"),
				moduleContext.optString("action"),
				moduleContext.optString("label"),
				moduleContext.optLong("value")).build());
	}

	public synchronized Tracker getTracker() {
		if (mTracker == null) {
			String id = getFeatureValue("googleAnalytics", "trackingID");
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
			analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
			Tracker tracker = analytics.newTracker(id);
			tracker.enableAdvertisingIdCollection(true);
			mTracker = tracker;
		}
		return mTracker;
	}
}
