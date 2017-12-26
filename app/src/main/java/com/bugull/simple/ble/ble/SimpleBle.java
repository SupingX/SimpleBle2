package com.bugull.simple.ble.ble;

import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Message;

import com.bugull.simple.ble.ble.bean.ConnectInfo;

import java.util.HashMap;

/**
 * Created by leaf on 2017/12/12.
 */

public class SimpleBle {
    private static final long DELAY = 12*1000;
    private ScanManager scanManager;
    private ConnectManager connectManager;
    private final static int MSG_AUTO_CONNECT = 0x01;
    private final static int MSG_stop_scan = 0x02;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_AUTO_CONNECT:
                    reconnect();
                    break;
                case MSG_stop_scan:
                    stopScan();
                    break;
            }
        }
    };



    private void init(){
        scanManager = new ScanManager();
        connectManager = new ConnectManager();
    }

    private void startScan(boolean autoConnect){
        scanManager.stopScanOld();
        scanManager.startScanOld();
        if (autoConnect){
            mHandler.removeMessages(MSG_AUTO_CONNECT);
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_CONNECT,DELAY);
        }else{
            mHandler.removeMessages(MSG_stop_scan);

        }
    }

    private void reconnect() {
        String[] addresses = AddressHelper.getInstance().getAddresses();
        if (addresses==null || addresses.length==0){
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_CONNECT,DELAY);
            return;
        }
        HashMap<String, ConnectInfo> connectInfoMap = connectManager.getGattMap();
        if (connectInfoMap == null) {
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_CONNECT,DELAY);
            return;
        }
        for (int i = 0; i <addresses.length ; i++) {
            ConnectInfo connectInfo = connectInfoMap.get(addresses[i]);
            if (connectInfo!=null){
                int state = connectInfo.getState();
                String address = connectInfo.getAddress();
                if (state!= BluetoothGatt.STATE_CONNECTED){
                    connectManager.connect(address);
                }
            }
        }



    }

    public void stopScan(){
        mHandler.removeMessages(MSG_AUTO_CONNECT);
        mHandler.removeMessages(MSG_stop_scan);
        scanManager.stopScanOld();
    }

    public void connect(String address){

    }

}
