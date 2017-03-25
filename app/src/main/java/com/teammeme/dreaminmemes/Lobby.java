package com.teammeme.dreaminmemes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// TODO: Game_init
// TODO: Game

public class Lobby extends AppCompatActivity {
    String name;
    ArrayList<String> players;  // Player IDs
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

    // Store lobby to DB
    private void storeLobby() {
        // Write to DB: TODO: Query object and alter contents? How?
        /*final ParseObject dataObject = ParseObject.create("Lobby");
        dataObject.put("name", name);
        dataObject.put("players", players);
        dataObject.put("judgeQueue", judgeQueue);
        dataObject.put("roundNum", roundNum);
        dataObject.put("state", state.ordinal());

        dataObject.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Successful save to DB
                    Intent i = new Intent(getApplicationContext(), Lobby.class);
                    i.putExtra("lobbyId", dataObject.getObjectId());
                    startActivity(i);
                } else {
                    // Failure
                    Log.d("*****TabGlobal", "Error saving lobby: " + e.getMessage());
                }
            }
        });*/
    }

    // Load specified lobby from DB
    void loadLobby(String lobbyId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Lobby");
        query.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    name = object.getString("name");
                    List<String> temp = object.getList("players");
                    players = new ArrayList<String>(temp);
                    temp = object.getList("judgeQueue");
                    judgeQueue = new LinkedList<String>(temp);
                    roundNum = object.getInt("roundNum");
                    state = State.values()[object.getInt("state")];
                    loadLayout();
                } else {
                    Log.d("*****Lobby", "Error: " + e.getMessage());
                }
            }
        });
    }
}
