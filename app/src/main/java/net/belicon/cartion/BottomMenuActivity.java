package net.belicon.cartion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.belicon.cartion.adapters.BannerPagerAdapter;
import net.belicon.cartion.adapters.ChangeAdapter;
import net.belicon.cartion.adapters.DownloadAdapter;
import net.belicon.cartion.adapters.IotSwitchAdapter;
import net.belicon.cartion.adapters.Mobile36SwitchAdapter;
import net.belicon.cartion.adapters.Mobile710SwitchAdapter;
import net.belicon.cartion.models.Banner;
import net.belicon.cartion.models.Cartion;
import net.belicon.cartion.models.Coupon;
import net.belicon.cartion.models.CouponList;
import net.belicon.cartion.models.Device;
import net.belicon.cartion.models.Down;
import net.belicon.cartion.models.MobileSwitch;
import net.belicon.cartion.models.MyPage;
import net.belicon.cartion.models.SwitchList;
import net.belicon.cartion.models.User;
import net.belicon.cartion.models.UserMobile;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import pyxis.uzuki.live.rollingbanner.RollingBanner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Typeface.BOLD;

public class BottomMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RetrofitInterface mRetInterface;
    private Realm realm;

    private RelativeLayout mSearchContainer, m36Container, m710Container;
    private LinearLayout mInfoContainer;
    private TextView mSearchNotiText, mNicNameText, mBatteryText, mTemperatureText;
    private ImageButton mCartionSearchBtn, m36PurchaseBtn, m710PurchaseBtn;
    private View m36Line, m710Line;
    private FrameLayout mHomeDialog;

    private RecyclerView mIotSwitchRecyclerView;
    private RecyclerView mMobileSwitch36RecyclerView;
    private RecyclerView mMobileSwitch710RecyclerView;

    private IotSwitchAdapter mIotSwitchAdapter;
    private Mobile36SwitchAdapter mMobile36SwitchAdapter;
    private Mobile710SwitchAdapter mMobile710SwitchAdapter;

    private RollingBanner mBannerPager;
    public static AppCompatToggleButton mHomeBtn, mMusicBtn, mMyPageBtn, mMoreBtn;

    private ProgressDialog mProgressDialog;

    private List<UserMobile> mSwitchMusic;
    private List<UserMobile> mDownMusic;

    private BleDevice mBleDevice;
    private String token, email, mac, serial;
    private byte[] convertByte;
    private boolean isUserCheck = false;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onCheckPermission();
        setContentView(R.layout.activity_bottom_menu);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("appdb.realm").build();
        Realm.setDefaultConfiguration(config);

        mBannerPager = findViewById(R.id.bottom_main_banner);
        mHomeBtn = findViewById(R.id.bottom_home_btn);
        mMusicBtn = findViewById(R.id.bottom_music_btn);
        mMyPageBtn = findViewById(R.id.bottom_my_page_btn);
        mMoreBtn = findViewById(R.id.bottom_more_btn);
        mCartionSearchBtn = findViewById(R.id.home_cartion_search_btn);
        mNicNameText = findViewById(R.id.home_cartion_nic_text);
        mSearchNotiText = findViewById(R.id.home_search_noti_text);
        mSearchContainer = findViewById(R.id.home_search_container);
        mInfoContainer = findViewById(R.id.home_cartion_event_container);
        mBatteryText = findViewById(R.id.home_battery_text);
        mTemperatureText = findViewById(R.id.home_temperature_text);
        mIotSwitchRecyclerView = findViewById(R.id.home_iot_switch_recycler_view);
        mMobileSwitch36RecyclerView = findViewById(R.id.home_mobile_switch_36_recycler_view);
        mMobileSwitch710RecyclerView = findViewById(R.id.home_mobile_switch_710_recycler_view);
        m36Container = findViewById(R.id.home_mobile_switch_36_container);
        m710Container = findViewById(R.id.home_mobile_switch_710_container);
        m36Line = findViewById(R.id.home_mobile_switch_36_line);
        m710Line = findViewById(R.id.home_mobile_switch_710_line);
        m36PurchaseBtn = findViewById(R.id.home_mobile_switch_36_purchase_btn);
        m710PurchaseBtn = findViewById(R.id.home_mobile_switch_710_purchase_btn);
        mHomeDialog = findViewById(R.id.home_dialog);

        String noti = mSearchNotiText.getText().toString();
        String cn = "'검색 버튼'";
        int start = noti.indexOf(cn);
        int end = start + cn.length();
        SpannableString span = new SpannableString(noti);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_7F44A6)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSearchNotiText.setText(span);

        mHomeBtn.setOnClickListener(this);
        mMusicBtn.setOnClickListener(this);
        mMyPageBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);

        if (mAuth.getCurrentUser() != null) {
            email = mAuth.getCurrentUser().getEmail();
        }

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(17)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

        mCartionSearchBtn.setOnClickListener(this);

        findViewById(R.id.home_switch_change_btn).setOnClickListener(this);

        mSwitchMusic = new ArrayList<>();
        mDownMusic = new ArrayList<>();

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        token = "Bearer " + User.getUserToken();

        ArrayList<String> data = new ArrayList<>(); //이미지 url를 저장하는 arraylist
        mRetInterface.getBannerList(token)
                .enqueue(new Callback<Banner>() {
                    @Override
                    public void onResponse(Call<Banner> call, Response<Banner> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                for (int i = 0; i < response.body().getData().getBannerList().size(); i++) {
                                    String url = response.body().getData().getBannerList().get(i).getImageUrl();
                                    data.add(url);
                                }
                                BannerPagerAdapter scrollAdapter = new BannerPagerAdapter(BottomMenuActivity.this, data);
                                mBannerPager.setAdapter(scrollAdapter); //Auto Viewpager에 Adapter 장착
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Banner> call, Throwable t) {

                    }
                });

        mHomeBtn.setChecked(true);
        onCoupon();

        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("LIFE", "onResume");
        mHomeDialog.setVisibility(View.GONE);
        if (realm.where(UserMobile.class).findAll().size() != 0) {
            mDownMusic = realm.where(UserMobile.class).findAll();
        }
    }

    private void onCoupon() {
        mRetInterface.getCoupon(token, email)
                .enqueue(new Callback<Coupon>() {
                    @Override
                    public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<CouponList> list = response.body().getData().getCouponList();
                                if (list.size() != 0) {
                                    String title = list.get(0).getCouponName();
                                    String content = list.get(0).getCouponText();
                                    String value = list.get(0).getCouponValue();
                                    onCouponView(value);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Coupon> call, Throwable t) {

                    }
                });
    }

    private void onCouponView(String value) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_coupon, null);

