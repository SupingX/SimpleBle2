package com.bugull.simple.ble.ble;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.bugull.simple.ble.app.BaseApp;
import com.bugull.simple.ble.ble.common.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

/**
 *
 * Created by leaf on 2017/12/12.
 */

public class ScanManager {
    private final static String TAG = "ScanManager"+ Constant.TAG;
    private ScanSettings scanSettings;
    private List<ScanFilter> scanFilters;
    // 搜索回调5.0 21
    private ScanCallback scanCallback;

    // 搜索回调
    private BluetoothAdapter.LeScanCallback leScanCallback;

    public ScanManager() {
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
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
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    if (Constant.DEBUG) {
                        String msg = "-----------------onScanFailed-----------------------" + "\n";
                        msg += "errorCode = " + errorCode + "\n";
                        Log.i(TAG, msg);
                    }
                }
            };

            // 配置默认ScanSettings
            ScanSettings.Builder builder = new ScanSettings.Builder();
            builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
//            builder.setReportDelay(4000);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setLegacy(false);
                builder.setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.v(TAG, "setCallbackType");
                builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
                builder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
                builder.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
            }
            scanSettings = builder.build();

            // 配置默认ScanFilter
            ScanFilter scanFilter1 = new ScanFilter.Builder()
//                    .setDeviceAddress("98:01:A7:9F:44:66")
//                    .setDeviceName()
//                    .setManufacturerData(1,new byte[]{1, 9, 32, 0, 5, -86, 48, -32, -54, -17, -25, 61, 24, -90, -68, 68, -50, 119, 120, -72, -22, -100, -104, -89, -38, -105, 26})
//                    .setServiceData()
//                    .setServiceUuid()
                    .build();
            ScanFilter scanFilter2 = new ScanFilter.Builder()
//                    .setDeviceAddress("6C:40:08:93:50:A9")
//                    .setDeviceName()
//                    .setManufacturerData()
//                    .setServiceData()
//                    .setServiceUuid()
                    .build();
            if (scanFilters == null) {
                scanFilters = new ArrayList<>();
            }
            scanFilters.add(scanFilter1);
            scanFilters.add(scanFilter2);

        } else {
            leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (Constant.DEBUG) {
                        String msg = "-----------------onLeScan-----------------------" + "\n";
                        msg += "device = " + device + "\n";
                        msg += "rssi = " + rssi + "\n";
                        msg += "scanRecord = " + new String(scanRecord) + "\n";
                        Log.i(TAG, msg);
                    }
                }
            };
        }
    }

    public void setScanSettings(ScanSettings scanSettings) {
        this.scanSettings = scanSettings;
    }

    public void setScanFilters(List<ScanFilter> scanFilters) {
        this.scanFilters = scanFilters;
    }

    private BluetoothAdapter getBluetoothAdapter(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        return bluetoothManager.getAdapter();
    }

    public boolean isEnable(Context context) {
        return getBluetoothAdapter(context).isEnabled();
    }


    public void startScan() {
        if (!isEnable(BaseApp.getInstance())) {
            if (Constant.DEBUG) {
                Log.i(TAG, "bluetooth is disable ...");
            }
            return;
        }
        //5.0使用BluetoothLeScanner 5.0以下使用LeScanCallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (scanCallback == null) {
                return;
            }
            BluetoothLeScanner bluetoothLeScanner = getBluetoothAdapter(BaseApp.getInstance()).getBluetoothLeScanner();
            if (scanSettings == null) {
                bluetoothLeScanner.startScan(scanCallback);
            } else {
                bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);
            }


//            bluetoothLeScanner.startScan(scanFilters,scanSettings,PendingIntent.getActivity());
        } else {
            startScanOld();
        }
    }

    private void startScan(UUID[] uuids) {
        if (!isEnable(BaseApp.getInstance())) {
            if (Constant.DEBUG) {
                Log.i(TAG, "bluetooth is disable ...");
            }
        }
        getBluetoothAdapter(BaseApp.getInstance()).startLeScan(uuids, leScanCallback);
    }

    public void startScanAppointed() {
//        startScan(new UUID[]{UUID.fromString()});
    }

    public void flushPendingScanResults() {
        if (!isEnable(BaseApp.getInstance())) {
            if (Constant.DEBUG) {
                Log.i(TAG, "bluetooth is disable ...");
            }
            return;
        }
        //5.0使用BluetoothLeScanner 5.0以下使用LeScanCallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (scanCallback == null) {
                return;
            }
            BluetoothLeScanner bluetoothLeScanner = getBluetoothAdapter(BaseApp.getInstance()).getBluetoothLeScanner();
           bluetoothLeScanner.flushPendingScanResults(scanCallback);
        } else {
            startScanOld();
        }
    }



    public void startScanOld() {
        if (!isEnable(BaseApp.getInstance())) {
            if (Constant.DEBUG) {
                Log.i(TAG, "bluetooth is disable ...");
            }
            return;
        }
        if (leScanCallback == null) {
            leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (Constant.DEBUG) {
                        String msg = "-----------------onLeScan-----------------------" + "\n";
                        msg += "device = " + device + "\n";
                        msg += "rssi = " + rssi + "\n";
                        msg += "scanRecord = " + scanRecord + "\n";
                        Log.i(TAG, msg);
                    }
                }
            };
        }
        getBluetoothAdapter(BaseApp.getInstance()).startLeScan(leScanCallback);
    }

    public void stopScanOld() {
        if (!isEnable(BaseApp.getInstance())) {
            if (Constant.DEBUG) {
                Log.i(TAG, "bluetooth is disable ...");
            }
            return;
        }
        if (leScanCallback == null) {
            return;
        }
        getBluetoothAdapter(BaseApp.getInstance()).stopLeScan(leScanCallback);
    }


    public void stopScan() {
        if (!isEnable(BaseApp.getInstance())) {
            if (Constant.DEBUG) {
                Log.i(TAG, "bluetooth is disable ...");
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (scanCallback == null) {
                return;
            }
            BluetoothLeScanner bluetoothLeScanner = getBluetoothAdapter(BaseApp.getInstance()).getBluetoothLeScanner();
            bluetoothLeScanner.stopScan(scanCallback);
        } else {
            stopScanOld();
        }
    }


    public void startScan(PendingIntent pendingIntent) {
        // SDK 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BluetoothLeScanner bluetoothLeScanner = getBluetoothAdapter(BaseApp.getInstance()).getBluetoothLeScanner();
            bluetoothLeScanner.startScan(scanFilters, scanSettings, pendingIntent);
        }
    }

    public void stopScan(PendingIntent pendingIntent) {
        // SDK 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BluetoothLeScanner bluetoothLeScanner = getBluetoothAdapter(BaseApp.getInstance()).getBluetoothLeScanner();
            bluetoothLeScanner.stopScan(pendingIntent);
        }
    }


}
