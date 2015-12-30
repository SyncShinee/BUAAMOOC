package cn.edu.buaamooc.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import cn.edu.buaamooc.CONST;
import cn.edu.buaamooc.R;

public class AppSetting extends Activity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);
        final SharedPreferences appSettingInfo = getSharedPreferences("appSetting", MODE_PRIVATE);
        //back to te xml MyInformation
        final ImageView appsetting_back = (ImageView) findViewById(R.id.Appsetting_Left_Return);
        appsetting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSetting.this.finish();
            }
        });

        Switch wifiSwitch = (Switch) findViewById(R.id.wifi_switch);
        boolean onlywifi = appSettingInfo.getBoolean("onlywifi", true);
        wifiSwitch.setChecked(onlywifi);
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor appSettingEditor = appSettingInfo.edit();
                appSettingEditor.putBoolean("onlywifi", isChecked);
                appSettingEditor.apply();
            }
        });

        final TextView DirText = (TextView) findViewById(R.id.textview_dir);
        DirText.setText(appSettingInfo.getString("videoDir", CONST.DEFAULTVIDEO));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageManager sm = (StorageManager) AppSetting.this.getSystemService(Context.STORAGE_SERVICE);
                // 获取sdcard的路径：外置和内置
                final ArrayList<String> paths = new ArrayList<>();
                try {
                    String[] tDirs = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
                    for(String s : tDirs){
                        String state = Environment.getExternalStorageState(new File(s));
                        if(state.equals("mounted"))
                            paths.add(s + File.separator + "MOOCAPP"+ File.separator + "VIDEO");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }


                String[] items = paths.toArray(new String[paths.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(AppSetting.this);
                builder.setTitle("请选择视频保存位置");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        DirText.setText(paths.get(which));
                        SharedPreferences.Editor appSettingEditor = appSettingInfo.edit();
                        appSettingEditor.putString("videoDir", paths.get(which));
                        appSettingEditor.apply();
                    }
                });
                builder.create().show();
            }
        };
        TextView textChooser = (TextView) findViewById(R.id.text_choosedir);
        textChooser.setOnClickListener(listener);
        ImageButton imageChooser = (ImageButton) findViewById(R.id.image_choosedir);
        imageChooser.setOnClickListener(listener);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_setting, menu);
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
