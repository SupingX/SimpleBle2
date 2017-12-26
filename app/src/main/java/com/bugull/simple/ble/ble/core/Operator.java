package com.bugull.simple.ble.ble.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bugull.simple.ble.app.BaseApp;
import com.bugull.simple.ble.ble.bean.Configuration;
import com.bugull.simple.ble.ble.bean.ConnectInfo;
import com.bugull.simple.ble.ble.common.Constant;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by leaf on 2017/12/26.
 */

public class Operator implements IOperator {
    private final static String TAG = "Operator" + Constant.TAG;
    private HashMap<String, ConnectInfo> connectInfoMap = new HashMap<>();
    private static final int MSG_SERVICES_DISCOVERED = 0X01;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SERVICES_DISCOVERED:

                    break;

            }

        }
    };

    private BluetoothGattCallback callback;

    private void init(){
        if (callback == null) {
            callback = new BluetoothGattCallback() {
                @Override
                public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyRead(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    // status ：
                    // GATT_SUCCESS：0
                    // GATT_READ_NOT_PERMITTED : 0x2
                    // GATT_WRITE_NOT_PERMITTED : 0x3
                    // GATT_INSUFFICIENT_AUTHENTICATION : 0x5
                    // GATT_REQUEST_NOT_SUPPORTED : 0x6
                    // GATT_INSUFFICIENT_ENCRYPTION : 0xf
                    // GATT_CONNECTION_CONGESTED 0x8f
                    // ...
                    log("[onConnectionStateChange] gatt=" + gatt + ",status=" + status + ",newState=" + newState);
                    String address = gatt.getDevice().getAddress();
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        if (newState == BluetoothGatt.STATE_CONNECTED) {
                            try {
                                Thread.sleep(50L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            gatt.discoverServices();
                            return;
                        }
                    }
                    remove(address);
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    boolean available = false;
                    log("[onServicesDiscovered] gatt=" + gatt + ",status=" + status);
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        BluetoothGattService service = gatt.getService(UUID.fromString(Configuration.UUID_SERVICE));
                        if (service != null) {
                            BluetoothGattCharacteristic notifyCharacteristic = service.getCharacteristic(UUID.fromString(Configuration.UUID_CHARACTERISTIC_NOTIFY));
                            BluetoothGattCharacteristic writeCharacteristic = service.getCharacteristic(UUID.fromString(Configuration.UUID_CHARACTERISTIC_WRITE));
                            if (notifyCharacteristic != null || writeCharacteristic != null) {
                                writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                                gatt.setCharacteristicNotification(notifyCharacteristic, true);
                                BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptor(UUID.fromString(Configuration.UUID_DESC_CCC));
                                if (descriptor!=null){
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(descriptor);
                                    // 连接成功。
                                    available = true;
                                }
                            }
                        }
                    }
                    String address = gatt.getDevice().getAddress();
                    if (!available){
                        log("[onServicesDiscovered] fail");
                        remove(address);
                    }else{
                        log("[onServicesDiscovered] success");
                        updateConnectDevice(address,gatt,BluetoothGatt.STATE_CONNECTED);
                        onServicesDiscoveredSuccess(gatt);
                    }

                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                }
            };
        }
        
    }

    @Override
    public int connect(String address) {
        int ret = -1;
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            if (Constant.DEBUG) {
                Log.e(TAG, "address is invalid : " + address);
            }
            return ret;
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!defaultAdapter.isEnabled()) {
            if (Constant.DEBUG) {
                Log.e(TAG, "address is disabled : " + address);
            }
            return ret;
        }

        BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(address);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            int phy = 1; // 物理？？？MASK （26支持）（默认PHY_LE_1M_MASK）
