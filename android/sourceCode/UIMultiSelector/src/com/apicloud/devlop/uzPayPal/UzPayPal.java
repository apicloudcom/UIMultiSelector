package com.apicloud.devlop.uzPayPal;

import java.math.BigDecimal;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class UzPayPal extends UZModule {

	private static final int REQUEST_CODE_PAYMENT = 1;
	private UZModuleContext mModuleContext;
	private boolean mIsStartSerivce = false;

	public UzPayPal(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_pay(UZModuleContext moduleContext) {
		mModuleContext = moduleContext;
		PayPalConfiguration config = initConfig(moduleContext);
		if (!mIsStartSerivce)
			startService(config);
		pay(moduleContext, config);
	}

	private void pay(UZModuleContext moduleContext, PayPalConfiguration config) {
		String currency = moduleContext.optString("currency");
		String description = moduleContext.optString("description");
		String price = moduleContext.optString("price");
		PayPalPayment thingToBuy = getThingToBuy(price, currency, description);
		Intent intent = new Intent(mContext, PaymentActivity.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	}

	private PayPalPayment getThingToBuy(String price, String currency,
			String description) {
		return new PayPalPayment(new BigDecimal(price), currency, description,
				PayPalPayment.PAYMENT_INTENT_SALE);
	}

	private PayPalConfiguration initConfig(UZModuleContext moduleContext) {
		String mode = moduleContext.optString("mode");
		if (mode.equals("production")) {
			mode = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
		} else if (mode.equals("sandbox")) {
			mode = PayPalConfiguration.ENVIRONMENT_SANDBOX;
		} else {
			mode = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
		}
		PayPalConfiguration config = new PayPalConfiguration()
				.environment(mode).clientId(getClientId(mode));
		return config;
	}

	private String getClientId(String mode) {
		if (mode == PayPalConfiguration.ENVIRONMENT_PRODUCTION) {
			String productionId = getSecureValue("paypal_productionID");
			return productionId;
		} else {
			String sandboxId = getSecureValue("paypal_sandboxID");
			return sandboxId;
		}
	}

	private void startService(PayPalConfiguration config) {
		Intent intent = new Intent(mContext, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		mContext.startService(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			PaymentConfirmation confirm = data
					.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
			if (confirm != null) {
				callBack(mModuleContext, confirm.toJSONObject(), "success");
			} else {
				callBack(mModuleContext, null, "fail");
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			callBack(mModuleContext, null, "cancel");
		} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
			callBack(mModuleContext, null, "fail");
		}
	}

	private void callBack(UZModuleContext moduleContext, JSONObject result,
			String state) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("state", state);
			if (state.equals("success")) {
				ret.put("client", result.optJSONObject("client"));
				ret.put("response", result.optJSONObject("response"));
				ret.put("response_type", result.optString("response_type"));
			}
			if (moduleContext != null)
				moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
