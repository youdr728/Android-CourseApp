package com.example.course_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class HomeFragment extends Fragment {

    String url = "https://course-app-zaish-youdr.azurewebsites.net/";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ListView courseList = view.findViewById(R.id.CourseList);
        ListView followedList = view.findViewById(R.id.commentsList);
        TextView coursesHeader = view.findViewById(R.id.coursetext);
        coursesHeader.setText(R.string.courses);
        TextView followedHeader = view.findViewById(R.id.followedtext);
        followedHeader.setText(R.string.followed);

        Button logout = view.findViewById(R.id.logout);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        SharedPreferences shared_info = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_info.edit();

        String currentFragment = "HomeFragment";
        editor.putString("current_fragment", currentFragment);
        editor.apply();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url+"get_user", null, response -> {
                    try {
                        String name = response.getString("username");
                        int id = response.getInt("id");
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
                headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                return headers;
            }

        };
        requestQueue.add(jsonObjectRequest);


        ArrayList courses_names = new ArrayList<>();
        ArrayList courses = new ArrayList<>();
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                courses_names);

        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest
                (Request.Method.GET, url+"courses", null, response -> {
                    try {
                        JSONArray coursesJson = response.getJSONArray("courses");
                        for (int i = 0; i < coursesJson.length(); i++) {
                            JSONObject currentCourse = coursesJson.getJSONObject(i);
                            Course course = new Course(currentCourse);
                            courses.add(course);
                            courses_names.add(course.getCourseName());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    courseList.setAdapter(adapter);
                }, error -> Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show()) {
        };
        requestQueue.add(jsonObjectRequest2);


        courseList.setOnItemClickListener((adapterView, view1, i, l) -> {
            Course current_course = (Course) courses.get(i);
            editor.putString("current_course_id", current_course.getId());
            editor.putString("current_course_name", current_course.getCourseName());
            editor.putString("current_course_info", current_course.getCourseInfo());
            editor.apply();
            CourseFragment courseFragment = new CourseFragment();
            FragmentTransaction manager = requireActivity().getSupportFragmentManager().beginTransaction();
            manager.replace(R.id.mainlayout, courseFragment).commit();
        });

        logout.setOnClickListener(view1 -> {
            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest
                    (Request.Method.POST, url + "user/logout", null, response -> {
                        FragmentTransaction manager = requireActivity().getSupportFragmentManager().beginTransaction();
                        manager.replace(R.id.mainlayout, new LoginFragment()).commit();
                    }, error -> Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest1);

        });

        // Inflate the layout for this fragment
        return view;
    }
}
