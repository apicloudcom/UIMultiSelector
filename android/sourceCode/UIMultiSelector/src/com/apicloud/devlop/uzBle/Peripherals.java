package com.apicloud.devlop.uzBle;

import android.bluetooth.BluetoothDevice;

public class Peripherals {
	private BluetoothDevice device;
	private int rssi;

	public Peripherals(BluetoothDevice device, int rssi) {
		this.device = device;
		this.rssi = rssi;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

}
