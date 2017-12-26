package com.bugull.simple.ble.ble.core;

import com.bugull.simple.ble.ble.bean.XScanResult;

import java.util.List;

/**
 * 搜索回调数据的过滤接口
 * Created by leaf on 2017/12/25.
 */

public interface IScanResultFilter {
    List<XScanResult> filter( List<XScanResult> xScanResults);
}
