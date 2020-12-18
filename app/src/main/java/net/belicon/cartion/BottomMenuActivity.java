package net.belicon.cartion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import net.belicon.cartion.R;
import net.belicon.cartion.adapters.BannerPagerAdapter;
import net.belicon.cartion.adapters.ChangeAdapter;
import net.belicon.cartion.adapters.DownloadAdapter;
import net.belicon.cartion.adapters.DownloadDeleteAdapter;
import net.belicon.cartion.adapters.IotSwitchAdapter;
import net.belicon.cartion.adapters.Mobile36SwitchAdapter;
import net.belicon.cartion.adapters.Mobile710SwitchAdapter;
import net.belicon.cartion.models.Banner;
import net.belicon.cartion.models.Cartion;
import net.belicon.cartion.models.Coupon;
import net.belicon.cartion.models.CouponList;
import net.belicon.cartion.models.Device;
import net.belicon.cartion.models.Down;
import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.MobileSwitch;
import net.belicon.cartion.models.MyPage;
import net.belicon.cartion.models.RefreshToken;
import net.belicon.cartion.models.SwitchList;
import net.belicon.cartion.models.User;
import net.belicon.cartion.models.UserMobile;
import net.belicon.cartion.retrofites.GlideApp;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
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
    private ImageButton mCartionSearchBtn, m36PurchaseBtn, m710PurchaseBtn, mEvent36Btn, mEvent710Btn;
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

    public static BleDevice mBleDevice;
    private String token, email, mac, serial;
    private byte[] convertByte;
    private boolean isUserCheck = false;
    private long pressedTime;
    private boolean isEventMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onCheckPermission();
        setContentView(R.layout.activity_bottom_menu);

        findViewById(R.id.bottom_menu_container).setOnClickListener(this);

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
        mEvent36Btn = findViewById(R.id.home_event_mode_36_btn);
        mEvent710Btn = findViewById(R.id.home_event_mode_710_btn);

        String noti = mSearchNotiText.getText().toString();
        String cn = "'검색 버튼'";
        int start = noti.indexOf(cn);
        int end = start + cn.length();
        SpannableString span = new SpannableString(noti);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface typeface = getResources().getFont(R.font.scd6);
            span.setSpan(new TypefaceSpan(typeface), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            span.setSpan(new StyleSpan(BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
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
                .setReConnectCount(3, 3000)
                .setSplitWriteNum(17)
                .setConnectOverTime(100000)
                .setOperateTimeout(5000);

        mCartionSearchBtn.setOnClickListener(this);

        findViewById(R.id.home_switch_change_btn).setOnClickListener(this);
        findViewById(R.id.home_lost_switch_btn).setOnClickListener(this);
        findViewById(R.id.home_mobile_switch_36_purchase_btn).setOnClickListener(this);
        findViewById(R.id.home_mobile_switch_710_purchase_btn).setOnClickListener(this);
        findViewById(R.id.home_event_mode_36_btn).setOnClickListener(this);
        findViewById(R.id.home_event_mode_710_btn).setOnClickListener(this);

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
                                    Log.e("BANNER", url);
                                    data.add(url);
                                }
                                BannerPagerAdapter scrollAdapter = new BannerPagerAdapter(BottomMenuActivity.this, data);
                                mBannerPager.setAdapter(scrollAdapter); //Auto Viewpager에 Adapter 장착

                                scrollAdapter.setOnBannerClickListener(new BannerPagerAdapter.OnBannerClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(response.body().getData().getBannerList().get(position).getLinkUrl())));
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Banner> call, Throwable t) {

                    }
                });

        mHomeBtn.setChecked(true);
        onCoupon();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHomeDialog.setVisibility(View.GONE);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "블루투스가 지원되지 않습니다.", Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "블루투스가 꺼져 있습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("BLE", "ON");
        }
        if (Realm.getDefaultInstance() != null) {
            realm = Realm.getDefaultInstance();
        }

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
                                } else {
                                    onSafeView();
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
        span.setSpan(new AbsoluteSizeSpan(18, true), one_start, one_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

        EditText coupon = view.findViewById(R.id.coupon_edit);
        view.findViewById(R.id.coupon_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coupon.getText().toString().equals("카션")) {
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
                } else {
                    Toast.makeText(BottomMenuActivity.this, "쿠폰을 올바르게 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onSafeView() {
        SharedPreferences preferences = getSharedPreferences("safe_key", Context.MODE_PRIVATE);
        if (preferences != null) {
            boolean safe = preferences.getBoolean("safe", false);
            if (!safe) {
                new SafeNotiDialogFragment().show(getSupportFragmentManager(), "safe_dialog");
            }
        }
    }

    public void onServerDevice() {
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
                                    if (macList.size() != 0) {
                                        isUserCheck = true;
                                        mac = devices.get(0).getDeviceMac();
                                        mNicNameText.setText(devices.get(0).getDeviceName());
                                        Log.e("USER MAC", mac);
                                    } else {
                                        isUserCheck = false;
                                        mNicNameText.setText("Cartion");
                                    }
                                    startScan();
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


    public void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                mHomeDialog.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (isUserCheck) {
                    if (bleDevice.getName() != null && bleDevice.getMac().startsWith(mac)) {
                        Log.e("CARTION", bleDevice.getName());
                        BleManager.getInstance().cancelScan();
                        connect(bleDevice);
                        Log.e("CartionMac", bleDevice.getMac());
                        mBleDevice = bleDevice;
                        mHomeDialog.setVisibility(View.GONE);
                        mac = bleDevice.getMac();
                    }
                } else {
                    if (bleDevice.getName() != null && bleDevice.getName().equals("Cartion") && bleDevice.getRssi() > -60) {
                        Log.e("CARTION", bleDevice.getName());
                        BleManager.getInstance().cancelScan();
                        connect(bleDevice);
                        mBleDevice = bleDevice;
                        mHomeDialog.setVisibility(View.GONE);
                        mac = bleDevice.getMac();
                    }
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                mHomeDialog.setVisibility(View.GONE);
//                if (mBleDevice != null) {
//                } else {
//                    Toast.makeText(BottomMenuActivity.this, "검색되는 카션이 없습니다.", Toast.LENGTH_LONG).show();
//                }
                if (mBleDevice == null) {
                    Toast.makeText(BottomMenuActivity.this, "검색되는 카션이 없습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("MAC", mac);
                }
            }
        });
    }

    public void connect(final BleDevice bleDevice) {
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
                mBleDevice = bleDevice;
                String SERVICE_UUID = gatt.getServices().get(2).getUuid().toString();
                String CHARACTERISTIC_UUID = gatt.getServices().get(2).getCharacteristics().get(1).getUuid().toString();
                Log.e("SERVICE UUID", SERVICE_UUID);
                Log.e("CHARACTERISTIC UUID", CHARACTERISTIC_UUID);
                notified(bleDevice, SERVICE_UUID, CHARACTERISTIC_UUID);
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
                                mHomeDialog.setVisibility(View.GONE);
//                                Log.e("NOTIFY", HexUtil.formatHexString(data, true));
                                String s = new String(data);
//                                Log.e("NOTIFY DATA 1", s);
                                Log.e("NOTIFIED", s);
                                if (s.startsWith("ca") || s.startsWith("FF")) {
                                    onVersionCheck();
                                    serial = s;
                                }

                                if (s.startsWith("FwVer")) {
                                    if (BleManager.getInstance().isConnected(mBleDevice)) {
                                        ExecutorService es = Executors.newSingleThreadExecutor();
                                        es.submit(new Runnable() {
                                            @Override
                                            public void run() {
                                                writeData(mBleDevice,
                                                        "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                        "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                        onEmailPost("id1:" + email.substring(0, email.indexOf("@")))
                                                );
                                            }
                                        });
                                        es.submit(new Runnable() {
                                            @Override
                                            public void run() {
                                                writeData(mBleDevice,
                                                        "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                        "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                        onAddressPost("id2:" + email.substring(email.indexOf("@") + 1))
                                                );
                                            }
                                        });
                                        es.shutdown();
                                    }
                                }

                                if (s.equals("Success") || s.equals("Free Passage")) {
                                    mSearchContainer.setVisibility(View.GONE);
                                    mInfoContainer.setVisibility(View.VISIBLE);
                                    if (!isUserCheck) {
                                        mRetInterface.postCartion(token, email, new Cartion(serial, mac.substring(0, 11), "Cartion"))
                                                .enqueue(new Callback<MyPage>() {
                                                    @Override
                                                    public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                                                        Log.e("CARTION SUCCESS", "" + response.code());
                                                        if (response.code() == 200 || response.code() == 201) {
                                                            Toast.makeText(BottomMenuActivity.this, "카션이 등록되었습니다.", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(BottomMenuActivity.this, "카션 등록이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<MyPage> call, Throwable t) {

                                                    }
                                                });
                                    }
                                } else if (s.equals("Failure")) {
                                    Toast.makeText(BottomMenuActivity.this, "다른 사용자 ID의 카션입니다.", Toast.LENGTH_LONG).show();
                                    mSearchContainer.setVisibility(View.VISIBLE);
                                    mInfoContainer.setVisibility(View.GONE);
                                }
                                if (s.contains("Battery Level")) {
                                    String battery = s.replace("Battery Level:", "");
                                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                                    anim.setDuration(1000);
                                    anim.setStartOffset(20);
                                    anim.setRepeatMode(Animation.REVERSE);
                                    anim.setRepeatCount(Animation.INFINITE);
                                    mBatteryText.setText(battery);
//                                    Log.e("TESTSESTS", Integer.parseInt(battery.replace("%", "")) + "");
                                    if (Integer.parseInt(battery.replace("%", "").replace(" ", "")) <= 25) {
                                        mBatteryText.setTextColor(Color.RED);
                                        mBatteryText.startAnimation(anim);
                                        Toast.makeText(BottomMenuActivity.this, "충전이 필요합니다.", Toast.LENGTH_LONG).show();
                                    }
                                } else if (s.contains("Temperature") && !s.contains("Max Temperature")) {
                                    mTemperatureText.setText(s.replace("Temperature:", "") + "°C");
                                }
                                if (s.contains("EEM:0")) {
                                    Log.e("Event Mode", "Enabled");
                                    isEventMode = true;
                                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_event_switch_label).into(mEvent36Btn);
                                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_event_switch_label).into(mEvent710Btn);
                                    mMobileSwitch36RecyclerView.setBackgroundResource(R.drawable.ic_event_switch_background);
                                    mMobileSwitch710RecyclerView.setBackgroundResource(R.drawable.ic_event_switch_background);
                                    if (mMobile36SwitchAdapter != null) {
                                        mMobile36SwitchAdapter.setItemViewType(Mobile36SwitchAdapter.EVENT_TYPE);
                                        mMobile710SwitchAdapter.setItemViewType(Mobile710SwitchAdapter.EVENT_TYPE);
                                    }
                                } else if (s.contains("EEM:1")) {
                                    Log.e("Event Mode", "Disabled");
                                    isEventMode = false;
                                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_moblie_switch_label_1).into(mEvent36Btn);
                                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_moblie_switch_label_2).into(mEvent710Btn);
                                    mMobileSwitch36RecyclerView.setBackgroundResource(R.drawable.ic_background_3_10);
                                    mMobileSwitch710RecyclerView.setBackgroundResource(R.drawable.ic_background_3_10);
                                    if (mMobile36SwitchAdapter != null) {
                                        mMobile36SwitchAdapter.setItemViewType(Mobile36SwitchAdapter.NORMAL_TYPE);
                                        mMobile710SwitchAdapter.setItemViewType(Mobile710SwitchAdapter.NORMAL_TYPE);
                                    }
                                }
                                if (s.contains("Event Mode Enabled")) {
                                    isEventMode = true;
                                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_event_switch_label).into(mEvent36Btn);
                                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_event_switch_label).into(mEvent710Btn);
                                    mMobileSwitch36RecyclerView.setBackgroundResource(R.drawable.ic_event_switch_background);
                                    mMobileSwitch710RecyclerView.setBackgroundResource(R.drawable.ic_event_switch_background);
                                    if (mMobile36SwitchAdapter != null) {
                                        mMobile36SwitchAdapter.setItemViewType(Mobile36SwitchAdapter.EVENT_TYPE);
                                        mMobile710SwitchAdapter.setItemViewType(Mobile710SwitchAdapter.EVENT_TYPE);
                                    }
                                } else if (s.contains("Event Mode Disabled")) {
                                    isEventMode = false;
                                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_moblie_switch_label_1).into(mEvent36Btn);
                                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_moblie_switch_label_2).into(mEvent710Btn);
                                    mMobileSwitch36RecyclerView.setBackgroundResource(R.drawable.ic_background_3_10);
                                    mMobileSwitch710RecyclerView.setBackgroundResource(R.drawable.ic_background_3_10);
                                    if (mMobile36SwitchAdapter != null) {
                                        mMobile36SwitchAdapter.setItemViewType(Mobile36SwitchAdapter.NORMAL_TYPE);
                                        mMobile710SwitchAdapter.setItemViewType(Mobile710SwitchAdapter.NORMAL_TYPE);
                                    }
                                }
                                if (s.contains("CTE")) {
                                    Log.e("CTE", s);
                                }
                                mHomeDialog.setVisibility(View.GONE);
                            }
                        });
                    }
                });
    }

    public void writeData(BleDevice bleDevice, String uuid_service, String uuid_characteristic_write, byte[] data) {
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
//                                Log.e("CURRENT", (int) ((double) current / (double) total * 100.0) + "");
                                mProgressDialog.setProgress((int) ((double) current / (double) total * 100.0));
                                if ((int) ((double) current / (double) total * 100.0) == 100) {
                                    mProgressDialog.dismiss();
                                    mRetInterface.getMobileSwitch(token, email)
                                            .enqueue(new Callback<MobileSwitch>() {
                                                @Override
                                                public void onResponse(Call<MobileSwitch> call, Response<MobileSwitch> response) {
                                                    if (response.code() == 200) {
                                                        if (response.body() != null) {
                                                            List<SwitchList> list = response.body().getData().getHornList();
                                                            for (int i = 0; i < list.size(); i++) {
                                                                String userId = list.get(i).getUserId();
                                                                String hornType = list.get(i).getHornType();
                                                                String hornId = list.get(i).getHornId();
                                                                String hornName = list.get(i).getHornName();
                                                                String categoryName = list.get(i).getCategoryName();
                                                                int mobileSwitch = list.get(i).getMobileSwitch();
                                                                int seq = list.get(i).getSeq();
                                                                String type = list.get(i).getType();
                                                                mSwitchMusic.set(i, new UserMobile(email, mobileSwitch, i + 1, hornName, categoryName, hornType, hornId));
                                                            }
                                                            mIotSwitchAdapter.notifyDataSetChanged();
                                                            mMobile36SwitchAdapter.notifyDataSetChanged();
                                                            mMobile710SwitchAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<MobileSwitch> call, Throwable t) {

                                                }
                                            });
                                }
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
            mSwitchMusic.add(new UserMobile(email, i + 1, i, "기본", "기본음", "horn", String.valueOf(i + 1)));
        }

        mRetInterface.getMobileSwitch(token, email)
                .enqueue(new Callback<MobileSwitch>() {
                    @Override
                    public void onResponse(Call<MobileSwitch> call, Response<MobileSwitch> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                int mobileSw = response.body().getData().getMobileSwitch();
                                if (mobileSw == 0) {
//                                    m36Container.setVisibility(View.INVISIBLE);
                                    m36PurchaseBtn.setVisibility(View.VISIBLE);
                                } else if (mobileSw == 1) {
                                    m36Line.setVisibility(View.VISIBLE);
                                    m36Container.setVisibility(View.VISIBLE);
//                                    m710Container.setVisibility(View.INVISIBLE);
                                    m710PurchaseBtn.setVisibility(View.VISIBLE);
                                } else if (mobileSw == 2) {
                                    m36Line.setVisibility(View.VISIBLE);
                                    m36Container.setVisibility(View.VISIBLE);
                                    m710Line.setVisibility(View.VISIBLE);
                                    m710Container.setVisibility(View.VISIBLE);
                                }
                                List<SwitchList> list = response.body().getData().getHornList();
                                for (int i = 0; i < list.size(); i++) {
                                    String userId = list.get(i).getUserId();
                                    String hornType = list.get(i).getHornType();
                                    String hornId = list.get(i).getHornId();
                                    String hornName = list.get(i).getHornName();
                                    String categoryName = list.get(i).getCategoryName();
                                    int mobileSwitch = list.get(i).getMobileSwitch();
                                    int seq = list.get(i).getSeq();
                                    String type = list.get(i).getType();
//                                    Log.e("SwP", mobileSwitch + "");
//                                    String wavPath = "";
//                                    if (realm.where(UserMobile.class).equalTo("hornName", hornName).findFirst() != null) {
//                                        if (realm.where(UserMobile.class).findAll().size() != 0) {
//                                            wavPath = realm.where(UserMobile.class).equalTo("hornName", hornName).findFirst().getWavPath();
//                                        }
//                                    } else {
//                                        List<String> stringList = Arrays.asList(getResources().getStringArray(R.array.normal));
//                                        wavPath = stringList.get(i);
//                                    }
//                                    StringBuffer name = new StringBuffer(hornName);
//                                    name.replace(hornName.lastIndexOf("_") + 1, hornName.length(), wavPath);
//                                    Log.e("NAME", name.toString());
                                    mSwitchMusic.set(i, new UserMobile(email, mobileSwitch, i + 1, hornName, categoryName, hornType, hornId));
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

        ChangeAdapter adapter = new ChangeAdapter(BottomMenuActivity.this, mSwitchMusic);
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
                for (int i = 0; i < mSwitchMusic.size(); i++) {
                    String hornType = mSwitchMusic.get(i).getHornType();
                    String hornId = mSwitchMusic.get(i).getHornId();
                    String hornName = mSwitchMusic.get(i).getHornName();
                    String categoryName = mSwitchMusic.get(i).getCategoryName();
                    int mobileSwitch = mSwitchMusic.get(i).getMobileSwitch();
                    mSwitchMusic.set(i, new UserMobile(email, mobileSwitch, i + 1, hornName, categoryName, hornType, hornId));
                }
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
                                        list += String.valueOf(mSwitchMusic.get(i).getMobileSwitch() - 1);
                                    }
                                    Log.e("CARTION POSITION", list);
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
                                mIotSwitchAdapter.notifyDataSetChanged();
                                mMobile36SwitchAdapter.notifyDataSetChanged();
                                mMobile710SwitchAdapter.notifyDataSetChanged();
                                onEventListen();
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<MyPage> call, Throwable t) {
                                Toast.makeText(BottomMenuActivity.this, "순서변경 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                onEventListen();
                                dialog.dismiss();
                            }
                        });
            }
        });

        adapter.setOnItemTouchListener(new ChangeAdapter.OnChangeTouchListener() {
            @Override
            public void onItemTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        onEventListen();
                        break;
                }
            }
        });
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

    private boolean isDelete = false;

    private void onSoundChange(String mobileSwitch, String index) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_download, null);
        RecyclerView recyclerView = view.findViewById(R.id.download_list_recycler_view);
        List<Down> musicList = new ArrayList<>();
        File file = new File(String.valueOf(getExternalFilesDir(null)));
        File[] files = file.listFiles();
        for (File tempFile : files) {
            String hornId = "";
            String categoryName = "기본";
            if (realm.where(UserMobile.class).equalTo("hornName", tempFile.getName()).findFirst() != null) {
                if (realm.where(UserMobile.class).findAll().size() != 0) {
                    hornId = realm.where(UserMobile.class).equalTo("hornName", tempFile.getName()).findFirst().getHornId();
                    categoryName = realm.where(UserMobile.class).equalTo("hornName", tempFile.getName()).findFirst().getCategoryName();
                    categoryName = (categoryName == null) ? "기본" : categoryName;
                } else {
                    hornId = "";
                    categoryName = "기본";
                }
            } else {
                hornId = "";
                categoryName = "기본";
            }
            musicList.add(new Down(tempFile.getName(), tempFile, hornId, categoryName));
        }
        DownloadAdapter adapter = new DownloadAdapter(BottomMenuActivity.this, musicList, token, mRetInterface);
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
                try {
                    convertStreamToByteArray(musicList.get(position).getFile());
                    onDownload(mBleDevice, musicList.get(position).getName(), mobileSwitch, index);
                    dialog.dismiss();
                } catch (IOException e) {
                    Log.e("ON DOWNLOAD", e.getLocalizedMessage());
                }
            }
        });

        ImageButton cancel = view.findViewById(R.id.download_cancel_btn);
        view.findViewById(R.id.download_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadDeleteAdapter deleteAdapter = new DownloadDeleteAdapter(musicList);
                recyclerView.setAdapter(deleteAdapter);
                isDelete = true;
                Glide.with(BottomMenuActivity.this).load(R.drawable.ic_confirm_button).into(cancel);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventListen();
                if (isDelete) {
                    Glide.with(BottomMenuActivity.this).load(R.drawable.ic_cancel_button).into(cancel);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isDelete = false;
                } else {
                    dialog.dismiss();
                }
            }
        });

        adapter.setOnItemTouchListener(new DownloadAdapter.OnDownTouchListener() {
            @Override
            public void onItemTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        onEventListen();
                        break;
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onEventListen();
                break;
        }
        return super.dispatchTouchEvent(ev);
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

    private void onDownload(BleDevice bleDevice, String downName, String mobileSwitch, String index) {
        Realm realm = Realm.getDefaultInstance();
        UserMobile data = realm.where(UserMobile.class).equalTo("hornName", downName).findFirst();
        if (data != null) {
            mRetInterface.putMobileSw(token, email, mobileSwitch, new UserMobile(email, Integer.parseInt(mobileSwitch), Integer.parseInt(index), data.getHornType(), data.getHornId()))
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

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                Log.e("CLICKED", "MainMainMainMainMainMainMainMainMainMainMain");
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_home_btn:
                onEventListen();
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
                onEventListen();
                fragmentContainer = new SoundListFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.bottom_menu_container, new SoundListFragment()).addToBackStack(null).commit();
                break;
            case R.id.bottom_my_page_btn:
                onEventListen();
                fragmentContainer = new MyPageFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.bottom_menu_container, new MyPageFragment()).addToBackStack(null).commit();
                break;
            case R.id.bottom_more_btn:
                fragmentContainer = new MoreFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.bottom_menu_container, new MoreFragment()).addToBackStack(null).commit();
                break;
            case R.id.home_cartion_search_btn:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(this, "블루투스가 지원되지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, "블루투스가 꺼져 있습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    BleManager.getInstance().disconnectAllDevice();
                    onServerDevice();
                }
                break;
            case R.id.home_switch_change_btn:
                onSwitchChange();
                break;
            case R.id.home_lost_switch_btn:
                onLostSwitch();
                break;
            case R.id.home_event_mode_36_btn:
            case R.id.home_event_mode_710_btn:
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    String commend;
                    if (isEventMode) {
                        isEventMode = false;
                        commend = "EEM:1";
                    } else {
                        isEventMode = true;
                        commend = "EEM:0";
                    }
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onEventMode(commend)
                    );
                }
                break;
            case R.id.home_mobile_switch_36_purchase_btn:
            case R.id.home_mobile_switch_710_purchase_btn:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cartion.co.kr/front/goods/goodsDetail.do?goodsNo=G2012161125_0017")));
                break;
        }
    }

    private byte[] onEventMode(String commend) {
        int checksum = 0;

        byte[] bytes = commend.getBytes();
        byte[] dataBytes = new byte[commend.length() + 1];
        for (int i = 0; i < commend.length(); i++) {
            dataBytes[i] = bytes[i];
            checksum += bytes[i];
        }
        dataBytes[commend.length()] = (byte) (checksum % 256);
        return dataBytes;
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
//                            onAction0("" + (mSwitchMusic.get(pos).getMobileSwitch() - 1))
                    );
                }
            }

            @Override
            public void onItemLongClickListener(View v, int pos) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onCommend0("" + (mSwitchMusic.get(pos).getMobileSwitch() - 1))
                    );
                }
                onEventListen();
                onSoundChange("" + mSwitchMusic.get(pos).getMobileSwitch(), "" + (pos + 1));
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
                            onCommend0("" + ((mSwitchMusic.get(position).getMobileSwitch() - 1) + 2))
                    );
                }
                onEventListen();
                onSoundChange("" + mSwitchMusic.get(position + 2).getMobileSwitch(), "" + (position + 3));
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
                            onCommend0("" + ((mSwitchMusic.get(position).getMobileSwitch() - 1) + 5))
                    );
                }
                onEventListen();
                onSoundChange("" + mSwitchMusic.get(position + 5).getMobileSwitch(), "" + (position + 6));
            }
        });
    }

    private void onLostSwitch() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_lost_switch, null);

        TextView messageText = view.findViewById(R.id.home_lost_message_text);

        String message = messageText.getText().toString();
        String str = "분실";
        int start = message.indexOf(str);
        int end = start + str.length();
        SpannableString span = new SpannableString(message);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_light)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

        view.findViewById(R.id.home_lost_switch_no_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.home_lost_switch_yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    writeData(mBleDevice,
                            "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                            "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                            onLost()
                    );
                    Toast.makeText(BottomMenuActivity.this, "기존 스위치가 제거 되었습니다. 앱을 종료 후, 등록할 스위치의 버튼을 눌러 주세요.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    private byte[] onLost() {
        String dsi = "DSI:0";
        int checksum = 0;

        byte[] dsiBytes = dsi.getBytes();
        Log.e("DSI", Arrays.toString(dsiBytes));
        byte[] dataBytes = new byte[6];
        for (int i = 0; i < dsiBytes.length; i++) {
            Log.e("GET DSI", dsiBytes[i] + "");
            dataBytes[i] = dsiBytes[i];
            checksum += dsiBytes[i];
        }
        dataBytes[5] = (byte) (checksum % 256);
        checksum = 0;
        Log.e("DATA DSI", Arrays.toString(dataBytes));
        return dataBytes;
    }

    private void onVersionCheck() {
        if (BleManager.getInstance().isConnected(mBleDevice)) {
            Log.e("COMMEND", "버전 확인");
            writeData(mBleDevice,
                    "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                    "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                    onVersion("AppVer:" + (getAndroidVersion() * 100))
            );
        }
    }

    public void onEventListen() {
        if (BleManager.getInstance().isConnected(mBleDevice)) {
            writeData(mBleDevice,
                    "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                    "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                    onListen("CTE:0")
            );
        }
    }

    private byte[] onVersion(String version) {
        int checksum = 0;

        byte[] bytes = version.getBytes();
        byte[] dataBytes = new byte[version.length() + 1];
        for (int i = 0; i < bytes.length; i++) {
            dataBytes[i] = bytes[i];
            checksum += bytes[i];
        }
        dataBytes[version.length()] = (byte) (checksum % 256);
        checksum = 0;
        return dataBytes;
    }

    private byte[] onListen(String event) {
        int checksum = 0;

        byte[] bytes = event.getBytes();
        byte[] dataBytes = new byte[event.length() + 1];
        for (int i = 0; i < bytes.length; i++) {
            dataBytes[i] = bytes[i];
            checksum += bytes[i];
        }
        dataBytes[event.length()] = (byte) (checksum % 256);
        checksum = 0;
        return dataBytes;
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
                    .setDeniedMessage("권한을 거부 할 경우 본 서비스를 이용하실 수 없습니다.\n\n[설정]> [권한]에서 권한을 켜주세요.")
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
                    .setDeniedMessage("권한을 거부 할 경우 본 서비스를 이용하실 수 없습니다.\n\n[설정]> [권한]에서 권한을 켜주세요.")
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
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        } else {
            if (pressedTime == 0) {
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if (seconds > 2000) {
                    pressedTime = 0;
                } else {
//                    super.onBackPressed();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }

    private int getAndroidVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int version_code = pInfo.versionCode;
            return version_code;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
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