package com.bugull.simple.ble.ble.core;

import android.bluetooth.BluetoothDevice;

import java.util.Collection;

/**
 *
 * Created by leaf on 2017/12/26.
 */

public interface IOperator {

    int connect(String address);
    int connect(BluetoothDevice device);
    int write(String address,byte [] data);
    int write(String address, Collection<byte[]> datas);
    int readRssi(String address);
    int read(String address);
    int close(String address);
    int disconnect(String address);
    int closeAll();
    int disconnectAll();
    int clearCache();
    boolean isConnectted(String address);
    boolean isAllConnectted(String address);
    int getConectState(String address);
}
