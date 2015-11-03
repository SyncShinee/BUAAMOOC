package cn.edu.buaamooc.tools;

import android.util.Log;

import org.json.JSONArray;

/**
 * Created by dt on 2015/11/3.
 */
public class CourseList {

    private static JSONArray allCourses;
    private static JSONArray myCourses;

    public CourseList(){

    }

    public CourseList setAllCourses(JSONArray allCourseJSONArray) {
        allCourses = allCourseJSONArray;
        Log.e("allCourseJSONArray", allCourseJSONArray.toString());
        return this;
    }

    public CourseList setMyCourses(JSONArray myCourseJSONArray) {
        myCourses = myCourseJSONArray;
        Log.e("myCourseJSONArray", myCourseJSONArray.toString());
        return this;
    }

}
