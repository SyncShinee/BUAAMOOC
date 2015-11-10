package cn.edu.buaamooc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.buaamooc.R;

public class PassWordReset extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_word_reset);

        //back to the xml AccountSetting
        ImageView passwordreset_back = (ImageView) findViewById(R.id.passwordReset_Left_Return);
        passwordreset_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassWordReset.this.finish();
            }
        });

        TextView button_reset = (TextView) findViewById(R.id.button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PassWordReset.this).setMessage("此功能暂未开放。请去往网页版修改密码。\"因为我们的问题给您造成的困扰请见谅！\"").setPositiveButton("确定",null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pass_word_reaet, menu);
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
