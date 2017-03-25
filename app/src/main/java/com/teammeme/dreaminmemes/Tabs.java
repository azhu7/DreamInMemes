package com.teammeme.dreaminmemes;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import org.w3c.dom.Text;

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

            // load the active game ScrollView
            // will need to use parse to get the number of active games the user is a part of

            int numActiveGames = 2;
            // this is the scrollView's one and only child, which holds all the active games
            LinearLayout scrollLayout = (LinearLayout)findViewById(R.id.LinLayoutActiveGames);

            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels20 = (int) (20*scale + 0.5f);
            int dpAsPixels15 = (int) (15*scale + 0.5f);
            int dpAsPixels70 = (int) (70*scale + 0.5f);
            int dpAsPixels1 = (int) (1*scale + 0.5f);

            for (int i = 0; i < numActiveGames; i++) {




                // create the relative layout
                RelativeLayout r = new RelativeLayout(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpAsPixels70);

                // create the nested linear layout -- will contain two text views
                LinearLayout l = new LinearLayout(getApplicationContext());
                RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                rParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                l.setOrientation(LinearLayout.VERTICAL);
                l.setId(View.generateViewId());

                // create the Game name text view
                TextView tv1 = new TextView(getApplicationContext());
                LinearLayout.LayoutParams gameNameParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                tv1.setText("Game Name");
                tv1.setPadding(dpAsPixels20, 0, 0, 0);
                tv1.setTextSize(20);
                tv1.setId(View.generateViewId());

                // create the Directions text view
                TextView tv2 = new TextView(getApplicationContext());
                LinearLayout.LayoutParams directionParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                tv2.setText("Directions");
                tv2.setPadding(dpAsPixels20, 0, 0, 0);
                tv2.setTextSize(15);
                tv2.setTextColor(Color.parseColor("#000000"));
                tv2.setId(View.generateViewId());

                // add
                l.addView(tv1, gameNameParams);
                l.addView(tv2, directionParams);


                // create the Directions text view
                TextView tv3 = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams timeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                timeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                timeParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                tv3.setText("24 Hours Left");
                tv3.setPadding(0,0, dpAsPixels20, 0);
                tv3.setTextSize(15);
                tv3.setId(View.generateViewId());


                // create the divider
                View divider = new View(getApplicationContext());
                RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        1);
                dividerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                divider.setBackgroundColor(Color.parseColor("#000000"));
                divider.setId(View.generateViewId());

                // add the linearLayout
                r.addView(l, rParams);
                r.addView(tv3, timeParams);
                r.addView(divider, dividerParams);

                scrollLayout.addView(r, params);

            }





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

    public void createNewLobby(View view) {

        Intent i = new Intent(getApplicationContext(), Lobby.class);
        i.putExtra("ID", 42);
        startActivity(i);


    }
}