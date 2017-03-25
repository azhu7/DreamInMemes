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

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("qBpMYLk5wOBosYWXVpAuedILE5JE3OsIWPUri4C5")
                .clientKey("9bySWn09g6GH6q4wBhGLHwjIC5CTcdrn7a2S9QrI")
                .server("https://parseapi.back4app.com/").build()
        );
    }

    public void go_to_create_game(View v) {
        setContentView(R.layout.tab_create_game);

    }


}
