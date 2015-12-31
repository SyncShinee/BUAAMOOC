package cn.edu.buaamooc;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Toast;

import com.nmbb.oplayer.database.SQLiteHelperOrm;
import com.nmbb.oplayer.preference.PreferenceUtils;
import com.nmbb.oplayer.ui.player.VP;

import cn.edu.buaamooc.db.CommonDBOpenHelper;
import cn.edu.buaamooc.view.Node;
import cn.edu.buaamooc.view.TreeAdapter;
import cn.edu.buaamooc.view.ViewHolder;
import io.vov.vitamio.utils.ScreenResolution;

/**
 * Created by 昊 on 2015/12/31.
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            DownloadManager.Query query = new DownloadManager.Query();
            //在广播中取出下载任务的id
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            query.setFilterById(id);
            Cursor c = manager.query(query);
            if (c.moveToFirst()) {
                //获取文件下载路径
                String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                String uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
                //如果文件名不为空，说明已经存在了，拿到文件名想干嘛都好
                if (filename != null) {
                    SQLiteDatabase db = new CommonDBOpenHelper(context, "cdb", null, 1).getWritableDatabase();
                    db.execSQL("update course_structure set path=? where url=?", new String[]{filename, uri});
                    Cursor c1=db.rawQuery("select percent from course_structure where url=?", new String[]{uri});
                    float f=0.1f;
                    if(c1.moveToFirst())
                    {
                        f=(float)c1.getInt(0)/100;
                        Toast.makeText(context,""+f,Toast.LENGTH_LONG).show();
                    }
                    c1.close();
                    if(TreeAdapter.holders!=null) {
                        ViewHolder holder = TreeAdapter.holders.get(uri);
                        if (holder != null) {
                            holder.download.setVisibility(View.GONE);
                            holder.condition.setText("已下载");
                            holder.condition.setVisibility(View.VISIBLE);
                            Node n = (Node) holder.label.getTag();
                            n.setUrl(filename);
                        }
                        PreferenceUtils
                                .put(filename + VP.SESSION_LAST_POSITION_SUFIX,f);
                        Toast.makeText(context, "下载完成", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
            long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            //点击通知栏取消下载
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(ids[0]);
            Cursor c = manager.query(query);
            if (c.moveToFirst()) {
                //获取文件下载路径
                String uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
                if(TreeAdapter.holders!=null) {
                    ViewHolder holder = TreeAdapter.holders.get(uri);
                    if (holder != null) {
                        holder.condition.setVisibility(View.GONE);
                        holder.download.setVisibility(View.VISIBLE);
                    }
                }
            }
            manager.remove(ids);
            Toast.makeText(context, "下载已取消", Toast.LENGTH_LONG).show();
        }
    }
}
