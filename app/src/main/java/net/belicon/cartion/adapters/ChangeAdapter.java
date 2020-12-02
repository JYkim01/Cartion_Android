package net.belicon.cartion.adapters;

import android.graphics.Color;
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

import net.belicon.cartion.ItemMoveCallback;
import net.belicon.cartion.R;
import net.belicon.cartion.models.Down;
import net.belicon.cartion.models.Switch;
import net.belicon.cartion.models.UserMobile;

import java.util.Collections;
import java.util.List;

public class ChangeAdapter extends RecyclerView.Adapter<ChangeAdapter.ChangeViewHolder> implements ItemMoveCallback.ItemTouchHelperAdapter {

    private List<UserMobile> mMusicList;

    private OnChangeClickListener mListener = null;

    public interface OnChangeClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnChangeClickListener listener) {
        this.mListener = listener;
    }

    public ChangeAdapter(List<UserMobile> mMusicList) {
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

        notifyItemMoved(fromPos, targetPos);
    }

    @Override
    public void onItemDismiss(int pos) {
        mMusicList.remove(pos);
        notifyItemRemoved(pos);
    }

    class ChangeViewHolder extends RecyclerView.ViewHolder {

        private TextView mMusicPositionText;
        private TextView mMusicTitleText;

        public ChangeViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicPositionText = itemView.findViewById(R.id.item_change_position_text);
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
        }
    }
}
