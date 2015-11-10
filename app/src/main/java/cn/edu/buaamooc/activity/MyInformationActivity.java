package cn.edu.buaamooc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.fragment.LoginFragment;

public class MyInformationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        //initialize tab controls

        //initialize xml skip

        //back to the xml MoocMainActivity
        ImageView information_back = (ImageView) findViewById(R.id.Information_Left_Return);
        information_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.this.setResult(0);
                MyInformationActivity.this.finish();
            }
        });
        //skip to the xml MoocMainActivity
        TextView mycourse = (TextView) findViewById(R.id.button_mycourse);
        mycourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.this.setResult(1);
                MyInformationActivity.this.finish();
            }
        });
        //skip to the xml MyDownloads
        TextView mydownloads = (TextView) findViewById(R.id.button_mydownloads);
        mydownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInformationActivity.this,MyDownloads.class);
                startActivityForResult(intent,1);
            }
        });
        //skip to the xml AccountSetting
        TextView accountsetting = (TextView) findViewById(R.id.button_accountsetting);
        accountsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInformationActivity.this,AccountSetting.class);
                startActivityForResult(intent, 1);
            }
        });
        //skip to the xml AppSetting
        TextView appsetting = (TextView) findViewById(R.id.button_appsetting);
        appsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInformationActivity.this,AppSetting.class);
                startActivityForResult(intent,1);
            }
        });
        //skip to the xml AboutUs
        TextView aboutus = (TextView) findViewById(R.id.button_aboutus);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInformationActivity.this,AboutUs.class);
                startActivityForResult(intent,1);
            }
        });
        //back to the xml LoginFragment
        TextView logout = (TextView) findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformationActivity.this.setResult(2);
                MyInformationActivity.this.finish();
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
