package cn.edu.buaamooc.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import org.json.JSONObject;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.CourseDetailActivity;
import cn.edu.buaamooc.db.DBUtil;
import cn.edu.buaamooc.exception.Logger;
import cn.edu.buaamooc.tools.MOOCConnection;


/**
 * Created by 昊 on 2015/10/29.
 */
public class IntroduceFragment extends Fragment {

    private Handler handler;
    private TextView introduce;
    private String content;
    private String course_id;
    private DBUtil dbUtil;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        try {
            course_id = ((CourseDetailActivity) getActivity()).getCourseId();
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x111) {
                    if (introduce != null) {
                        introduce.setText(content);
                    }
                }
            }
        };
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Layout = inflater.inflate(R.layout.fragment_course_introduce, container, false);
        introduce = (TextView) Layout.findViewById(R.id.course_introduce);
        dbUtil = new DBUtil(getActivity());

        SQLiteDatabase cdb = dbUtil.getCDB();
        try {
            course_id = ((CourseDetailActivity) getActivity()).getCourseId();
            Cursor c = cdb.rawQuery("select course_about from course where course_id = '"+course_id+"'",null);
            if (c.getCount() == 0) {
                getCourseintroduce();
            } else {
                c.moveToFirst();
                String content = c.getString(0);
                introduce.setText(content);
            }
            c.close();
            cdb.close();
        } catch (Exception e) {
            Logger.e(e.toString());
        }

        return Layout;
    }

    public void getCourseintroduce() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject JsonObject = new MOOCConnection().MOOCCourseAbout(course_id);
                    //获取保存课程信息的JSON

                    if (JsonObject == null) {
                        //没有获取到课程信息
                        Message m = new Message();
                        m.what = 0x100;
                        handler.sendMessage(m);
                    } else {
                        //发送信息，说明获取信息成功
                        String htmldata = JsonObject.getString("about");
                        Message m = new Message();
                        htmldata = htmldata.replace("\\/", "/");
                        String index[] = htmldata.split("<");
                        for (int i = 0; i < index.length; i++) {
                            index[i] = index[i].substring(index[i].indexOf('>') + 1);
                        }
                        content = "";
                        for (String anIndex : index) {
                            content += anIndex;
                        }
//                        new DBUtil(getContext()).update("course","course",new String []{"course_about"},
//                            new String[]{htmldata},new String [] {"course_id"},new String[] {course_id});
//                        dbUtil.insert("common", "course", new String[]{"course_id", "course_about"},
//                                new String[]{course_id, content});
                        m.what = 0x111;
                        handler.sendMessage(m);

                    }
                } catch (Exception e) {
                    Logger.e(e.toString());
                }

            }
        }).start(); //
    }
}
