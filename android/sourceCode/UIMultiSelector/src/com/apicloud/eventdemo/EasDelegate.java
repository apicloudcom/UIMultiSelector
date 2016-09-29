package com.apicloud.eventdemo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.uzmap.pkg.uzcore.uzmodule.ApplicationDelegate;

public class EasDelegate extends ApplicationDelegate {

	/**
	 * 继承自ApplicationDelegate的类，APICloud引擎在应用初始化之初就会将该类初始化一次（即new一个出来），并保持引用，
	 * 在应用整个运行期间，会将生命周期事件通过该对象通知出来。<br>
	 * 该类需要在module.json中配置，其中name字段可以任意配置，因为该字段将被忽略，请参考module.json中EasDelegate的配置
	 */
	public EasDelegate() {
		Log.d("APICloud", "EasDelegate : instance");
		//应用运行期间，该对象只会初始化一个出来
	}

	@Override
	public void onApplicationCreate(Context context) {
		Log.d("APICloud", "EasDelegate : onApplicationCreate");
		//请在这个函数中初始化模块中需要在ApplicationCreate中初始化的东西
	}

	@Override
	public void onActivityResume(Activity activity) {
		Log.d("APICloud", "EasDelegate : onActivityResume");
	}

	@Override
	public void onActivityPause(Activity activity) {
		Log.d("APICloud", "EasDelegate : onActivityPause");
	}

	@Override
	public void onActivityFinish(Activity activity) {
		Log.d("APICloud", "EasDelegate : onActivityFinish");
	}

}
