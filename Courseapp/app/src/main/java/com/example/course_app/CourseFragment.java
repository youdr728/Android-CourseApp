package com.example.course_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

/**
 * Handles the user interface and functionality for course details.
 */

public class CourseFragment extends Fragment {

    String url = "https://course-app-zaish-youdr.azurewebsites.net/";
    ArrayAdapter<String> adapter;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        // Setup TextView for course information
        TextView courseInfo = view.findViewById(R.id.course_info);
        SharedPreferences shared_info = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_info.edit();
        courseInfo.setText(shared_info.getString("current_course_info", null));

        // Volley request queue for API calls
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Setup ListView and adapter for comments
        ListView commentList = view.findViewById(R.id.commentList);
        ArrayList JsoncommentArray = new ArrayList<>();
        ArrayList commentArray = new ArrayList<String>();

        // Buttons for interacting with the course
        Button post_comment = view.findViewById(R.id.post_comment);
        Button like_course = view.findViewById(R.id.likeCourse);
        Button unlike_course = view.findViewById(R.id.unlikeCourse);

        Button return_button = view.findViewById(R.id.returnButton);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);

        adapter = new ArrayAdapter<String>(getActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                commentArray);

        // Fetch comments for the course
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url+"comments/" + shared_info.getString("current_course_id", null), null, response -> {
                    try {
                        JSONArray comments = response.getJSONArray("courses");

                        for (int i = 0 ; i < comments.length(); i++) {
                            JSONObject obj = comments.getJSONObject(i);
                            Comment new_comment = new Comment(obj);
                            JsoncommentArray.add(new_comment);
                            commentArray.add(new_comment.getText());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    commentList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }, error -> Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show()) {

        };
        requestQueue.add(jsonObjectRequest);

        // Add a new comment
        post_comment.setOnClickListener(view1 -> {
            EditText comment_tf = view.findViewById(R.id.addComment);
            String comment = comment_tf.getText().toString();
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("text", comment);
                System.out.println(jsonBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest
                    (Request.Method.POST, url+"comment_course/" + shared_info.getString("current_course_id", null), jsonBody, response -> {
                        try {
                            JSONObject commentJSON = response.getJSONObject("new_comment");
                            Comment new_comment = new Comment(commentJSON);
                            JsoncommentArray.add(new_comment);
                            commentArray.add(new_comment.getText());

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
            requestQueue.add(jsonObjectRequest2);
            comment_tf.setText("");

            int id = navController.getCurrentDestination().getId();
            navController.popBackStack(id, true);
            navController.navigate(id);

        });

        // Like a course
        like_course.setOnClickListener(view1 -> {
            String course_name = shared_info.getString("current_course_name", null);

            JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest
                    (Request.Method.POST, url+"like_course/" + course_name, null, response -> {
                        Toast.makeText(requireContext(), "Course Liked!", Toast.LENGTH_SHORT).show();
                    }, error -> Toast.makeText(getContext(), "Already liked!", Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>() {
                    };
                    headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest3);
        });

        unlike_course.setOnClickListener(view1 -> {
            String course_name = shared_info.getString("current_course_name", null);

            JsonObjectRequest jsonObjectRequest4 = new JsonObjectRequest
                    (Request.Method.POST, url+"unlike_course/" + course_name, null, response -> {
                        Toast.makeText(requireContext(), "Course Unliked!", Toast.LENGTH_SHORT).show();
                    }, error -> Toast.makeText(getContext(), "You have to like the course first to perform this action", Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>() {
                    };
                    headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest4);
        });

        return_button.setOnClickListener(view1 -> {
            navController.popBackStack();
        });
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Comment target_comment = (Comment) JsoncommentArray.get(position);
                String username = target_comment.getUsername();
                editor.putString("current_user_name", username);
                editor.apply();
                navController.navigate(R.id.action_courseFragment_to_userFragment2);
            }
        });
        return view;
    }
}