package cn.edu.buaamooc.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.buaamooc.R;

/**
 * Created by Yanhao on 15-7-29.
 */
public class yh_HeadControlPanel extends LinearLayout {

    private Activity mContext;
    private TextView mMidleTitle;
    private TextView mRightTitle;
    private LinearLayout mLeftTitle;
    private TextView left_text;
    public yh_HeadControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=(Activity)context;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        left_text=(TextView)findViewById(R.id.left_text);
        mMidleTitle = (TextView)findViewById(R.id.midle_title);
        mRightTitle = (TextView)findViewById(R.id.btn_head_right);
        mLeftTitle=(LinearLayout)findViewById(R.id.btn_head_left);
        mLeftTitle.setVisibility(INVISIBLE);
        super.onFinishInflate();
    }
    public void setMiddleTitle(String s){
        mMidleTitle.setText(s);
    }
    public void setLeftTitle(){
        mLeftTitle.setVisibility(VISIBLE);
        left_text.setText("返回");
        mLeftTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.onBackPressed();
            }
        });

    }



}
