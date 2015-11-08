package cn.edu.buaamooc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.Calendar;

import cn.edu.buaamooc.R;

public class AccountSetting extends Activity {

    private TextView showData;
    private Button pickData;
    private TextView showEducation;
    private Button pickEducation;
    private TextView showSex;
    private Button pickSex;

    private static final int SHOW_DATAPICK = 0;
    private static final int DATE_DIALOG_ID = 1;
    private static final int SHOW_EDUPICK= 2;
    private static final int DATE_DIALOG_ID1 = 3;
    private static final int SHOW_SEXPICK= 4;
    private static final int DATE_DIALOG_ID2 = 5;

    private int mYear;
    private int mMonth;
    private int mDay;
    private String mSex = "男";
    private String mEdu = "大学";
    private String mDate;

 //   private SharedPreferences preferences=getSharedPreferences("AccoutSetting", Context.MODE_PRIVATE);
 //   private SharedPreferences.Editor edit = preferences.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);



        //back to te xml MyInformation
        ImageView accountsetting_back = (ImageView) findViewById(R.id.AccountSetting_Left_Return);
        accountsetting_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSetting.this.finish();
            }
        });

        //skip to the xml PassWordReset
        TextView passwordreset = (TextView) findViewById(R.id.PasswordEdit);
        passwordreset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSetting.this, PassWordReset.class);
                startActivityForResult(intent,1);
            }
        });

        initializeViews();

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        setDateTime();

   //     showData.setText(preferences.getString("mData","2015-11-08"));
    //    showSex.setText(preferences.getString("mSex","男"));
   //     showEducation.setText(preferences.getString("mEdu","大学"));
    }

    private void initializeViews(){
        showData = (TextView) findViewById(R.id.YearEdit);
        pickData = (Button) findViewById(R.id.changeData);
        showEducation = (TextView) findViewById(R.id.EducationEdit);
        pickEducation = (Button) findViewById(R.id.changeEdu);
        showSex = (TextView) findViewById(R.id.SexEdit);
        pickSex = (Button) findViewById(R.id.changeSex);

        pickData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                if (pickData.equals((Button) v)) {
                    msg.what = AccountSetting.SHOW_DATAPICK;
                }
                AccountSetting.this.dateandtimeHandler.sendMessage(msg);
            }
        });

        pickSex.setOnClickListener(new View.OnClickListener() {
            String[] sexChoice = {"男", "女"};

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AccountSetting.this).setTitle("选择性别").setSingleChoiceItems(sexChoice, 0, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSex = sexChoice[which];
             //           edit.putString("mSex",mSex);
             //           edit.commit();
                        showSex.setText(mSex);
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showSex.setText(mSex);
                    //    edit.putString("mSex",mSex);
                    //    edit.commit();
                    }
                }).show();
            }
        });

        pickEducation.setOnClickListener(new View.OnClickListener() {
            String[] EducationChoice = {"小学","初中","高中","大学","硕士","博士"};

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AccountSetting.this).setTitle("选择学历").setSingleChoiceItems(EducationChoice, 0, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEdu= EducationChoice[which];
                    //    edit.putString("mEdu",mEdu);
                     //   edit.commit();
                        showEducation.setText(mEdu);
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showEducation.setText(mEdu);
                  //      edit.putString("mEdu",mEdu);
                 //       edit.commit();
                    }
                }).show();
            }
        });
    }

    private void setDateTime(){
        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        updateDateDisplay();
    }



    private void updateDateDisplay(){
        mDate = new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay).toString();
        showData.setText(mDate);
       // edit.putString("mDate",mDate);
      //  edit.commit();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            updateDateDisplay();
        }
    };

    Handler dateandtimeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AccountSetting.SHOW_DATAPICK:
                    showDialog(DATE_DIALOG_ID);
                    break;
                case AccountSetting.SHOW_SEXPICK:
                    showDialog(DATE_DIALOG_ID2);
                    break;
                case AccountSetting.SHOW_EDUPICK:
                    showDialog(DATE_DIALOG_ID1);
                    break;

            }
        }

    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                        mDay);

        }

        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
