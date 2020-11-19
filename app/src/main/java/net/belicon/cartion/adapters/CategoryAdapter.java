package net.belicon.cartion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.belicon.cartion.R;

import java.util.List;
import java.util.zip.Inflater;

public class CategoryAdapter extends BaseAdapter {

    private List<String> mCategoryList;

    public CategoryAdapter(List<String> mCategoryList) {
        this.mCategoryList = mCategoryList;
    }

    @Override
    public int getCount() {
        if (mCategoryList.size() == 0) {
            return 0;
        }
        return mCategoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);
        }

        if(mCategoryList!=null){
            String text = mCategoryList.get(position);
            ((TextView)convertView.findViewById(R.id.item_category_text)).setText(text);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_drop, parent, false);
        }

        String text = mCategoryList.get(position);
        ((TextView)convertView.findViewById(R.id.item_category_drop_text)).setText(text);

        return convertView;
    }
}
