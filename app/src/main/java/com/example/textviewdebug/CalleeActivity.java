package com.example.textviewdebug;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CalleeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callee);
        postponeEnterTransition();
    }
}