package cn.edu.buaamooc.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.buaamooc.CONST;
import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.CourseDetailActivity;
import cn.edu.buaamooc.activity.MoocMainActivity;
import cn.edu.buaamooc.tools.MOOCConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseListFragment extends Fragment {

    private MoocMainActivity activity;

    private int tabIndex;

    private JSONArray courseArray;
    private JSONObject myCourseObject;
    private ArrayList<HashMap<String, Object>> sourceList;
    private SimpleAdapter simpleAdapter;


    public CourseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_course_list, container, false);

        Log.e("CourseListFragment", "onCreateView.");
        activity = (MoocMainActivity) getActivity();
        sourceList = new ArrayList<>();

        Bundle bundle = getArguments();
        tabIndex = bundle.getInt("tabIndex");
        //test git
        ListView list = (ListView) layout.findViewById(R.id.listview_course);

        simpleAdapter = new SimpleAdapter(
                activity,
                sourceList,
                R.layout.listview_item_courses_list,
                new String[]{"image", "title", "start"},
                new int[]{R.id.listview_item_course_pic, R.id.listview_item_course_title, R.id.listview_item_course_date});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                //判断是否为我们要处理的对象
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView iv = (ImageView) view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }
                else {
                    return false;
                }
            }

        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String courseId = String.valueOf(sourceList.get(position).get("course_id"));
                String name = String.valueOf(sourceList.get(position).get("title"));
                Intent intent =new Intent(getActivity(), CourseDetailActivity.class);
                intent.putExtra("course_id",courseId);
                intent.putExtra("course_name",name);
                startActivityForResult(intent,0);
            }
        });
        list.setAdapter(simpleAdapter);


//        CourseListAdapter courseListAdapter = new CourseListAdapter(activity, sourceList);
//        list.setAdapter(courseListAdapter);

        refreshList();

        return layout;
    }

    @Override
    public void onDestroyView() {
        Log.e("CourseListFragment", "onDestroyView.");
        super.onDestroyView();
    }

    /**
     * Get courses form server and update listView.
     */
    public void refreshList() {
        final String name = tabIndex==0?"display_name":"course_title";


        final Handler handler = new Handler(){
            @Override
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                Log.e("Handle", "handle Message.");
                if (myCourseObject == null && courseArray==null){
                    Toast.makeText(activity, "获取课程失败，请重试。",Toast.LENGTH_SHORT).show();
                    Log.e("myCourseObject", "null");
                    Log.e("courseArray", "null");
                    return;
                }
                if (tabIndex == 0){
                    try {
                        boolean status = false;
                        if (myCourseObject != null) {
                            status = !myCourseObject.isNull("status") && myCourseObject.getBoolean("status");
                        }
                        if (status){
                            courseArray = myCourseObject.getJSONArray("enrollment");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (courseArray == null){
                    Toast.makeText(activity, "获取课程失败，请重试。",Toast.LENGTH_SHORT).show();
                    Log.e("courseArray", "null");
                    return;
                }
                int n;
                JSONObject json;
                HashMap<String, Object> map;
                try{
                    n = courseArray.length();
                    for(int i = 0; i < n; i++) {
                        json = courseArray.getJSONObject(i);
                        map = new HashMap<String, Object>();
                        map.put("title",json.isNull(name)?"无":json.getString(name));
                        map.put("start","开课时间：" + (json.isNull("course_start")?"无":json.getString("course_start").substring(0, 10)));
                        map.put("image",R.drawable.buaa_logo);
                        map.put("course_id",json.isNull("course_id")?"":json.getString("course_id"));
                        map.put("image_url", json.isNull("course_image_url") ? "" : json.getString("course_image_url"));
                        String display = json.isNull("display_number")?"":json.getString("display_number");
                        map.put("courseId", display);
                        if (tabIndex == 1) {
                            if (display.equals("M_G06B2830")
                                    || display.equals("M_E06B2150")
                                    || display.equals("M_E06B3150")){
                                sourceList.add(0,map);
                            }
                            else {
                                sourceList.add(map);
                            }
                        }
                        else {
                            sourceList.add(map);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (tabIndex == 1) {
                    while (sourceList.size()>10) {
                        sourceList.remove(10);
                    }
                }
                simpleAdapter.notifyDataSetChanged();
                refreshImage();
            }
        };

        sourceList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MOOCConnection mooc = new MOOCConnection();
                switch (tabIndex) {
                    case 1:
                    case 2:
                        courseArray = mooc.MOOCCourses();
                        break;
                    case 0:
                        myCourseObject = mooc.MOOCGetCourseEnrollment();
                        break;
                }
                try {
                    Thread.sleep(100,1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(new Message());
            }
        }).start();

    }

    /**
     * Load course images.
     */
    public void refreshImage(){

        final Handler mHandler = new Handler(){
            @Override
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                if(msg.what != -1){
                    HashMap<String, Object> oldmap = sourceList.get(msg.what);
                    HashMap<String, Object> newmap = new HashMap<>();
                    Bitmap bitmap = msg.getData().getParcelable("bitmap");
                    newmap.put("title", oldmap.get("title"));
                    newmap.put("start", oldmap.get("start"));
                    newmap.put("course_id",oldmap.get("course_id"));
                    newmap.put("courseId", oldmap.get("courseId"));
                    newmap.put("image", bitmap);
                    sourceList.set(msg.what,newmap);
                    //将获取的图片放到map中，以改变listItems中对应位置的图片
                    simpleAdapter.notifyDataSetChanged();
                    //刷新页面
                }
            }

        };

        new Thread(new Runnable() {
            public void run() {
                try{
                    int n = sourceList.size();
                    for(int i = 0 ; i<n; i++){
                        String path = sourceList.get(i).get("image_url").toString(); //获取课程图片的存储路径
                        Log.e("image_url", path);
                        Bitmap bitmap;
                        String filedir = CONST.COURSEPIC+ File.separator + path;
                        File file = new File(filedir);
                        if (file.exists()){
                            //从本地缓存读取图片，保存在bitmap中
//                            BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inSampleSize = 10;
                            bitmap = BitmapFactory.decodeFile(filedir);
                        }
                        else {
                            //从网络下载图片，保存在bitmap中
                            bitmap=new MOOCConnection().MOOCGetImage(path);
                        }
                        if(bitmap ==null){
                            continue ;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("bitmap", bitmap);
                        Message m=new Message();
                        m.what=i;
                        m.setData(bundle);
                        mHandler.sendMessage(m);
                        //给mHandler发送消息，消息内容是要刷新图片的位置
                    }

                }
                catch(Exception e){
                    Message m=new Message();
                    m.what=-1;
                    mHandler.sendMessage(m);
                    //捕获到异常，则不刷新页面
                }

            }
        }).start();
    }

//    @Override
//    public void onStart(){
//        Log.e("CourseListFragment", "onStart" + tabIndex);
//        super.onStart();
//    }
//
//    @Override
//    public void onResume(){
//        Log.e("CourseListFragment", "onResume" + tabIndex);
//        super.onResume();
//    }
//
//    @Override
//    public void onPause(){
//        Log.e("CourseListFragment", "onPause" + tabIndex);
//        super.onPause();
//    }
//
//    @Override
//    public void onStop(){
//        Log.e("CourseListFragment", "onStop" + tabIndex);
//        super.onStop();
//    }

}
