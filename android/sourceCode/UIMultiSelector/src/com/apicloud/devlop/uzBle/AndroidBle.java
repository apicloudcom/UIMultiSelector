package com.apicloud.devlop.uzBle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

@SuppressLint("NewApi")
public class AndroidBle implements IBle {
	public static final UUID DESC_CCC = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	private BluetoothAdapter mBluetoothAdapter;
	private BleCallBack mBleCallBack;
	private Map<String, Peripherals> mDeviceMap;
	private Map<String, BluetoothGatt> mBluetoothGatts;
	private boolean mIsScanning;
	private Context mContext;

	public AndroidBle(Context context, BluetoothAdapter mBluetoothAdapter,
			BleCallBack bleCallBack) {
		this.mContext = context;
		this.mBluetoothAdapter = mBluetoothAdapter;
		this.mBleCallBack = bleCallBack;
		mDeviceMap = new HashMap<String, Peripherals>();
		mBluetoothGatts = new HashMap<String, BluetoothGatt>();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void scan(UUID[] uuids) {
		if (mBluetoothAdapter != null) {
			if (uuids != null) {
				mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
			} else {
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			}
			mIsScanning = true;
		}
	}

	private LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			mDeviceMap.put(device.getAddress(), new Peripherals(device, rssi));
		}
	};

	public boolean isScanning() {
		return mIsScanning;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void stopScan() {
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mIsScanning = false;
		}
	}

	@Override
	public boolean connect(String address) {
		if (address == null || address.length() == 0) {
			mBleCallBack.connectCallBack(null, false, 1);
			return false;
		}
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		BluetoothGatt gatt = device.connectGatt(mContext, false, mGattCallback);
		if (gatt == null) {
			mBleCallBack.connectCallBack(null, false, -1);
			mBluetoothGatts.remove(address);
			return false;
		} else {
			mBluetoothGatts.put(address, gatt);
			return true;
		}
	}

	@Override
	public void connectPeripherals(JSONArray address) {
		if (address == null || address.length() == 0) {
			mBleCallBack.connectsCallBack(null, false);
			return;
		}
		for (int i = 0; i < address.length(); i++) {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address
					.optString(i));
			BluetoothGatt gatt = device.connectGatt(mContext, false,
					mGattsCallback);
			if (gatt == null) {
				mBleCallBack.connectCallBack(null, false, -1);
				mBluetoothGatts.remove(address.optString(i));
			} else {
				mBluetoothGatts.put(address.optString(i), gatt);
			}
		}
	}

	private BluetoothGattCallback mGattsCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				mBleCallBack.connectsCallBack(gatt.getDevice(), true);
			} else {
				mBleCallBack.connectsCallBack(gatt.getDevice(), false);
			}
		}

	};

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				mBleCallBack.connectCallBack(gatt.getDevice(), true, 0);
			}
		}

	};

	@Override
	public void disconnect(String address) {
		if (mBluetoothGatts.containsKey(address)) {
			BluetoothGatt gatt = mBluetoothGatts.remove(address);
			if (gatt != null) {
				gatt.disconnect();
				gatt.close();
			}
			mBleCallBack.disconnectCallBack(address, true);
		} else {
			mBleCallBack.disconnectCallBack(address, false);
		}
	}

	@Override
	public void isConnected(String address) {
		mBleCallBack.isConnectedCallBack(mBluetoothGatts.containsKey(address));
	}

	@Override
	public void discoverCharacteristics(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID) {
		if (mBleCallBack
				.errCallBack(moduleContext, serviceUUID, peripheralUUID)) {
			return;
		}
		BluetoothGatt gatt = mBluetoothGatts.get(peripheralUUID);
		if (gatt != null) {
			BluetoothGattService service = gatt.getService(UUID
					.fromString(serviceUUID));
			if (service != null) {
				List<BluetoothGattCharacteristic> list = service
						.getCharacteristics();
				if (list != null) {
					JSONObject ret = new JSONObject();
					JSONArray characteristics = new JSONArray();
					try {
						ret.put("status", true);
						ret.put("characteristics", characteristics);
						for (BluetoothGattCharacteristic b : list) {
							JSONObject item = new JSONObject();
							item.put("uuid", b.getUuid());
							item.put("serviceUUID", serviceUUID);
							item.put("value", b.getValue().toString());
							item.put("permissions", b.getPermissions());
							item.put("propertie", b.getProperties());
							characteristics.put(item);
						}
						moduleContext.success(ret, false);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				mBleCallBack.errCallBack(moduleContext, 3);
			}
		} else {
			mBleCallBack.errCallBack(moduleContext, 4);
		}
	}

	@Override
	public void characteristicNotification(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID, String characteristicUUID) {
		if (mBleCallBack.errCallBack(moduleContext, serviceUUID,
				peripheralUUID, characteristicUUID)) {
			return;
		}
		BluetoothGatt gatt = mBluetoothGatts.get(peripheralUUID);
		if (gatt != null) {
			BluetoothGattService service = gatt.getService(UUID
					.fromString(serviceUUID));
			if (service != null) {
				BluetoothGattCharacteristic characteristic = service
						.getCharacteristic(UUID.fromString(characteristicUUID));
				if (characteristic != null) {
					gatt.setCharacteristicNotification(characteristic, true);
					BluetoothGattDescriptor descriptor = characteristic
							.getDescriptor(DESC_CCC);
					descriptor
							.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					gatt.writeDescriptor(descriptor);
					successCallBack(moduleContext, characteristicUUID,
							serviceUUID, characteristic);
				} else {
					mBleCallBack.errCallBack(moduleContext, 4);
				}
			} else {
				mBleCallBack.errCallBack(moduleContext, 5);
			}
		} else {
			mBleCallBack.errCallBack(moduleContext, 6);
		}
	}

	@Override
	public void readValueForCharacteristic(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID, String characteristicUUID) {
		if (mBleCallBack.errCallBack(moduleContext, serviceUUID,
				peripheralUUID, characteristicUUID)) {
			return;
		}
		BluetoothGatt gatt = mBluetoothGatts.get(peripheralUUID);
		if (gatt != null) {
			BluetoothGattService service = gatt.getService(UUID
					.fromString(serviceUUID));
			if (service != null) {
				BluetoothGattCharacteristic characteristic = service
						.getCharacteristic(UUID.fromString(characteristicUUID));
				if (characteristic != null) {
					gatt.readCharacteristic(characteristic);
					successCallBack(moduleContext, characteristicUUID,
							serviceUUID, characteristic);
				} else {
					mBleCallBack.errCallBack(moduleContext, 4);
				}
			} else {
				mBleCallBack.errCallBack(moduleContext, 5);
			}
		} else {
			mBleCallBack.errCallBack(moduleContext, 6);
		}
	}

	@Override
	public void writeValueForCharacteristic(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID,
			String characteristicUUID, String value) {
		if (mBleCallBack.errCallBack(moduleContext, serviceUUID,
				peripheralUUID, characteristicUUID, value)) {
			return;
		}
		BluetoothGatt gatt = mBluetoothGatts.get(peripheralUUID);
		if (gatt != null) {
			BluetoothGattService service = gatt.getService(UUID
					.fromString(serviceUUID));
			if (service != null) {
				BluetoothGattCharacteristic characteristic = service
						.getCharacteristic(UUID.fromString(characteristicUUID));
				try {
					if (characteristic != null) {
						characteristic.setValue(Hex.decodeHex(value
								.toCharArray()));
						gatt.writeCharacteristic(characteristic);
						successCallBack(moduleContext, characteristicUUID,
								serviceUUID, characteristic);
					} else {
						mBleCallBack.errCallBack(moduleContext, 5);
					}
				} catch (DecoderException e) {
					e.printStackTrace();
				}
			} else {
				mBleCallBack.errCallBack(moduleContext, 6);
			}
		} else {
			mBleCallBack.errCallBack(moduleContext, 7);
		}
	}

	private void successCallBack(UZModuleContext moduleContext, String uuid,
			String serviceId, BluetoothGattCharacteristic characteristic) {
		JSONObject ret = new JSONObject();
		JSONObject characteristics = new JSONObject();
		try {
			ret.put("status", true);
			ret.put("characteristics", characteristics);
			characteristics.put("uuid", uuid);
			characteristics.put("serviceUUID", serviceId);
			characteristics.put("value", characteristic.getValue().toString());
			characteristics.put("permissions", characteristic.getPermissions());
			characteristics.put("propertie", characteristic.getProperties());
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
