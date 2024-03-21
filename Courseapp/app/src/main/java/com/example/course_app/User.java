package com.example.course_app;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String username;
    private String password;  // Be aware this should be managed securely, consider not including password if it's not necessary

    public User(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.username = jsonObject.getString("username");
            this.password = jsonObject.getString("password");  // Be aware this should be managed securely, consider not including password if it's not necessary
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
