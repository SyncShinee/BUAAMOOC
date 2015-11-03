package cn.edu.buaamooc.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.buaamooc.activity.MoocMainActivity;

/**
 * Created by dt on 2015/11/2.
 */
public class Login {
    private String username;
    private String password;
    private boolean rememberMe;

    private JSONObject resultJsonObject;
    private Handler mHandler; //用于处理实现登陆函数的线程返回的数据
    private Handler mHandler1; //用于处理登陆之后获取已选课程和全部课程的线程返回的数据
    private Handler mHandler2; //用于处理浏览课程功能获取全部课程的线程返回的数据

    private JSONArray allCourseArray;
    private JSONArray myCourseArray;


    private JSONObject myCourse;

    private Context mContext;

    public Login(final String username, final String password) {
        this.username = username;
        this.password = password;
        rememberMe = false;
        resultJsonObject = new JSONObject();


        mHandler=new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0x111){
                    //登录成功，获取已选课程和全部课程
                    Toast.makeText(mContext, "登陆成功",Toast.LENGTH_SHORT).show();
                    //更新MoocMainActivity中的ViewPager中的第三个fragment，变为CourseListFragment
                    mHandler1=new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            if(msg.what==0x111){

                                try {
                                    boolean status = !myCourse.isNull("status") && myCourse.getBoolean("status");
                                    if (status){
                                        myCourseArray = myCourse.getJSONArray("enrollment");
                                        ((MoocMainActivity) mContext).refreshLoginInfo(true);
                                    }
                                    //获取已选课程
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //页面跳转
                            }
                            else if(msg.what==0x010){
                                Toast.makeText(mContext, "网络连接失败，请重试。",Toast.LENGTH_SHORT).show();
                            }

                            else{
                                //用户名密码错误，弹出对话框
                                Toast.makeText(mContext, "用户名或者密码错误，请重新输入。",Toast.LENGTH_SHORT).show();
                            }
                        }

                    };

                    try{
                        new Thread(new Runnable() {
                            @SuppressLint("HandlerLeak")
                            public void run() {
                                MOOCConnection mooc = new MOOCConnection() ;
                                allCourseArray = mooc.MOOCCourses();
                                //获取全部课程
                                myCourse = mooc.MOOCGetCourseEnrollment();

                                if (rememberMe){
                                    SharedPreferences loginInfo = mContext.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor loginEditor = loginInfo.edit();
                                    loginEditor.putString("username", username);
                                    loginEditor.putString("password", password);
                                    loginEditor.apply();
                                }

                                Message m=new Message();
                                m.what=0x111;
                                mHandler1.sendMessage(m);   //发送成功信息
                            }
                        }).start();

                        //JSONObject resultJsonObject = mooc.MOOCLogin();

                    }
                    catch(Exception ee){

                    }

                }else{
                    Toast.makeText(mContext, "用户名或者密码错误，请重新输入。",Toast.LENGTH_SHORT).show();
                }
            }

        };
    }

    public Login setContext(Context mContext){
        this.mContext = mContext;
        return this;
    }

    public Login setRememberMe(){
        rememberMe = true;
        return this;
    }

    public void login(){
        try{
            new Thread(new Runnable() {
                public void run() {
                    MOOCConnection mooc = new MOOCConnection() ;
                    resultJsonObject = mooc.MOOCLogin(username,password);
                    try {
                        if(resultJsonObject.getBoolean("success")==true){
                            //登陆成功
                            Message m=new Message();
                            m.what=0x111;
                            mHandler.sendMessage(m);	//发送信息

                        }
                        else{
                            //登录失败
                            Message m=new Message();
                            m.what=0x110;
                            mHandler.sendMessage(m);	//发送信息
                        }
                    } catch (JSONException e) {
                        // TODO 自动生成的 catch 块
                        //登陆出现异常，网络有问题
                        Message m=new Message();
                        m.what=0x010;
                        mHandler.sendMessage(m);
                    }
                }
            }).start();

        }
        catch(Exception ee){
            ee.printStackTrace();
        }
    }

}
