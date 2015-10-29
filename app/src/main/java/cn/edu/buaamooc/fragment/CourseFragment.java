package cn.edu.buaamooc.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.MoocMainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends Fragment {

    /**
     * @param
     */

    private MoocMainActivity activity;

    private ListView list;
    public CourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        activity = (MoocMainActivity) getActivity();

        //test git
        final View layout = inflater.inflate(R.layout.fragment_course, container, false);
        ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("img",R.drawable.icon1);
        map.put("title","Course Title 1");
        map.put("date","2015-10-27");
        dataList.add(map);
        map.clear();

        map.put("img",R.drawable.icon2);
        map.put("title","Course Title 2");
        map.put("date", "2015-10-28");
        dataList.add(map);
        list = (ListView) layout.findViewById(R.id.listview_course);

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                activity,
                dataList,
                R.layout.listview_item_courses_list,
                new String[]{"img","title","date"},
                new int[]{R.id.listview_item_course_pic,R.id.listview_item_course_title,R.id.listview_item_course_date});
        list.setAdapter(simpleAdapter);


        return layout;
    }


}
