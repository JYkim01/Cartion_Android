package net.belicon.cartion.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import net.belicon.cartion.BottomMenuActivity;
import net.belicon.cartion.R;
import net.belicon.cartion.models.MyPage;
import net.belicon.cartion.retrofites.RetrofitInterface;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartionSettingAdapter extends RecyclerView.Adapter<CartionSettingAdapter.CartionSettingViewHolder> {

    private Activity mActivity;
    private RetrofitInterface mRetrofit;
    private BleDevice mBleDevice;
    private List<String> mCartions;

    private String token, email;

    public CartionSettingAdapter(BottomMenuActivity activity, RetrofitInterface mRetrofit, BleDevice mBleDevice, List<String> mCartions, String token, String email) {
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
        String device = mCartions.get(position);
        holder.mSerialText.setText(device);

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BleManager.getInstance().isConnected(mBleDevice)) {
                    mRetrofit.deleteCartion(token, email, device)
                            .enqueue(new Callback<MyPage>() {
                                @Override
                                public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                                    if (response.code() == 200) {
                                        mCartions.remove(position);
                                        notifyDataSetChanged();
                                        ((BottomMenuActivity) mActivity).writeData(mBleDevice,
                                                "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                                "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                                                onDelete()
                                        );
                                        Toast.makeText(holder.mDeleteBtn.getContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyPage> call, Throwable t) {

                                }
                            });
                } else {
                    Toast.makeText(holder.mDeleteBtn.getContext(), "블루투스를 연결 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 사용자 정보 삭제 커맨드
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
        private ImageButton mDeleteBtn;

        public CartionSettingViewHolder(@NonNull View itemView) {
            super(itemView);
            mSerialText = itemView.findViewById(R.id.item_cartion_serial_text);
            mDeleteBtn = itemView.findViewById(R.id.item_cartion_delete_btn);
        }
    }
}
