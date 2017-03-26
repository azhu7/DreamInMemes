package com.teammeme.dreaminmemes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// Lobby facilitates game initialization (inviting players) and the actual game play.
public class Lobby extends AppCompatActivity {
    String lobbyId;  // this objectId
    String name;
    ArrayList<ParseObject> players;  // Player Infos
    LinkedList<String> judgeQueue;  // Maintains order of judges
    //TODO: ArrayList<String> invited;  // Invited players -- For restoring state of pending games
    int roundNum;
    boolean isJudge;
    State state;

    public enum State {
        GameInit, ChoosePicture, Captioning, ChooseWinner, ShowResults
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Select which layout to open
        lobbyId = getIntent().getStringExtra("lobbyId");
        loadLobby(lobbyId);
        //run();
    }

    // Load layout based on current state
    private void loadLayout() {
        //setContentView(R.layout.game_init);  // Temporary
        if (isJudge) {
            switch (state) {
                case GameInit:
                    setContentView(R.layout.game_init_judge);
                    if (state == State.GameInit) {
                        EditText et_title = (EditText)findViewById(R.id.et_title);
                        et_title.setText(name);
                    }
                    break;
                case ChoosePicture:
                    setContentView(R.layout.choose_picture_judge);
                    TextView tv_roundNum = (TextView)findViewById(R.id.tv_roundNum);
                    tv_roundNum.setText("Round " + roundNum + "/" + players.size());
                    break;
                case Captioning:
                    setContentView(R.layout.captioning_judge);
                    TextView tv_captioning_judge = (TextView)findViewById(R.id.tv_captioning_judge);
                    tv_captioning_judge.setText("Round " + roundNum + "/" + players.size());
                    break;
                case ChooseWinner:
                    //setContentView(R.layout.choose_winner_judge);
                    fetchMemes();
                    break;
                case ShowResults:
                    //setContentView(R.layout.show_scores);
                    break;
            }
        } else {
            switch (state) {
                case GameInit:
                    setContentView(R.layout.game_init_others);
                    TextView txtGameName = (TextView)findViewById(R.id.txtGameName);
                    txtGameName.setText(name);
                    break;
                case ChoosePicture:
                    //setContentView(R.layout.choose_picture_others);
                    break;
                case Captioning:
                    setContentView(R.layout.captioning_others);
                    final TextView tv_captioning_others = (TextView)findViewById(R.id.tv_captioning_others);
                    String currentJudgeId = judgeQueue.getFirst();
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("objectId", currentJudgeId);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                tv_captioning_others.setText(objects.get(0).getString("username"));
                            } else {
                                Log.d("*****CaptioningOthers", "Error: " + e.getMessage());
                            }
                        }
                    });
                    break;
                case ChooseWinner:
                    //setContentView(R.layout.choose_winner_others);
                    break;
                case ShowResults:
                    //setContentView(R.layout.show_scores);
                    break;
            }
        }
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
                case ShowResults:
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
                case ShowResults:
                    // Show scores. Next round starts in 1 minute ->
            }
        }
    }

    // Owner instantiates game in Lobby, incorporating information from Create game page.
    public void startGame(View v) {
        EditText et_title = (EditText) findViewById(R.id.et_title);
        name = et_title.getText().toString();
        if (name.equals("")) {
            Toast.makeText(getApplicationContext(), "Must name the lobby!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Delete any lingering lobby requests
        ParseQuery<ParseObject> query = ParseQuery.getQuery("userRequest");
        query.whereEqualTo("lobbyId", lobbyId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i)
                        objects.get(i).deleteInBackground();
                } else {
                    Log.d("*****startGame", "Error: " + e.getMessage());
                }
            }
        });

        state = State.ChoosePicture;

        // Query the lobby
        ParseQuery<ParseObject> dataObject = ParseQuery.getQuery("Lobby");
        dataObject.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // Write certain values
                    object.put("name", name);
                    object.put("roundNum", roundNum);
                    object.put("state", state.ordinal());

                    // Read values changed by other players joining
                    List<ParseObject> tempPlayers = object.getList("players");
                    players = new ArrayList<>(tempPlayers);
                    List<String> tempJudgeQueue = object.getList("judgeQueue");
                    judgeQueue = new LinkedList<>(tempJudgeQueue);

                    object.saveInBackground();
                    loadLayout();
                } else {
                    Log.d("*****Lobby", "Error saving lobby: " + e.getMessage());
                }
            }
        });
    }

    // Judge submits picture and players begin to caption.
    public void startCaptioning(View v) {
        // Check that judge actually chose a picture
        final ImageView iv_selected = (ImageView)findViewById(R.id.iv_selected);
        if (iv_selected.getDrawable() == null) {
            Toast.makeText(getApplicationContext(), "Must select a picture for captioning!", Toast.LENGTH_SHORT).show();
            return;
        }

        state = State.Captioning;

        // Query the lobby
        ParseQuery<ParseObject> dataObject = ParseQuery.getQuery("Lobby");
        dataObject.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // Write certain values, including meme selected
                    object.put("state", state.ordinal());
                    object.put("memeFilename", iv_selected.getTag().toString());
                    object.saveInBackground();
                    loadLayout();
                } else {
                    Log.d("*****Lobby", "Error startCaptioning: " + e.getMessage());
                }
            }
        });
    }

    // Players done captioning, judge updates state and gets pictures
    public void startWinnerSelection() {
        state = State.ChooseWinner;
        // Query the lobby
        ParseQuery<ParseObject> dataObject = ParseQuery.getQuery("Lobby");
        dataObject.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // Write certain values
                    object.put("state", state.ordinal());
                    object.saveInBackground();
                    loadLayout();
                } else {
                    Log.d("*****Lobby", "Error saving lobby: " + e.getMessage());
                }
            }
        });
    }

    // Judge chose best picture. Now everyone sees same View - winner and leaderboard
    public void startShowResults() {

    }

    // Store lobby to DB. Requires that specified lobby exists in DB already.
    private void saveLobby() {
        // Write to DB: query object by objectId and alter contents
        if (state == State.GameInit) {
            EditText et = (EditText)findViewById(R.id.et_title);
            name = et.getText().toString();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.run();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load specified lobby from DB
    void loadLobby(final String lobbyId_) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Lobby");
        query.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    lobbyId = lobbyId_;
                    name = object.getString("name");
                    List<ParseObject> tempPlayers = object.getList("players");
                    players = new ArrayList<>(tempPlayers);
                    List<String> tempJudgeQueue = object.getList("judgeQueue");
                    judgeQueue = new LinkedList<>(tempJudgeQueue);
                    roundNum = object.getInt("roundNum");
                    isJudge = judgeQueue.peekFirst().equals(ParseUser.getCurrentUser().getObjectId());
                    state = State.values()[object.getInt("state")];
                    loadLayout();
                } else {
                    Log.d("*****Lobby", "Error loading lobby: " + e.getMessage());
                }
            }
        });
    }

    // Delete specified lobby from Parse Cloud. Also delete any mentions of the lobby's objectId.
    public void deleteLobby(View v) {
        ParseQuery<ParseObject> dataObject = ParseQuery.getQuery("Lobby");
        dataObject.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                if (e == null) {
                    // Remove userInfos
                    List<ParseObject> playerInfos = object.getList("players");
                    for (ParseObject userInfo : playerInfos) {
                        userInfo.deleteInBackground();
                    }
                    // Remove userRequests
                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("userRequest");
                    query1.whereEqualTo("lobbyId", lobbyId);
                    query1.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> userRequests, ParseException e) {
                            if (e == null) {
                                for (int i = 0; i < userRequests.size(); ++i)
                                    userRequests.get(i).deleteInBackground();
                            } else {
                                Log.d("*****deleteLobby", "Error: " + e.getMessage());
                            }
                        }
                    });

                    List<String> players = object.getList("judgeQueue");
                    if (players == null) {
                        object.deleteInBackground();
                        return;
                    }
                    // Remove lobbyId from each user
                    for (final String userId : players) {
                        ParseQuery<ParseUser> query2 = ParseUser.getQuery();
                        query2.whereEqualTo("objectId", userId);
                        // Query for user with this userId
                        query2.findInBackground(new FindCallback<ParseUser>() {
                            public void done(List<ParseUser> users, ParseException e) {
                                if (e == null && users.size() == 1) {
                                    // The query was successful.
                                    ParseUser user = users.get(0);
                                    List<String> lobbies = user.getList("lobbies");
                                    lobbies.remove(object.getObjectId());  // Remove this lobby
                                    user.put("lobbies", lobbies);
                                    user.saveInBackground();
                                } else {
                                    Log.d("*****Lobby", "deleteLobby failed to find user with objectId: " + userId);
                                }
                            }
                        });
                    }
                    object.deleteInBackground();
                    finish();
                } else {
                    Log.d("*****Lobby", "Error deleting lobby: " + e.getMessage());
                }
            }
        });
    }

    // Leave lobby, but don't delete it.
    public void leaveLobby(View v) {
        saveLobby();
        finish();
    }

    // Invite a user by username.
    public void inviteUser(View v) {
        // TODO: Make sure users can't add same person multiple times
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Add a friend:");
        alert.setMessage("Enter a friend's username to invite them to your lobby!");

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
                            ParseObject userRequest = ParseObject.create("userRequest");
                            userRequest.put("userId", objects.get(0).getObjectId());
                            userRequest.put("lobbyId", lobbyId);
                            userRequest.saveInBackground();
                        } else {
                            Toast.makeText(getApplicationContext(), "Could not find user!", Toast.LENGTH_SHORT).show();
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

    // Non-judge player submits captioned meme. Note: each player may only submit one meme.
    public void submitMeme(View v) {
        // Iterate over playerInfos
        for (int i = 0; i < players.size(); ++i) {
            String currentUserId = ParseUser.getCurrentUser().getObjectId();
            // Find current player
            if (players.get(i).getString("objectId").equals(currentUserId)) {
                if (!players.get(i).getBoolean("submitted")) {
                    //TODO: submit File to Parse
                    //byte[] data;  //TODO: = some ImageView image?
                    //ParseFile captionedMeme = new ParseFile(currentUserId, data);
                    //captionedMeme.saveInBackground();
                    players.get(i).put("submitted", true);
                    // Query lobby and increment the number of submitted entries
                    ParseQuery<ParseObject> dataObject = ParseQuery.getQuery("Lobby");
                    dataObject.getInBackground(lobbyId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                object.increment("numSubmitted");
                                // Everyone submitted. Time for leader to choose winner.
                                if (object.getInt("numSubmitted") == players.size() - 1) {
                                    startWinnerSelection();
                                }
                            } else {
                                Log.d("*****Lobby", "Error submitting meme: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Can only submit one meme per round!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        throw new RuntimeException("User not part of this Lobby.");
    }

    private void fetchMemes() {
        // Get submitted memes
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseFile");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

            }
        });
    }



    private void createFriendInviteListItem(LinearLayout LL, String username) {

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels5 = (int) (5*scale + 0.5f);

        LinearLayout l = new LinearLayout(getApplicationContext());
        l.setGravity(Gravity.CENTER_HORIZONTAL);
        l.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
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



    public void goToMemeCollection(View v) {
        Intent i = new Intent(getApplicationContext(), MemeCollection.class);
        int requestCode = 42;
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42 && resultCode == RESULT_OK && data != null) {
            String memePath = data.getStringExtra("path");
            ImageView iv_selected = (ImageView)findViewById(R.id.iv_selected);
            if (memePath.equals("badluckbrian.png"))
                iv_selected.setImageResource(R.drawable.badluckbrian);
            else if (memePath.equals("oldharold.png"))
                iv_selected.setImageResource(R.drawable.oldharold);
            else if (memePath.equals("thinkaboutit.png"))
                iv_selected.setImageResource(R.drawable.thinkaboutit);
            else if (memePath.equals("willywonka.png"))
                iv_selected.setImageResource(R.drawable.willywonka);
            iv_selected.setTag(memePath);
        }
    }



}
