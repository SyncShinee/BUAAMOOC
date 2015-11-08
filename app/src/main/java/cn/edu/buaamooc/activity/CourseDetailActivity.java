package cn.edu.buaamooc.activity;


import android.content.res.Resources;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import cn.edu.buaamooc.R;
import cn.edu.buaamooc.fragment.DirectoryFragment;
import cn.edu.buaamooc.fragment.DiscussionFragment;
import cn.edu.buaamooc.fragment.IntroduceFragment;
import cn.edu.buaamooc.view.yh_HeadControlPanel;

public class CourseDetailActivity extends FragmentActivity {

    private Handler mHandler,mHandler1;
    private ViewPager viewPager;
    private String course_id;
    private int currIndex = 0;
    private int position_one;
    private int position_two;
    private ImageView ivBottomLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetail);
        course_id=getIntent().getStringExtra("course_id");
        if(course_id.equals("")) {
            Toast.makeText(this,"不存在该课程",Toast.LENGTH_LONG).show();
            finish();
        }
        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
        yh_HeadControlPanel head = (yh_HeadControlPanel) findViewById(R.id.course_head);
        head.setLeftTitle();
        head.setMiddleTitle(getIntent().getStringExtra("course_name"));
        InitWidth();
        InitViewPager();
        InitTab();
    }

    private void setChecked(int text_id) {
        Resources resources = getResources();
        TextView text = (TextView) findViewById(text_id);
        text.setTextColor(resources.getColor(R.color.course_black));
    }

    private void unChecked(int text_id) {
        Resources resources = getResources();
        TextView text = (TextView) findViewById(text_id);
        text.setTextColor(resources.getColor(R.color.course_gray));
    }

    public String getCourseId() {
        return course_id;
    }

    /**
     * 初始化底栏，获取相应宽度信息
     */
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

    /**
     * 初始化ViewPager
     */
    private void InitViewPager() {
        //获取布局中的viewpager控件
        viewPager = (ViewPager) findViewById(R.id.course_detail_page);
        //给ViewPager添加适配器
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        //设置默认的视图为第0个
        viewPager.setCurrentItem(0);
        //给Viewpager添加监听事件
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 设置三个按键的点击事件
     */
    private void InitTab() {
        findViewById(R.id.course_introduce_text).setOnClickListener(new CourseOnClickListener(0));
        findViewById(R.id.course_directory_text).setOnClickListener(new CourseOnClickListener(1));
        findViewById(R.id.course_discussion_text).setOnClickListener(new CourseOnClickListener(2));
    }


//    public void initSystemBar(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(activity, true);
//        }
//        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//        tintManager.setStatusBarTintEnabled(true);
//        // 使用颜色资源
//        tintManager.setStatusBarTintResource(R.color.course_transparent);
//    }
//
//    @TargetApi(19)
//    private static void setTranslucentStatus(Activity activity, boolean on) {
//        Window win = activity.getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int index) {
            //动画
            Animation animation = null;
            switch (index) {
                case 0:
                    if (currIndex == 1) {
                        //代码生成滑动动画
                        animation = new TranslateAnimation(position_one, 0, 0, 0);
                        //改变目录的颜色值，使其没有选中的效果
                        unChecked(R.id.course_directory_text);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, 0, 0, 0);
                        unChecked(R.id.course_discussion_text);
                    }
                    //改变课程简介的颜色值，使其有选中的效果
                    setChecked(R.id.course_introduce_text);
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(0, position_one, 0, 0);
                        unChecked(R.id.course_introduce_text);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
                        unChecked(R.id.course_discussion_text);
                    }
                    setChecked(R.id.course_directory_text);
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(0, position_two, 0, 0);
                        unChecked(R.id.course_introduce_text);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
                        unChecked(R.id.course_directory_text);
                    }
                    setChecked(R.id.course_discussion_text);
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

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentArrayList;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentArrayList = new ArrayList<>();
            fragmentArrayList.add(new DirectoryFragment());
            fragmentArrayList.add(new IntroduceFragment());
            fragmentArrayList.add(new DiscussionFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    /**
     * 自定义监听类  。
     */
    private class CourseOnClickListener implements View.OnClickListener {
        private int index = 0;

        public CourseOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            //设置ViewPager的当前view
            viewPager.setCurrentItem(index);
        }
    }

}
