package com.bugull.simple.ble.ble.bean;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;


/**
 * 搜索结果统一结果
 *
 * Created by leaf on 2017/12/22.
 */

public class XScanResult {
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;
    private ScanResult scanResult; // 大于 5.0
    private int callbackType; // 大于 5.0
    private int errorCode; // 大于 5.0
    private BluetoothDevice bluetoothDevice;
    private int rssi;
    private byte[] scanRecord;

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }

        if (obj instanceof XScanResult){
            XScanResult xScanResult = (XScanResult)obj;
            return address!=null && address.equals(xScanResult.getAddress());
        }
        return false;

    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public int getCallbackType() {
        return callbackType;
    }

    public void setCallbackType(int callbackType) {
        this.callbackType = callbackType;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    @Override
    public String toString() {
        return "XScanResult{" +
                "address=" + address +
                '}';
    }
}
