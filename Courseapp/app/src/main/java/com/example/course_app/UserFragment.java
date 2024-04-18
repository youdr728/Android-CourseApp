package com.example.course_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFragment extends Fragment {

    String url = "https://course-app-zaish-youdr.azurewebsites.net/";

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        TextView username = view.findViewById(R.id.userText);
        Button follow = view.findViewById(R.id.followButton);
        Button unfollow = view.findViewById(R.id.unfollowButton);
        Button returnButton = view.findViewById(R.id.returnButtonfromuser);
        ListView liked_courses = view.findViewById(R.id.likesList);
        ListView comments = view.findViewById(R.id.commentsList);
        SharedPreferences shared_info = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        username.setText(shared_info.getString("current_user_name", null));

        follow.setOnClickListener(view1 -> {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url+"follow_user/" + shared_info.getString("current_user_name", null), null, response -> {
                        Toast.makeText(requireContext(), "Followed User: " + shared_info.getString("current_user_name", null), Toast.LENGTH_SHORT).show();
                    }, error -> Toast.makeText(getContext(), "Allready Following!", Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>() {
                    };
                    headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                    return headers;
                }

            };
            requestQueue.add(jsonObjectRequest);
        });

        unfollow.setOnClickListener(view1 -> {
            JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest
                    (Request.Method.POST, url+"unfollow_user/" + shared_info.getString("current_user_name", null), null, response -> {
                        Toast.makeText(requireContext(), "Unfollowed User: " + shared_info.getString("current_user_name", null), Toast.LENGTH_SHORT).show();
                    }, error -> Toast.makeText(getContext(), "You must follow the user first", Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>() {
                    };
                    headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                    return headers;
                }

            };
            requestQueue.add(jsonObjectRequest2);
        });


        return view;
    }
}