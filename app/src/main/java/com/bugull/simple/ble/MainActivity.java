package com.bugull.simple.ble;

import android.Manifest;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bugull.simple.ble.ble.ScanManager;
import com.bugull.simple.ble.ble.bean.XScanResult;
import com.bugull.simple.ble.ble.core.IScan;
import com.bugull.simple.ble.ble.core.IScanResultFilter;
import com.bugull.simple.ble.ble.core.Scan;
import com.bugull.simple.ble.ble.core.IScanResultCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ScanManager simpleBle;
    private IScan scan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        simpleBle = new ScanManager();
        requestPermission();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(MainActivity.this, "需要再次申请权限！",Toast.LENGTH_SHORT).show();
                // 这个API主要用于给用户一个申请权限的解释，该方法只有在用户在上一次已经拒绝过你的这个权限申请。也就是说，用户已经拒绝一次了，你又弹个授权框，你需要给用户一个解释，为什么要授权，则使用该方法。
            }else{
                Toast.makeText(MainActivity.this,"需要申请权限！",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

        }else{
            Toast.makeText(MainActivity.this,"已经有权限！",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(MainActivity.this,"申请成功！",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,"申请失败！",Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    private void initViews() {
        TextView tvInfo = (TextView) findViewById(R.id.tv_info);
        tvInfo.setText(Build.VERSION.SDK_INT +"-" + Build.VERSION.CODENAME);

        Scan.Builder builder = new Scan.Builder()
                .scanTimeOut(8 * 1000L)
                .scanInterval(4 * 1000L);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 配置默认ScanSettings
            ScanSettings.Builder scanSettingBuilder = new ScanSettings.Builder();
            scanSettingBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
            scanSettingBuilder.setReportDelay(4000);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                scanSettingBuilder.setLegacy(false);
                scanSettingBuilder.setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                scanSettingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
                scanSettingBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
                scanSettingBuilder.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
            }

            builder.scanSetting(scanSettingBuilder.build());
        }
        List<IScanResultFilter> scanResultFilters = new ArrayList<>();
        scanResultFilters.add(new IScanResultFilter() {
            @Override
            public List<XScanResult> filter( List<XScanResult> old) {
                if (old.size()>0){
                    old.remove(0);
                }
                return old;
            }
        });
        scanResultFilters.add(new IScanResultFilter() {
            @Override
            public List<XScanResult> filter( List<XScanResult> old) {
                if (old.size()>0){
                    old.remove(0);
                }
                return old;
            }
        });
        builder.scanResultFilters(scanResultFilters);

        scan = builder.builder();
    }


    public void startScan(View view){
//        simpleBle.flushPendingScanResults();
//        simpleBle.startScan();
    }

    public void stopScan(View view){
//        simpleBle.stopScan();
        if (scan!=null){
            scan.stopScan();
        }
    }

    public void startScanOld(View view){
//        simpleBle.startScanOld();
//        simpleBle.startScan();

        scan.startScan(new IScanResultCallback() {
            @Override
            public void onScan(XScanResult xScanResult, boolean isAboveLollipop) {
                Log.v("zeej","xScanResult : " + xScanResult);
            }

            @Override
            public void onFinish(List<XScanResult> xScanResultList) {
                if (xScanResultList != null) {
                    Log.v("zeej","xScanResultList : size = " +xScanResultList.size()
                            +"--->" + xScanResultList);
                }

            }

            @Override
            public void error(int error) {
                Log.v("zeej","error : " + error);
            }
        });
    }
}
