package net.belicon.cartion.adapters;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.clj.fastble.BleManager;

import net.belicon.cartion.BottomMenuActivity;
import net.belicon.cartion.R;
import net.belicon.cartion.models.HornList;
import net.belicon.cartion.models.UserMobile;
import net.belicon.cartion.retrofites.RetrofitInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListViewHolder> {

    private RetrofitInterface retrofit;
    private List<HornList> mMusicList;
    private List<String> mDownList;
    private String mEmail, mAuth, type, categoryName;
    private String music, music_name;
    private String select;

    private boolean isPlaying = false;

    public MusicListAdapter(RetrofitInterface retrofit, List<HornList> mMusicList, List<String> mDownList, String mEmail, String mAuth, String type, String categoryName) {
        this.retrofit = retrofit;
        this.mMusicList = mMusicList;
        this.mDownList = mDownList;
        this.mEmail = mEmail;
        this.mAuth = mAuth;
        this.type = type;
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int position) {
        holder.mMusicPositionText.setText("음원" + (position + 1));
        if (mMusicList.get(position).getCategoryName() != null) {
            select = "horn";
            if (mMusicList.get(position).getCategoryName().equals("기본")) {
                holder.mMusicCategoryText.setText("카션 " + mMusicList.get(position).getCategoryName() + "음");
            } else {
                holder.mMusicCategoryText.setText("카션 " + mMusicList.get(position).getCategoryName());
            }
        } else {
            select = "custom";
            holder.mMusicCategoryText.setText("나만의 음원");
        }
        holder.mMusicTitleText.setText(mMusicList.get(position).getHornName());
//        if (position > 1) {
//            holder.mMusicPositionText.setBackgroundResource(R.drawable.ic_sound_list_small_flag_1);
//        } else {
//            holder.mMusicPositionText.setBackgroundResource(R.drawable.ic_sound_list_small_flag);
//        }


        if (isPlaying) {
            if (music != null) {
                if (music.equals(mMusicList.get(position).getHornId())) {
                    holder.mMusicTitleText.setSelected(true);
                    holder.mMusicTitleText.setTextColor(holder.mMusicTitleText.getContext().getResources().getColor(R.color.color_7F44A6));
                    Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon).into(holder.mMusicPreviewBtn);
                } else {
                    holder.mMusicTitleText.setSelected(true);
                    holder.mMusicTitleText.setTextColor(Color.WHITE);
                    Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                }
            } else {
                holder.mMusicTitleText.setSelected(true);
                holder.mMusicTitleText.setTextColor(Color.WHITE);
                Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
            }
        } else {
            holder.mMusicTitleText.setSelected(true);
            holder.mMusicTitleText.setTextColor(Color.WHITE);
            Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
        }

        holder.mMusicPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
//                    isPlaying = true;
                    Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon).into(holder.mMusicPreviewBtn);
                    music = mMusicList.get(position).getHornId();
                    music_name = mMusicList.get(position).getHornName();
                    if (select.equals("horn")) {
                        playHorn(holder, position);
                    } else if (select.equals("custom")) {
                        playCustom(holder, position);
                    }
                } else {
                    Toast.makeText(holder.mMusicPreviewBtn.getContext(), "다른 음원이 재생 중 입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.mMusicDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownList.contains(mMusicList.get(position).getHornName() + ".wav")) {
                    Toast.makeText(holder.mMusicDownloadBtn.getContext(), "이미 다운받은 음원입니다", Toast.LENGTH_SHORT).show();
                } else {
                    if (select.equals("horn")) {
                        downHorn(holder, position);
                    } else if (select.equals("custom")) {
                        downCustom(holder, position);
                    }
                }
            }
        });
    }

    private void playHorn(MusicListViewHolder holder, int position) {
        retrofit.getPCM(mAuth, music)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("PCM CODE", "" + response.code());
                        if (response.code() == 200) {
                            try {
                                playWav(holder.mMusicPreviewBtn.getContext(), holder.mMusicTitleText, holder.mMusicPreviewBtn, response.body().bytes(), music_name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isPlaying = false;
                            Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                            Toast.makeText(holder.mMusicPreviewBtn.getContext(), "재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isPlaying = false;
                        Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                        Toast.makeText(holder.mMusicPreviewBtn.getContext(), "재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void playCustom(MusicListViewHolder holder, int position) {
        retrofit.getCustomPCM(mAuth, music)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("C_PCM CODE", "" + response.code());
                        if (response.code() == 200) {
                            try {
                                playWav(holder.mMusicPreviewBtn.getContext(), holder.mMusicTitleText, holder.mMusicPreviewBtn, response.body().bytes(), music_name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isPlaying = false;
                            Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                            Toast.makeText(holder.mMusicPreviewBtn.getContext(), "재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isPlaying = false;
                        Glide.with(holder.mMusicPreviewBtn.getContext()).load(R.drawable.ic_preview_listening_icon_off).into(holder.mMusicPreviewBtn);
                        Toast.makeText(holder.mMusicPreviewBtn.getContext(), "재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downHorn(MusicListViewHolder holder, int position) {
        retrofit.getADPCM(mAuth, mMusicList.get(position).getHornId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("DOWN RESPONSE", "" + response.code());
                        if (response.code() == 200) {
                            mDownList.add(mMusicList.get(position).getHornName() + ".wav");
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    UserMobile data = realm.createObject(UserMobile.class);
                                    data.setUserId(mEmail);
                                    data.setMobileSwitch(0);
                                    data.setHornName(mMusicList.get(position).getHornName() + ".wav");
                                    data.setHornType("horn");
                                    data.setHornId(mMusicList.get(position).getHornId());
                                    data.setCategoryName(mMusicList.get(position).getCategoryName());
                                    data.setWavPath(mMusicList.get(position).getAdpcmPath());
                                }
                            });
                            writeResponseBodyToDisk(holder.mMusicDownloadBtn.getContext(), response.body(), mMusicList.get(position).getHornName());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    private void downCustom(MusicListViewHolder holder, int position) {
        retrofit.getCustomADPCM(mAuth, mMusicList.get(position).getHornId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("C_DOWN RESPONSE", "" + response.code());
                        if (response.code() == 200) {
                            mDownList.add(mMusicList.get(position).getHornName() + ".wav");
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    UserMobile data = realm.createObject(UserMobile.class);
                                    data.setUserId(mEmail);
                                    data.setMobileSwitch(0);
                                    data.setHornName(mMusicList.get(position).getHornName() + ".wav");
                                    data.setHornType("customHorn");
                                    data.setHornId(mMusicList.get(position).getHornId());
                                    data.setCategoryName("나만의 음원");
                                    data.setWavPath(mMusicList.get(position).getAdpcmPath());
                                }
                            });
                            writeResponseBodyToDisk(holder.mMusicDownloadBtn.getContext(), response.body(), mMusicList.get(position).getHornName());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    static class MusicListViewHolder extends RecyclerView.ViewHolder {

        private TextView mMusicPositionText;
        private TextView mMusicCategoryText;
        private TextView mMusicTitleText;
        private ImageButton mMusicPreviewBtn;
        private ImageButton mMusicDownloadBtn;

        public MusicListViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicPositionText = itemView.findViewById(R.id.item_sound_position_text);
            mMusicCategoryText = itemView.findViewById(R.id.item_sound_category_text);
            mMusicTitleText = itemView.findViewById(R.id.item_sound_title_text);
            mMusicPreviewBtn = itemView.findViewById(R.id.item_sound_preview_btn);
            mMusicDownloadBtn = itemView.findViewById(R.id.item_sound_download_btn);

            mMusicTitleText.setSelected(true);
        }
    }

    private void playWav(Context context, TextView nameTextView, ImageButton previewBtn, byte[] mp3SoundByteArray, String name) {
        try {
            File path = new File(context.getCacheDir(), name + ".wav");

            FileOutputStream fos = new FileOutputStream(path);
            fos.write(mp3SoundByteArray);
            fos.close();

            FileInputStream fis = new FileInputStream(path);

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context.getCacheDir() + "/" + name + ".wav");
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (!isPlaying) {
                        if (nameTextView.getText().toString().equals(name)) {
                            nameTextView.setTextColor(nameTextView.getContext().getResources().getColor(R.color.color_7F44A6));
                            mediaPlayer.start();
                            isPlaying = true;
                        } else {
                            nameTextView.setTextColor(Color.WHITE);
                        }
                    } else {
                        nameTextView.setTextColor(Color.WHITE);
                    }
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Glide.with(previewBtn).load(R.drawable.ic_preview_listening_icon_off).into(previewBtn);
                    nameTextView.setTextColor(Color.WHITE);
                    isPlaying = false;
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            });
        } catch (IOException ex) {
            String s = ex.toString();
            Log.e("PLAYER ERROR", s);
        }
    }

    private boolean writeResponseBodyToDisk(Context context, ResponseBody body, String name) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(context.getExternalFilesDir(null), name + ".wav");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.e("File Download: ", fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
                Toast.makeText(context, "앱에 저장 완료.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            return false;
        }
    }
}
