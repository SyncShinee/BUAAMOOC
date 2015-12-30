package cn.edu.buaamooc.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

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
    private static LoginHandler loginHandler; //用于处理实现登录函数的线程返回的数据
    private static LoginJumpHandler loginJumpHandler; //用于处理登录之后获取已选课程和全部课程的线程返回的数据

    private boolean autoLogin;


    private Context mContext;

    public Login(){

    }

    static class LoginHandler extends Handler{
        private Context mContext;
        private String username;
        private String password;
        private String name;
        private boolean rememberMe;

        LoginHandler(Context mContext){
            this.mContext = mContext;
        }

        public LoginHandler setUserInfo(String username, String password){
            this.username = username;
            this.password = password;
            return this;
        }

        public LoginHandler setRememberMe(boolean rememberMe){
            this.rememberMe = rememberMe;
            return this;
        }

        public LoginHandler setName(String name){
            this.name = name;
            return this;
        }

        @Override
        public void handleMessage(Message msg){
            if(msg.what==0x111){
                Toast.makeText(mContext, "登录成功",Toast.LENGTH_SHORT).show();
                SharedPreferences loginInfo = mContext.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor loginEditor = loginInfo.edit();
                loginEditor.putString("user_name", name);
                loginEditor.apply();
                try{
                    new Thread(new Runnable() {
                        @SuppressLint("HandlerLeak")
                        public void run() {
                            if (rememberMe){
                                SharedPreferences loginInfo = mContext.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                                SharedPreferences.Editor loginEditor = loginInfo.edit();
                                loginEditor.putString("loginname", username);
                                loginEditor.putString("password", password);
                                loginEditor.apply();
                            }

                            Message m=new Message();
                            m.what=0x111;
                            loginJumpHandler.sendMessage(m);   //发送成功信息
                        }
                    }).start();

                    //JSONObject resultJsonObject = mooc.MOOCLogin();

                }
                catch(Exception ee){

                }

            }else{
                Message m = new Message();
                m.what = msg.what;
                loginJumpHandler.sendMessage(m);
//                Toast.makeText(mContext, "用户名或者密码错误，请重新输入。",Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class LoginJumpHandler extends Handler{
        private Context mContext;
        private boolean autoLogin;

        LoginJumpHandler(Context mContext){
            this.mContext = mContext;
        }

        public LoginJumpHandler setAutoLogin(boolean autoLogin) {
            this.autoLogin = autoLogin;
            return this;
        }

        @Override
        public void handleMessage(Message msg) {
            //更新MoocMainActivity中的ViewPager中的第三个fragment，变为CourseListFragment
            if (msg.what == 0x111) {
                if (autoLogin) {
                    ((MoocMainActivity) mContext).initializeViewPager();
                }
                else {
                    ((MoocMainActivity) mContext).refreshLoginInfo(true, autoLogin);
                }
//              ((MoocMainActivity) mContext).setLogCondition(true).initializeViewPager();
                //获取已选课程
            }
            else if (msg.what == 0x010) {
                Toast.makeText(mContext, "网络连接失败，请重试。", Toast.LENGTH_SHORT).show();
                ((MoocMainActivity) mContext).failedLogin().initializeViewPager();
            }

            else {
                //用户名密码错误，弹出对话框
                Toast.makeText(mContext, "用户名或者密码错误，请重新输入。", Toast.LENGTH_SHORT).show();
                ((MoocMainActivity) mContext).failedLogin().initializeViewPager();
            }
        }
    }

    public Login(final String username, final String password) {
        this.username = username;
        this.password = password;
        rememberMe = false;
        resultJsonObject = new JSONObject();
    }

    /**
     * Set argument needed.
     * @param mContext Context : MoocMainActivity.
     * @return this object.
     */
    public Login setContext(Context mContext){
        this.mContext = mContext;
        return this;
    }

    /**
     * Store username & password & auto log in information.
     * @return this object.
     */
    public Login setRememberMe(){
        rememberMe = true;
        return this;
    }

    /**
     * set autoLogin info.
     * @param autoLogin if true, that means it's auto log in. Default false.
     * @return this object.
     */
    public Login setAuto(boolean autoLogin) {
        this.autoLogin = autoLogin;
        return this;
    }

    /**
     * Set username and password.
     * @param username username of account.
     * @param password password of account.
     * @return this object.
     */
    public Login setUserInfo(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    /**
     * Log in...
     */
    public void login(){
        loginHandler = new LoginHandler(mContext);
        loginHandler.setUserInfo(username, password).setRememberMe(rememberMe);
        loginJumpHandler = new LoginJumpHandler(mContext).setAutoLogin(autoLogin);
        try{
            new Thread(new Runnable() {
                public void run() {
                    MOOCConnection mooc = new MOOCConnection() ;
                    resultJsonObject = mooc.MOOCLogin(username,password);
                    try {
                        if(resultJsonObject!= null && !resultJsonObject.isNull("success") && resultJsonObject.getBoolean("success")){
                            //登录成功
                            String name = resultJsonObject.isNull("user_name")?"":resultJsonObject.getString("user_name");
                            loginHandler.setName(name);
                            Message m=new Message();
                            m.what=0x111;
                            loginHandler.sendMessage(m);	//发送信息

                        }
                        else{
                            //登录失败
                            Message m=new Message();
                            m.what=0x110;
                            loginHandler.sendMessage(m);	//发送信息
                        }
                    } catch (JSONException e) {
                        // 自动生成的 catch 块
                        //登录出现异常，网络有问题
                        Message m=new Message();
                        m.what=0x010;
                        loginHandler.sendMessage(m);
                    }
                }
            }).start();

        }
        catch(Exception ee){
            ee.printStackTrace();
        }
    }

    /**
     * Log out...
     */
    public void logout(){
        SharedPreferences loginInfo = mContext.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginInfo.edit();
        loginEditor.clear();
        loginEditor.apply();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new MOOCConnection().refreshDataAndReInit();
            }
        });
        ((MoocMainActivity) mContext).refreshLoginInfo(false);
    }
}
