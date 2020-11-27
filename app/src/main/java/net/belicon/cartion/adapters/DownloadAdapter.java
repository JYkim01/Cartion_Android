package net.belicon.cartion.adapters;

import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.R;
import net.belicon.cartion.models.Down;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    private OnDownClickListener mListener = null ;

    private List<Down> mMusicList;
    private String mAuth;

    private boolean isPlaying = false;

    public interface OnDownClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnDownClickListener listener) {
        this.mListener = listener ;
    }

    public DownloadAdapter(List<Down> mMusicList, String mAuth) {
        this.mMusicList = mMusicList;
        this.mAuth = mAuth;
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
        holder.mMusicPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getHornId().equals("")) {
                    Toast.makeText(holder.mMusicPreviewBtn.getContext(), "미리듣기가 불가능한 음원 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/x-www-form-urlencode");
                        headers.put("Authorization", mAuth);

                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(holder.mMusicPreviewBtn.getContext(), Uri.parse("https://api.cartion.co.kr:9983/api/horn/wav/" + item.getHornId() + "/"), headers);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                if (!isPlaying) {
                                    holder.mMusicTitleText.setTextColor(holder.mMusicPreviewBtn.getContext().getResources().getColor(R.color.color_7F44A6));
                                    mediaPlayer.start();
                                    isPlaying = true;
                                }
                            }
                        });

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                isPlaying = false;
                                holder.mMusicTitleText.setTextColor(Color.WHITE);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder {

        private TextView mMusicPositionText;
        private TextView mMusicTitleText;
        private ImageButton mMusicPreviewBtn;
        private ImageButton mMusicDownloadBtn;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicPositionText = itemView.findViewById(R.id.item_download_position_text);
            mMusicTitleText = itemView.findViewById(R.id.item_download_title_text);
            mMusicPreviewBtn = itemView.findViewById(R.id.item_download_preview_btn);
            mMusicDownloadBtn = itemView.findViewById(R.id.item_download_download_btn);

            mMusicDownloadBtn.setOnClickListener(new View.OnClickListener() {
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
