package net.belicon.cartion.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.belicon.cartion.R;
import net.belicon.cartion.models.ExpandData;

import java.util.List;

public class ExpandAdapter extends BaseExpandableListAdapter {

    private List<ExpandData> mExpandList;

    public ExpandAdapter(List<ExpandData> mExpandList) {
        this.mExpandList = mExpandList;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mExpandList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        if (mExpandList.size() == 0) {
            return 0;
        }
        return mExpandList.size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_title, parent, false);
        }
        TextView titleText = convertView.findViewById(R.id.item_expand_title_text);
        titleText.setText(mExpandList.get(groupPosition).getTitle());
        ImageView img = convertView.findViewById(R.id.item_expand_indicator);
        if (isExpanded) {
            img.setImageResource(R.drawable.ic_up_arrow);
        } else {
            img.setImageResource(R.drawable.ic_drop_arrow);
        }
        return convertView;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mExpandList.get(groupPosition).getChild().get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mExpandList.get(groupPosition).getChild().size() == 0) {
            return 0;
        }
        return mExpandList.get(groupPosition).getChild().size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child, parent, false);
        }
        TextView titleText = convertView.findViewById(R.id.item_expand_child_text);
        titleText.setText(Html.fromHtml(mExpandList.get(groupPosition).getChild().get(childPosition)));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
