package com.bugull.simple.ble.ble.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bugull.simple.ble.ble.bean.BleStatus;
import com.bugull.simple.ble.ble.common.Constant;
import com.bugull.simple.ble.ble.bean.XScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 低功耗蓝牙BLE 搜索工具
 *
 * Created by leaf on 2017/12/25.
 */


public class Scan implements IScan {
    private final static String TAG = "Scan" + Constant.TAG;
    private boolean isScanning = false; // 是否正在搜索
    private long timeout = 10 * 1000L;  // 搜索持续时间
    private long interval = 2 * 1000L; // 搜索间隔
    private ScanSettings mScanSettings;    // 搜索设置
    private List<ScanFilter> mScanFilters;  // 搜索过滤
    private List<XScanResult> xScanResultList; // 搜索的结果集合
    private List<IScanResultFilter> scanResultFilters; //  搜索结果过滤
    private IScanResultCallback scanResultCallback; // 搜索回调

    {
        scanResultFilters = new ArrayList<>();
        mScanFilters = new ArrayList<>();
        xScanResultList = new ArrayList<>();
    }

    private Scan(Builder builder) {
        this.timeout = builder.timeout;
        this.interval = builder.interval;
        this.mScanSettings = builder.scanSettings;
        this.mScanFilters = builder.scanFilters;
        this.scanResultFilters = builder.scanResultFilters;
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (scanResultCallback != null) {
                XScanResult xScanResult = new XScanResult();
                xScanResult.setCallbackType(callbackType);
                xScanResult.setScanResult(result);
                xScanResult.setAddress(result.getDevice().getAddress());
                scanResultCallback.onScan(xScanResult, true);

                if (!xScanResultList.contains(xScanResult)) {
                    xScanResultList.add(xScanResult);
                }
            }

            if (Constant.DEBUG) {
                String msg = "------------------onScanResult----------------------" + "\n";
                msg += "callbackType = " + callbackType + "\n";
                msg += "ScanResult = " + result + "\n";
                Log.i(TAG, msg);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            if (Constant.DEBUG) {
                String msg = "-------------------onBatchScanResults---------------------" + "\n";
                msg += "results = " + results + "\n";
                Log.i(TAG, msg);
            }

            if (scanResultCallback != null) {
                if (results != null && results.size() > 0) {
                    xScanResultList.clear();
                    for (int i = 0; i < results.size(); i++) {
                        XScanResult xScanResult = new XScanResult();
                        ScanResult scanResult = results.get(i);
                        xScanResult.setAddress(scanResult.getDevice().getAddress());
                        xScanResult.setScanResult(scanResult);
                        if (!xScanResultList.contains(xScanResult)) {
                            xScanResultList.add(xScanResult);
                        }
                    }
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            if (Constant.DEBUG) {
                String msg = "-----------------onScanFailed-----------------------" + "\n";
                msg += "errorCode = " + errorCode + "\n";
                Log.i(TAG, msg);
            }

            if (scanResultCallback != null) {
                scanResultCallback.error(errorCode);
            }
        }
    };

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (Constant.DEBUG) {
                String msg = "-----------------onLeScan-----------------------" + "\n";
                msg += "device = " + device + "\n";
                msg += "rssi = " + rssi + "\n";
                msg += "scanRecord = " + new String(scanRecord) + "\n";
                Log.i(TAG, msg);
            }

            if (scanResultCallback != null) {
                XScanResult xScanResult = new XScanResult();
                xScanResult.setBluetoothDevice(device);
                xScanResult.setAddress(device.getAddress());
                xScanResult.setRssi(rssi);
                xScanResult.setScanRecord(scanRecord);
                scanResultCallback.onScan(xScanResult, false);


                if (!xScanResultList.contains(xScanResult)) {
                    xScanResultList.add(xScanResult);
                }
            }


        }
    };


    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            loggerI("timeoutRunnable");
            stopScan();
            if (scanResultCallback != null) {
                // 统计搜索到的设备
                scanResultCallback.onFinish(getResult());
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startScan(scanResultCallback);


        }
    };

    private List<XScanResult> getResult() {
        loggerV("[getResult] start = "+xScanResultList.size());
        synchronized (xScanResultList) {
            if (scanResultFilters.isEmpty()) {
                return xScanResultList;
            }
            for (int i = 0; i < scanResultFilters.size(); i++) {
                IScanResultFilter iScanResultFilter = scanResultFilters.get(i);
                xScanResultList = iScanResultFilter.filter(xScanResultList);
                loggerV("[getResult] end = "+xScanResultList.size());
            }

            return xScanResultList;
        }
    }

    // 2017 12 25 先不考虑搜索条件
    @Override
    public void startScan(IScanResultCallback scanResultCallback) {

        this.scanResultCallback = scanResultCallback;

        if (!isScanning) {

            xScanResultList.clear();

            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter == null) {
                if (scanResultCallback != null) {
                    scanResultCallback.error(BleStatus.ERROR_BLE_NOT_SUPPORTED);
                }
                throw new NullPointerException("BluetoothAdapter is null");
            }
            if (!defaultAdapter.isEnabled()) {
                // 提示打开蓝牙
                if (scanResultCallback != null) {
                    scanResultCallback.error(BleStatus.ERROR_BLE_DISABLE);
                }
                return;
            }

            isScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BluetoothLeScanner bluetoothLeScanner = defaultAdapter.getBluetoothLeScanner();
                if (mScanSettings != null) {
                    bluetoothLeScanner.startScan(mScanFilters, mScanSettings, scanCallback);
                } else {
                    bluetoothLeScanner.startScan(scanCallback);
                }
            } else {
                defaultAdapter.startLeScan(leScanCallback);
            }
            mHandler.removeCallbacks(timeoutRunnable);
            mHandler.postDelayed(timeoutRunnable, timeout);
        }
    }

    @Override
    public void stopScan() {
        if (isScanning) {
            mHandler.removeCallbacks(timeoutRunnable);
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter == null) {
                if (scanResultCallback != null) {
                    scanResultCallback.error(BleStatus.ERROR_BLE_NOT_SUPPORTED);
                }
                throw new NullPointerException("BluetoothAdapter is null");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BluetoothLeScanner bluetoothLeScanner = defaultAdapter.getBluetoothLeScanner();
                bluetoothLeScanner.stopScan(scanCallback);
            } else {
                defaultAdapter.stopLeScan(leScanCallback);
            }

            isScanning = false;

//            if (scanResultCallback != null) {
//                scanResultCallback.onFinish(xScanResultList);
//            }
        }

    }


    private void loggerV(String msg) {
        if (Constant.DEBUG) {
            Log.v(TAG, msg);
        }
    }

    private void loggerI(String msg) {
        if (Constant.DEBUG) {
            Log.v(TAG, msg);
        }
    }

    private void loggerE(String msg) {
        if (Constant.DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public static class Builder {
        private long timeout;
        private long interval;
        private ScanSettings scanSettings;
        private List<ScanFilter> scanFilters;
        private List<IScanResultFilter> scanResultFilters;

        public Builder() {
        }

        public Builder scanTimeOut(long scanTimeout) {
            this.timeout = scanTimeout;
            return this;
        }

        public Builder scanInterval(long scanInterval) {
            this.interval = scanInterval;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public Builder scanSetting(ScanSettings scanSettings) {
            this.scanSettings = scanSettings;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public Builder scanFilters(List<ScanFilter> scanFilters) {
            this.scanFilters = scanFilters;
            return this;
        }

        public Builder scanResultFilters(List<IScanResultFilter> scanResultFilters) {
            this.scanResultFilters = scanResultFilters;
            return this;
        }

        public Scan builder() {
            return new Scan(this);
        }
    }


}
