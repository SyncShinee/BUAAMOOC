package cn.edu.buaamooc.activity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.fragment.CourseListFragment;
import cn.edu.buaamooc.fragment.LoginFragment;
import cn.edu.buaamooc.tools.Login;
import cn.edu.buaamooc.tools.MOOCConnection;

public class MoocMainActivity extends FragmentActivity {

    /**
     * @param fm FragmentManager 实例;
     * @param hotTab 热门课程tab标签实例
     * @param allTab 所有课程tab标签实例
     * @param myTab 我的课程tab标签实例
     */

    private FragmentManager fm;
    private TextView hotTab;
    private TextView allTab;
    private TextView myTab;
    private View hotUnderline;
    private View allUnderline;
    private View myUnderline;

    private ArrayList<Fragment> fragmentList;
    private ViewPager viewPager;
    private FragmentPagerAdapter fpAdapter;
    private int currIndex;
    private ViewFlipper viewFlipper;
    private boolean loadLoginFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mooc_main);

        //initialize variables
        loadLoginFragment = false;

        //initialize tab controls
        hotTab = (TextView) findViewById(R.id.tab_title_hot);
        allTab = (TextView) findViewById(R.id.tab_title_all);
        myTab = (TextView) findViewById(R.id.tab_title_my);
        hotUnderline = findViewById(R.id.tab_underline_hot);
        allUnderline = findViewById(R.id.tab_underline_all);
        myUnderline = findViewById(R.id.tab_underline_my);


        final SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
        final String username = loginInfo.getString("username", "");
        if(!username.equals("")) {
            loadLoginFragment = true;
        }
        final Handler mHandler = new Handler() {
            //用来处理初始化函数的返回信息
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x111) {
                    //页面跳转到登陆界面
                    if(loadLoginFragment) {
                        String password = loginInfo.getString("password","");
                        new Login(username,password).setContext(MoocMainActivity.this).login();
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
//        setHotCourse();
        //initialize fragmentList

        fragmentList = new ArrayList<>(3);


        initializeViewPager();

        //set onclickListener event
        hotTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoocMainActivity.this.setHotCourse();
            }
        });
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

    }

    private void initializeViewPager() {
//        ArrayList<View> listViews = new ArrayList<View>();
//        LayoutInflater mInflater = getLayoutInflater();
//        listViews.add(mInflater.inflate(R.layout.fragment_course_list, null));
//        listViews.add(mInflater.inflate(R.layout.fragment_course_list, null));

        addFragments();

        fpAdapter = new FragmentPagerAdapter(fm) {
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
        setHotCourse();
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });

    }

