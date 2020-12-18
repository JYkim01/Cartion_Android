package net.belicon.cartion.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.R;
import net.belicon.cartion.models.Down;

import java.io.File;
import java.util.List;

public class DownloadDeleteAdapter extends RecyclerView.Adapter<DownloadDeleteAdapter.DownloadDeleteViewHolder> {

    private List<Down> mDownList;

    public DownloadDeleteAdapter(List<Down> mDownList) {
        this.mDownList = mDownList;
    }

    @NonNull
    @Override
    public DownloadDeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadDeleteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_delete, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadDeleteAdapter.DownloadDeleteViewHolder holder, int position) {
        Down item = mDownList.get(position);
        holder.mMusicPositionText.setText(String.valueOf(position + 1));
        holder.mMusicTitleText.setText(item.getName());
        holder.mMusicDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.mMusicDeleteBtn.getContext());
                builder.setTitle("음원 삭제").setMessage(item.getName() + "을(를) 삭제 하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    File file = new File(String.valueOf(holder.mMusicDeleteBtn.getContext().getExternalFilesDir(null)) + "/" + item.getName());
                                    if (file.exists()) {
                                        file.delete();
                                        mDownList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDownList.size() == 0) {
            return 0;
        }
        return mDownList.size();
    }

    static class DownloadDeleteViewHolder extends RecyclerView.ViewHolder {

        private TextView mMusicPositionText;
        private TextView mMusicTitleText;
        private ImageButton mMusicDeleteBtn;

        public DownloadDeleteViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicPositionText = itemView.findViewById(R.id.item_down_delete_position_text);
            mMusicTitleText = itemView.findViewById(R.id.item_down_delete_title_text);
            mMusicDeleteBtn = itemView.findViewById(R.id.item_down_delete_btn);
        }
    }
}
