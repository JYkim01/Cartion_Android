package net.belicon.cartion.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.R;
import net.belicon.cartion.models.UserMobile;

import java.util.List;

public class Mobile710SwitchAdapter extends RecyclerView.Adapter<Mobile710SwitchAdapter.Mobile710ViewHolder> {

    private On710ClickListener mListener = null;

    private List<UserMobile> mDataList;

    public interface On710ClickListener {
        void on710ClickListener(View view, int position);
        void on710LongClickListener(View view, int position);
    }

    public void setOn710ClickListener(On710ClickListener listener) {
        this.mListener = listener;
    }

    public Mobile710SwitchAdapter(List<UserMobile> mDataList) {
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public Mobile710ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Mobile710ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mobile_710_switch, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Mobile710ViewHolder holder, int position) {
        UserMobile data = mDataList.get(position + 6);
        if (data.getCategoryName().equals("기본")) {
            holder.mTypeText.setText("카션 " + data.getCategoryName() + "음");
        } else {
            holder.mTypeText.setText("카션 " + data.getCategoryName());
        }
        holder.mPosText.setText("음원" + (position + 7));
        holder.mNameText.setText(data.getHornName().replaceAll("_", "\n"));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() == 0) {
            return 0;
        }
        return 4;
    }

    public class Mobile710ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTypeText;
        private TextView mPosText;
        private TextView mNameText;

        public Mobile710ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPosText = itemView.findViewById(R.id.item_710_switch_pos_text);
            mTypeText = itemView.findViewById(R.id.item_710_switch_type);
            mNameText = itemView.findViewById(R.id.item_710_switch_play_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.on710ClickListener(v, pos);
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
                            }, 500);
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
                            mListener.on710LongClickListener(v, pos);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
}
