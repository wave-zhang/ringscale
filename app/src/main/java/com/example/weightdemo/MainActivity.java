package com.example.weightdemo;

import android.Manifest;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.weightdemo.databinding.ActivityMainBinding;
//import com.health.openscale.core.OpenScale;
//import com.health.openscale.core.bluetooth.BluetoothCommunication;
//import com.health.openscale.core.datatypes.ScaleMeasurement;
import com.orhanobut.logger.Logger;
import com.wave.view.RingScaleView;
import com.wz.libs.core.WzDataBindingActivity;
import com.wz.libs.core.annotations.Activity;
import com.wz.libs.core.annotations.After;
//import timber.log.Timber;

@Activity(R.layout.activity_main)
public class MainActivity extends WzDataBindingActivity<ActivityMainBinding> {

    float fatValue,weightValue;

    @After
    public void onAfter(){
        //MI_SCALE ' (88:0F:10:A2:83:AF)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        if (Build.VERSION.SDK_INT >= 23){
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //setBleAvailable(3);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},10002);
            }
        }

        getViewDataBinding().setFat(fatValue = 18.0f);
        getViewDataBinding().setWeight(weightValue = 50.0f);
        getViewDataBinding().rs.setValue(weightValue);
        //getViewDataBinding().rs.setMaxSelectValue(100);
        getViewDataBinding().rs.setOnRingScaleViewChangedListener(new RingScaleView.OnRingScaleViewChangedListener() {
            @Override
            public void beforeValueChanged(float value) {

            }

            @Override
            public void onValueChanged(float oldValue, float newValue) {
                setScaleValue(newValue);
            }

            @Override
            public void afterValueChanged(float value) {
                setScaleValue(value);
            }
        });

        getViewDataBinding().rgTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //boolean isOpen  = checkedId == R.id.rb_1 ? true : false;
                checkFatOrWeightNumber();
                float value = weightValue;
                int maxValue = 299;
                int minValue = 10;
                if(isFat){
                    value = fatValue;
                    maxValue = 40;
                    minValue = 5;
                }
                getViewDataBinding().rs.setMaxSelectValue(maxValue);
                getViewDataBinding().rs.setMinSelectValue(minValue);
                getViewDataBinding().rs.setValue(value);
            }
        });
    }

    void setScaleValue(float value){
        // 保留一位小数
        value = Math.round(value * 10F) / 10F;
//        NumberFormat nf = NumberFormat.getNumberInstance();
//        nf.setMaximumFractionDigits(1);
//        nf.format(value);
        if(!isFat){
            getViewDataBinding().setWeight(weightValue = value);
        }else {
            getViewDataBinding().setFat(fatValue = value);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void onXiaomiClick(View view){
//        String deviceName = "MI_SCALE";
//        String hwAddress = "88:0F:10:A2:83:AF";
        String deviceName = "MIBFS";
        String hwAddress = "0C:95:41:B1:4E:D2";
        //OpenScale.getInstance().connectToBluetoothDevice(deviceName,hwAddress,callbackBtHandler);
    }

    public void onCloseClick(View view){
        //OpenScale.getInstance().disconnectFromBluetoothDevice();
        find();
    }

//    private final Handler callbackBtHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
//            BluetoothCommunication.BT_STATUS btStatus = BluetoothCommunication.BT_STATUS.values()[msg.what];
//
//            switch (btStatus) {
//                case RETRIEVE_SCALE_DATA:
////                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_success);
//                    ScaleMeasurement scaleBtData = (ScaleMeasurement) msg.obj;
//                    Timber.d("Bluetooth retrieve scale data getFat = "+scaleBtData.getFat());
//                    Timber.d("Bluetooth retrieve scale data getWeight = "+scaleBtData.getWeight());
//                    getViewDataBinding().setFat(scaleBtData.getFat());
//                    getViewDataBinding().setWeight(scaleBtData.getWeight());
////                    OpenScale openScale = OpenScale.getInstance();
////
////                    if (prefs.getBoolean("mergeWithLastMeasurement", true)) {
////                        List<ScaleMeasurement> scaleMeasurementList = openScale.getScaleMeasurementList();
////
////                        if (!scaleMeasurementList.isEmpty()) {
////                            ScaleMeasurement lastMeasurement = scaleMeasurementList.get(0);
////                            scaleBtData.merge(lastMeasurement);
////                        }
////                    }
////
////                    openScale.addScaleData(scaleBtData, true);
//                    break;
//                case INIT_PROCESS:
////                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_success);
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_init), Toast.LENGTH_SHORT).show();
//                    Timber.d("Bluetooth initializing");
//                    break;
//                case CONNECTION_LOST:
////                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_connection_lost), Toast.LENGTH_SHORT).show();
//                    Timber.d("Bluetooth connection lost");
//                    break;
//                case NO_DEVICE_FOUND:
////                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_no_device), Toast.LENGTH_SHORT).show();
//                    Timber.e("No Bluetooth device found");
//                    break;
//                case CONNECTION_RETRYING:
////                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_searching);
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_no_device_retrying), Toast.LENGTH_SHORT).show();
//                    Timber.e("No Bluetooth device found retrying");
//                    break;
//                case CONNECTION_ESTABLISHED:
////                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_success);
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_connection_successful), Toast.LENGTH_SHORT).show();
//                    Timber.d("Bluetooth connection successful established");
//                    break;
//                case CONNECTION_DISCONNECT:
////                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_connection_disconnected), Toast.LENGTH_SHORT).show();
//                    Timber.d("Bluetooth connection successful disconnected");
//                    break;
//                case UNEXPECTED_ERROR:
////                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
////                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_connection_error) + ": " + msg.obj, Toast.LENGTH_SHORT).show();
//                    Timber.e("Bluetooth unexpected error: %s", msg.obj);
//                    break;
//                case SCALE_MESSAGE:
//                    String toastMessage = String.format(getResources().getString(msg.arg1), msg.obj);
//                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//    };

    BluetoothAdapter bluetoothAdapter;

    final int REQUEST_ENABLE_BT = 10001;

    void find(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Logger.d("Device doesn't support Bluetooth");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        if(!bluetoothAdapter.startDiscovery()){
            Log.d(tag," 无法进行扫描 !!! ");
            Log.d(tag,"无法进行扫描");
            return;
        }
    }

    final String tag = "main";
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(
                        BluetoothDevice.EXTRA_RSSI);
                int iRssi = Math.abs(rssi);
                //double power = (iRssi - 59) / 25.0;
                double power = (iRssi-50)/(10*2.5);
                power = Math.pow(10,power);

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(tag,"deviceName = "+deviceName +" deviceHardwareAddress = "+deviceHardwareAddress + "  power = "+power);
                //Log.d(tag,"deviceName = "+deviceName +" deviceHardwareAddress = "+deviceHardwareAddress);
            }else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                Log.d(tag,"ACTION_ACL_DISCONNECTED .....");
                //Log.d(tag,"ACTION_ACL_DISCONNECTED");
            }
        }
    };

    float tvFatY = 0,tvWeightY = 0;

    void setFatYWeightY(){
        tvWeightY = tvWeightY <= 0 ? getViewDataBinding().llWeight.getY(): tvWeightY;
        tvFatY = tvFatY <= 0 ? getViewDataBinding().llFat.getY() : tvFatY;
        getViewDataBinding().tvWeight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,48);
        getViewDataBinding().tvFat.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
    }

    public void onMoveClick(View view){
        setFatYWeightY();
//        Timber.d("tvFatY = "+tvFatY+"  tvWeightY = "+tvWeightY);
//        getViewDataBinding().tvFat.setTextSize(TypedValue.COMPLEX_UNIT_DIP,48);
//        getViewDataBinding().llFat.setY(tvWeightY);
//        getViewDataBinding().tvWeight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
//        getViewDataBinding().llWeight.setY(tvFatY);
        showAnimator(!isFat);
        Log.d(tag,"getViewDataBinding().tvFat = "+getViewDataBinding().tvFat.getY()+"  getViewDataBinding().tvWeight = "+getViewDataBinding().tvWeight.getY());
    }

    android.animation.ValueAnimator valAnimator = null;

    boolean isFat;


    void showAnimator(boolean isFat){
        this.isFat = isFat;
        float sFatY = getViewDataBinding().llFat.getY();
        float sWeightY = getViewDataBinding().llWeight.getY();
        float start = isFat ? 0 : 1;
        float end = isFat ? 1 : 0;
        float sFatSize = getViewDataBinding().tvFat.getTextSize();
        float sWeightSize = getViewDataBinding().tvWeight.getTextSize();

        float distance = Math.abs(tvFatY - sWeightY);
        //Timber.d("value = "+value +" sFatY = "+sFatY +" sWeightY = "+sWeightY + " sFatSize = "+sFatSize +" sWeightSize = "+sWeightSize);
        //tvWeightY - tvFatY;
        if (valAnimator == null) {
            valAnimator = ValueAnimator.ofFloat(start, end);
            valAnimator.setInterpolator(new DecelerateInterpolator());
            valAnimator.setDuration(300);
            valAnimator.addUpdateListener((animation) -> onAnimationUpdate(animation,distance,sFatY,sWeightY,sFatSize,sWeightSize));
        } else {
            valAnimator.cancel();
            valAnimator.setFloatValues(start, end);
        }
        valAnimator.start();
    }

    void checkFatOrWeightNumber(){
        setFatYWeightY();
        showAnimator(!isFat);
    }

    void check(boolean isFat){
        setFatYWeightY();
        showAnimator(isFat);
    }

    void onAnimationUpdate(ValueAnimator animation,float distance,float sFatY,float sWeightY,float sFatSize,float sWeightSize) {
        Object ob = animation.getAnimatedValue();

        if(ob instanceof Float){
            float value = (Float) ob;
//            float cFatY = isOpen ? sFatY - value : sFatY + value;
//            float cWeightY = isOpen ? sWeightY + value : sWeightY - value;
//            Timber.d("value = "+value +"  cFatY = "+cFatY +" cWeightY = "+cWeightY);
//            getViewDataBinding().llFat.setY(cFatY);
//            getViewDataBinding().llWeight.setY(cWeightY);

            float percentValue = distance * value;
            getViewDataBinding().llFat.setY(sFatY - percentValue);
            getViewDataBinding().llWeight.setY(sWeightY + percentValue);

            float percentTvSize = 24 * value;
            getViewDataBinding().tvWeight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,48 - percentTvSize);
            getViewDataBinding().tvFat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24 + percentTvSize);

        }
    }

}
