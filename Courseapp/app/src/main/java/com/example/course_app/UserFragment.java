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
 * Manages user profile displaying user's comments and liked courses, and allows to follow or unfollow the user.
 */
public class UserFragment extends Fragment {

    String url = "https://course-app-zaish-youdr.azurewebsites.net/";
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapter2;


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
        ListView liked_courses_lv = view.findViewById(R.id.likesList);
        ListView comments_lv = view.findViewById(R.id.commentList);

        SharedPreferences shared_info = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);

        ArrayList commentArray = new ArrayList<String>();
        ArrayList JsoncommentArray = new ArrayList<>();

        ArrayList likesArray = new ArrayList<String>();

        adapter1 = new ArrayAdapter<String>(getActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                commentArray);
        adapter2 = new ArrayAdapter<String>(getActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                likesArray);


        username.setText(shared_info.getString("current_user_name", null));


        // Follow button functionality
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

        // Unfollow button functionality
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

        // Return button functionality
        returnButton.setOnClickListener(view1 -> {
            navController.popBackStack();
        });


        // Load user comments
        JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest
                (Request.Method.GET, url + "show_users_comments/" + shared_info.getString("current_user_name", null), null, response -> {
                    try {
                        JSONArray comments = response.getJSONArray("comments");

                        for (int j = 0; j < comments.length(); j++) {
                            JSONObject obj = comments.getJSONObject(j);
                            Comment new_comment = new Comment(obj);
                            JsoncommentArray.add(new_comment);
                            commentArray.add(new_comment.getText());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    comments_lv.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                }, error -> Toast.makeText(getContext(), "Couldn't load comments", Toast.LENGTH_SHORT).show()) {
        };
        requestQueue.add(jsonObjectRequest3);

        // Load user liked courses
        JsonObjectRequest jsonObjectRequest4 = new JsonObjectRequest
                (Request.Method.GET, url + "show_users_likes/" + shared_info.getString("current_user_name", null), null, response -> {

                    try {
                        JSONArray likes = response.getJSONArray("courses");
                        System.out.println("likes json: " + likes);

                        for (int j = 0; j < likes.length(); j++) {
                            JSONObject obj = likes.getJSONObject(j);
                            likesArray.add(obj.getString("course_name"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    liked_courses_lv.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();

                }, error -> Toast.makeText(getContext(), "Couldn't load likes", Toast.LENGTH_SHORT).show()) {
        };
        requestQueue.add(jsonObjectRequest4);




        return view;
    }



}