package cn.edu.buaamooc.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.exception.Logger;
import cn.edu.buaamooc.fragment.CourseListFragment;
import cn.edu.buaamooc.fragment.LoginFragment;
import cn.edu.buaamooc.tools.Login;
import cn.edu.buaamooc.tools.MOOCConnection;

public class MoocMainActivity extends FragmentActivity {

    private FragmentManager fm;
    private TextView hotTab;
//    private RelativeLayout hotTabLayout;
    private TextView allTab;
    private TextView myTab;

    //fragment list in viewPager
    private ArrayList<Fragment> fragmentList;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter fpAdapter;
    private int currIndex;
    private boolean loadLoginFragment;
    private ImageView ivBottomLine;
    private int position_one;
    private int position_two;

    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mooc_main);

        //initialize variables
        loadLoginFragment = false;
        currIndex = 0;
        //initialize tab controls
        hotTab = (TextView) findViewById(R.id.tab_title_hot);
//        hotTabLayout = (RelativeLayout) findViewById(R.id.tab_title_hot_layout);
        allTab = (TextView) findViewById(R.id.tab_title_all);
        myTab = (TextView) findViewById(R.id.tab_title_my);
        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);

        InitWidth();

        final SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
        username = loginInfo.getString("username", "");
        final String password = loginInfo.getString("password","");
        if(!password.equals("")) {
            loadLoginFragment = true;
        }
        final Handler mHandler = new Handler() {
            //用来处理初始化函数的返回信息
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x111) {
                    //页面跳转到登录界面
                    if(loadLoginFragment) {
                        new Login(username,password).setContext(MoocMainActivity.this).setAuto(true).login();
                    }
                    else {
                        initializeViewPager();
                    }
                }
                else {
                    Toast.makeText(MoocMainActivity.this, "无网络连接，请重试", Toast.LENGTH_SHORT).show();
                }
            }

        };

        new Thread(new Runnable() {
            public void run() {
                boolean initInfo = new MOOCConnection().MOOCInit();
                if (initInfo) {
                    Message m = new Message();
                    m.what = 0x111;
                    mHandler.sendMessage(m);    //发送成功信息
                }
                else {
                    Message m = new Message();
                    m.what = 0x110;
                    mHandler.sendMessage(m);    //发送信息showDialog("无网络连接，请检查后重试");
                }
            }
        }).start();    //开启一个线程


        fm = getSupportFragmentManager();
        //initialize fragmentList
        fragmentList = new ArrayList<>(3);

        //set onclickListener event
        hotTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoocMainActivity.this.setHotCourse();
            }
        });
//        hotTabLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MoocMainActivity.this.setHotCourse();
//            }
//        });
        allTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoocMainActivity.this.setALLCourse();
            }
        });
        myTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoocMainActivity.this.setMyCourse();
            }
        });

//        initializeViewPager();
//        setMyCourse();


        ImageButton userInfo = (ImageButton) findViewById(R.id.button_user_info);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoocMainActivity.this, MyInformationActivity.class);
                startActivityForResult(intent,0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (resultCode) {
            case 1:
                // see my enrolled course, from myInformationActivity.
                setMyCourse();
                break;
            case 2:
                //log out, from myInformationActivity.
                new Login().setContext(this).logout();
                setALLCourse();
                break;
            case 3:
                //refresh my course list, from course detail activity.
                Fragment fragment = fragmentList.get(0);
                if (fragment instanceof CourseListFragment) {
                    ((CourseListFragment) fragment).refreshList();
                }
        }
    }

    /**
     * Initialize ViewPager.
     */
    public void initializeViewPager() {
        fragmentList.clear();
        addFragments();

        fpAdapter = new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return 3;
            }
        };


        viewPager = (ViewPager) findViewById(R.id.viewpager_course_list);
        viewPager.setAdapter(fpAdapter);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
