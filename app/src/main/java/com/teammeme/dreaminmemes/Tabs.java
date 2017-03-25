package com.teammeme.dreaminmemes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Tabs extends AppCompatActivity {
    private TabGlobal tab_global;
    private TabNotifications tab_notifications;
    private TabUser tab_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tab_global = new TabGlobal();
        tab_notifications = new TabNotifications();
        tab_user = new TabUser();

        // Start with Global tab
        tab_global.open();
    }

    private class TabGlobal {
        void open() {
            setContentView(R.layout.tab_global);
        }
    }

    private class TabNotifications {
        void open() {
            setContentView(R.layout.tab_notifications);
        }
    }

    private class TabUser {
        void open() {
            setContentView(R.layout.tab_user);
        }
    }
}