package com.androidsaper.android.gui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androidsaper.R;

public class GameLevelSelection extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_level_selection);
    }

    void ReturnGameSettings(int w, int h, int b) {
        Intent returnIntent = getIntent();//new Intent();
        returnIntent.putExtra("w", w);
        returnIntent.putExtra("h", h);
        returnIntent.putExtra("b", b);
        setResult(1, returnIntent);
        finish();
    }

    public void HandleClick(View v) {
        Button b = (Button)v;
        switch(b.getText().toString().toLowerCase())
        {
            case "easy":
                ReturnGameSettings(10, 10, 10);
                return;
            case "moderate":
                ReturnGameSettings(20, 20, 50);
                return;
            case "hard":
                ReturnGameSettings(30, 20, 100);
                return;
            case "very hard":
                ReturnGameSettings(50, 50, 200);
                return;
            case "impossible":
                ReturnGameSettings(100, 100, 2000);
                return;
            case "custom":
                Intent intent = new Intent(this, GameSettingsActivity.class);
                startActivityForResult(intent, 1);
                return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1: {
                setResult(1, data);
                finish();
                break;
            }
        }
    }
}
