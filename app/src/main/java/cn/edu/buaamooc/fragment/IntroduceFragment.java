package cn.edu.buaamooc.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
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
import android.widget.Toast;


import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import cn.edu.buaamooc.CONST;
import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.CourseDetailActivity;
import cn.edu.buaamooc.db.DBUtil;
import cn.edu.buaamooc.exception.Logger;
import cn.edu.buaamooc.tools.MOOCConnection;


/**
 * Created by 昊 on 2015/10/29.
 */
public class IntroduceFragment extends Fragment {

    public static Handler handler;
    private TextView introduce;
    private String course_id;
    private DBUtil dbUtil;
    private TextView btn_quit_enroll;
    private Resources resources;
    private String content;
    private Boolean registered;
    private Thread net;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        try {
            course_id = ((CourseDetailActivity) getActivity()).getCourseId();
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONST.about_succeed:
                        btn_quit_enroll.setVisibility(View.VISIBLE);
                        introduce.setText(content);
                        if(registered) {
                            btn_quit_enroll.setText(resources.getString(R.string.course_quit));
                        }
                        else {
                            btn_quit_enroll.setText(resources.getString(R.string.course_enroll));
                        }
                        break;
                    case CONST.enroll_succeed:
                        Toast.makeText(getActivity(), "选课成功", Toast.LENGTH_LONG).show();
                        btn_quit_enroll.setText(getResources().getString(R.string.course_quit));
                        ((DirectoryFragment) ((CourseDetailActivity) getActivity()).getFragment(1)).updateview();
                        break;
                    case CONST.enroll_fail:
                        Toast.makeText(getActivity(), "选课失败", Toast.LENGTH_LONG).show();
                        break;
                    case CONST.unenroll_fail:
                        Toast.makeText(getActivity(), "退课失败", Toast.LENGTH_LONG).show();
                        break;
                    case CONST.unenroll_succeed:
                        Toast.makeText(getActivity(), "退课成功", Toast.LENGTH_LONG).show();
                        btn_quit_enroll.setText(getResources().getString(R.string.course_enroll));
                        ((DirectoryFragment) ((CourseDetailActivity) getActivity()).getFragment(1)).updateview();
                        break;
                }
                setbtnClickable();
            }
        };
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Layout = inflater.inflate(R.layout.fragment_course_introduce, container, false);
        introduce = (TextView) Layout.findViewById(R.id.course_introduce);
        btn_quit_enroll = (TextView) Layout.findViewById(R.id.course_enroll);
        resources = getResources();
        dbUtil = new DBUtil(getActivity());

        initText();
        initenrollbtn();
        return Layout;
    }

    public void initText() {
        SQLiteDatabase cdb = dbUtil.getCDB();
        try {
            course_id = ((CourseDetailActivity) getActivity()).getCourseId();
            Cursor c = cdb.rawQuery("select course_about from course where course_id = '" + course_id + "'", null);
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
    }

    public void initenrollbtn() {
        btn_quit_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = btn_quit_enroll.getText().toString();
                setbtnUnClickable();
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
                                    setbtnClickable();
                                }
                            }).create().show();
                }
            }
        });
    }

    private void setbtnUnClickable() {
        btn_quit_enroll.setClickable(false);
        btn_quit_enroll.setBackgroundColor(resources.getColor(R.color.colorDefaultBg));
        btn_quit_enroll.setTextColor(resources.getColor(R.color.black));
    }

    private void setbtnClickable() {
        btn_quit_enroll.setClickable(true);
        btn_quit_enroll.setBackgroundResource(R.drawable.course_btn_bg);
        btn_quit_enroll.setTextColor(resources.getColor(R.color.black));
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
                    handler.sendMessage(msg);
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
                    handler.sendMessage(msg);
                }
            }).start();
        } catch (Exception e) {

        }
    }

    public void getCourseintroduce() {
        net= new Thread(new Runnable() {
            public void run() {
                try {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    JSONObject JsonObject = new MOOCConnection().MOOCCourseAbout(course_id);
                    //获取保存课程信息的JSON
                    if (JsonObject == null) {
                        //没有获取到课程信息
                        Message m = new Message();
                        m.what = CONST.enroll_succeed;
                        handler.sendMessage(m);
                    } else {
                        //发送信息，说明获取信息成功
                        String htmldata = JsonObject.getString("about");
                        registered = JsonObject.getBoolean("registered");
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
                        m.what = CONST.about_succeed;
                        handler.sendMessage(m);

                    }
                } catch (Exception e) {
                    Logger.e(e.toString());
                }

            }
        });
        net.start(); //
    }

    public void setenrollbtn(int status) {
        if (status == CONST.enrolled) {
            btn_quit_enroll.setText(resources.getString(R.string.course_quit));
            btn_quit_enroll.setVisibility(View.VISIBLE);
        } else {
            btn_quit_enroll.setText(resources.getString(R.string.course_enroll));
            btn_quit_enroll.setVisibility(View.VISIBLE);
        }
    }

    public void onDestroy()
    {
        handler.removeCallbacks(net);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}
