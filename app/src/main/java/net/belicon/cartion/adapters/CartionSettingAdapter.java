package net.belicon.cartion.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import net.belicon.cartion.BottomMenuActivity;
import net.belicon.cartion.R;
import net.belicon.cartion.models.Cartion;
import net.belicon.cartion.models.MyPage;
import net.belicon.cartion.retrofites.RetrofitInterface;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartionSettingAdapter extends RecyclerView.Adapter<CartionSettingAdapter.CartionSettingViewHolder> {

    private Activity mActivity;
    private RetrofitInterface mRetrofit;
    private BleDevice mBleDevice;
    private List<Cartion> mCartions;

    private String token, email;
    private boolean isNic = false;

    public CartionSettingAdapter(BottomMenuActivity activity, RetrofitInterface mRetrofit, BleDevice mBleDevice, List<Cartion> mCartions, String token, String email) {
        this.mActivity = activity;
        this.mRetrofit = mRetrofit;
        this.mCartions = mCartions;
        this.mBleDevice = mBleDevice;
        this.token = token;
        this.email = email;
    }

    @NonNull
    @Override
    public CartionSettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartionSettingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cartion_setting, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartionSettingViewHolder holder, int position) {
        Cartion device = mCartions.get(position);
        holder.mSerialText.setText(device.getDeviceId());
        if (device.getDeviceName().equals("Cartion") || device.getDeviceName().equals("")) {
            holder.mNicText.setText("");
            holder.mNicText.setHint("??????");
        } else {
            holder.mNicText.setHint(device.getDeviceName());
        }

        holder.mNicText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isNic = true;
                Glide.with(holder.mDeleteBtn.getContext()).load(R.drawable.ic_confirm_button).into(holder.mDeleteBtn);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNic) {
                    if (holder.mNicText.getText().toString().isEmpty()) {
                        Glide.with(holder.mDeleteBtn.getContext()).load(R.drawable.ic_delete).into(holder.mDeleteBtn);
                    } else {
                        mRetrofit.putCartionName(token, device.getDeviceId(), new Cartion(null, null, holder.mNicText.getText().toString()))
                                .enqueue(new Callback<MyPage>() {
                                    @Override
                                    public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                                        if (response.code() == 200) {
                                            isNic = false;
                                            Glide.with(holder.mDeleteBtn.getContext()).load(R.drawable.ic_delete).into(holder.mDeleteBtn);
                                            mCartions.set(position, new Cartion(device.getDeviceId(), device.getDeviceMac(), holder.mNicText.getText().toString()));
                                            Toast.makeText(mActivity, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyPage> call, Throwable t) {

                                    }
                                });
                    }
                } else {
                    if (BleManager.getInstance().isConnected(mBleDevice)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.mDeleteBtn.getContext());
                        builder.setTitle("?????? ??????").setMessage("????????? ???????????? ????????? ???????????????. ??? ?????? ???, ????????? ????????? ????????? ?????? ??? ????????????. ?????? ???????????????????")
                                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mRetrofit.deleteCartion(token, email, device.getDeviceId())
                                                .enqueue(new Callback<MyPage>() {
                                                    @Override
                                                    public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                                                        if (response.code() == 200) {
                                                            mCartions.remove(position);
                                                            notifyDataSetChanged();
                                                            ((BottomMenuActivity) mActivity).writeData(mBleDevice,
                                                                    "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                                    "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                                    onReset()
                                                            );
                                                            ((BottomMenuActivity) mActivity).writeData(mBleDevice,
                                                                    "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                                    "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                                    onDelete()
                                                            );
                                                            ((BottomMenuActivity) mActivity).isUserCheck = false;
                                                            Toast.makeText(holder.mDeleteBtn.getContext(), "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<MyPage> call, Throwable t) {

                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Toast.makeText(holder.mDeleteBtn.getContext(), "???(?????????)??????????????? ?????? ???????????? ?????? ??? ?????? ???????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // ?????? ?????? ?????????
    private byte[] onReset() {
        String post = "SSM:0123456789";
        int checksum = 0;

        byte[] bytes = post.getBytes();
        byte[] dataBytes = new byte[post.length() + 1];
        for (int i = 0; i < bytes.length; i++) {
            Log.e("GET PSD", bytes[i] + "");
            dataBytes[i] += bytes[i];
            checksum += bytes[i];
        }
        dataBytes[post.length()] = (byte) (checksum % 256);
        checksum = 0;
        return dataBytes;
    }

    // ????????? ?????? ?????? ?????????
    private byte[] onDelete() {
        String post = "DUI:0";
        int checksum = 0;

        byte[] bytes = post.getBytes();
        byte[] dataBytes = new byte[post.length() + 1];
        for (int i = 0; i < bytes.length; i++) {
            Log.e("GET PSD", bytes[i] + "");
            dataBytes[i] += bytes[i];
            checksum += bytes[i];
        }
        dataBytes[post.length()] = (byte) (checksum % 256);
        checksum = 0;
        return dataBytes;
    }

    @Override
    public int getItemCount() {
        return mCartions.size();
    }

    static class CartionSettingViewHolder extends RecyclerView.ViewHolder {

        private TextView mSerialText;
        private EditText mNicText;
        private ImageButton mDeleteBtn;

        public CartionSettingViewHolder(@NonNull View itemView) {
            super(itemView);
            mSerialText = itemView.findViewById(R.id.item_cartion_serial_text);
            mNicText = itemView.findViewById(R.id.item_cartion_nic_text);
            mDeleteBtn = itemView.findViewById(R.id.item_cartion_delete_btn);
        }
    }
}
