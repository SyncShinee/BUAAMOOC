package cn.edu.buaamooc.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.edu.buaamooc.exception.Logger;


/**
 * Created by 昊 on 2015/11/4.
 */
public class DBUtil {

    private Context context;

    public DBUtil(Context context) {
        this.context=context;
    }

    /**
     *
     * @return
     */
    public String getUID() {
        SQLiteDatabase cdb=getCDB();
        Cursor c=cdb.rawQuery("select _id from user where current_user = 'true' ",null);
        if(c.getCount()==0) {
            c.close();
            cdb.close();
            return "-1";
        }
        else {
            String uid=c.getString(0);
            c.close();
            cdb.close();
            return uid;
        }
    }

    /**
     *
     * @param db
     * @param table
     * @param items
     * @param values_i
     * @param key
     * @param values_k
     * @return
     */
    private boolean update(SQLiteDatabase db,String table,String[] items,String[] values_i,String[] key,String[] values_k){
        String str1="";
        String str2="";
        str1=items[0]+" = '"+values_i[0]+"' ";
        str2=key[0]+" ='"+values_k[0]+"' ";
        for(int i=1;i<items.length-1;i++){
            str1+=", "+items[i]+" = "+"'"+values_i[i]+"' ";
        }
        for(int i=1;i<key.length-1;i++){
            str2+="and "+key[i]+" = '"+values_k[i]+"' ";
        }
        String sql="update "+table+" set "+str1+"where "+str2;
        Cursor cursor=db.rawQuery("select * from "+table+" where "+str2,null);
        if(cursor.getCount()>0) {
            try {
                db.execSQL(sql);
                db.close();
                return true;
            }
            catch (Exception e)
            {
                db.close();
                Logger.e(e.toString());
                e.printStackTrace();
                return  false;
            }
        }
        else {
            db.close();
            return false;
        }
    }

    private boolean update(SQLiteDatabase db,String table,String[] items,String[] values_i){
        String str1="";
        str1=items[0]+" = '"+values_i[0]+"' ";
        for(int i=1;i<items.length-1;i++){
            str1+=", "+items[i]+" = "+"'"+values_i[i]+"' ";
        }
        String sql="update "+table+" set "+str1;
        Cursor cursor=db.rawQuery("select * from "+table,null);
        if(cursor.getCount()>0) {
            try {
                db.execSQL(sql);
                cursor.close();
                db.close();
                return true;
            }
            catch (Exception e)
            {
                db.close();
                cursor.close();
                Logger.e(e.toString());
                e.printStackTrace();
                return  false;
            }
        }
        else {
            cursor.close();
            db.close();
            return false;
        }
    }

    /**
     *
     * @param db
     * @param table
     * @param items
     * @param values
     */
    private String insert(SQLiteDatabase db,String table,String[] items,String[] values){
        String str1="";
        if(items.length>1)
            str1="("+items[0];
        else
            str1=items[0];
        for(int i=1;i<items.length-1;i++){
            str1+=", "+items[i];
        }
        if(items.length>1)
            str1+=","+items[items.length-1]+" )";

        String str2;
        if(items.length>1)
            str2="('"+values[0]+"'";
        else
            str2="'"+values[0]+"'";

        for(int i=1;i<items.length-1;i++){
            str2+=", '"+values[i]+"'";
        }
        if(items.length>1)
            str2+=", '"+values[items.length-1]+"')";
        String sql="insert into "+table+" "+ str1+" values "+str2;
        try {
            db.execSQL(sql);
            Cursor cursor=db.rawQuery("select * from " + table, null);
            cursor.moveToLast();
            String first=cursor.getString(0);
            cursor.close();
            db.close();
            return first;
        }
        catch (Exception e) {
            db.close();
            Logger.e(e.toString());
            e.printStackTrace();
            return "-1";
        }
    }

    /**
     *
     * @param db
     * @param table
     * @param items
     * @param values_i
     * @param key
     * @param values_k
     * @return
     */
    public boolean update(String db,String table,String[] items,String[] values_i,String[] key,String[] values_k) {
        if(db.equals("user")) {
            return update(getUDB(),table, items, values_i, key,values_k);
        }
        else if(db.equals("common")) {
            return update(getCDB(),table,items,values_i,key,values_k);
        }
        return false;
    }

    public boolean update(String db,String table,String[] items,String[] values_i) {
        if(db.equals("user")) {
            return update(getUDB(), table, items, values_i);
        }
        else if(db.equals("common")) {
            return update(getCDB(), table, items, values_i);
        }
        return false;
    }

    /**
     *
     * @param db
     * @param table
     * @param items
     * @param values
     * @return
     */
    public String insert(String db,String table,String[] items,String[] values){
        if(db.equals("user")) {
            return insert(getUDB(), table, items, values);
        }
        else if(db.equals("common")) {
            return insert(getCDB(), table, items, values);
        }
        return "-1";
    }

    /**
     *
     * @return
     */
    public SQLiteDatabase getUDB() {
        return new UserDBOpenHelper(context,"udb_"+getUID(),null,1).getWritableDatabase();
    }

    /**
     *
     * @return
     */
    public SQLiteDatabase getCDB() {
        return new CommonDBOpenHelper(context,"cdb",null,1).getWritableDatabase();
    }

    public boolean setCurrentUser(String user_name) {
        SQLiteDatabase cdb = getCDB();
        Cursor cursor = cdb.rawQuery("select * from user where user_name = 'username'",null);
        if (cursor.getCount() > 0) { //防止将所有current_user置为false
            cursor.close();
            update(cdb, "user", new String[]{"current_user"}, new String[]{"false"});
            return update("user", "user", new String[]{"current_user"}, new String[]{"true"},
                    new String[]{"user_name"}, new String[]{user_name});
        }
        else {
            cdb.close();
            cursor.close();
        }
        return false;
    }

}
