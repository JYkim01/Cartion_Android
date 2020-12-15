package net.belicon.cartion.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.belicon.cartion.R;

import java.util.ArrayList;

import pyxis.uzuki.live.rollingbanner.RollingViewPagerAdapter;

public class BannerPagerAdapter extends RollingViewPagerAdapter<String> {

    private OnBannerClickListener listener;

    private Context context;
    private ArrayList<String> data;

    public interface OnBannerClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    public BannerPagerAdapter(Context context, ArrayList<String> data) {
        super(context, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public View getView(int i, String integer) {

        int pos = i % data.size();

        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null, false);
        ImageView image_container = (ImageView) view.findViewById(R.id.item_banner_image);
        Glide.with(context).load(data.get(pos)).into(image_container);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, pos);
            }
        });

        return view;
    }
}