//    private void InitWidth() {
//        //获取底栏白色滑动线的宽度
//        int bottomLineWidth = ivBottomLine.getLayoutParams().width;
//        int offset;
//        //获取屏幕宽度
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int screenW = dm.widthPixels;
//        //屏幕分3份，计算出每份滑条外的间隔距离
//        offset = (int) ((screenW / 3.0 - bottomLineWidth) / 2);
//        Matrix matrix = new Matrix();//这个matrix死活都用不了，设置偏移使用marginleft代替，60dp占标签的一半
//        matrix.setTranslate(offset, 0);
//        ivBottomLine.setImageMatrix(matrix);
//        //计算出底栏的位置
//        position_one = offset * 2 + bottomLineWidth;
//        position_two = position_one * 2;
//    }

    private void setChecked(int tabIndex) {

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int index) {
            //动画
            Animation animation = null;
//            switch (index) {
//                case 0:
//                    if (currIndex == 1) {
//                        //代码生成滑动动画
//                        animation = new TranslateAnimation(position_one, 0, 0, 0);
//                        //改变目录的颜色值，使其没有选中的效果
//                        unChecked(R.id.course_directory_text);
//                    } else if (currIndex == 2) {
//                        animation = new TranslateAnimation(position_two, 0, 0, 0);
//                        unChecked(R.id.course_discussion_text);
//                    }
//                    //改变课程简介的颜色值，使其有选中的效果
//                    setChecked(R.id.course_introduce_text);
//                    break;
//                case 1:
//                    if (currIndex == 0) {
//                        animation = new TranslateAnimation(0, position_one, 0, 0);
//                        unChecked(R.id.course_introduce_text);
//                    } else if (currIndex == 2) {
//                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
//                        unChecked(R.id.course_discussion_text);
//                    }
//                    setChecked(R.id.course_directory_text);
//                    break;
//                case 2:
//                    if (currIndex == 0) {
//                        animation = new TranslateAnimation(0, position_two, 0, 0);
//                        unChecked(R.id.course_introduce_text);
//                    } else if (currIndex == 1) {
//                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
//                        unChecked(R.id.course_directory_text);
//                    }
//                    setChecked(R.id.course_discussion_text);
//                    break;
//            }
            //记录当前的页面位置
            currIndex = index;
            switch (currIndex) {
                case 0:
                    setHotCourse();
                    break;
                case 1:
                    setALLCourse();
                    break;
                case 2:
                    setMyCourse();
                    break;
            }
            //动画播放完后，保持结束时的状态
//            assert animation != null;
//            animation.setFillAfter(true);
            //动画持续时间
//            animation.setDuration(300);
            //底栏滑动白线开始动画
//            findViewById(R.id.iv_bottom_line).startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }

    private void addFragments() {
        fragmentList = new ArrayList<>(3);

        Bundle bundle;
        CourseListFragment courseListFragment;

        bundle = new Bundle();
        courseListFragment = new CourseListFragment();
        bundle.putInt("tabIndex", 0);
        courseListFragment.setArguments(bundle);
        fragmentList.add(courseListFragment);

        bundle = new Bundle();
        courseListFragment = new CourseListFragment();
        bundle.putInt("tabIndex", 1);
        courseListFragment.setArguments(bundle);
        fragmentList.add(courseListFragment);

        addMyCourseFragment();

    }

    private void addMyCourseFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt("tabIndex", 2);
        if (loadLoginFragment) {
            CourseListFragment courseListFragment = new CourseListFragment();
            courseListFragment.setArguments(bundle);
            fragmentList.add(courseListFragment);
        }
        else {
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setArguments(bundle);
            fragmentList.add(loginFragment);
        }
    }

    public void refreshLoginInfo(boolean logged){
        loadLoginFragment = logged;
        Bundle bundle = new Bundle();
        bundle.putInt("tabIndex", 2);
        if (logged) {
            CourseListFragment courseListFragment = new CourseListFragment();
            courseListFragment.setArguments(bundle);
            fragmentList.set(2, courseListFragment);
        }
        else {
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setArguments(bundle);
            fragmentList.set(2, loginFragment);
        }
        fpAdapter.notifyDataSetChanged();
    }

    /**
     * set the current tab to HotCourse tab.
     */
    private void setHotCourse() {
        viewPager.setCurrentItem(0);
        hotUnderline.setVisibility(View.INVISIBLE);
        allUnderline.setVisibility(View.VISIBLE);
        myUnderline.setVisibility(View.VISIBLE);
        hotTab.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        allTab.setBackgroundColor(getResources().getColor(R.color.colorBgLightBlue));
        myTab.setBackgroundColor(getResources().getColor(R.color.colorBgLightBlue));
    }

    /**
     * set the current tab to AllCourse tab.
     */
    private void setALLCourse() {
        viewPager.setCurrentItem(1);
        hotUnderline.setVisibility(View.VISIBLE);
        allUnderline.setVisibility(View.INVISIBLE);
        myUnderline.setVisibility(View.VISIBLE);
        hotTab.setBackgroundColor(getResources().getColor(R.color.colorBgLightBlue));
        allTab.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        myTab.setBackgroundColor(getResources().getColor(R.color.colorBgLightBlue));
    }

    /**
     * set the current tab to MyCourse tab.
     */
    private void setMyCourse() {
        viewPager.setCurrentItem(2);
        hotUnderline.setVisibility(View.VISIBLE);
        allUnderline.setVisibility(View.VISIBLE);
        myUnderline.setVisibility(View.INVISIBLE);
        hotTab.setBackgroundColor(getResources().getColor(R.color.colorBgLightBlue));
        allTab.setBackgroundColor(getResources().getColor(R.color.colorBgLightBlue));
        myTab.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
    }

    /**
     * set variable loadLoginFragment
     *
     * @param logged boolean type. is user loadLoginFragment?
     */
    public void setLogCondition(boolean logged) {
        this.loadLoginFragment = logged;
    }


}
