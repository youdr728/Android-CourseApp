package com.example.course_app;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a user entity with id, username, and password.
 */
public class User {
    private int id;
    private String username;
    private String password;  // Be aware this should be managed securely, consider not including password if it's not necessary

    /**
     * Constructs a User object from a JSON representation.
     * throws JSONException if there is an error parsing the JSON
     */
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
