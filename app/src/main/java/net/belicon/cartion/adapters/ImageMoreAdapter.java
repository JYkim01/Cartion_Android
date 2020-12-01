package net.belicon.cartion.adapters;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

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
        mScaleGestureDetector = new ScaleGestureDetector(holder.image.getContext(), new ScaleListener(holder.image, mScaleFactor));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        //변수로 선언해 놓은 ScaleGestureDetector
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private ImageView imageView;
        private float mScaleFactor = 1.0f;

        public ScaleListener(ImageView imageView, float mScaleFactor) {
            this.imageView = imageView;
            this.mScaleFactor = mScaleFactor;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            // ScaleGestureDetector에서 factor를 받아 변수로 선언한 factor에 넣고
            mScaleFactor *= scaleGestureDetector.getScaleFactor();

            // 최대 10배, 최소 10배 줌 한계 설정
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            // 이미지뷰 스케일에 적용
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
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
