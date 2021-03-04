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

public class IotSwitchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NORMAL_TYPE = 0;
    public static final int EVENT_TYPE = 1;
    private int VIEW_TYPE = 0;

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder view;
        switch (viewType) {
            case NORMAL_TYPE:
                view = new IotSwitchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_iot_switch, parent, false));
                break;
            case EVENT_TYPE:
                view = new EventIotSwitchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_iot_switch, parent, false));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserMobile data = mDataList.get(position);
        switch (holder.getItemViewType()) {
            case NORMAL_TYPE:
                IotSwitchViewHolder normal = (IotSwitchViewHolder) holder;
                normal.mIotText.setText("IoT 스위치" + (position + 1));
                if (data.getHornType().equals("horn")) {
                    if (data.getCategoryName().equals("기본")) {
                        normal.mTypeText.setText("카션 " + data.getCategoryName() + "음");
                    } else {
                        normal.mTypeText.setText("카션 " + data.getCategoryName());
                    }
                } else {
                    normal.mTypeText.setText("나만의 음원");
                }
                normal.mPosText.setText("음원" + (position + 1));
                normal.mNameText.setText(data.getHornName().replaceAll("_", "\n"));
                break;
            case EVENT_TYPE:
                EventIotSwitchViewHolder event = (EventIotSwitchViewHolder) holder;
                event.mIotText.setText("IoT 스위치" + (position + 1));
                if (data.getHornType().equals("horn")) {
                    if (data.getCategoryName().equals("기본")) {
                        event.mTypeText.setText("카션 " + data.getCategoryName() + "음");
                    } else {
                        event.mTypeText.setText("카션 " + data.getCategoryName());
                    }
                } else {
                    event.mTypeText.setText("나만의 음원");
                }
                event.mPosText.setText("음원" + (position + 1));
                event.mNameText.setText(data.getHornName().replaceAll("_", "\n"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() == 0) {
            return 0;
        }
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE;
    }

    public void setItemViewType(int type) {
        VIEW_TYPE = type;
        notifyDataSetChanged();
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
                            mNameText.setTextColor(mNameText.getContext().getResources().getColor(R.color.color_531F57));
                            mNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sound_play_icon, 0);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
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
                            mListener.onItemLongClickListener(v, pos);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    public class EventIotSwitchViewHolder extends RecyclerView.ViewHolder {

        private TextView mIotText;
        private TextView mTypeText;
        private TextView mPosText;
        private TextView mNameText;

        public EventIotSwitchViewHolder(@NonNull View itemView) {
            super(itemView);

            mIotText = itemView.findViewById(R.id.item_event_switch_iot_text);
            mPosText = itemView.findViewById(R.id.item_iot_event_pos_text);
            mTypeText = itemView.findViewById(R.id.item_iot_event_type);
            mNameText = itemView.findViewById(R.id.item_iot_event_play_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClickListener(v, pos);
                            mNameText.setTextColor(mNameText.getContext().getResources().getColor(R.color.color_FC5F3A));
                            mNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_event_play_icon, 0);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
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
