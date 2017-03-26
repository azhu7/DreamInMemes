package com.teammeme.dreaminmemes;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by dillo on 3/25/2017.
 */

public class MemeCollection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meme_collection);
    }

    public void returnMemePath(View v) {
        ImageView iv = (ImageView) v;
        Intent output = new Intent();
        //iv.setTag("hello");
        String tag = iv.getTag().toString();
        output.putExtra("path", tag);
        setResult(RESULT_OK, output);
        finish();
    }

}
