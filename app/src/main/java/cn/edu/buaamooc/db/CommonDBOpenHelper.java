package cn.edu.buaamooc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 昊 on 2015/11/4.
 */
public class CommonDBOpenHelper extends SQLiteOpenHelper {
    public CommonDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户
        String sql = "CREATE TABLE IF NOT EXISTS user(" +
                "_id integer primary key autoincrement," +
                "user_full_name text," +
                "redirect_url text," +
                "current_user text,"+                       //标记是否为当前用户
                "user_name text unique)";
        db.execSQL(sql);

        //课程
        //各个属性都是服务器返回的json中的字段
        sql="CREATE TABLE IF NOT EXISTS course(" +
                "course_id text primary key," +
                "course_title text,"+
                "display_name text,"+
                "display_number text,"+
                "active text,"+
                "course_end text,"+
                "course_start text,"+
                "enrolment_start text," +
                "display_organization text,"+
                "advertised_start text,"+
                "course_image_url text,"+
                "course_image_path text,"+                      //缓存到本地的课程图片地址
                "is_full text,"+
                "course_about text)";
        db.execSQL(sql);

        //课程结构
        //存放课程结构、学习进度及视频下载到本地的地址
        sql="CREATE TABLE IF NOT EXISTS course_structure(" +
                "_id integer primary key autoincrement," +
                "course_id text,"+
                "parent text,"+                                 //上级列表的id如果为根节点则不填
                "child text,"+                                  //以空格为间隔的数组
                "url text,"+                                    //在线视频地址
                "path text,"+                                   //本地地址
                "label text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
