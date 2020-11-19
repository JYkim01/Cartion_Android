package net.belicon.cartion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.R;
import net.belicon.cartion.models.Down;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    private OnDownClickListener mListener = null ;

    private List<Down> mMusicList;

    public interface OnDownClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnDownClickListener listener) {
        this.mListener = listener ;
    }

    public DownloadAdapter(List<Down> mMusicList) {
        this.mMusicList = mMusicList;
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        Down item = mMusicList.get(position);
        holder.mMusicPositionText.setText(String.valueOf(position + 1));
        holder.mMusicTitleText.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder {

        private TextView mMusicPositionText;
        private TextView mMusicTitleText;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicPositionText = itemView.findViewById(R.id.item_download_position_text);
            mMusicTitleText = itemView.findViewById(R.id.item_download_title_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}
