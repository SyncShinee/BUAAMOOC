package cn.edu.buaamooc.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.fragment.CourseFragment;
import cn.edu.buaamooc.fragment.LoginFragment;

public class MoocMainActivity extends Activity {

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

    private boolean logged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mooc_main);

        //initialize variables
        logged = false;

        //initialize tab controls
        hotTab = (TextView) findViewById(R.id.tab_title_hot);
        allTab = (TextView) findViewById(R.id.tab_title_all);
        myTab = (TextView) findViewById(R.id.tab_title_my);
        hotUnderline = findViewById(R.id.tab_underline_hot);
        allUnderline = findViewById(R.id.tab_underline_all);
        myUnderline = findViewById(R.id.tab_underline_my);

        fm = getFragmentManager();
        setHotCourse();

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
    /**
     * set the current tab to HotCourse tab.
     */
    private void setHotCourse(){
        hotUnderline.setVisibility(View.INVISIBLE);
        allUnderline.setVisibility(View.VISIBLE);
        myUnderline.setVisibility(View.VISIBLE);
        hotTab.setBackgroundColor(getResources().getColor(R.color.colorMainWhite));
        allTab.setBackgroundColor(getResources().getColor(R.color.colorMainbgGolden));
        myTab.setBackgroundColor(getResources().getColor(R.color.colorMainbgGolden));
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("tabIndex", 0);
        CourseFragment courseFragment = new CourseFragment();
        courseFragment.setArguments(bundle);
        ft.replace(R.id.frame_main, courseFragment);
        ft.commit();
    }
    /**
     * set the current tab to AllCourse tab.
     */
    private void setALLCourse(){
        hotUnderline.setVisibility(View.VISIBLE);
        allUnderline.setVisibility(View.INVISIBLE);
        myUnderline.setVisibility(View.VISIBLE);
        hotTab.setBackgroundColor(getResources().getColor(R.color.colorMainbgGolden));
        allTab.setBackgroundColor(getResources().getColor(R.color.colorMainWhite));
        myTab.setBackgroundColor(getResources().getColor(R.color.colorMainbgGolden));
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("tabIndex",1);
        CourseFragment courseFragment = new CourseFragment();
        courseFragment.setArguments(bundle);
        ft.replace(R.id.frame_main, courseFragment);
        ft.commit();
    }
    /**
     * set the current tab to MyCourse tab.
     */
    private void setMyCourse(){
        hotUnderline.setVisibility(View.VISIBLE);
        allUnderline.setVisibility(View.VISIBLE);
        myUnderline.setVisibility(View.INVISIBLE);
        hotTab.setBackgroundColor(getResources().getColor(R.color.colorMainbgGolden));
        allTab.setBackgroundColor(getResources().getColor(R.color.colorMainbgGolden));
        myTab.setBackgroundColor(getResources().getColor(R.color.colorMainWhite));
        FragmentTransaction ft = fm.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        ft.replace(R.id.frame_main, loginFragment);
        ft.commit();
    }

    /**
     * set variable logged
     *
     * @param logged boolean type. is user logged?
     */
    public void setLogCondition(boolean logged){
        this.logged = logged;
    }
}