//        TextView mCouponTitle = view.findViewById(R.id.coupon_title);
        TextView mCouponContent = view.findViewById(R.id.coupon_content);
//        mCouponTitle.setText(title);
        String coupon_message = mCouponContent.getText().toString();
        String one = "'카션'";
        int one_start = coupon_message.indexOf(one);
        int one_end = one_start + one.length();
        String two = "'모바일 스위치1개 쿠폰'";
        SpannableString span = new SpannableString(coupon_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface typeface = getResources().getFont(R.font.scd6);
            span.setSpan(new TypefaceSpan(typeface), one_start, one_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            span.setSpan(new StyleSpan(BOLD), one_start, one_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        span.setSpan(new AbsoluteSizeSpan(16, true), one_start, one_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_7F44A6)), one_start, one_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_7F44A6)), coupon_message.indexOf(two), coupon_message.indexOf(two) + two.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mCouponContent.setText(span);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = 1200;
        dialog.show();
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        view.findViewById(R.id.coupon_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRetInterface.putCoupon(token, email, value)
                        .enqueue(new Callback<MyPage>() {
                            @Override
                            public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                                if (response.code() == 200) {
                                    if (response.body() != null) {
                                        Toast.makeText(BottomMenuActivity.this, "쿠폰을 정상적으로 사용하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(BottomMenuActivity.this, "쿠폰 사용에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<MyPage> call, Throwable t) {
                                Toast.makeText(BottomMenuActivity.this, "쿠폰 사용에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
            }
        });
    }

    private void onServerDevice(BleDevice bleDevice, BluetoothGatt gatt) {
        if (mAuth.getCurrentUser() != null) {
            mRetInterface.getUserData(token, mAuth.getCurrentUser().getEmail())
                    .enqueue(new Callback<MyPage>() {
                        @Override
                        public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    mHomeDialog.setVisibility(View.VISIBLE);
                                    List<Device> devices = response.body().getData().getDevices();
                                    List<String> macList = new ArrayList<>();
                                    for (int i = 0; i < devices.size(); i++) {
                                        macList.add(devices.get(i).getDeviceMac());
                                    }
                                    if (macList.contains(mBleDevice.getMac())) {
                                        isUserCheck = true;
                                    } else {
                                        isUserCheck = false;
                                    }
                                    mSearchContainer.setVisibility(View.GONE);
                                    mInfoContainer.setVisibility(View.VISIBLE);
                                    String SERVICE_UUID = gatt.getServices().get(2).getUuid().toString();
                                    String CHARACTERISTIC_UUID = gatt.getServices().get(2).getCharacteristics().get(1).getUuid().toString();
                                    Log.e("SERVICE UUID", SERVICE_UUID);
                                    Log.e("CHARACTERISTIC UUID", CHARACTERISTIC_UUID);
                                    mNicNameText.setText(mBleDevice.getName());
                                    notified(bleDevice, SERVICE_UUID, CHARACTERISTIC_UUID);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyPage> call, Throwable t) {

                        }
                    });
        }
    }

    private byte[] onEmailPost(String email) {
        int checksum = 0;

        byte[] bytes = email.getBytes();
        byte[] dataBytes = new byte[email.length() + 1];
        for (int i = 0; i < bytes.length; i++) {
            Log.e("GET EMAIL", bytes[i] + "");
            dataBytes[i] = bytes[i];
            checksum += bytes[i];
        }
        dataBytes[email.length()] = (byte) (checksum % 256);
        checksum = 0;
        Log.e("DATA EMAIL", Arrays.toString(dataBytes));
        return dataBytes;
    }

    private byte[] onAddressPost(String address) {
        int checksum = 0;

        byte[] bytes = address.getBytes();
        byte[] dataBytes = new byte[address.length() + 1];
        for (int i = 0; i < bytes.length; i++) {
            Log.e("GET ADDRESS", bytes[i] + "");
            dataBytes[i] = bytes[i];
            checksum += bytes[i];
        }
        dataBytes[address.length()] = (byte) (checksum % 256);
        checksum = 0;
        Log.e("DATA ADDRESS", Arrays.toString(dataBytes));
        return dataBytes;
    }


    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                mHomeDialog.setVisibility(View.VISIBLE);
                Toast.makeText(BottomMenuActivity.this, "Scanning...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (bleDevice.getName() != null && bleDevice.getName().equals("Cartion") && bleDevice.getRssi() > -65) {
//                    Log.e("CARTION", bleDevice.getName());
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                    mBleDevice = bleDevice;
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                mHomeDialog.setVisibility(View.GONE);
                if (mBleDevice != null) {
                    mac = mBleDevice.getMac();
                    Log.e("MAC", mac);
                }
            }
        });
    }

    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Toast.makeText(BottomMenuActivity.this, "Connected failed.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                onServerDevice(bleDevice, gatt);
                onSwitch();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                mSearchContainer.setVisibility(View.VISIBLE);
                mInfoContainer.setVisibility(View.GONE);
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void notified(BleDevice device, String uuid_service, String uuid_characteristic_read) {
        BleManager.getInstance().notify(
                device,
                uuid_service,
                uuid_characteristic_read,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {

                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Log.e("NOTIFY", HexUtil.formatHexString(data, true));
                                String s = new String(data);
//                                Log.e("NOTIFY DATA 1", s);
                                Log.e("NOTIFIED", s);
                                if (s.startsWith("ca") || s.startsWith("00")) {
                                    serial = s;
                                    Log.e("SERIAL NUM", serial);
                                    if (BleManager.getInstance().isConnected(mBleDevice)) {
                                        ExecutorService es = Executors.newSingleThreadExecutor();
                                        es.submit(new Runnable() {
                                            @Override
                                            public void run() {
                                                writeData(mBleDevice,
                                                        "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                        "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                        onEmailPost(email.substring(0, email.indexOf("@")))
                                                );
                                            }
                                        });
                                        es.submit(new Runnable() {
                                            @Override
                                            public void run() {
                                                writeData(mBleDevice,
                                                        "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                        "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                        onAddressPost(email.substring(email.indexOf("@") + 1))
                                                );
                                            }
                                        });
                                        es.shutdown();
                                    }
                                }

                                if (isUserCheck && s.equals("Success")) {
                                    Log.e("CARTION STATUS", "카션등록");
                                    mRetInterface.postCartion(token, email, new Cartion(serial, mac, "Cartion"))
                                            .enqueue(new Callback<MyPage>() {
                                                @Override
                                                public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                                                    if (response.code() == 200) {
                                                        Toast.makeText(BottomMenuActivity.this, "카션이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<MyPage> call, Throwable t) {

                                                }
                                            });
                                } else if (!isUserCheck && s.equals("Failure")) {
                                    Log.e("CARTION STATUS", "카션없음");
                                    Toast.makeText(BottomMenuActivity.this, "주변에 등록된 카션이 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                if (s.contains("Battery Level")) {
                                    mBatteryText.setText(s.replace("Battery Level:", ""));
                                } else if (s.contains("Temperature") && !s.contains("Max Temperature")) {
                                    mTemperatureText.setText(s.replace("Temperature:", "") + "°C");
                                }
                                mHomeDialog.setVisibility(View.GONE);
                            }
                        });
                    }
                });
    }

    private void writeData(BleDevice bleDevice, String uuid_service, String uuid_characteristic_write, byte[] data) {
        BleManager.getInstance().write(
                bleDevice,
                uuid_service,
                uuid_characteristic_write,
                data,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        String s = new String(justWrite);
                        if (mProgressDialog != null) {
                            if (!s.contains("SDN") && !s.contains("PSD") && !s.contains("com") && !s.contains("net")) {
                                Log.e("CURRENT", (int) ((double) current / (double) total * 100.0) + "");
                                mProgressDialog.setProgress((int) ((double) current / (double) total * 100.0));
                            }
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.e("WRITE ERROR", exception.getDescription());
                    }
                });
    }

    private void onSwitch() {
        mSwitchMusic.clear();
        for (int i = 0; i < 10; i++) {
            mSwitchMusic.add(new UserMobile(email, i + 1, "기본", "배려/안전", "horn", String.valueOf(i + 1)));
        }
        mRetInterface.getMobileSwitch(token, email)
                .enqueue(new Callback<MobileSwitch>() {
                    @Override
                    public void onResponse(Call<MobileSwitch> call, Response<MobileSwitch> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                int mobileSw = response.body().getData().getMobileSwitch();
//                                if (mobileSw == 0) {
//                                    m36Container.setVisibility(View.INVISIBLE);
//                                    m36PurchaseBtn.setVisibility(View.VISIBLE);
//                                } else if (mobileSw == 1) {
                                    m36Line.setVisibility(View.VISIBLE);
                                    m36Container.setVisibility(View.VISIBLE);
////                                    m710Container.setVisibility(View.INVISIBLE);
////                                    m710PurchaseBtn.setVisibility(View.VISIBLE);
//                                    m710Line.setVisibility(View.VISIBLE);
//                                    m710Container.setVisibility(View.VISIBLE);
//                                } else if (mobileSw == 2) {
//                                m36Line.setVisibility(View.VISIBLE);
//                                m36Container.setVisibility(View.VISIBLE);
                                    m710Line.setVisibility(View.VISIBLE);
                                    m710Container.setVisibility(View.VISIBLE);
//                                }
                                List<SwitchList> list = response.body().getData().getHornList();
                                for (int i = 0; i < list.size(); i++) {
                                    String userId = list.get(i).getUserId();
                                    String hornType = list.get(i).getHornType();
                                    String hornId = list.get(i).getHornId();
                                    String hornName = list.get(i).getHornName();
                                    String categoryName = list.get(i).getCategoryName();
                                    int seq = list.get(i).getMobileSwitch();
                                    String type = list.get(i).getType();
                                    mSwitchMusic.set(seq - 1, new UserMobile(email, seq, hornName, categoryName, hornType, hornId));
                                }

                                mIotSwitchAdapter = new IotSwitchAdapter(mSwitchMusic);
                                mIotSwitchRecyclerView.setAdapter(mIotSwitchAdapter);

                                mMobile36SwitchAdapter = new Mobile36SwitchAdapter(mSwitchMusic);
                                mMobileSwitch36RecyclerView.setAdapter(mMobile36SwitchAdapter);

                                mMobile710SwitchAdapter = new Mobile710SwitchAdapter(mSwitchMusic);
                                mMobileSwitch710RecyclerView.setAdapter(mMobile710SwitchAdapter);

                                onIotSwitchListener();
                                on36SwitchListener();
                                on710SwitchListener();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MobileSwitch> call, Throwable t) {

                    }
                });
    }

    private void onSwitchChange() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_change, null);
        RecyclerView recyclerView = view.findViewById(R.id.change_list_recycler_view);

        ChangeAdapter adapter = new ChangeAdapter(mSwitchMusic);
        //item drag&drop, swipe 설정
        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = 1800;
        dialog.show();
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        view.findViewById(R.id.change_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();
                mRetInterface.putMobileList(token, email, mSwitchMusic)
                        .enqueue(new Callback<MyPage>() {
                            @Override
                            public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                Log.e("TEST", gson.toJson(mSwitchMusic));
                                Log.e("CHANGE", response.code() + "");
                                if (response.code() == 200) {
                                    String list = "";
                                    for (int i = 0; i < mSwitchMusic.size(); i++) {
                                        list += String.valueOf(mSwitchMusic.get(i).getMobileSwitch());
                                    }
                                    if (BleManager.getInstance().isConnected(mBleDevice)) {
                                        writeData(mBleDevice,
                                                "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                onChange(list)
                                        );
                                    }
                                } else {
                                    Toast.makeText(BottomMenuActivity.this, "순서변경 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<MyPage> call, Throwable t) {
                                Toast.makeText(BottomMenuActivity.this, "순서변경 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
            }
        });

//        view.findViewById(R.id.change_cancel_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
    }

    private byte[] onChange(String collection) {
        String csm = "SSM:" + collection;
        int checksum = 0;

        byte[] csmBytes = csm.getBytes();
        Log.e("SSM", Arrays.toString(csmBytes));
        byte[] dataBytes = new byte[15];
        for (int i = 0; i < csmBytes.length; i++) {
            Log.e("GET SSM", csmBytes[i] + "");
            dataBytes[i] = csmBytes[i];
            checksum += csmBytes[i];
        }
        dataBytes[14] = (byte) (checksum % 256);
        checksum = 0;
        Log.e("DATA SSM", Arrays.toString(dataBytes));
        return dataBytes;
    }

    private void onSoundChange(String index) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_download, null);
        RecyclerView recyclerView = view.findViewById(R.id.download_list_recycler_view);
        List<Down> musicList = new ArrayList<>();
        File file = new File(getExternalFilesDir(null).getAbsolutePath());
        File[] files = file.listFiles();
        for (File tempFile : files) {
            musicList.add(new Down(tempFile.getName(), tempFile));
        }
        DownloadAdapter adapter = new DownloadAdapter(musicList);
        recyclerView.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = 1800;
        dialog.show();
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        adapter.setOnItemClickListener(new DownloadAdapter.OnDownClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mProgressDialog = new ProgressDialog(BottomMenuActivity.this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMessage("다운로드 중 취소되면 오류가\n생길 수 있습니다.");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMax(100);
                mProgressDialog.show();
//                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                try {
                    convertStreamToByteArray(musicList.get(position).getFile());
                    onDownload(mBleDevice, musicList.get(position).getName(), index);
                    dialog.dismiss();
                } catch (IOException e) {
                    Log.e("ON DOWNLOAD", e.getLocalizedMessage());
                }
            }
        });

        view.findViewById(R.id.download_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void convertStreamToByteArray(File file) throws IOException {
        FileInputStream inStream = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i = Integer.MAX_VALUE;
        while ((i = inStream.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        convertByte = baos.toByteArray();
    }

    private byte[] onData() throws IOException {
        long range = convertByte.length;
        long data_off_set = 0;

        byte[] tempList = new byte[(int) (range + ((range / 16) + 1))];
        int i = 0;
        int checksum = 0;
        do {
            System.arraycopy(convertByte, i, tempList, (int) (i + data_off_set), 16);

            for (long j = 0; j < 16; j++) {
                checksum += tempList[(int) (i + data_off_set + j)];
            }

            i += 16;
            tempList[(int) (i + data_off_set)] = (byte) checksum;

            data_off_set++;
            checksum = 0;
        } while (i + 16 < range);

        return tempList;
    }

    private void onDownload(BleDevice bleDevice, String downName, String index) {
        Realm realm = Realm.getDefaultInstance();
        UserMobile data = realm.where(UserMobile.class).equalTo("hornName", downName).findFirst();
        if (data != null) {
            mRetInterface.putMobileSw(token, email, index, new UserMobile(email, Integer.parseInt(index) + 1, data.getHornType(), data.getHornId()))
                    .enqueue(new Callback<MyPage>() {
                        @Override
                        public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                            Log.e("DOWNLOAD CODE", response.code() + "");
                            if (response.code() == 200) {
                                if (BleManager.getInstance().isConnected(bleDevice)) {
                                    try {
                                        writeData(bleDevice,
                                                "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                onData()
                                        );
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyPage> call, Throwable t) {

                        }
                    });
        }
    }

    /**
     * 모바일 스위치 자리 1 ~ 10
     **/
    private byte[] onCommend0(String index) {
        String sdn = "SDN:" + index;
        int checksum = 0;

        byte[] sdnBytes = sdn.getBytes();
        Log.e("SDN", Arrays.toString(sdnBytes));
        byte[] dataBytes = new byte[6];
        for (int i = 0; i < sdnBytes.length; i++) {
            Log.e("GET SDN", sdnBytes[i] + "");
            dataBytes[i] += sdnBytes[i];
            checksum += sdnBytes[i];
        }
        dataBytes[5] = (byte) (checksum % 256);
        checksum = 0;
        Log.e("DATA SDN", Arrays.toString(dataBytes));
        return dataBytes;
    }

    /**
     * 모바일 스위치 재생 1 ~ 10
     **/
    private byte[] onAction0(String index) {
        String psd = "PSD:" + index;
        int checksum = 0;

        byte[] psdBytes = psd.getBytes();
        Log.e("PSD", Arrays.toString(psdBytes));
        byte[] dataBytes = new byte[6];
        for (int i = 0; i < psdBytes.length; i++) {
            Log.e("GET PSD", psdBytes[i] + "");
            dataBytes[i] += psdBytes[i];
            checksum += psdBytes[i];
        }
        dataBytes[5] = (byte) (checksum % 256);
        checksum = 0;
        Log.e("DATA PSD", Arrays.toString(dataBytes));
        return dataBytes;
    }

    private Fragment fragmentContainer;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_home_btn:
//                getSupportFragmentManager().beginTransaction().add(R.id.bottom_menu_container, new HomeFragment()).addToBackStack(null).commit();
                if (fragmentContainer != null) {
                    mHomeDialog.setVisibility(View.GONE);
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mHomeBtn.setChecked(true);
                    mMusicBtn.setChecked(false);
                    mMyPageBtn.setChecked(false);
                    mMoreBtn.setChecked(false);
                    if (realm.where(UserMobile.class).findAll().size() != 0) {
                        mDownMusic = realm.where(UserMobile.class).findAll();
                    }
                }
                break;
            case R.id.bottom_music_btn:
                fragmentContainer = new SoundListFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.bottom_menu_container, new SoundListFragment()).addToBackStack(null).commit();
                break;
            case R.id.bottom_my_page_btn:
                fragmentContainer = new MyPageFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.bottom_menu_container, new MyPageFragment()).addToBackStack(null).commit();
                break;
            case R.id.bottom_more_btn:
                fragmentContainer = new MoreFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.bottom_menu_container, new MoreFragment()).addToBackStack(null).commit();
                break;
            case R.id.home_cartion_search_btn:
                startScan();
                break;
            case R.id.home_switch_change_btn:
                onSwitchChange();
                break;
        }
    }

    private void onIotSwitchListener() {
        mIotSwitchAdapter.setOnIotClickListener(new IotSwitchAdapter.OnIotClickListener() {
            @Override
            public void onItemClickListener(View v, int pos) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onAction0("" + pos)
                    );
                }
            }

            @Override
            public void onItemLongClickListener(View v, int pos) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onCommend0("" + pos)
                    );
                }
                onSoundChange("" + pos + 1);
            }
        });
    }

    private void on36SwitchListener() {
        mMobile36SwitchAdapter.setOn36ClickListener(new Mobile36SwitchAdapter.On36ClickListener() {
            @Override
            public void on36ClickListener(View view, int position) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onAction0("" + (position + 2))
                    );
                }
            }

            @Override
            public void on36LongClickListener(View view, int position) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onCommend0("" + (position + 2))
                    );
                }
                onSoundChange("" + (position + 3));
            }
        });
    }

    private void on710SwitchListener() {
        mMobile710SwitchAdapter.setOn710ClickListener(new Mobile710SwitchAdapter.On710ClickListener() {
            @Override
            public void on710ClickListener(View view, int position) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onAction0("" + (position + 5))
                    );
                }
            }

            @Override
            public void on710LongClickListener(View view, int position) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onCommend0("" + (position + 5))
                    );
                }
                onSoundChange("" + (position + 6));
            }
        });
    }

    private void onCheckPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
//                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } else {
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }
    }

    @Override
    public void onBackPressed() {
        if (pressedTime == 0) {
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if (seconds > 2000) {
                pressedTime = 0;
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BleManager.getInstance().disconnectAllDevice();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BleManager.getInstance().disconnectAllDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
    }
}