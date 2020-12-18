package net.belicon.cartion;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.belicon.cartion.adapters.CartionSettingAdapter;
import net.belicon.cartion.adapters.ChangeAdapter;
import net.belicon.cartion.models.Cartion;
import net.belicon.cartion.models.Device;
import net.belicon.cartion.models.MyPage;
import net.belicon.cartion.models.PasswordModify;
import net.belicon.cartion.models.PhoneModify;
import net.belicon.cartion.models.User;
import net.belicon.cartion.models.UserMobile;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MyPageFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RetrofitInterface mRetInterface;

    private TextView mEmailText;
    private EditText mMallEmailText, mPhoneText;
    private View mMallEmailLine, mPhoneLine;
    private RecyclerView mDeviceRecyclerView;
    private ImageButton mConfirmBtn;

    private InputMethodManager imm;

    private DeviceAdapter mAdapter;
    private CartionSettingAdapter mSettingAdapter;
    private List<Cartion> mDeviceList;

    private String token, email, mac;

    public MyPageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onViewCreated(view, savedInstanceState);

        mEmailText = view.findViewById(R.id.my_page_email_text);
        mMallEmailText = view.findViewById(R.id.my_page_mall_email_edit);
        mMallEmailText.setEnabled(false);
        mMallEmailLine = view.findViewById(R.id.my_page_mall_email_line);
        mPhoneText = view.findViewById(R.id.my_page_phone_edit);
        mPhoneText.setEnabled(false);
        mPhoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mPhoneLine = view.findViewById(R.id.my_page_phone_line);
        mDeviceRecyclerView = view.findViewById(R.id.my_page_device_recycler_view);
        mConfirmBtn = view.findViewById(R.id.my_page_settings_confirm_btn);

        token = "Bearer " + User.getUserToken();
        if (mAuth.getCurrentUser() != null) {
            email = mAuth.getCurrentUser().getEmail();
        }

        mEmailText.setText(email);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        mDeviceList = new ArrayList<>();
        mRetInterface.getUserData(token, email)
                .enqueue(new Callback<MyPage>() {
                    @Override
                    public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                        Log.e("RESPONSE CODE", "" + response.code());
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                String phone = response.body().getData().getPhoneNumber();
                                mPhoneText.setText(phone);

                                for (int i = 0; i < response.body().getData().getDevices().size(); i++) {
                                    String id = response.body().getData().getDevices().get(i).getDeviceId();
                                    String mac = response.body().getData().getDevices().get(i).getDeviceMac();
                                    String name = response.body().getData().getDevices().get(i).getDeviceName();
                                    mDeviceList.add(new Cartion(id, mac, name));
                                }
                                mAdapter = new DeviceAdapter(mDeviceList);
                                mDeviceRecyclerView.setAdapter(mAdapter);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPage> call, Throwable t) {

                    }
                });

        view.findViewById(R.id.my_page_container).setOnClickListener(this);
