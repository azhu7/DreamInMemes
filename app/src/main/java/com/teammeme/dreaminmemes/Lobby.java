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
    ArrayList<Integer> players;  // Player IDs
    LinkedList<Integer> judgeQueue;  // Maintains order of judges
    int roundNum;
    boolean isJudge;
    State state;

    private enum State {
        GameInit, ChoosePicture, Captioning, ChooseWinner, ShowScores
    }

    // Store blank lobby (containing owner only) to DB
    static String store_blank_lobby(Integer ownerId) {
        ArrayList<Integer> players = new ArrayList<>();
        LinkedList<Integer> judgeQueue = new LinkedList<>();
        players.add(ownerId);
        judgeQueue.add(ownerId);
        return store_lobby("blank", players, judgeQueue, 1, State.GameInit);
    }

    // Store lobby to DB
    private static String store_lobby(String name, ArrayList<Integer> players,
                            LinkedList<Integer> judgeQueue, int roundNum, State state) {
        // Write to DB
        ParseObject dataObject = ParseObject.create("Lobby");
        dataObject.put("name", name);
        dataObject.put("players", players);
        dataObject.put("judgeQueue", judgeQueue);
        dataObject.put("roundNum", roundNum);
        dataObject.put("state", state.ordinal());
        dataObject.saveInBackground();
        return dataObject.getObjectId();
    }

    // Load specified lobby from DB
    void load_lobby(String lobbyId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Lobby");
        query.getInBackground(lobbyId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    name = object.getString("name");
                    List<Integer> temp = object.getList("players");
                    players = new ArrayList<Integer>(temp);
                    temp = object.getList("judgeQueue");
                    judgeQueue = new LinkedList<Integer>(temp);
                    roundNum = object.getInt("roundNum");
                    state = State.values()[object.getInt("state")];
                } else {
                    Log.d("*****Lobby", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Select which layout to open
        String lobbyId = getIntent().getStringExtra("ID");
        load_lobby(lobbyId);
        //boolean isJudge = false;
        load_layout();
        //run();
    }

    // Load layout based on current state
    private void load_layout() {
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
}
