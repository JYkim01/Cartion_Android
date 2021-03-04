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

public class Mobile710SwitchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NORMAL_TYPE = 0;
    public static final int EVENT_TYPE = 1;
    private int VIEW_TYPE = 0;

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder view;
        switch (viewType) {
            case NORMAL_TYPE:
                view = new Mobile710ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mobile_710_switch, parent, false));
                break;
            case EVENT_TYPE:
                view = new Event710SwitchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_710_switch, parent, false));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserMobile data = mDataList.get(position + 6);
        switch (holder.getItemViewType()) {
            case NORMAL_TYPE:
                Mobile710ViewHolder normal = (Mobile710ViewHolder) holder;
                if (data.getHornType().equals("horn")) {
                    if (data.getCategoryName().equals("기본")) {
                        normal.mTypeText.setText("카션 " + data.getCategoryName() + "음");
                    } else {
                        normal.mTypeText.setText("카션 " + data.getCategoryName());
                    }
                } else {
                    normal.mTypeText.setText("나만의 음원");
                }
                normal.mPosText.setText("음원" + (position + 7));
                normal.mNameText.setText(data.getHornName().replaceAll("_", "\n"));
                break;
            case EVENT_TYPE:
                Event710SwitchViewHolder event = (Event710SwitchViewHolder) holder;
                if (data.getHornType().equals("horn")) {
                    if (data.getCategoryName().equals("기본")) {
                        event.mTypeText.setText("카션 " + data.getCategoryName() + "음");
                    } else {
                        event.mTypeText.setText("카션 " + data.getCategoryName());
                    }
                } else {
                    event.mTypeText.setText("나만의 음원");
                }
                event.mPosText.setText("음원" + (position + 3));
                event.mNameText.setText(data.getHornName().replaceAll("_", "\n"));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE;
    }

    public void setItemViewType(int type) {
        VIEW_TYPE = type;
        notifyDataSetChanged();
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
                            mListener.on710LongClickListener(v, pos);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    public class Event710SwitchViewHolder extends RecyclerView.ViewHolder {

        private TextView mTypeText;
        private TextView mPosText;
        private TextView mNameText;

        public Event710SwitchViewHolder(@NonNull View itemView) {
            super(itemView);

            mPosText = itemView.findViewById(R.id.item_710_event_pos_text);
            mTypeText = itemView.findViewById(R.id.item_710_event_type);
            mNameText = itemView.findViewById(R.id.item_710_event_play_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.on710ClickListener(v, pos);
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
