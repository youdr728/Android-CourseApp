package com.example.course_app;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a comment entity with related user and course information.
 */
public class Comment {
    private int id;
    private String text;
    private int courseId;
    private int userId;
    private String username;

    /**
     * Constructs a Comment object from a JSON representation.
     */
    public Comment(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.text = jsonObject.getString("text");
            this.courseId = jsonObject.getInt("course_id");
            this.userId = jsonObject.getInt("user_id");
            this.username = jsonObject.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
