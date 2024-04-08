package com.example.course_app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private String courseName;
    private String courseInfo;
    private List<Comment> comments;

    public Course(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.courseName = jsonObject.getString("course_name");
        this.courseInfo = jsonObject.getString("course_info");

        if (jsonObject.has("comments")) {
            comments = new ArrayList<>();
            try {
                JSONArray commentsJson = jsonObject.getJSONArray("comments");
                for (int i = 0; i < commentsJson.length(); i++) {
                    JSONObject commentJson = commentsJson.getJSONObject(i);
                    Comment comment = new Comment(commentJson);
                    this.comments.add(comment);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            Log.d("CourseFragment", "No comments found in the JSON response");
        }

    }

    public String getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseInfo() {
        return courseInfo;
    }

    public List<Comment> getComments() {
        return comments;
    }

}
