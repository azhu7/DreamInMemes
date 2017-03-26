package com.teammeme.dreaminmemes;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

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
        Log.d("*****onCreate()", "opening Global");
        tabGlobal.open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("******onResume()", "opening Global");
        tabGlobal.open();
    }

    private class TabGlobal {
        void open() {
            setContentView(R.layout.tab_global);
            populateGames();
        }

        // Populate active and pending games by querying user's lobbies
        void populateGames() {
            final ParseUser user = ParseUser.getCurrentUser();
            if (user == null) {
                Toast.makeText(getApplicationContext(), "populateGames null user pointer??", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> lobbies = user.getList("lobbies");

            // Set up views
            final LinearLayout pendingLL = (LinearLayout) findViewById(R.id.LinLayoutPendingGames);
            final LinearLayout activeLL = (LinearLayout) findViewById(R.id.LinLayoutActiveGames);

            pendingLL.removeAllViews();
            activeLL.removeAllViews();

            if (lobbies == null)
                return;

            // Query for all of user's lobbies
            for (String lobbyId : lobbies) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Lobby");
                query.getInBackground(lobbyId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            // PENDING

                            if (object.getInt("state") == Lobby.State.GameInit.ordinal()) {
                                createPendingRelativeLayout(pendingLL, "Game Name", 3, 24);


                                Log.d("*****populateGames", "User is in pending lobby: " + object.getObjectId());
                            }
                            // ACTIVE
                            else {
                                // GENERATE ACTIVE BOX
                                createActiveRelativeLayout(activeLL, "Game Name", "Directions", 24);

                                List<String> temp = object.getList("judgeQueue");
                                LinkedList<String> judgeQueue = new LinkedList<String>(temp);
                                if (judgeQueue.peekFirst().equals(user.getObjectId())) {
                                    // JUDGE
                                    Log.d("*****populateGames", "User is judge of " + object.getObjectId());
                                } else {
                                    // NOT JUDGE
                                    Log.d("*****populateGames", "User is not judge of " + object.getObjectId());
                                }
                            }
                        } else if (object == null) {
                            return;  // Half error
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
                                Log.d("*****Tabs", "Starting a new lobby from Tabs.openNewLobby");
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

        LinearLayout LL = new LinearLayout(getApplicationContext());
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        LL.setOrientation(LinearLayout.VERTICAL);
        LL.setId(View.generateViewId());

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
        LL.addView(tv1, gameNameParams);
        LL.addView(tv2, directionParams);

        r.addView(LL, rParams);

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

        LinearLayout LL = new LinearLayout(getApplicationContext());
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        LL.setPadding(0, 0, dpAsPixels20, 0);
        LL.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton acceptButton = new ImageButton(getApplicationContext());
        LinearLayout.LayoutParams acceptParams = new LinearLayout.LayoutParams(dpAsPixels50,
                dpAsPixels50);

        ImageButton rejectButton = new ImageButton(getApplicationContext());
        LinearLayout.LayoutParams rejectParams = new LinearLayout.LayoutParams(dpAsPixels50,
                dpAsPixels50);

        // set onClick listeners for buttons
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove
                // Add to lobby
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove
            }
        });

        LL.addView(acceptButton, acceptParams);
        LL.addView(rejectButton, rejectParams);

        r.addView(LL, rParams);
    }

    private void createActiveRelativeLayout(LinearLayout l, String gameName, String directions, int timeLeft) {
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels20 = (int) (20*scale + 0.5f);
        int dpAsPixels15 = (int) (15*scale + 0.5f);
        int dpAsPixels70 = (int) (70*scale + 0.5f);
        int dpAsPixels50 = (int) (50*scale + 0.5f);
        int dpAsPixels1 = (int) (1*scale + 0.5f);

        // create the relative layout
        RelativeLayout r = new RelativeLayout(getApplicationContext());
        r.setId(View.generateViewId());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpAsPixels70);

        createNestedLinearLayoutWithTextViews(r, gameName, directions);

        // create the Directions text view
        TextView tv3 = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams timeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        timeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        timeParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        tv3.setText(timeLeft + " Hours Left");
        tv3.setPadding(0,0, dpAsPixels20, 0);
        tv3.setTextSize(15);
        tv3.setId(View.generateViewId());

        // create the divider
        createDivider(r);

        // add the views
        r.addView(tv3, timeParams);
        r.setClickable(true);
        r.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "Hello from " + v.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        l.addView(r, params);
    }

    private void createPendingRelativeLayout(LinearLayout LL, String gameName, int playersPending, int timeLeft) {

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels20 = (int) (20*scale + 0.5f);
        int dpAsPixels15 = (int) (15*scale + 0.5f);
        int dpAsPixels70 = (int) (70*scale + 0.5f);
        int dpAsPixels50 = (int) (50*scale + 0.5f);
        int dpAsPixels1 = (int) (1*scale + 0.5f);

        RelativeLayout r = new RelativeLayout(getApplicationContext());
        r.setId(View.generateViewId());
        LinearLayout.LayoutParams rParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                dpAsPixels50);

        // Text view with game name
        TextView gameNameText = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams gameNameParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        gameNameParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        gameNameText.setText(gameName);
        gameNameText.setTextSize(20);
        gameNameText.setId(View.generateViewId());
        gameNameText.setPadding(dpAsPixels20, 0, 0, 0);

        // Text view with players pending
        TextView pendingPlayers = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams pendingPlayersParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        pendingPlayersParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        pendingPlayersParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        pendingPlayers.setText(playersPending + " Pending Players");
        pendingPlayers.setPadding(0, 0, dpAsPixels20, 0);
        pendingPlayers.setTextSize(15);


        r.addView(gameNameText, gameNameParams);
        r.addView(pendingPlayers, pendingPlayersParams);

        r.setClickable(true);
        r.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "Hello from " + v.getId(), Toast.LENGTH_SHORT).show();
            }

        });

        LL.addView(r, rParams);
    }
}