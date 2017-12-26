package com.bugull.simple.ble.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * 作者：Aaron Zhang on 2016/8/2 09:51
 * 描述：权限检查管理
 */
public class PermissionUtil {


    public static final int PERMISSION_STORAGE = 0X11;
    public static final int PERMISSION_BLE = 0X12;
    public static final int PERMISSION_CAMERA = 0X13;


    private static PermissionUtil instance = null;
    private PermissionUtil(){

    }
    public static PermissionUtil getInstance(){
        if(instance == null){
            instance = new PermissionUtil();
        }
        return instance;
    }
    public void checkStorage(Activity activity){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(activity,"请您打开文件访问权限", Toast.LENGTH_LONG).show();
                if( ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ){
                    Intent intent = new
                            Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    activity.startActivity(intent);
                    return;
                }
                activity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
            }

        }


    }

    public void checkPhoneState(Activity activity){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(activity,"请您打开获取电话状态权限", Toast.LENGTH_LONG).show();
                if( ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE) ){
                    Intent intent = new
                            Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    activity.startActivity(intent);
                    return;
                }
                activity.requestPermissions(new String[]{READ_PHONE_STATE}, PERMISSION_CAMERA);

            }

        }


    }


    public void checkCamera(Activity activity){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(activity,"请您打开拍照权限", Toast.LENGTH_LONG).show();
                if( ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ){
                    Intent intent = new
                            Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    activity.startActivity(intent);
                    return;
                }
                activity.requestPermissions(new String[]{ android.Manifest.permission.CAMERA}, PERMISSION_CAMERA);

            }

        }


    }


    public boolean checkLocation(Activity activity){

        Log.e("test", "checkLocation: 检查");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if( ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ){
                    Intent intent = new
                            Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    activity.startActivity(intent);
                    return false;
                }
                Log.e("test", "checkLocation: 检查权限失败 开启权限");
                activity.requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_BLE);
                return false;
            }

        }

        return true;


    }



    public boolean checkAll(Activity activity){

        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
            Intent intent = new
                    Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            activity.startActivity(intent);
            return false;
        }

    //    boolean isGranted = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                activity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                return false;
            }
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                activity.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_BLE);
                return false;
            }
            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                activity.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                return false;
            }
        }
        return true;
    }


//    public boolean checkSystem(Activity activity){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                  //  ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(activity,activity.getString(R.string.premission_desc), Toast.LENGTH_LONG).show();
//                Intent intent = new
//                        Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
//                activity.startActivity(intent);
//                return false;
//            }
//
//
//        }
//        return true;
//    }
}