//        view.findViewById(R.id.my_page_phone_modify_btn).setOnClickListener(this);
        view.findViewById(R.id.my_page_cartion_settings_btn).setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomMenuActivity.mHomeBtn.setChecked(false);
        BottomMenuActivity.mMusicBtn.setChecked(false);
        BottomMenuActivity.mMyPageBtn.setChecked(true);
        BottomMenuActivity.mMoreBtn.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        imm.hideSoftInputFromWindow(mPhoneText.getWindowToken(), 0);
        switch (v.getId()) {
//            case R.id.my_page_password_modify_btn:
//                mRetInterface.putPasswordModify(token, email, new PasswordModify(password))
//                        .enqueue(new Callback<MyPage>() {
//                            @Override
//                            public void onResponse(Call<MyPage> call, Response<MyPage> response) {
//                                if (response.code() == 200) {
//                                    Toast.makeText(getActivity(), "변경 되었습니다.", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<MyPage> call, Throwable t) {
//
//                            }
//                        });
//                break;
//            case R.id.my_page_phone_modify_btn:
//                if (!mPhoneText.isEnabled()) {
//                    mPhoneText.setEnabled(true);
//                } else {
//                    String phone = mPhoneText.getText().toString();
//                    mRetInterface.putPhoneModify(token, email, new PhoneModify(phone))
//                            .enqueue(new Callback<MyPage>() {
//                                @Override
//                                public void onResponse(Call<MyPage> call, Response<MyPage> response) {
//                                    if (response.code() == 200) {
//                                        mPhoneText.setEnabled(false);
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyPage> call, Throwable t) {
//
//                                }
//                            });
//                }
//                break;
            case R.id.my_page_cartion_settings_btn:
                if (BleManager.getInstance().isConnected(BottomMenuActivity.mBleDevice)) {
                    if (getActivity() != null) {
                        ((BottomMenuActivity) getActivity()).onEventListen();
                    }
                }
                if (!mMallEmailText.isEnabled()) {
                    mMallEmailText.setEnabled(true);
                }
                if (!mPhoneText.isEnabled()) {
                    mPhoneText.setEnabled(true);
                }
                mMallEmailLine.setVisibility(View.VISIBLE);
                mPhoneLine.setVisibility(View.VISIBLE);
                CartionSettingAdapter adapter = new CartionSettingAdapter(((BottomMenuActivity) getActivity()), mRetInterface, BottomMenuActivity.mBleDevice, mDeviceList, token, email);
                mDeviceRecyclerView.setAdapter(adapter);
                mConfirmBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.my_page_settings_confirm_btn:
                Pattern p = Pattern.compile("^[a-zA-X0-9]@[a-zA-Z0-9].[a-zA-Z0-9]");
                Matcher m = p.matcher(mMallEmailText.getText().toString());
                String phone = mPhoneText.getText().toString().replaceAll("-", "");
                if (!mMallEmailText.getText().toString().equals("") && !m.matches()) {
                    Toast.makeText(getActivity(), "쇼핑몰 아이디가 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                } else if (phone.length() != 11) {
                    Toast.makeText(getActivity(), "전화번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    onModify(phone);
                }
                break;
        }
    }

    private void onModify(String phone) {
        mMallEmailLine.setVisibility(View.GONE);
        mMallEmailText.setEnabled(false);
        mPhoneLine.setVisibility(View.GONE);
        mPhoneText.setEnabled(false);
        mRetInterface.putPhoneModify(token, email, new PhoneModify(phone))
                .enqueue(new Callback<MyPage>() {
                    @Override
                    public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                        if (response.code() == 200) {
                            // 전화번호 변경
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPage> call, Throwable t) {

                    }
                });
//                mAdapter = new DeviceAdapter(mDeviceList);
        mDeviceRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mConfirmBtn.setVisibility(View.GONE);
    }

//    private void onPasswordChange() {
//        String password = mModifyPassword.getText().toString();
//        AuthCredential credential = EmailAuthProvider.getCredential(email, mPassword.getText().toString());
//
//        // Prompt the user to re-provide their sign-in credentials
//        mAuth.getCurrentUser().reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            mAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        // Update
//                                    } else {
//                                        //
//                                    }
//                                }
//                            });
//                        } else {
//                            //
//                        }
//                    }
//                });
//    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

        private List<Cartion> mDeviceList;

        public DeviceAdapter(List<Cartion> mDeviceList) {
            this.mDeviceList = mDeviceList;
        }

        @NonNull
        @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
            Cartion item = mDeviceList.get(position);
            holder.mDeviceText.setText(item.getDeviceId() + " (" + item.getDeviceName() + ")");
        }

        @Override
        public int getItemCount() {
            if (mDeviceList.size() == 0) {
                return 0;
            }
            return mDeviceList.size();
        }

        private class DeviceViewHolder extends RecyclerView.ViewHolder {

            private TextView mDeviceText;

            public DeviceViewHolder(@NonNull View itemView) {
                super(itemView);

                mDeviceText = itemView.findViewById(R.id.item_device_id);
            }
        }
    }
}
