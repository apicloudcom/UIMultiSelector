package com.apicloud.devlop.uzBle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import com.broadcom.bt.gatt.BluetoothGatt;
import com.broadcom.bt.gatt.BluetoothGattAdapter;
import com.broadcom.bt.gatt.BluetoothGattCallback;
import com.broadcom.bt.gatt.BluetoothGattCharacteristic;
import com.broadcom.bt.gatt.BluetoothGattDescriptor;
import com.broadcom.bt.gatt.BluetoothGattService;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class BroadcomBle implements IBle {
	public static final UUID DESC_CCC = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	private BluetoothAdapter mBtAdapter;
	private BleCallBack mBleCallBack;
	private BluetoothGatt mBluetoothGatt;
	private boolean mIsScanning;
	private List<String> mAdress;

	public BroadcomBle(BleCallBack bleCallBack, Context context) {
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothGattAdapter.getProfileProxy(context, mProfileServiceListener,
				BluetoothGattAdapter.GATT);
		mAdress = new ArrayList<String>();
	}

	@Override
	public void scan(UUID[] uuids) {
		if (mBluetoothGatt != null) {
			if (uuids != null) {
				mBluetoothGatt.startScan(uuids);
			} else {
				mBluetoothGatt.startScan();
			}
			mIsScanning = true;
		}
	}

	private final BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {

		@Override
		public void onScanResult(BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			mBleCallBack.scanCallBack(new Peripherals(device, rssi));
		}

		@Override
		public void onConnectionStateChange(BluetoothDevice device, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				if (mAdress.contains(device.getAddress())) {
					mBleCallBack.connectsCallBack(device, true);
				} else {
					mBleCallBack.connectCallBack(device, true, 0);
				}
			} else {
				if (mAdress.contains(device.getAddress())) {
					mBleCallBack.connectsCallBack(device, false);
				}
			}
		}
	};

	@SuppressLint("NewApi")
	private final BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
		@Override
		public void onServiceConnected(int profile, BluetoothProfile proxy) {
			mBluetoothGatt = (BluetoothGatt) proxy;
			mBluetoothGatt.registerApp(mGattCallbacks);
		}

		@Override
		public void onServiceDisconnected(int profile) {
			for (BluetoothDevice d : mBluetoothGatt.getConnectedDevices()) {
				mBluetoothGatt.cancelConnection(d);
			}
			mBluetoothGatt = null;
		}
	};

	public boolean isScanning() {
		return mIsScanning;
	}

	@Override
	public void stopScan() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.stopScan();
			mIsScanning = false;
		}
	}

	@Override
	public boolean connect(String address) {
		if (address == null || address.length() == 0) {
			mBleCallBack.connectCallBack(null, false, 1);
			return false;
		}
		mAdress.remove(address);
		BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
		if (!mBluetoothGatt.connect(device, false)) {
			mBleCallBack.connectCallBack(null, false, -1);
			return false;
		}
		return true;
	}

	@Override
	public void connectPeripherals(JSONArray address) {
		if (address == null || address.length() == 0) {
			mBleCallBack.connectsCallBack(null, false);
			return;
		}
		for (int i = 0; i < address.length(); i++) {
			mAdress.add(address.optString(i));
			BluetoothDevice device = mBtAdapter.getRemoteDevice(address
					.optString(i));
			if (!mBluetoothGatt.connect(device, false)) {
				mBleCallBack.connectsCallBack(null, false);
			}
		}
	}

	@Override
	public void disconnect(String address) {
		BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
		if (device != null) {
			mBluetoothGatt.cancelConnection(device);
			mBleCallBack.disconnectCallBack(address, true);
		} else {
			mBleCallBack.disconnectCallBack(address, false);
		}
	}

	@Override
	public void isConnected(String address) {
		mBleCallBack.isConnectedCallBack(address);
	}

	@Override
	public void discoverCharacteristics(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID) {
		if (mBleCallBack
				.errCallBack(moduleContext, serviceUUID, peripheralUUID)) {
			return;
		}
		if (mBluetoothGatt != null) {
			BluetoothDevice device = mBtAdapter.getRemoteDevice(peripheralUUID);
			if (device == null) {
				mBleCallBack.errCallBack(moduleContext, 4);
				return;
			}
			BluetoothGattService service = mBluetoothGatt.getService(device,
					UUID.fromString(serviceUUID));
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
		}
	}

	@Override
	public void characteristicNotification(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID, String characteristicUUID) {
		BluetoothDevice device = mBtAdapter.getRemoteDevice(peripheralUUID);
		if (device == null) {
			mBleCallBack.errCallBack(moduleContext, 6);
			return;
		}
		BluetoothGattService service = mBluetoothGatt.getService(device,
				UUID.fromString(serviceUUID));
		if (service == null) {
			mBleCallBack.errCallBack(moduleContext, 5);
			return;
		}
		BluetoothGattCharacteristic characteristic = service
				.getCharacteristic(UUID.fromString(characteristicUUID));
		if (characteristic == null) {
			mBleCallBack.errCallBack(moduleContext, 4);
			return;
		}
		if (!mBluetoothGatt.setCharacteristicNotification(characteristic, true)) {
			return;
		}

		BluetoothGattDescriptor descriptor = characteristic
				.getDescriptor(DESC_CCC);
		if (descriptor == null) {
			return;
		}
		mBluetoothGatt.readDescriptor(descriptor);
		successCallBack(moduleContext, characteristicUUID, serviceUUID,
				characteristic);
	}

	@Override
	public void readValueForCharacteristic(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID, String characteristicUUID) {
		BluetoothDevice device = mBtAdapter.getRemoteDevice(peripheralUUID);
		if (device == null) {
			mBleCallBack.errCallBack(moduleContext, 6);
			return;
		}
		BluetoothGattService service = mBluetoothGatt.getService(device,
				UUID.fromString(serviceUUID));
		if (service == null) {
			mBleCallBack.errCallBack(moduleContext, 5);
			return;
		}
		BluetoothGattCharacteristic characteristic = service
				.getCharacteristic(UUID.fromString(characteristicUUID));
		if (characteristic == null) {
			mBleCallBack.errCallBack(moduleContext, 4);
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
		successCallBack(moduleContext, characteristicUUID, serviceUUID,
				characteristic);
	}

	@Override
	public void writeValueForCharacteristic(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID,
			String characteristicUUID, String value) {
		BluetoothDevice device = mBtAdapter.getRemoteDevice(peripheralUUID);
		if (device == null) {
			mBleCallBack.errCallBack(moduleContext, 7);
			return;
		}
		BluetoothGattService service = mBluetoothGatt.getService(device,
				UUID.fromString(serviceUUID));
		if (service == null) {
			mBleCallBack.errCallBack(moduleContext, 6);
			return;
		}
		BluetoothGattCharacteristic characteristic = service
				.getCharacteristic(UUID.fromString(characteristicUUID));
		try {
			if (characteristic != null) {
				characteristic.setValue(Hex.decodeHex(value.toCharArray()));
				mBluetoothGatt.writeCharacteristic(characteristic);
				successCallBack(moduleContext, characteristicUUID, serviceUUID,
						characteristic);
			} else {
				mBleCallBack.errCallBack(moduleContext, 5);
			}
		} catch (DecoderException e) {
			e.printStackTrace();
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
