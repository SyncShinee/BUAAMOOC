package cn.edu.buaamooc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 昊 on 2015/11/4.
 */
public class UserDBOpenHelper extends SQLiteOpenHelper {


    public UserDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //已选课程列表
        //保存选课日期
        String sql="CREATE TABLE IF NOT EXISTS course_enroll(" +
                "course_id text primary key," +
                "enrollment text,"+
                "enrollment_date text)";
        db.execSQL(sql);

        //课程进度
        //保存用户学习进度
        sql="CREATE TABLE IF NOT EXISTS course_structure(" +
                "_id integer primary key autoincrement," +
                "finished text)";
        db.execSQL(sql);

        //用户设置
//        sql="CREATE TABLE IF NOT EXISTS user_setting(" +

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
