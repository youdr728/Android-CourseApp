package com.example.course_app;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles user login and registration navigation within the app.
 */

public class LoginFragment extends Fragment {

    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Login and registration buttons
        Button loginButton = view.findViewById(R.id.loginButton);
        Button regButton = view.findViewById(R.id.loginRegButton);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);


        // Handle login button click
        loginButton.setOnClickListener(v -> {
            EditText userNameText = view.findViewById(R.id.loginUserName);
            EditText passwordText = view.findViewById(R.id.loginPassword);
            String username = userNameText.getText().toString();
            String password = passwordText.getText().toString();
            if(username.isEmpty() || password.isEmpty()){
                return;
            }

            // Setup Volley request queue for network requests
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            String url = "https://course-app-zaish-youdr.azurewebsites.net/";
            JSONObject jsonBody = null;
            try {
                jsonBody = new JSONObject("{\"username\": " + username + ", \"password\": " + password + "}");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Create request to perform login
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url+"user/login", jsonBody, response -> {
                        HomeFragment homeFragment = new HomeFragment();
                        SharedPreferences shared_info = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        try {
                            String token = response.getString("token");
                            SharedPreferences.Editor editor = shared_info.edit();
                            editor.putString("current_user_token", token);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        navController.navigate(R.id.action_loginFragment_to_homeFragment2);
                    }, error -> {
                        Toast.makeText(requireContext(), "Response error", Toast.LENGTH_SHORT).show();
                    });
            requestQueue.add(jsonObjectRequest);
            passwordText.setText("");

        });

        // Handle registration button click
        regButton.setOnClickListener(v -> {
            navController.navigate(R.id.registerFragment);
        });

        return view;
    }
}