package com.example.course_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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


public class RegisterFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button regButton = view.findViewById(R.id.registerButton);
        Button regLoginButton = view.findViewById(R.id.regloginButton);

        regButton.setOnClickListener(v -> {
            EditText usernametext = view.findViewById(R.id.regUserName);
            EditText passwordtext = view.findViewById(R.id.addComment);
            String username = usernametext.getText().toString();
            String password = passwordtext.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                return;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            String url = "http://10.244.35.120:5000/";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", username);
                jsonBody.put("password", password);
                System.out.println(jsonBody + "..." + url+"register" + "... new body:" + jsonBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url+"register", jsonBody, response -> {
                        Toast.makeText(requireContext(), "You Registerd!", Toast.LENGTH_SHORT).show();
                        FragmentTransaction manager = requireActivity().getSupportFragmentManager().beginTransaction();
                        manager.replace(R.id.mainlayout, new LoginFragment()).commit();
                    }, error -> Toast.makeText(requireContext(), "Response error", Toast.LENGTH_SHORT).show());

            requestQueue.add(jsonObjectRequest);
            passwordtext.setText("");

        });

        regLoginButton.setOnClickListener(v -> {
            LoginFragment inloggFragment = new LoginFragment();
            FragmentTransaction manager = requireActivity().getSupportFragmentManager().beginTransaction();
            manager.replace(R.id.mainlayout, inloggFragment).commit();
        });

        // Inflate the layout for this fragment
        return view;
    }
}