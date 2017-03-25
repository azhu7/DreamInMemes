package com.teammeme.dreaminmemes;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// TODO: Game_init
// TODO: Game

// Lobby facilitates game initialization (inviting players) and the actual game play.
public class Lobby extends AppCompatActivity {
    String name;
    ArrayList<ParseObject> players;  // Player IDs
    LinkedList<String> judgeQueue;  // Maintains order of judges
    int roundNum;
    boolean isJudge;
    State state;

    public enum State {
        GameInit, ChoosePicture, Captioning, ChooseWinner, ShowScores
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Select which layout to open
        String lobbyId = getIntent().getStringExtra("lobbyId");
        loadLobby(lobbyId);
        //boolean isJudge = false;
        //run();
    }

    // Load layout based on current state
    private void loadLayout() {
        setContentView(R.layout.game_init);  // Temporary
        /*if (isJudge) {
            switch (state) {
                case GameInit:
                    setContentView(R.layout.game_init_judge);
                case ChoosePicture:
                    setContentView(R.layout.choose_picture_judge);
                case Captioning:
                    setContentView(R.layout.captioning_judge);
                case ChooseWinner:
                    setContentView(R.layout.choose_winner_judge);
                case ShowScores:
                    setContentView(R.layout.show_scores);
            }
        } else {
            switch (state) {
                case GameInit:
                    setContentView(R.layout.game_init_others);
                case ChoosePicture:
                    setContentView(R.layout.choose_picture_others);
                case Captioning:
                    setContentView(R.layout.captioning_others);
                case ChooseWinner:
                    setContentView(R.layout.choose_winner_others);
                case ShowScores:
                    setContentView(R.layout.show_scores);
            }
        }*/
    }

    // Perform different actions based on current state
    private void run() {
        if (isJudge) {
            switch (state) {
                case GameInit:
                    // Input game information, invite people. Click start ->
                case ChoosePicture:
                    // Choose picture. Click post ->
                case Captioning:
                    // Idle. All players submit ->
                case ChooseWinner:
                    // Swipe. Select winner ->
                case ShowScores:
                    // Show scores. Next round starts in 1 minute ->
            }
        } else {
            switch (state) {
                case GameInit:
                    // Wait for accept, idle if already accepted. Judge click start ->
                case ChoosePicture:
                    // Idle. Judge post ->
                case Captioning:
                    // Caption. Submit. All players submit ->
                case ChooseWinner:
                    // Idle. Judge select winner ->
                case ShowScores:
                    // Show scores. Next round starts in 1 minute ->
            }
        }
    }

    // Store lobby to DB. Requires that specified lobby exists in DB already.
    private void saveLobby(final String lobbyId) {
        // Write to DB: query object by objectId and alter contents
        ParseQuery<ParseObject> dataObject = ParseQuery.getQuery("Lobby");
        dataObject.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put("name", name);
                    object.put("players", players);
                    object.put("judgeQueue", judgeQueue);
                    object.put("roundNum", roundNum);
                    object.put("state", state.ordinal());
                    object.saveInBackground();
                } else {
                    Log.d("*****Lobby", "Error saving lobby: " + e.getMessage());
                }
            }
        });
    }

    // Load specified lobby from DB
    void loadLobby(final String lobbyId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Lobby");
        query.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    name = object.getString("name");
                    List<ParseObject> tempPlayers = object.getList("players");
                    players = new ArrayList<ParseObject>(tempPlayers);
                    List<String> tempJudgeQueue = object.getList("judgeQueue");
                    judgeQueue = new LinkedList<String>(tempJudgeQueue);
                    roundNum = object.getInt("roundNum");
                    isJudge = judgeQueue.peekFirst().equals(ParseUser.getCurrentUser().getObjectId());
                    state = State.values()[object.getInt("state")];
                    loadLayout();
                    //TODO: for saveLobby debug
                    //name = "Changed!";
                    //judgeQueue.add("Another user!");
                    //saveLobby(lobbyId);
                } else {
                    Log.d("*****Lobby", "Error loading lobby: " + e.getMessage());
                }
            }
        });
    }

    public void inviteUser(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Title");
        alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String username = input.getText().toString();
                if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                    Toast.makeText(getApplicationContext(), "You can't invite yourself to the lobby.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("username", username);
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null && objects.size() == 1) {
                            // Make the list item
                            LinearLayout LL = (LinearLayout)findViewById(R.id.LinLayoutFriendInvite);
                            createFriendInviteListItem(LL, username);
                        } else {
                            Toast.makeText(getApplicationContext(), "Could not find user!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    private void createFriendInviteListItem(LinearLayout LL, String username) {

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels5 = (int) (5*scale + 0.5f);

        LinearLayout l = new LinearLayout(getApplicationContext());
        l.setGravity(Gravity.CENTER);
        l.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView iv = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(25, 25);
        iv.setBackgroundColor(Color.parseColor("#000000"));

        TextView tv = new TextView(getApplicationContext());
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setText(username);
        tvParams.setMargins(0, dpAsPixels5, 0, 0);

        l.addView(iv, ivParams);
        l.addView(tv, tvParams);
        LL.addView(l, layoutParams);
    }


}
