package com.example.course_app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private int id;
    private String courseName;
    private String courseInfo;
    private List<Comment> comments;

    public Course(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.courseName = jsonObject.getString("course_name");
            this.courseInfo = jsonObject.getString("course_info");
            comments = new ArrayList<>();
            JSONArray commentArray = jsonObject.getJSONArray("comments");
            for (int i = 0; i < commentArray.length(); i++) {
                Comment comment = new Comment(commentArray.getJSONObject(i));
                comments.add(comment);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
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
