package com.example.course_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseFragment extends Fragment {

    String url = "https://course-app-zaish-youdr.azurewebsites.net/";

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        EditText comment_tf = view.findViewById(R.id.addComment);
        ListView commentList = view.findViewById(R.id.commentsList);
        ArrayList commentArray = new ArrayList<>();


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("text", comment_tf.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JSONArray commentsJson = jsonBody.getJSONArray("comments");


        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                commentArray);

        //@Override
        //public Map<String, String> getHeaders() {
            //Map<String, String> headers = new HashMap<String, String>();
            //};
            //System.out.println(headers);
            //headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
          // System.out.println(headers);
           // return headers;
        //}

        comment_tf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean done = false;
                if (i == comment_tf.callOnClick()) {
                    sendMessage();
                    handled = true;
                }
                return done;
            }
        });
        comment_tf.setOnClickListener();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url+"comment_course/1"/*shared_info.getString("current_course_id", null)*/, jsonBody, response -> {
                    try {
                        JSONObject commentJSON = response.getJSONObject("new_comment");
                        Comment new_comment = new Comment(commentJSON);
                        commentArray.add(new_comment);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    commentList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }, error -> Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>() {
                };
                headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                return headers;
            }

        };

        requestQueue.add(jsonObjectRequest);
        return view;
    }
}