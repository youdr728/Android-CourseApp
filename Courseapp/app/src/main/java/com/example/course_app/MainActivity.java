package com.example.course_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Main activity for the course application, serves as the entry point.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Initializes the activity and sets the initial fragment.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.mainlayout, LoginFragment.class, null)
                    .commit();
        }

}
}