package com.apicloud.devlop.uzBle;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

@SuppressLint("NewApi")
public class UzBle extends UZModule {
	private IBle mIBle;
	private BluetoothAdapter mBluetoothAdapter;
	private BleCallBack mBleCallBack;

	public UzBle(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_initManager(UZModuleContext moduleContext) {
		if (!isBlePermission()) {
			initCallBack(moduleContext, "unauthorized");
		} else if (!isBleSupported()) {
			initCallBack(moduleContext, "unsupported");
		} else {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			switch (mBluetoothAdapter.getState()) {
			case BluetoothAdapter.STATE_OFF:
				initCallBack(moduleContext, "poweredOff");
				break;
			case BluetoothAdapter.STATE_ON:
				initCallBack(moduleContext, "poweredOn");
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
			case BluetoothAdapter.STATE_TURNING_ON:
			case BluetoothAdapter.STATE_CONNECTING:
			case BluetoothAdapter.STATE_DISCONNECTING:
				initCallBack(moduleContext, "resetting");
				break;
			default:
				initCallBack(moduleContext, "unknown");
				break;
			}
		}
	}

	public void jsmethod_scan(UZModuleContext moduleContext) {
		BLESDK sdk = getBleSDK();
		if (mBleCallBack == null) {
			mBleCallBack = new BleCallBack();
		}
		if (mIBle == null) {
			if (sdk == BLESDK.ANDROID) {
				mIBle = new AndroidBle(mContext, mBluetoothAdapter,
						mBleCallBack);
			} else if (sdk == BLESDK.SAMSUNG) {
				mIBle = new SamsungBle(mBleCallBack, mContext);
			} else if (sdk == BLESDK.BROADCOM) {
				mIBle = new BroadcomBle(mBleCallBack, mContext);
			} else {
				scanCallBack(moduleContext, false);
				return;
			}
		}
		JsParamsUtil jsParamsUtil = JsParamsUtil.getInstance();
		mIBle.scan(jsParamsUtil.getUUIDS(moduleContext));
	}

	public void jsmethod_stopScan(UZModuleContext moduleContext) {
		if (mIBle != null) {
			mIBle.stopScan();
		}
	}

	public void jsmethod_isScanning(UZModuleContext moduleContext) {
		isScanningCallBack(moduleContext);
	}

	public void jsmethod_getPeripheral(UZModuleContext moduleContext) {
		if (mBleCallBack != null) {
			mBleCallBack.getPeripheralCallBack(moduleContext);
		}
	}

	public void jsmethod_connect(UZModuleContext moduleContext) {
		if (mBleCallBack != null) {
			mBleCallBack.setConnectModuleContext(moduleContext);
		}
		if (mIBle != null) {
			mIBle.connect(moduleContext.optString("peripheralUUID"));
		} else {
			if (mBleCallBack != null) {
				mBleCallBack.connectCallBack(null, false, 2);
			}
		}
	}
	
	public void jsmethod_connectPeripherals(UZModuleContext moduleContext) {
		if (mBleCallBack != null) {
			mBleCallBack.setConnectModuleContext(moduleContext);
		}
		if (mIBle != null) {
			JSONArray peripheralUUIDs = moduleContext.optJSONArray("peripheralUUIDs");
			mIBle.connectPeripherals(peripheralUUIDs);
		}
	}

	public void jsmethod_disconnect(UZModuleContext moduleContext) {
		if (mBleCallBack != null) {
			mBleCallBack.setDisconnectModuleContext(moduleContext);
		}
		if (mIBle != null) {
			mIBle.disconnect(moduleContext.optString("peripheralUUID"));
		} else {
			if (mBleCallBack != null) {
				mBleCallBack.disconnectCallBack(null, false);
			}
		}
	}

	public void jsmethod_isConnected(UZModuleContext moduleContext) {
		if (mIBle != null) {
			mBleCallBack.setIsConnectModuleContext(moduleContext);
			mIBle.isConnected(moduleContext.optString("peripheralUUID"));
		}
	}

	public void jsmethod_discoverCharacteristics(UZModuleContext moduleContext) {
		if (mIBle != null) {
			mIBle.discoverCharacteristics(moduleContext,
					moduleContext.optString("serviceUUID"),
					moduleContext.optString("peripheralUUID"));
		}
	}

	public void jsmethod_setNotify(UZModuleContext moduleContext) {
		if (mIBle != null) {
			mIBle.characteristicNotification(moduleContext,
					moduleContext.optString("serviceUUID"),
					moduleContext.optString("peripheralUUID"),
					moduleContext.optString("characteristicUUID"));
		}
	}

	public void jsmethod_readValueForCharacteristic(
			UZModuleContext moduleContext) {
		if (mIBle != null) {
			mIBle.readValueForCharacteristic(moduleContext,
					moduleContext.optString("serviceUUID"),
					moduleContext.optString("peripheralUUID"),
					moduleContext.optString("characteristicUUID"));
		}
	}

	public void jsmethod_writeValueForCharacteristic(
			UZModuleContext moduleContext) {
		if (mIBle != null) {
			mIBle.writeValueForCharacteristic(moduleContext,
					moduleContext.optString("serviceUUID"),
					moduleContext.optString("peripheralUUID"),
					moduleContext.optString("characteristicUUID"),
					moduleContext.optString("value"));
		}
	}

	private boolean isBlePermission() {
		PackageManager pm = mContext.getPackageManager();
		boolean permission = (PackageManager.PERMISSION_GRANTED == pm
				.checkPermission("android.permission.BLUETOOTH",
						mContext.getPackageName()));
		return permission;
	}

	private boolean isBleSupported() {
		if (mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			// android 4.3
			return true;
		}

		ArrayList<String> libraries = new ArrayList<String>();
		for (String i : mContext.getPackageManager()
				.getSystemSharedLibraryNames()) {
			libraries.add(i);
		}

		if (android.os.Build.VERSION.SDK_INT >= 17) {
			// android 4.2.2
			if (libraries.contains("com.samsung.android.sdk.bt")) {
				return true;
			} else if (libraries.contains("com.broadcom.bt")) {
				return true;
			}
		}
		return false;
	}

	private void isScanningCallBack(UZModuleContext moduleContext) {
		JSONObject ret = new JSONObject();
		try {
			if (mIBle != null) {
				ret.put("status", mIBle.isScanning());
			} else {
				ret.put("status", false);
			}
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initCallBack(UZModuleContext moduleContext, String state) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("state", state);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void scanCallBack(UZModuleContext moduleContext, boolean status) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", status);
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private BLESDK getBleSDK() {
		if (mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			// android 4.3
			return BLESDK.ANDROID;
		}

		ArrayList<String> libraries = new ArrayList<String>();
		for (String i : mContext.getPackageManager()
				.getSystemSharedLibraryNames()) {
			libraries.add(i);
		}

		if (android.os.Build.VERSION.SDK_INT >= 17) {
			// android 4.2.2
			if (libraries.contains("com.samsung.android.sdk.bt")) {
				return BLESDK.SAMSUNG;
			} else if (libraries.contains("com.broadcom.bt")) {
				return BLESDK.BROADCOM;
			}
		}
		return BLESDK.NOT_SUPPORTED;
	}

	public enum BLESDK {
		NOT_SUPPORTED, ANDROID, SAMSUNG, BROADCOM
	}
}
