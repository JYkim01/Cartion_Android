package net.belicon.cartion.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.belicon.cartion.BottomMenuActivity;
import net.belicon.cartion.R;
import net.belicon.cartion.models.Down;
import net.belicon.cartion.retrofites.RetrofitInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    private OnDownClickListener mListener = null;
    private OnDownTouchListener mTouchListener = null;

    private BottomMenuActivity mActivity;
    private RetrofitInterface retrofit;

    private List<Down> mMusicList;
    private String mAuth;

    private boolean isPlaying = false;

    public interface OnDownClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnDownTouchListener {
        void onItemTouch(View v, MotionEvent event);
    }

    public void setOnItemClickListener(OnDownClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemTouchListener(OnDownTouchListener listener) {
        this.mTouchListener = listener;
    }

    public DownloadAdapter(BottomMenuActivity activity, List<Down> mMusicList, String mAuth, RetrofitInterface retrofit) {
        this.mActivity = activity;
        this.mMusicList = mMusicList;
        this.mAuth = mAuth;
        this.retrofit = retrofit;
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
        if (mMusicList.get(position).getHornType().equals("horn")) {
            if (item.getCategoryName().equals("기본")) {
                holder.mMusicCategoryText.setText("카션 " + item.getCategoryName() + "음");
            } else {
                holder.mMusicCategoryText.setText("카션 " + item.getCategoryName());
            }
        } else {
            holder.mMusicCategoryText.setText("나만의 음원");
        }
        holder.mMusicTitleText.setText(item.getName());
        holder.mMusicPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    if (item.getHornId().equals("")) {
                        Toast.makeText(holder.mMusicPreviewBtn.getContext(), "미리듣기가 불가능한 음원 입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        mActivity.onEventListen();
                        Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon).into(holder.mMusicPreviewBtn);
                        if (item.getHornType().equals("horn")) {
                            playHorn(holder, position, item);
                        } else {
                            playCustom(holder, position, item);
                        }
                    }
                } else {
                    Toast.makeText(mActivity, "다른 음원이 재생 중 입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        holder.mMusicDeleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(holder.mMusicDeleteBtn.getContext());
//                builder.setTitle("음원 삭제").setMessage(item.getName() + "을(를) 삭제 하시겠습니까?")
//                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                try {
//                                    File file = new File(String.valueOf(holder.mMusicDeleteBtn.getContext().getExternalFilesDir(null)) + "/" + item.getName());
//                                    if (file.exists()) {
//                                        file.delete();
//                                        mMusicList.remove(position);
//                                        notifyDataSetChanged();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        })
//                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });
    }

    private void playHorn(DownloadViewHolder holder, int position, Down item) {
        retrofit.getPCM(mAuth, item.getHornId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                playWav(holder.mMusicPreviewBtn.getContext(), holder.mMusicPreviewBtn, response.body().bytes(), item.getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isPlaying = false;
                            Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                            Toast.makeText(holder.mMusicPreviewBtn.getContext(), "미리듣기 재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isPlaying = false;
                        Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                        Toast.makeText(holder.mMusicPreviewBtn.getContext(), "미리듣기 재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void playCustom(DownloadViewHolder holder, int position, Down item) {
        retrofit.getCustomPCM(mAuth, item.getHornId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                playWav(holder.mMusicPreviewBtn.getContext(), holder.mMusicPreviewBtn, response.body().bytes(), item.getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isPlaying = false;
                            Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                            Toast.makeText(holder.mMusicPreviewBtn.getContext(), "미리듣기 재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isPlaying = false;
                        Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                        Toast.makeText(holder.mMusicPreviewBtn.getContext(), "미리듣기 재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    private void playWav(Context context, ImageButton previewBtn, byte[] mp3SoundByteArray, String name) {
        try {

            File path = new File(context.getCacheDir(), name + ".wav");

            FileOutputStream fos = new FileOutputStream(path);
            fos.write(mp3SoundByteArray);
            fos.close();

            MediaPlayer mediaPlayer = new MediaPlayer();

            FileInputStream fis = new FileInputStream(path);
            mediaPlayer.setDataSource(context.getCacheDir() + "/" + name + ".wav");

            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (!isPlaying) {
                        mediaPlayer.start();
                        isPlaying = true;
                    }
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    Glide.with(previewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(previewBtn);
                }
            });
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder {

        private TextView mMusicPositionText;
        private TextView mMusicCategoryText;
        private TextView mMusicTitleText;
        private ImageButton mMusicPreviewBtn;
        private ImageButton mMusicDownloadBtn;
//        private ImageButton mMusicDeleteBtn;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicPositionText = itemView.findViewById(R.id.item_download_position_text);
            mMusicCategoryText = itemView.findViewById(R.id.item_download_category_text);
            mMusicTitleText = itemView.findViewById(R.id.item_download_title_text);
            mMusicPreviewBtn = itemView.findViewById(R.id.item_download_preview_btn);
            mMusicDownloadBtn = itemView.findViewById(R.id.item_download_download_btn);
//            mMusicDeleteBtn = itemView.findViewById(R.id.item_download_delete_btn);

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
