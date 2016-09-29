package com.apicloud.devlop.uzBle;

import java.util.UUID;
import org.json.JSONArray;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public interface IBle {
	public void scan(UUID[] uuids);

	public void stopScan();

	public boolean isScanning();

	public boolean connect(String address);
	
	public void connectPeripherals(JSONArray address);

	public void disconnect(String address);

	public void isConnected(String address);

	public void discoverCharacteristics(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID);

	public void characteristicNotification(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID, String characteristicUUID);

	public void readValueForCharacteristic(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID, String characteristicUUID);

	public void writeValueForCharacteristic(UZModuleContext moduleContext,
			String serviceUUID, String peripheralUUID,
			String characteristicUUID, String value);
}
