package com.example.textviewdebug;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.textviewdebug.TextLayout.TextLayoutActivity;
import com.example.textviewdebug.TextLayout.TextViewTestActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Process;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.textviewdebug.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private TextView tv_share;
    private Switch swc_isShare;
    private Button btn_font;
    private ImageButton btn_textViewTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //binding.getRoot().setPadding(0,0,0, 100);
        tv_share = findViewById(R.id.textView_Share);
        swc_isShare = findViewById(R.id.switch_isShare);
        swc_isShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swc_isShare.isChecked()) {
                    tv_share.setText("系统分享");
                } else {
                    tv_share.setText("打开方式");
                }
            }
        });
        //
        getWindow().getDecorView().getRootView().setPadding(0,0,0,200);
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // test share
                boolean isShare = false;
                if (swc_isShare.isChecked()) {
                    Intent textIntent = new Intent(Intent.ACTION_SEND);
                    textIntent.setType("text/plain"); // image/jpeg // text/plain
                    textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
                    startActivity(Intent.createChooser(textIntent, "我的分享"));
                    //startActivity(textIntent);
                } else { // test open-style(system:ui)
                    Intent textIntent = new Intent(Intent.ACTION_SEND);
                    textIntent.setType("image/jpeg"); // image/jpeg // text/plain
                    textIntent.putExtra(Intent.EXTRA_TITLE, "打开方式");
                    textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
                    //startActivity(Intent.createChooser(textIntent, "我的分享"));
                    startActivity(textIntent);
                }

                //SymlinkUtils.syncFontForWebView(getApplicationContext());
                //Intent startInfo = new Intent("myCallee");
                //startActivity(startInfo);
            }
        });
        //
        Log.i("", System.currentTimeMillis() + "ms");
        ///////////////////////////////////////////////////////////
        //                   TextLayout                          //
        ///////////////////////////////////////////////////////////
        btn_font = findViewById(R.id.button_font);
        btn_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TextLayoutActivity.class));
            }
        });
        //image button a-z Test TextView standard behavior
        btn_textViewTest = findViewById(R.id.imageButton_az);
        btn_textViewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TextViewTestActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}