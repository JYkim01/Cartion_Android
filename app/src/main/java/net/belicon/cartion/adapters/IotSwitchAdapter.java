package net.belicon.cartion.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.R;
import net.belicon.cartion.models.MobileSwitch;
import net.belicon.cartion.models.Switch;
import net.belicon.cartion.models.UserMobile;

import java.util.List;

public class IotSwitchAdapter extends RecyclerView.Adapter<IotSwitchAdapter.IotSwitchViewHolder> {

    private OnIotClickListener mListener = null;

    private List<UserMobile> mDataList;

    public interface OnIotClickListener {
        void onItemClickListener(View v, int pos);
        void onItemLongClickListener(View v, int pos);
    }

    public void setOnIotClickListener(OnIotClickListener listener) {
        this.mListener = listener;
    }

    public IotSwitchAdapter(List<UserMobile> mDataList) {
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public IotSwitchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IotSwitchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_iot_switch, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IotSwitchViewHolder holder, int position) {
        UserMobile data = mDataList.get(position);
        holder.mIotText.setText("IoT 스위치" + (position + 1));
        holder.mTypeText.setText(data.getCategoryName());
        holder.mPosText.setText("경적" + (position + 1));
        holder.mNameText.setText(data.getHornName());
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() == 0) {
            return 0;
        }
        return 2;
    }

    public class IotSwitchViewHolder extends RecyclerView.ViewHolder {

        private TextView mIotText;
        private TextView mTypeText;
        private TextView mPosText;
        private TextView mNameText;

        public IotSwitchViewHolder(@NonNull View itemView) {
            super(itemView);

            mIotText = itemView.findViewById(R.id.item_mobile_switch_iot_text);
            mPosText = itemView.findViewById(R.id.item_switch_pos_text);
            mTypeText = itemView.findViewById(R.id.item_switch_type);
            mNameText = itemView.findViewById(R.id.item_switch_play_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClickListener(v, pos);
                            mTypeText.setTextColor(mTypeText.getContext().getResources().getColor(R.color.color_7F44A6));
                            mNameText.setTextColor(mNameText.getContext().getResources().getColor(R.color.color_531F57));
                            mNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sound_play_icon, 0);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTypeText.setTextColor(mTypeText.getContext().getResources().getColor(R.color.color_A1AAB1));
                                    mNameText.setTextColor(mNameText.getContext().getResources().getColor(R.color.color_A1AAB1));
                                    mNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_home_play_button_disable, 0);
                                }
                            }, 5000);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemLongClickListener(v, pos);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
}
