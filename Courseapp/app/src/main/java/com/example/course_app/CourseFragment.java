package com.example.course_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseFragment extends Fragment {

    String url = "http://10.244.35.120:5000/";

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        TextView courseInfo = view.findViewById(R.id.course_info);

        SharedPreferences shared_info = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        courseInfo.setText(shared_info.getString("current_course_info", null));

        EditText comment_tf = view.findViewById(R.id.addComment);
        ListView commentList = view.findViewById(R.id.commentsList);
        ArrayList commentArray = new ArrayList<>();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("text", comment_tf.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url+"comment_course/"+shared_info.getInt("current_course_id", -1), jsonBody, response -> {
                    try {
                        JSONObject commentJSON = response.getJSONObject("new_comment");
                        Comment new_comment = new Comment(commentJSON);
                        editor.putInt("current_user_id", id);
                        editor.putString("current_user_name", name);
                        editor.apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>() {
                };
                System.out.println(headers);
                headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                System.out.println(headers);
                return headers;
            }

        };
        requestQueue.add(jsonObjectRequest);
    */





        // Inflate the layout for this fragment
        return view;
    }
}