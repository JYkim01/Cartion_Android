package net.belicon.cartion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.belicon.cartion.R;
import net.belicon.cartion.models.ExpandData;

import java.util.List;

public class ImageMoreAdapter extends RecyclerView.Adapter<ImageMoreAdapter.ImageMoreViewHolder> {

    private List<String> mImageList;

    public ImageMoreAdapter(List<String> mImageList) {
        this.mImageList = mImageList;
    }

    @NonNull
    @Override
    public ImageMoreAdapter.ImageMoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_more_lay, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageMoreViewHolder holder, int position) {
        String data = mImageList.get(position);
        Glide.with(holder.image.getContext()).load(data).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    static class ImageMoreViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ImageMoreViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.item_image_more);
        }
    }
}
