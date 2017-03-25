package com.teammeme.dreaminmemes;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Tabs extends AppCompatActivity {
    private TabGlobal tabGlobal;
    private TabNotifications tabNotifications;
    private TabUser tabUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabGlobal = new TabGlobal();
        tabNotifications = new TabNotifications();
        tabUser = new TabUser();

        // Start with Global tab
        tabGlobal.open();
    }

    private class TabGlobal {
        void open() {

            List<Fragment> al = getSupportFragmentManager().getFragments();
            if (al != null) {
                for (Fragment frag : al) {
                    getSupportFragmentManager().beginTransaction().remove(frag).commit();
                }
            }

            setContentView(R.layout.tab_global);

            // load the active game ScrollView
            // will need to use parse to get the number of active games the user is a part of

            int numActiveGames = 5;
            // this is the scrollView's one and only child, which holds all the active games
            LinearLayout scrollLayout = (LinearLayout)findViewById(R.id.LinLayoutActiveGames);

            // pendingScrollView's child
            LinearLayout pendingScrollLayout = (LinearLayout)findViewById(R.id.LinLayoutPendingGames);

            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels20 = (int) (20*scale + 0.5f);
            int dpAsPixels15 = (int) (15*scale + 0.5f);
            int dpAsPixels70 = (int) (70*scale + 0.5f);
            int dpAsPixels50 = (int) (50*scale + 0.5f);
            int dpAsPixels1 = (int) (1*scale + 0.5f);

            for (int i = 0; i < numActiveGames; i++) {
                // create the relative layout
                RelativeLayout r = new RelativeLayout(getApplicationContext());
                r.setId(View.generateViewId());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpAsPixels70);

                createNestedLinearLayoutWithTextViews(r, "Game Name", "Directions");

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
                createDivider(r);

                // add the views
                r.addView(tv3, timeParams);

                scrollLayout.addView(r, params);

                r.setClickable(true);
                r.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                       Toast.makeText(getApplicationContext(), "Hello from " + v.getId(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            int numPendingGames = 5;

            for (int i = 0; i < numPendingGames; i++) {
                RelativeLayout r = new RelativeLayout(getApplicationContext());
                r.setId(View.generateViewId());
                LinearLayout.LayoutParams rParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        dpAsPixels50);

                // Text view with game name
                TextView gameName = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams gameNameParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                gameNameParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                gameName.setText("Game Name");
                gameName.setTextSize(20);
                gameName.setId(View.generateViewId());
                gameName.setPadding(dpAsPixels20, 0, 0, 0);

                // Text view with players pending
                TextView pendingPlayers = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams pendingPlayersParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                pendingPlayersParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                pendingPlayersParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                pendingPlayers.setText("2 Pending Players");
                pendingPlayers.setPadding(0, 0, dpAsPixels20, 0);
                pendingPlayers.setTextSize(15);


                r.addView(gameName, gameNameParams);
                r.addView(pendingPlayers, pendingPlayersParams);

                pendingScrollLayout.addView(r, rParams);

                r.setClickable(true);
                r.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                        Toast.makeText(getApplicationContext(), "Hello from " + v.getId(), Toast.LENGTH_SHORT).show();
                    }

                });
            }
        }

        // Populate active and pending games by querying user's lobbies
        void populateGames() {
            final ParseUser user = ParseUser.getCurrentUser();
            assert user != null : "TabGlobal::populateGames error: user doesn't exist.";
            List<String> lobbies = user.getList("lobbies");

            // Set up views
            LinearLayout pendingLL = (LinearLayout) findViewById(R.id.LinLayoutPendingGames);
            LinearLayout activeLL = (LinearLayout) findViewById(R.id.LinLayoutActiveGames);

            // Query for all of user's lobbies
            for (String lobbyId : lobbies) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Lobby");
                query.getInBackground(lobbyId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            // PENDING

                            if (object.getInt("state") == Lobby.State.GameInit.ordinal()) {
                                // GENERATE PENDING BOX
                                Log.d("*****populateGames", "User is in pending lobby: " + object.getObjectId());
                            }
                            // ACTIVE
                            else {
                                // GENERATE ACTIVE BOX


                                List<String> temp = object.getList("judgeQueue");
                                LinkedList<String> judgeQueue = new LinkedList<String>(temp);
                                if (judgeQueue.peekFirst() == user.getObjectId()) {
                                    // JUDGE
                                    Log.d("*****populateGames", "User is judge of " + object.getObjectId());
                                } else {
                                    // NOT JUDGE
                                    Log.d("*****populateGames", "User is not judge of " + object.getObjectId());
                                }
                            }
                        } else {
                            // ERROR
                            Log.d("*****populateGames", "Error: could not pull up user's lobby: " + object.getObjectId());
                        }
                    }
                });
            }
        }

        // Store blank lobby (containing owner only) to DB and switch activity
        void openNewLobby(String lobbyId) {
            ArrayList<ParseObject> players = new ArrayList<>();
            LinkedList<String> judgeQueue = new LinkedList<>();
            ParseObject ownerInfo = ParseObject.create("LobbyUserInfo");
            ownerInfo.put("userId", ParseUser.getCurrentUser().getObjectId());
            ownerInfo.put("score", 0);
            players.add(ownerInfo);
            judgeQueue.add(lobbyId);

            // Write to DB
            final ParseObject dataObject = ParseObject.create("Lobby");
            dataObject.put("name", "blank");
            dataObject.put("players", players);
            dataObject.put("judgeQueue", judgeQueue);
            dataObject.put("roundNum", 1);
            dataObject.put("state", Lobby.State.GameInit.ordinal());

            dataObject.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Successful save to DB
                        // Add lobby to user's lobbies
                        ParseUser user = ParseUser.getCurrentUser();
                        user.add("lobbies", dataObject.getObjectId());
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Intent i = new Intent(getApplicationContext(), Lobby.class);
                                i.putExtra("lobbyId", dataObject.getObjectId());
                                startActivity(i);
                            }
                        });
                    } else {
                        // Failure
                        Log.d("*****TabGlobal", "Error saving lobby: " + e.getMessage());
                    }
                }
            });
        }

        // Switch activity and load specified lobby
        void openExistingLobby(String lobbyId) {
            Intent i = new Intent(getApplicationContext(), Lobby.class);
            i.putExtra("lobbyId", lobbyId);
            startActivity(i);
        }
    }

    public void goToNotifications(View v){
        tabNotifications.open();
    }

    public void goToGlobal(View v) {
        tabGlobal.open();
    }

    public void goToUser(View v) {
        tabUser.open();
    }

    private class TabNotifications {
        void open() {
            List<Fragment> al = getSupportFragmentManager().getFragments();
            if (al != null) {
                for (Fragment frag : al) {
                    getSupportFragmentManager().beginTransaction().remove(frag).commit();
                }
            }

            setContentView(R.layout.tab_notifications);

            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels20 = (int) (20*scale + 0.5f);
            int dpAsPixels15 = (int) (15*scale + 0.5f);
            int dpAsPixels70 = (int) (70*scale + 0.5f);
            int dpAsPixels50 = (int) (50*scale + 0.5f);
            int dpAsPixels1 = (int) (1*scale + 0.5f);

            int numGameRequests = 5;

            LinearLayout gameRequestsLayout = (LinearLayout)findViewById(R.id.LinLayoutGameRequests);



            for (int i = 0; i < numGameRequests; i++) {
                // create the relative layout
                RelativeLayout r = new RelativeLayout(getApplicationContext());
                r.setId(View.generateViewId());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        dpAsPixels70);

                createNestedLinearLayoutWithTextViews(r, "Game Name", "Username Invited You");

                createAcceptDeclineButtons(r);

                createDivider(r);

                gameRequestsLayout.addView(r, params);


            }





        }
    }

    private class TabUser {
        void open() {
            setContentView(R.layout.tab_user);
        }
    }

    public void logOut(View v){
        ParseUser user = ParseUser.getCurrentUser();
        user.logOut();
        finish();
    }

    // Attached to new lobby button
    public void createNewLobby(View view) {
        ParseUser user = ParseUser.getCurrentUser();
        tabGlobal.openNewLobby(user.getObjectId());
    }

    private void createNestedLinearLayoutWithTextViews(RelativeLayout r, String gameName, String belowMessage) {

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels20 = (int) (20*scale + 0.5f);

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
        tv1.setText(gameName);
        tv1.setPadding(dpAsPixels20, 0, 0, 0);
        tv1.setTextSize(20);
        tv1.setId(View.generateViewId());

        // create the Directions text view
        TextView tv2 = new TextView(getApplicationContext());
        LinearLayout.LayoutParams directionParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        tv2.setText(belowMessage);
        tv2.setPadding(dpAsPixels20, 0, 0, 0);
        tv2.setTextSize(15);
        tv2.setTextColor(Color.parseColor("#000000"));
        tv2.setId(View.generateViewId());

        // add
        l.addView(tv1, gameNameParams);
        l.addView(tv2, directionParams);

        r.addView(l, rParams);

    }

    private void createDivider(RelativeLayout r) {
        // create the divider
        View divider = new View(getApplicationContext());
        RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                1);
        dividerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        divider.setBackgroundColor(Color.parseColor("#000000"));
        divider.setId(View.generateViewId());
        r.addView(divider, dividerParams);
    }

    private void createAcceptDeclineButtons(RelativeLayout r) {
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels20 = (int) (20*scale + 0.5f);
        int dpAsPixels70 = (int) (70*scale + 0.5f);
        int dpAsPixels50 = (int) (50*scale + 0.5f);

        LinearLayout l = new LinearLayout(getApplicationContext());
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        l.setPadding(0, 0, dpAsPixels20, 0);
        l.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton acceptButton = new ImageButton(getApplicationContext());
        LinearLayout.LayoutParams acceptParams = new LinearLayout.LayoutParams(dpAsPixels50,
                dpAsPixels50);

        ImageButton rejectButton = new ImageButton(getApplicationContext());
        LinearLayout.LayoutParams rejectParams = new LinearLayout.LayoutParams(dpAsPixels50,
                dpAsPixels50);

        // set on click listeners for buttons

        l.addView(acceptButton, acceptParams);
        l.addView(rejectButton, rejectParams);

        r.addView(l, rParams);



    }
}