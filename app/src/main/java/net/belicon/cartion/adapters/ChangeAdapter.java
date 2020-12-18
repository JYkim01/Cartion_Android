package net.belicon.cartion.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.BottomMenuActivity;
import net.belicon.cartion.ItemMoveCallback;
import net.belicon.cartion.R;
import net.belicon.cartion.models.Down;
import net.belicon.cartion.models.Switch;
import net.belicon.cartion.models.UserMobile;

import java.util.Collections;
import java.util.List;

public class ChangeAdapter extends RecyclerView.Adapter<ChangeAdapter.ChangeViewHolder> implements ItemMoveCallback.ItemTouchHelperAdapter {

    private BottomMenuActivity mActivity;
    private List<UserMobile> mMusicList;

    private OnChangeClickListener mListener = null;
    private OnChangeTouchListener mTouchListener = null;

    public interface OnChangeClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnChangeTouchListener {
        void onItemTouch(View v, MotionEvent event);
    }

    public void setOnItemClickListener(OnChangeClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemTouchListener(OnChangeTouchListener listener) {
        this.mTouchListener = listener;
    }

    public ChangeAdapter(BottomMenuActivity activity, List<UserMobile> mMusicList) {
        this.mActivity = activity;
        this.mMusicList = mMusicList;
    }

    @NonNull
    @Override
    public ChangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChangeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeViewHolder holder, int position) {
        UserMobile item = mMusicList.get(position);
        holder.mMusicPositionText.setText(String.valueOf(position + 1));
        if (item.getCategoryName().equals("기본")) {
            holder.mMusicCategoryText.setText("카션 " + item.getCategoryName() + "음");
        } else {
            holder.mMusicCategoryText.setText("카션 " + item.getCategoryName());
        }
        holder.mMusicTitleText.setText(item.getHornName());
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    @Override
    public void onItemMove(int fromPos, int targetPos) {
        if (fromPos < targetPos) {
            for (int i = fromPos; i < targetPos; i++) {
                Collections.swap(mMusicList, i, i + 1);
            }
        } else {
            for (int i = fromPos; i > targetPos; i--) {
                Collections.swap(mMusicList, i, i - 1);
            }
        }

        mActivity.onEventListen();
        notifyItemMoved(fromPos, targetPos);
    }

    @Override
    public void onItemDismiss(int pos) {
        mMusicList.remove(pos);
        notifyItemRemoved(pos);
    }

    class ChangeViewHolder extends RecyclerView.ViewHolder {

        private TextView mMusicPositionText;
        private TextView mMusicCategoryText;
        private TextView mMusicTitleText;

        public ChangeViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicPositionText = itemView.findViewById(R.id.item_change_position_text);
            mMusicCategoryText = itemView.findViewById(R.id.item_change_category_text);
            mMusicTitleText = itemView.findViewById(R.id.item_change_title_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            if (mMusicTitleText.getCurrentTextColor() == Color.parseColor("#7F44A6")) {
                                mMusicTitleText.setTextColor(Color.parseColor("#9D67FF"));
                            } else {
                                mMusicTitleText.setTextColor(Color.parseColor("#7F44A6"));
                            }
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mTouchListener != null) {
                        mTouchListener.onItemTouch(v, event);
                    }
                    return true;
                }
            });
        }
    }
}
