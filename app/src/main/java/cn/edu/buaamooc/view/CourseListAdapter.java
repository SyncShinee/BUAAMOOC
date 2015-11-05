package cn.edu.buaamooc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.buaamooc.R;

/**
 * Created by dt on 2015/11/5.
 */
public class CourseListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<HashMap<String, Object>> items;

    public CourseListAdapter(Context context, ArrayList<HashMap<String, Object>> items) {
        this.mInflater = LayoutInflater.from(context);
        this.items = items;
    }
    @Override
    public int getCount(){
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        HashMap<String, Object> map = items.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listview_item_courses_list,null);
            holder.image = (ImageView) convertView.findViewById(R.id.listview_item_course_pic);
            holder.title = (TextView) convertView.findViewById(R.id.listview_item_course_title);
            holder.date = (TextView) convertView.findViewById(R.id.listview_item_course_date);
            holder.enroll = (ImageButton) convertView.findViewById(R.id.listview_item_course_enroll);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (map.get("image") instanceof Bitmap) {
            holder.image.setImageBitmap((Bitmap) map.get("image"));
        }
        else {
            holder.image.setImageResource(R.drawable.buaa_logo);
        }
        holder.title.setText(String.valueOf(map.get("title")));
        holder.date.setText(String.valueOf(map.get("start")));
        holder.enroll.setImageResource(R.drawable.add2);

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return position<items.size()?items.get(position):null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView date;
        public ImageButton enroll;
    }
}
