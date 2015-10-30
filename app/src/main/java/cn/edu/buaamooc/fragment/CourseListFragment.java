package cn.edu.buaamooc.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.MoocMainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseListFragment extends Fragment {

    /**
     * @param
     */

    private MoocMainActivity activity;

    private int tabIndex;
    private View layout;

    private ListView list;
    public CourseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("CourseListFragment", "onCreateView.");
        activity = (MoocMainActivity) getActivity();

        Bundle bundle = getArguments();
        tabIndex = bundle.getInt("tabIndex");

        //test git
        layout = inflater.inflate(R.layout.fragment_course_list, container, false);
        ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("img",R.drawable.icon1);
        map.put("title", "Course Title " + String.valueOf(tabIndex));
        map.put("date", "2015-10-27");
        dataList.add(map);
        map.clear();

        map.put("img",R.drawable.icon2);
        map.put("title", "Course Title " + String.valueOf(tabIndex));
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

    @Override
    public void onDestroyView(){
        Log.e("CourseListFragment", "onDestroyView.");
        super.onDestroyView();
    }


}
