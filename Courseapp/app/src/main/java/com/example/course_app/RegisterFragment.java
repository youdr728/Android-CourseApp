package com.example.course_app;

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
 * Manages user registration and navigation to the login screen.
 */

public class RegisterFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Registration and login navigation buttons
        Button regButton = view.findViewById(R.id.registerButton);
        Button regLoginButton = view.findViewById(R.id.regloginButton);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);


        // Handle registration button click
        regButton.setOnClickListener(v -> {
            EditText usernametext = view.findViewById(R.id.regUserName);
            EditText passwordtext = view.findViewById(R.id.addComment);
            String username = usernametext.getText().toString();
            String password = passwordtext.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                return;
            }

            // Setup Volley request queue for network requests
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            String url = "https://course-app-zaish-youdr.azurewebsites.net/";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", username);
                jsonBody.put("password", password);
                System.out.println(jsonBody + "..." + url+"register" + "... new body:" + jsonBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Create request to perform registration
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url+"register", jsonBody, response -> {
                        Toast.makeText(requireContext(), "You Registerd!", Toast.LENGTH_SHORT).show();
                        navController.popBackStack();
                    }, error -> Toast.makeText(requireContext(), "Response error", Toast.LENGTH_SHORT).show());

            requestQueue.add(jsonObjectRequest);
            passwordtext.setText("");

        });

        // Handle navigation to login fragment
        regLoginButton.setOnClickListener(v -> {
            navController.popBackStack();
        });

        // Inflate the layout for this fragment
        return view;
    }
}