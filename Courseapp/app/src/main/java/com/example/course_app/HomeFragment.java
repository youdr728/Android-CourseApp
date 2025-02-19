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
 * Manages the home interface of the app, displaying lists of courses and followed users.
 */

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup list views and headers for courses and followed users
        ListView courseList = view.findViewById(R.id.CourseList);
        ListView followedList = view.findViewById(R.id.commentList);
        TextView coursesHeader = view.findViewById(R.id.coursetext);
        coursesHeader.setText(R.string.courses);
        TextView followedHeader = view.findViewById(R.id.followedtext);
        followedHeader.setText(R.string.followed);

        Button logout = view.findViewById(R.id.logout);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);

        // Volley request queue for API calls
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        SharedPreferences shared_info = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_info.edit();

        // Fetch logged-in user's username
        JsonObjectRequest jsonObjectRequestuser = new JsonObjectRequest
                (Request.Method.GET, url+"get_user", null, response -> {
                    try {
                        JSONObject user = response.getJSONObject("user");

                        editor.putString("current_logged_user", user.getString("username"));
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
        requestQueue.add(jsonObjectRequestuser);

        // Setup course list with adapter
        ArrayList courses_names = new ArrayList<>();
        ArrayList courses = new ArrayList<>();
        ArrayAdapter<String> adapter1;
        adapter1 = new ArrayAdapter<String>(getActivity(),
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
                    courseList.setAdapter(adapter1);
                }, error -> Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show()) {
        };
        requestQueue.add(jsonObjectRequest2);

        // Enable interaction with course list
        courseList.setOnItemClickListener((adapterView, view1, i, l) -> {
            Course current_course = (Course) courses.get(i);
            // save course info
            editor.putString("current_course_id", current_course.getId());
            editor.putString("current_course_name", current_course.getCourseName());
            editor.putString("current_course_info", current_course.getCourseInfo());
            editor.apply();
            navController.navigate(R.id.action_homeFragment2_to_courseFragment);
        });

        // Logout functionality
        logout.setOnClickListener(view1 -> {
            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest
                    (Request.Method.POST, url + "user/logout", null, response -> {
                        navController.navigate(R.id.action_homeFragment2_to_loginFragment);
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

        ArrayList followedArray = new ArrayList<String>();
        ArrayAdapter<String> adapter2;
        // adapter for followed users list
        adapter2 = new ArrayAdapter<String>(getActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                followedArray);
        JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest
                (Request.Method.GET, url+"show_followed_users", null, response -> {
                    try {
                        JSONArray followed_users = response.getJSONArray("followed");

                        for (int i = 0 ; i < followed_users.length(); i++) {
                            followedArray.add(followed_users.getString(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    followedList.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();
                }, error -> Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + shared_info.getString("current_user_token", null));
                return headers;
            }

        };
        requestQueue.add(jsonObjectRequest3);

        // Enable interaction with followed list
        followedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String username = followedList.getItemAtPosition(position).toString();
                editor.putString("current_user_name", username);
                editor.apply();
                UserFragment userFragment = new UserFragment();
                navController.navigate(R.id.action_homeFragment2_to_userFragment2);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
