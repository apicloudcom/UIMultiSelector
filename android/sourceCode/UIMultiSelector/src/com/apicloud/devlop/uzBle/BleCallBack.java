package com.apicloud.devlop.uzBle;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

@SuppressLint("NewApi")
public class BleCallBack {
	private UZModuleContext mConnectModuleContext;
	private UZModuleContext mConnectsModuleContext;
	private UZModuleContext mDisconnectModuleContext;
	private UZModuleContext mIsconnectedModuleContext;
	private Map<String, Peripherals> mDeviceMap;
	private Map<String, BluetoothDevice> mConnectedDeviceMap;

	public BleCallBack() {
		if (mDeviceMap == null)
			mDeviceMap = new HashMap<String, Peripherals>();
		if (mConnectedDeviceMap == null)
			mConnectedDeviceMap = new HashMap<String, BluetoothDevice>();
	}

	public void scanCallBack(Peripherals peripherals) {
		mDeviceMap.put(peripherals.getDevice().getAddress(), peripherals);
	}

	@SuppressLint("NewApi")
	public void getPeripheralCallBack(UZModuleContext moduleContext) {
		JSONObject ret = new JSONObject();
		JSONArray peripherals = new JSONArray();
		try {
			ret.put("peripherals", peripherals);
			for (Map.Entry<String, Peripherals> entry : mDeviceMap.entrySet()) {
				JSONObject peripheral = new JSONObject();
				Peripherals p = entry.getValue();
				peripheral.put("uuid", p.getDevice().getAddress());
				peripheral.put("name", p.getDevice().getName());
				peripheral.put("rssi", p.getRssi());
				ParcelUuid[] parcelUuids = p.getDevice().getUuids();
				JSONArray uuids = new JSONArray();
				if (parcelUuids != null) {
					for (ParcelUuid parcelUuid : parcelUuids) {
						uuids.put(parcelUuid.getUuid());
					}
				}
				peripheral.put("services", uuids);
				peripherals.put(peripheral);
			}
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void connectCallBack(BluetoothDevice device, boolean status, int code) {
		if (status) {
			mConnectedDeviceMap.put(device.getAddress(), device);
		}
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			ret.put("status", status);
			if (!status) {
				err.put("code", code);
			}
			mConnectModuleContext.error(ret, err, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void connectsCallBack(BluetoothDevice device, boolean status) {
		if (status) {
			mConnectedDeviceMap.put(device.getAddress(), device);
		}
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", status);
			ret.put("peripheralUUID", device.getAddress());
			mConnectsModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void disconnectCallBack(String address, boolean status) {
		JSONObject ret = new JSONObject();
		try {
			if (status)
				mConnectedDeviceMap.remove(address);
			ret.put("status", status);
			mDisconnectModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void isConnectedCallBack(boolean status) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", status);
			mIsconnectedModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void isConnectedCallBack(String address) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", mConnectedDeviceMap.containsKey(address));
			mIsconnectedModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void errCallBack(UZModuleContext moduleContext, int code) {
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			ret.put("status", false);
			err.put("code", code);
			moduleContext.error(ret, err, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public boolean errCallBack(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID) {
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			ret.put("status", false);
			if (peripheralUUID == null || peripheralUUID.length() == 0) {
				err.put("code", 1);
				moduleContext.error(ret, err, false);
				return true;
			} else if (serviceUUID == null || serviceUUID.length() == 0) {
				err.put("code", 2);
				moduleContext.error(ret, err, false);
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean errCallBack(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID, String characteristicUUID) {
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			ret.put("status", false);
			if (peripheralUUID == null || peripheralUUID.length() == 0) {
				err.put("code", 1);
				moduleContext.error(ret, err, false);
				return true;
			} else if (serviceUUID == null || serviceUUID.length() == 0) {
				err.put("code", 2);
				moduleContext.error(ret, err, false);
				return true;
			} else if (characteristicUUID == null
					|| characteristicUUID.length() == 0) {
				err.put("code", 3);
				moduleContext.error(ret, err, false);
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean errCallBack(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID,
			String characteristicUUID, String value) {
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			ret.put("status", false);
			if (peripheralUUID == null || peripheralUUID.length() == 0) {
				err.put("code", 1);
				moduleContext.error(ret, err, false);
				return true;
			} else if (serviceUUID == null || serviceUUID.length() == 0) {
				err.put("code", 2);
				moduleContext.error(ret, err, false);
				return true;
			} else if (characteristicUUID == null
					|| characteristicUUID.length() == 0) {
				err.put("code", 3);
				moduleContext.error(ret, err, false);
				return true;
			} else if (value == null || value.length() == 0) {
				err.put("code", 5);
				moduleContext.error(ret, err, false);
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setConnectModuleContext(UZModuleContext connectModuleContext) {
		this.mConnectModuleContext = connectModuleContext;
	}

	public void setConnectsModuleContext(UZModuleContext mConnectsModuleContext) {
		this.mConnectsModuleContext = mConnectsModuleContext;
	}

	public void setDisconnectModuleContext(
			UZModuleContext disconnectModuleContext) {
		this.mDisconnectModuleContext = disconnectModuleContext;
	}

	public void setIsConnectModuleContext(UZModuleContext isConnectModuleContext) {
		this.mIsconnectedModuleContext = isConnectModuleContext;
	}
}
