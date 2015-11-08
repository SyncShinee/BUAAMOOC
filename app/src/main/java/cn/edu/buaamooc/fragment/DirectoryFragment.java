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


/**
 * Created by 昊 on 2015/10/29.
 */
public class DirectoryFragment extends Fragment {

    private ListView mListView;
    private boolean first = true;
    private TreeAdapter adapter;
    private static Handler mHandler;
    private Handler dHandler;
    private static List<Node> mDataList;
    private String course_id;
    private boolean course_id_right = true;
    private static JSONObject course_ware;
    private TextView btn_quit_enroll;
    private Resources resources;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataList = new ArrayList<Node>();
        initData();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Layout = inflater.inflate(R.layout.fragemnt_course_directory, container, false);
        btn_quit_enroll = (TextView) Layout.findViewById(R.id.course_enroll);
        try {
            course_id = ((CourseDetailActivity) getActivity()).getCourseId();
        } catch (Exception e) {
            course_id_right = false;
        }
        resources=getResources();
        initenrollbtn();
        mListView = (ListView) Layout.findViewById(R.id.course_structure_list);
        adapter = new TreeAdapter(getActivity(), mDataList);
        mListView.setAdapter(adapter);
        return Layout;
    }

    public void initenrollbtn() {
        dHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONST.enroll_succeed:
                        Toast.makeText(getActivity(),"选课成功",Toast.LENGTH_LONG).show();
                        btn_quit_enroll.setText(getResources().getString(R.string.course_quit));
                        break;
                    case CONST.enroll_fail:
                        Toast.makeText(getActivity(),"选课失败",Toast.LENGTH_LONG).show();
                        break;
                    case CONST.unenroll_fail:
                        Toast.makeText(getActivity(),"退课失败",Toast.LENGTH_LONG).show();
                        break;
                    case CONST.unenroll_succeed:
                        Toast.makeText(getActivity(),"退课成功",Toast.LENGTH_LONG).show();
                        btn_quit_enroll.setText(getResources().getString(R.string.course_enroll));
                        break;
                }
                btn_quit_enroll.setBackground(getActivity().getDrawable(R.drawable.course_btn_bg));
                btn_quit_enroll.setClickable(true);
            }
        };
        btn_quit_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = btn_quit_enroll.getText().toString();
                btn_quit_enroll.setClickable(false);
                btn_quit_enroll.setBackgroundColor(resources.getColor(R.color.colorDefaultBg));
                if (label.equals(resources.getString(R.string.course_enroll))) {
                    enroll();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(resources.getString(R.string.unenroll_query))
                            .setTitle(resources.getString(R.string.remind))
                            .setPositiveButton(resources.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    unenroll();
                                }
                            })
                            .setNegativeButton(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                }
            }
        });
    }

    public void enroll() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    if (new MOOCConnection().MOOCCourseEnroll(course_id, 1)) {
                        msg.what = CONST.enroll_succeed;
                    } else {
                        msg.what = CONST.enroll_fail;
                    }
                    dHandler.sendMessage(msg);
                }
            }).start();
        } catch (Exception e) {

        }
    }

    public void unenroll() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    if (new MOOCConnection().MOOCCourseEnroll(course_id, 0)) {
                        msg.what = CONST.unenroll_succeed;
                    } else {
                        msg.what = CONST.unenroll_fail;
                    }
                    dHandler.sendMessage(msg);
                }
            }).start();
        } catch (Exception e) {

        }
    }

    private void initData() {
        mHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == CONST.enrolled) {
                    try {
                        if (adapter != null) {
                            Logger.e("update tree structure");
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Logger.e("update tree structure :" + e.toString());
                        e.printStackTrace();
                    }
                } else if (msg.what == CONST.unenrolled) {
                    btn_quit_enroll.setText(getResources().getString(R.string.course_enroll));
                    btn_quit_enroll.setVisibility(View.VISIBLE);
                }
            }
        };

        try {
            Thread courseware_thread = new Thread(new Runnable() {
                public void run() {
                    Logger.e("run");
                    if (course_id_right) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        course_ware = new MOOCConnection().MOOCCourseware(course_id);
                        try {
                            if (course_ware.getBoolean("status")) {
                                btn_quit_enroll.setVisibility(View.GONE);
                                Message m = new Message();
                                m.what = CONST.enrolled;
                                mHandler.sendMessage(m);
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
                                Message m = new Message();
                                m.what = CONST.unenrolled;
                                mHandler.sendMessage(m);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.e(e.toString());
                        }
                    }
                }
            });
            courseware_thread.start();
        } catch (Exception ee) {
            Logger.e(ee.toString());
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

}
