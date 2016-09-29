package com.apicloud.devlop.uzAMapNavigation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * 语音播报组件
 */
public class TTSController implements SynthesizerListener, AMapNaviListener {

	public static TTSController ttsManager;
	boolean isfinish = true;
	private Context mContext;
	// 合成对象.
	private SpeechSynthesizer mSpeechSynthesizer;

	private InitListener initListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				Log.e("TTSController", "success");
			} else {
				Log.e("TTSController", "fail:" + code);
			}
		}
	};

	TTSController(Context context) {
		mContext = context;
	}
	
	private <WebViewClassic> void geti(){
		WebViewClassic w = null;
	}

	public static TTSController getInstance(Context context) {
		if (ttsManager == null) {
			ttsManager = new TTSController(context);
		}
		return ttsManager;
	}

	public void init() {
		initUser();
		// 初始化合成对象.
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext,
				initListener);
		initSpeechSynthesizer();
	}

	private void initUser() {
		StringBuffer param = new StringBuffer();
		param.append("appid=569f28eb");
		param.append(",");
		param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
		SpeechUtility.createUtility(mContext, param.toString());
	}

	/**
	 * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
	 * 
	 * @param
	 */
	public void playText(String playText) {
		if (!isfinish) {
			return;
		}
		if (null == mSpeechSynthesizer) {
			// 创建合成对象.
			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext,
					initListener);
			initSpeechSynthesizer();
		}
		// 进行语音合成.
		mSpeechSynthesizer.startSpeaking(playText, this);

	}

	public void stopSpeaking() {
		if (mSpeechSynthesizer != null)
			mSpeechSynthesizer.stopSpeaking();
	}

	public void startSpeaking() {
		isfinish = true;
	}

	private void initSpeechSynthesizer() {
		// 设置发音人
		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		// 设置语速
		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "tts_speed");
		// 设置音量
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");
		// 设置语调
		mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, "tts_pitch");

	}

	@Override
	public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {

	}

	@Override
	public void onCompleted(SpeechError arg0) {

		isfinish = true;
	}

	@Override
	public void onSpeakBegin() {

		isfinish = false;

	}

	@Override
	public void onSpeakPaused() {

	}

	@Override
	public void onSpeakProgress(int arg0, int arg1, int arg2) {

	}

	@Override
	public void onSpeakResumed() {

	}

	public void destroy() {
		if (mSpeechSynthesizer != null) {
			mSpeechSynthesizer.stopSpeaking();
		}
	}

	@Override
	public void onArriveDestination() {

		this.playText("到达目的地");
	}

	@Override
	public void onArrivedWayPoint(int arg0) {

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		this.playText("路径计算失败，请检查网络或输入参数");
	}

	@Override
	public void onCalculateRouteSuccess() {
		String calculateResult = "路径计算就绪";

		this.playText(calculateResult);
	}

	@Override
	public void onEndEmulatorNavi() {
		this.playText("导航结束");

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {

		this.playText(arg1);
	}

	@Override
	public void onInitNaviFailure() {

	}

	@Override
	public void onInitNaviSuccess() {

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {

		this.playText("前方路线拥堵，路线重新规划");
	}

	@Override
	public void onReCalculateRouteForYaw() {

		this.playText("您已偏航");
	}

	@Override
	public void onStartNavi(int arg0) {

	}

	@Override
	public void onTrafficStatusUpdate() {

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {

	}

	@Override
	public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

	}

	@Override
	public void showCross(AMapNaviCross aMapNaviCross) {

	}

	@Override
	public void hideCross() {

	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes,
			byte[] bytes1) {

	}

	@Override
	public void hideLaneInfo() {

	}

	@Override
	public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

	}
}
