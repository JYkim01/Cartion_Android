package net.belicon.cartion.adapters;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.R;
import net.belicon.cartion.models.HornList;
import net.belicon.cartion.models.UserMobile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListViewHolder> {

    private List<HornList> mMusicList;
    private List<String> mDownList;
    private String mEmail, mAuth, type;

    private int downloadId;
    private boolean isPlaying = false;

    private DownloadManager mDownloadManager; //다운로드 매니저.

    private class OnDownloadComplete extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int id = (int) intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                if (downloadId == id) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(id);
                    Cursor cursor = mDownloadManager.query(query);
                    if (!cursor.moveToFirst()) {
                        return;
                    }

                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Toast.makeText(context, "다운로드 완료", Toast.LENGTH_SHORT).show();
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        Toast.makeText(context, "다운로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
//                Toast.makeText(context, "Notification clicked", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public MusicListAdapter(List<HornList> mMusicList, List<String> mDownList, String mEmail, String mAuth, String type) {
        this.mMusicList = mMusicList;
        this.mDownList = mDownList;
        this.mEmail = mEmail;
        this.mAuth = mAuth;
        this.type = type;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int position) {
        holder.mMusicPositionText.setText("경적" + (position + 1));
        holder.mMusicTitleText.setText(mMusicList.get(position).getHornName());
        if (position > 1) {
            holder.mMusicPositionText.setBackgroundResource(R.drawable.ic_sound_list_small_flag_1);
        }
        holder.mMusicPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencode");
                    headers.put("Authorization", mAuth);

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(holder.mMusicPreviewBtn.getContext(), Uri.parse("http://49.50.172.53:9983/api/horn/wav/" + mMusicList.get(position).getHornId() + "/"), headers);
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
        });

        holder.mMusicDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = false;
                if (mDownList.contains(mMusicList.get(position).getHornName() + ".wav")) {
                    Toast.makeText(holder.mMusicDownloadBtn.getContext(), "이미 존재하는 음원입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Uri downloadUri = Uri.parse("http://49.50.172.53:9983/api/horn/ADPCM/" + mMusicList.get(position).getHornId() + "/");
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
//                            mUserData.add(new UserMobile(mEmail, 0, "horn", mMusicList.get(position).getHornId()));
                            UserMobile data = realm.createObject(UserMobile.class);
                            data.setUserId(mEmail);
                            data.setMobileSwitch(0);
                            data.setHornName(mMusicList.get(position).getHornName() + ".wav");
                            data.setHornType(type);
                            data.setHornId(mMusicList.get(position).getHornId());
                        }
                    });
                    downloadFile(holder.mMusicDownloadBtn.getContext(), mAuth, downloadUri, mMusicList.get(position).getHornName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    static class MusicListViewHolder extends RecyclerView.ViewHolder {

        private TextView mMusicPositionText;
        private TextView mMusicTitleText;
        private ImageButton mMusicPreviewBtn;
        private ImageButton mMusicDownloadBtn;

        public MusicListViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicPositionText = itemView.findViewById(R.id.item_sound_position_text);
            mMusicTitleText = itemView.findViewById(R.id.item_sound_title_text);
            mMusicPreviewBtn = itemView.findViewById(R.id.item_sound_preview_btn);
            mMusicDownloadBtn = itemView.findViewById(R.id.item_sound_download_btn);
        }
    }

    private void downloadFile(Context context, String user, Uri url, String name) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        context.registerReceiver(new OnDownloadComplete(), intentFilter);

        mDownloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        File file = new File(context.getExternalFilesDir(null).getAbsolutePath(), name + ".wav");

        DownloadManager.Request request = new DownloadManager.Request(url)
                .addRequestHeader("Authorization", user)
                .setTitle("Downloading a Cartion")
                .setDescription("Downloading...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationUri(Uri.fromFile(file))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        downloadId = (int) mDownloadManager.enqueue(request);
        Log.e("PATH", file.getPath());
    }
}
