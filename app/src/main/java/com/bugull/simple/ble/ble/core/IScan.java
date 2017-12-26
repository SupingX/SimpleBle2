package com.bugull.simple.ble.ble.core;

/**
 * 低功耗蓝牙搜索功能
 * Created by leaf on 2017/12/25.
 */

public interface IScan {
    void startScan(IScanResultCallback IScanResultCallback);
    void stopScan();

}