//        setHotCourse();
        setMyCourse();

    }

    private void InitWidth() {
        //获取底栏白色滑动线的宽度
        int bottomLineWidth = ivBottomLine.getLayoutParams().width;
        int offset;
        //获取屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        //屏幕分3份，计算出每份滑条外的间隔距离
        offset = (int) ((screenW / 3.0 - bottomLineWidth) / 2);
        Matrix matrix = new Matrix();//这个matrix死活都用不了，设置偏移使用marginleft代替，60dp占标签的一半
        matrix.setTranslate(offset, 0);
        ivBottomLine.setImageMatrix(matrix);
        //计算出底栏的位置
        position_one = offset * 2 + bottomLineWidth;
        position_two = position_one * 2;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int index) {
            //动画
            hintKbTwo();
            Animation animation = null;
            switch (index) {
                case 0:
                    if (currIndex == 1) {
                        //代码生成滑动动画
                        animation = new TranslateAnimation(position_one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, 0, 0, 0);
                    }
                    //改变课程简介的颜色值，使其有选中的效果
                    setMyCourse();
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(0, position_one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
                    }
                    setHotCourse();
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(0, position_two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
                    }
                    setALLCourse();
                    break;
            }
            //记录当前的页面位置
            currIndex = index;
            //动画播放完后，保持结束时的状态
            assert animation != null;
            animation.setFillAfter(true);
            //动画持续时间
            animation.setDuration(300);
            //底栏滑动白线开始动画
            findViewById(R.id.iv_bottom_line).startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }

    /**
     * Add fragments to ViewPager.
     */
    private void addFragments() {
//        fragmentList = new ArrayList<>(3);
        addMyCourseFragment();

        Bundle bundle;
        CourseListFragment courseListFragment;

        bundle = new Bundle();
        courseListFragment = new CourseListFragment();
        bundle.putInt("tabIndex", 1);
        courseListFragment.setArguments(bundle);
        fragmentList.add(courseListFragment);

        bundle = new Bundle();
        courseListFragment = new CourseListFragment();
        bundle.putInt("tabIndex", 2);
        courseListFragment.setArguments(bundle);
        fragmentList.add(courseListFragment);


    }

    /**
     * Initializing the third fragment. Login fragment or my course fragment.
     */
    private void addMyCourseFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt("tabIndex", 0);
        if (loadLoginFragment) {
            CourseListFragment courseListFragment = new CourseListFragment();
            courseListFragment.setArguments(bundle);
            fragmentList.add(courseListFragment);
        }
        else {
            LoginFragment loginFragment = new LoginFragment();
            bundle.putString("username", username);
            loginFragment.setArguments(bundle);
            fragmentList.add(loginFragment);
        }
    }

    public void refreshLoginInfo(boolean logged, boolean autoLogin) {
        refreshLoginInfo(logged);
        if (!autoLogin) {
            setALLCourse();
        }
    }
    /**
     * Change between login fragment and my course fragment.
     * @param logged record the login information.
     */
    public void refreshLoginInfo(boolean logged){
        loadLoginFragment = logged;
        Bundle bundle = new Bundle();
        bundle.putInt("tabIndex", 0);
        if (logged) {
            CourseListFragment courseListFragment = new CourseListFragment();
            courseListFragment.setArguments(bundle);
            fragmentList.set(0, courseListFragment);
        }
        else {
            LoginFragment loginFragment = new LoginFragment();
            bundle.putString("username", username);
            loginFragment.setArguments(bundle);
            fragmentList.set(0, loginFragment);
        }
//        setHotCourse();
        fpAdapter.notifyDataSetChanged();

    }

    /**
     * set the current tab to HotCourse tab.
     */
    public void setHotCourse() {
        viewPager.setCurrentItem(1);
        hotTab.setTextColor(getResources().getColor(R.color.course_black));
        allTab.setTextColor(getResources().getColor(R.color.course_gray));
        myTab.setTextColor(getResources().getColor(R.color.course_gray));
    }

    /**
     * set the current tab to AllCourse tab.
     */
    public void setALLCourse() {
        viewPager.setCurrentItem(2);
        hotTab.setTextColor(getResources().getColor(R.color.course_gray));
        allTab.setTextColor(getResources().getColor(R.color.course_black));
        myTab.setTextColor(getResources().getColor(R.color.course_gray));
    }

    /**
     * set the current tab to MyCourse tab.
     */
    public void setMyCourse() {
        viewPager.setCurrentItem(0);
        hotTab.setTextColor(getResources().getColor(R.color.course_gray));
        allTab.setTextColor(getResources().getColor(R.color.course_gray));
        myTab.setTextColor(getResources().getColor(R.color.course_black));
    }

    /**
     * Record the last username.
     * @param username the last username after log in.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
