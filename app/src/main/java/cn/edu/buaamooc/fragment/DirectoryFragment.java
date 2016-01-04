package cn.edu.buaamooc.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.buaamooc.CONST;
import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.CourseDetailActivity;
import cn.edu.buaamooc.db.CommonDBOpenHelper;
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
    private static Handler mHandler;
    private TextView remind_enroll;
    private List<Node> mDataList;
    private String course_id;
    private boolean course_id_right = true;
    private JSONObject course_ware;
    private Resources resources;
    private Thread net;
    private SQLiteDatabase db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataList = new ArrayList<Node>();
        db= new CommonDBOpenHelper(getContext(),"cdb",null,1).getWritableDatabase();
        try {
            course_id = ((CourseDetailActivity) getActivity()).getCourseId();
        } catch (Exception e) {
            course_id_right = false;
        }
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
            }
        };
        initData();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Layout = inflater.inflate(R.layout.fragemnt_course_directory, container, false);

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
        Cursor cursor=db.rawQuery("select * from course_structure where course_id='"+course_id+"'",null);
        if(cursor.getCount()>0)
        {
            getdatafromlocal();
        }
        else {
            getdatafromserver();
        }
        cursor.close();
    }

    public void updateview() {
        remind_enroll.setVisibility(View.GONE);
        initData();
    }

    private void getdatafromserver() {
        try {
            net = new Thread(new Runnable() {
                public void run() {
                    if (course_id_right) {
                        course_ware = new MOOCConnection().MOOCCourseware(course_id);
                        if(course_ware==null)
                        {
                            Logger.e("null");
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getdatafromserver();
                            return;
                        }
                        try {
                            Message m = new Message();
                            if (course_ware.getBoolean("status")) {
                                m.what = CONST.enrolled;
                                JSONArray sections = course_ware.getJSONArray("sections");
                                JSONObject section;
                                JSONArray subsections;
                                JSONObject subsection;
                                JSONArray units;
                                JSONObject unit;
                                JSONArray videos;
                                JSONObject video;
                                Node root1, root2, root3;
                                int id1,id2,id3;
                                String child1="",child2="";
                                Cursor cursor=db.rawQuery("select max(_id) as id from course_structure",null);
                                cursor.moveToFirst();
                                int _id=cursor.getInt(cursor.getColumnIndex("id"));
                                for (int i = 0; i < sections.length(); i++) {
                                    section = sections.getJSONObject(i);
                                    subsections = section.getJSONArray("sections");
                                    root1 = new Node(null, section.getString("display_name"));
                                    id1=++_id;
                                    if(id1==52)
                                    {
                                        int a=1+1;
                                    }
                                    db.execSQL("insert into course_structure (_id,course_id,parent,percent,label) values (?,?,?,?,?)"
                                            , new String[]{"" + id1, course_id, "", "0", section.getString("display_name")});//一级目录
                                    for (int j = 0; j < subsections.length(); j++) {
                                        subsection = subsections.getJSONObject(j);
                                        units = subsection.getJSONArray("units");
                                        root2 = new Node(root1, subsection.getString("display_name"));
                                        id2=++_id;
                                        child1+=""+id2+",";
                                        db.execSQL("insert into course_structure (course_id,parent,percent,label) values (?,?,?,?)"
                                                ,new String[]{course_id,id1+"","0",subsection.getString("display_name")});//二级目录
                                        for (int k = 0; k < units.length(); k++) {
                                            unit = units.getJSONObject(k);
                                            videos = unit.getJSONArray("verticals");
                                            for (int n = 0; n < videos.length(); n++) {
                                                video = videos.getJSONObject(n);
                                                if(video.isNull("video")) {
                                                    id3=++_id;
                                                    child2+=""+id3+",";
                                                    try {
                                                        String address = video.getString("video_sources");
                                                        address = address.substring(address.indexOf('\"') + 1, address.lastIndexOf('\"'));
                                                        address = address.replace("\\", "");
                                                        if (address.startsWith("/"))
                                                            address = CONST.URL + address;
                                                        root3 = new Node(root2, unit.getString("name"), address);
                                                        db.execSQL("insert into course_structure (course_id,parent,child,percent,label,url) values (?,?,?,?,?,?)"
                                                                ,new String[]{course_id,id2+"","","0",unit.getString("name"),address});//三级目录
                                                    }catch (Exception e) {
                                                        root3 = new Node(root2, unit.getString("name"), CONST.URL);
                                                        db.execSQL("insert into course_structure (course_id,parent,child,percent,label,url) values (?,?,?,?,?,?)"
                                                                ,new String[]{course_id,id2+"","","0",unit.getString("name"),CONST.URL});//三级目录
                                                        Logger.e(e.toString()+unit.getString("name"));
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                        db.execSQL("update course_structure set child = ? where _id=?"
                                            ,new String[]{child2,""+id2});//二级目录
                                        child2="";
                                    }
                                    mDataList.add(root1);
                                    db.execSQL("update course_structure set child = ? where _id=?"
                                            , new String[]{child1, "" + id1});//一级目录
                                    child1="";
                                }
                                cursor.close();
                            } else {
                                mDataList.clear();
                                m.what = CONST.unenrolled;
                            }
                            mHandler.sendMessage(m);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.e(e.toString());
                        }
                    }
                }
            });
            net.start();
        } catch (Exception ee) {
            Logger.e(ee.toString());
        }
    }

    private void getdatafromlocal()
    {
        String sql= String.format("select * from course_structure where parent='' and course_id='%s'",course_id);
        String sql1,sql2;
        String label1,label2,label3;
        Cursor c=db.rawQuery(sql, null);
        Cursor c1,c2;
        Node root1, root2, root3;
        if(c.getCount()==0)
            return;
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())//第一级目录
        {
            root1 = new Node(null, c.getString(c.getColumnIndex("label")));
            String child_s11=c.getString(c.getColumnIndex("child"));
            if(child_s11!=null) {
                String[] childs =child_s11.split(",");
                for (String child : childs) {//第二级目录
                    if (child != null && !child.equals("")) {
                        sql1 = String.format("select * from course_structure where _id= %s", child);
                        c1 = db.rawQuery(sql1, null);
                        c1.moveToFirst();
                        root2 = new Node(root1, c1.getString(c1.getColumnIndex("label")));
                        String childs_a = c1.getString(c1.getColumnIndex("child"));
                        if (childs_a != null) {
                            String[] childs1 = childs_a.split(",");
                            for (String aChilds1 : childs1) {//第三级目录
                                if (aChilds1 != null && !aChilds1.equals("")) {
                                    sql2 = String.format("select * from course_structure where _id= %s", aChilds1);
                                    c2 = db.rawQuery(sql2, null);
                                    c2.moveToFirst();
                                    String url, path;
                                    url = c2.getString(c.getColumnIndex("url"));
                                    path = c2.getString(c2.getColumnIndex("path"));
                                    if (path != null)
                                        root3 = new Node(root2, c2.getString(c2.getColumnIndex("label")), path);
                                    else
                                        root3 = new Node(root2, c2.getString(c2.getColumnIndex("label")), url);
                                    c2.close();
                                }
                            }
                        }
                        c1.close();
                    }
                }
            }
            mDataList.add(root1);
        }
        c.close();
    }

    public void onDestroy() {
        mHandler.removeCallbacks(net);
        super.onDestroy();
    }
    public void onResume() {
        super.onResume();
        if(TreeAdapter.current_holder!=null) {
            String url = ((Node) TreeAdapter.current_holder.label.getTag()).getUrl();
            if (url != null) {
                Cursor c = db.rawQuery("select percent,path from course_structure where url=? or path=?", new String[]{url,url});
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    TreeAdapter.current_holder.progress.setText(c.getString(0) + "%");
                    if(c.getString(1)!=null&&!c.getString(1).equals(""))
                    {
                        TreeAdapter.current_holder.download.setVisibility(View.GONE);
                        TreeAdapter.current_holder.condition.setText("已下载");
                        TreeAdapter.current_holder.condition.setVisibility(View.VISIBLE);
                    }
                }
                c.close();
            }
        }
        MobclickAgent.onPageStart("MainScreen");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}
