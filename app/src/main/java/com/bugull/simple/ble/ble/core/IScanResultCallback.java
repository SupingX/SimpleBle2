package com.bugull.simple.ble.ble.core;

import com.bugull.simple.ble.ble.bean.XScanResult;

import java.util.List;

/**
 * 蓝牙搜索结果回调接口
 *
 * Created by leaf on 2017/12/25.
 */

public interface IScanResultCallback {
     void onScan(XScanResult xScanResult,boolean isAboveLollipop);
     void onFinish(List<XScanResult> xScanResultList);
     void error(int error);
}
