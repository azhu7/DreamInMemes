package com.teammeme.dreaminmemes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Alex on 3/25/2017.
 */

public class Lobby extends AppCompatActivity {
    ArrayList<Integer> players;  // Player IDs
    LinkedList<Integer> judge_queue;  // Maintains order of judges
    int round_num;
    State state;

    private enum State {
        GameInit, ChoosePicture, Captioning, ChooseWinner
    }

    static void store_blank_lobby(Integer owner_id) {
        ArrayList<Integer> players = new ArrayList<Integer>();
        LinkedList<Integer> judge_queue = new LinkedList<Integer>();
        players.add(owner_id);
        judge_queue.add(owner_id);
        store_lobby(players, judge_queue, 1, State.GameInit);
    }

    static void store_lobby(ArrayList<Integer> players, LinkedList<Integer> judge_queue,
                            int round_num, State state) {
        // Write to DB
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Select which layout to open
        // get game id from init.getextra
        // query DB with id
        load_layout();

    }

    // Need to load different depending on judge or not...
    private void load_layout() {
        setContentView(R.layout.game_init);  // Temporary
        /*Boolean is_judge = false;
        if (is_judge) {
            switch (state) {
                case GameInit:
                    setContentView(R.layout.game_init_judge);
                case ChoosePicture:
                    setContentView(R.layout.choose_picture_judge);
                case Captioning:
                    setContentView(R.layout.captioning_judge);
                case ChooseWinner:
                    setContentView(R.layout.choose_winner_judge);
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
            }
        }*/
    }

}
