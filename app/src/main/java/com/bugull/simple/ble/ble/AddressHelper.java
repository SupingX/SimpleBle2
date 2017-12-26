package com.bugull.simple.ble.ble;

import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 地址
 *
 * Created by leaf on 2017/12/2.
 */

public class AddressHelper {
    public static final String TAG  = "AddressHelper";
    public static final boolean DEBUG  = true;

    private static AddressHelper instance;
    private AddressHelper(){

    }
    public static AddressHelper getInstance(){
        if (instance == null) {
            synchronized (AddressHelper.class){
                if (instance == null) {
                    instance = new AddressHelper();
                }
            }
        }
        return instance;
    }
    private HashSet<String> macSet = new HashSet<>();
    public String [] getAddresses(){
        if (macSet.isEmpty()){
            return null;
        }
        String [] result = new String[macSet.size()];
        Iterator<String> iterator = macSet.iterator();
        int index= 0;
        while (iterator.hasNext()){
            result[index] =iterator.next();
            index++;
            if (DEBUG){
                Log.v(TAG,iterator.next());
            }
        }

        return result;
    }

    public void addAddress(String address){
        if (!macSet.contains(address)){
            macSet.add(address);
        }
    }

    public void deleteAddress(String address){
        if (macSet.contains(address)){
            macSet.remove(address);
        }
    }
}
