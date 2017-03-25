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

    public void go_to_create_game(View v) {
        setContentView(R.layout.tab_create_game);

    }


}