//            BluetoothGatt bluetoothGatt = remoteDevice.connectGatt(BaseApp.getInstance()
//                    , true
//                    , callback
//                    , transport
//                    , phy
//                    , mHandler
//            );
//            remoteDevice.connectGatt(BaseApp.getInstance(),true,callback,transport,phy);
//        }
        if (Constant.DEBUG) {
            Log.v(TAG, "connect address = " + address + "，SDK版本：" + Build.VERSION.SDK_INT);
        }
        BluetoothGatt bluetoothGatt = null;
        ConnectInfo connectInfo = connectInfoMap.get(address);
        if (connectInfo!=null){
            bluetoothGatt = connectInfo.getGatt();
        }
        if (bluetoothGatt != null) {
            // 自动连接比较慢
//            if (Constant.DEBUG) {
//                Log.v(TAG, "auto connect");
//            }
//            bluetoothGatt.connect();
            // 关闭旧的连接
            bluetoothGatt.close();
            connectInfoMap.put(address,null);
            // 给出缓冲时间，开启新的连接
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Constant.DEBUG) {
                Log.v(TAG, "connect >=5.0");
            }
            int transport = BluetoothDevice.TRANSPORT_LE; // 5.0 23支持 优先模式？一个是BR/EDR（传统蓝牙）TRANSPORT_BREDR； 一个是SMART（LE）TRANSPORT_LE； 或者自动（默认TRANSPORT_AUTO）
            bluetoothGatt = remoteDevice.connectGatt(BaseApp.getInstance(), false, callback, transport);
        } else {
            bluetoothGatt = remoteDevice.connectGatt(BaseApp.getInstance(), false, callback);
        }

        connectInfoMap.put(address, new ConnectInfo(bluetoothGatt,address,BluetoothGatt.STATE_DISCONNECTED));


        if (bluetoothGatt != null) {
            ret = 1;
        }
        return ret;
    }

    @Override
    public int connect(BluetoothDevice device) {
        return connect(device.getAddress());
    }

    @Override
    public int write(String address, byte[] data) {
        ConnectInfo connectInfo = connectInfoMap.get(address);
        if (connectInfo == null) {
            return -1;
        }
        return write(connectInfo.getGatt(),data);
    }

    @Override
    public int write(String address, Collection<byte[]> datas) {
        ConnectInfo connectInfo = connectInfoMap.get(address);
        if (connectInfo == null) {
            return -1;
        }
        return write(connectInfo.getGatt(),datas);
    }

    @Override
    public int readRssi(String address) {
        return 0;
    }

    @Override
    public int read(String address) {
        return 0;
    }

    @Override
    public int close(String address) {
        return 0;
    }

    @Override
    public int disconnect(String address) {
        return 0;
    }

    @Override
    public int closeAll() {
        return 0;
    }

    @Override
    public int disconnectAll() {
        return 0;
    }

    @Override
    public int clearCache() {
        return 0;
    }

    @Override
    public boolean isConnectted(String address) {
        return false;
    }

    @Override
    public boolean isAllConnectted(String address) {
        return false;
    }

    @Override
    public int getConectState(String address) {
        return 0;
    }


    public int write(BluetoothGatt gatt, byte[]data) {
        if (gatt == null || data == null ) {
            return -1;
        }
        BluetoothGattService service = gatt.getService(UUID.fromString(Configuration.UUID_SERVICE));
        if (service == null) {
            return -1;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(Configuration.UUID_CHARACTERISTIC_WRITE));
        if (characteristic == null) {
            return -1;
        }

        characteristic.setValue(data);
        return gatt.writeCharacteristic(characteristic)?1:-1;
    }

    /**
     *  写入多个数据
     * @param gatt
     * @param datas
     */
    public int write(BluetoothGatt gatt, Collection<byte[]> datas) {
        if (gatt == null || datas == null || datas.size() == 0) {
            return -1;
        }
        BluetoothGattService service = gatt.getService(UUID.fromString(Configuration.UUID_SERVICE));
        if (service == null) {
            return -1;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(Configuration.UUID_CHARACTERISTIC_WRITE));
        if (characteristic == null) {
            return -1;
        }

        gatt.beginReliableWrite();
        Iterator<byte[]> iterator = datas.iterator();
        while (iterator.hasNext()){
            byte[] data = iterator.next();
            characteristic.setValue(data);
            boolean b = gatt.writeCharacteristic(characteristic);
            if (!b) {
                gatt.abortReliableWrite();
            }
            return -1;
        }
        boolean b = gatt.executeReliableWrite();
        return b?1:-1;

    }

    private void onServicesDiscoveredSuccess(BluetoothGatt gatt) {
        mHandler.sendEmptyMessage(MSG_SERVICES_DISCOVERED);
    }


    /**
     * 更新连接状态
     *
     * @param gatt
     */
    private void updateConnectDevice(String address, BluetoothGatt gatt,
                                     int state) {
        if (connectInfoMap != null && !connectInfoMap.isEmpty()) {
            ConnectInfo connectInfo = connectInfoMap.get(address);
            if (connectInfo != null) {
                connectInfo.setState(state);
            }
        }
    }

    private void remove(String address) {
        ConnectInfo connectInfo = connectInfoMap.get(address);
        if (connectInfo == null) {
            return;
        }
        BluetoothGatt bluetoothGatt = connectInfo.getGatt();
        if (bluetoothGatt != null) {
            connectInfoMap.remove(address);
            // bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
    }

    public HashMap<String, ConnectInfo> getGattMap() {
        return connectInfoMap;
    }

    private void log(String s) {
        if (Constant.DEBUG) {
            Log.i(TAG, s);
        }
    }
}
