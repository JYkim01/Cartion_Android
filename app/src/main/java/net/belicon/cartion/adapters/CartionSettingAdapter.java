package net.belicon.cartion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.R;
import net.belicon.cartion.models.MyPage;
import net.belicon.cartion.retrofites.RetrofitInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartionSettingAdapter extends RecyclerView.Adapter<CartionSettingAdapter.CartionSettingViewHolder> {

    private RetrofitInterface mRetrofit;
    private List<String> mCartions;

    private String token, email;

    public CartionSettingAdapter(RetrofitInterface mRetrofit, List<String> mCartions, String token, String email) {
        this.mRetrofit = mRetrofit;
        this.mCartions = mCartions;
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
                mRetrofit.deleteCartion(token, email, device)
                        .enqueue(new Callback<MyPage>() {
                            @Override
                            public void onResponse(Call<MyPage> call, Response<MyPage> response) {
                                if (response.code() == 200) {
                                    mCartions.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(holder.mDeleteBtn.getContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MyPage> call, Throwable t) {

                            }
                        });
            }
        });
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
