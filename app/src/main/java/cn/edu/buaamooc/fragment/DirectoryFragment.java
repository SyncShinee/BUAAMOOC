package cn.edu.buaamooc.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.buaamooc.CONST;
import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.CourseDetailActivity;
import cn.edu.buaamooc.exception.Logger;
import cn.edu.buaamooc.tools.MOOCConnection;
import cn.edu.buaamooc.view.Node;
import cn.edu.buaamooc.view.TreeAdapter;
import io.vov.vitamio.utils.Log;


/**
 * Created by 昊 on 2015/10/29.
 */
public class DirectoryFragment extends Fragment {

    private ListView mListView;
    private boolean first = true;
    private TreeAdapter adapter;
    private Handler mHandler;
    private TextView remind_enroll;
    private List<Node> mDataList;
    private String course_id;
    private boolean course_id_right = true;
    private JSONObject course_ware;
    private Resources resources;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataList = new ArrayList<Node>();
        mHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == CONST.enrolled) {
                    try {
                        if (adapter != null) {
                            remind_enroll.setVisibility(View.GONE);
                            Logger.e("update tree structure");
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Logger.e("update tree structure :" + e.toString());
                        e.printStackTrace();
                    }
                } else if (msg.what == CONST.unenrolled) {
                    remind_enroll.setVisibility(View.VISIBLE);
                }
                ((IntroduceFragment)((CourseDetailActivity)getActivity()).getFragment(0)).setenrollbtn(msg.what);
            }
        };
        initData();
        Logger.e("initdata");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Layout = inflater.inflate(R.layout.fragemnt_course_directory, container, false);
        try {
            course_id = ((CourseDetailActivity) getActivity()).getCourseId();
        } catch (Exception e) {
            course_id_right = false;
        }
        resources=getResources();
        mListView = (ListView) Layout.findViewById(R.id.course_structure_list);
        remind_enroll=(TextView) Layout.findViewById(R.id.remind_enroll);
        initListview();
        return Layout;
    }

    private void initListview() {
        adapter = new TreeAdapter(getActivity(), mDataList);
        mListView.setAdapter(adapter);
    }

    private void initData() {
        getdatafromserver();
    }

    public void updateview() {
        initData();
    }

    private void getdatafromserver() {
        try {
            new Thread(new Runnable() {
                public void run() {
                    Logger.e("run");
                    if (course_id_right) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        course_ware = new MOOCConnection().MOOCCourseware(course_id);
                        try {
                            Message m = new Message();
                            Message m_introduce =new Message();
                            if (course_ware.getBoolean("status")) {
                                m.what = CONST.enrolled;
                                m_introduce.what=CONST.enrolled;
                                JSONArray sections = course_ware.getJSONArray("sections");
                                JSONObject section;
                                JSONArray subsections;
                                JSONObject subsection;
                                JSONArray units;
                                JSONObject unit;
                                JSONArray videos;
                                JSONObject video;
                                Node root1, root2, root3;
                                for (int i = 0; i < sections.length(); i++) {
                                    section = sections.getJSONObject(i);
                                    subsections = section.getJSONArray("sections");
                                    root1 = new Node(null, section.getString("display_name"));
                                    for (int j = 0; j < subsections.length(); j++) {
                                        subsection = subsections.getJSONObject(j);
                                        units = subsection.getJSONArray("units");
                                        root2 = new Node(root1, subsection.getString("display_name"));
                                        for (int k = 0; k < units.length(); k++) {
                                            unit = units.getJSONObject(k);
                                            videos = unit.getJSONArray("verticals");
                                            for (int n = 0; n < videos.length(); n++) {
                                                video = videos.getJSONObject(n);
                                                String address = video.getString("video_sources");
                                                address = address.substring(address.indexOf('\"') + 1, address.lastIndexOf('\"'));
                                                address = address.replace("\\", "");
                                                if (address.startsWith("/"))
                                                    address = CONST.URL + address;
                                                root3 = new Node(root2, unit.getString("name"), address);
                                            }
                                        }
                                    }
                                    mDataList.add(root1);
                                }
                            } else {
                                m.what = CONST.unenrolled;
                                m_introduce.what=CONST.unenrolled;
                            }
                            mHandler.sendMessage(m);
//                            IntroduceFragment.handler.sendMessage(m_introduce);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.e(e.toString());
                        }
                    }
                }
            }).start();
        } catch (Exception ee) {
            Logger.e(ee.toString());
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
