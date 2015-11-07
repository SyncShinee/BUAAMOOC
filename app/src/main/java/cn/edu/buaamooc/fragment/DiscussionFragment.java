package cn.edu.buaamooc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.buaamooc.R;

/**
 * Created by 昊 on 2015/10/29.
 */
public class DiscussionFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Layout= inflater.inflate(R.layout.fragment_course_discussion, container, false);
        return Layout;
    }
}
