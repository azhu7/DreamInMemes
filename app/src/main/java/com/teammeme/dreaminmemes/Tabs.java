package com.teammeme.dreaminmemes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.parse.Parse;

public class Tabs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_global);
    }

    // Buttons can use these functions to switch layouts (tabs)
    public void open_tab_global(View v) {
        setContentView(R.layout.tab_create_game);
    }
    public void open_tab_notifications(View v) {
        setContentView(R.layout.tab_global);
    }
    public void open_tab_user(View v) {
        setContentView(R.layout.tab_global);
    }
}