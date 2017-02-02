package com.androidsaper.android.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.MainWindow;
import com.androidsaper.R;

public class GameActivity extends Activity implements MainWindow {

	private GameView GameView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Controller.getInstance().SetMainWindow(this);
		Controller.getInstance().BeginGame();

		setContentView(R.layout.activity_gamescreen);
		GameView = (GameView)findViewById(R.id.gameView);
	}

	@Override
	protected void onPause() {
		GameView.SetRenderingState(false);
		super.onPause();
	}

	@Override
	protected void onResume() {
		GameView.SetRenderingState(true);
		super.onResume();
	}

	@Override
	public void ResetWindow(int bw, int bh) {
	}

	@Override
	public void OnNewGame(NewGameAction action) {
		if(GameView != null) {
			GameView.RecreateSkin();
			GameView.SetGameState(true);
		}
		switch(action) {
		case UserNewGame:
			break;
		default:
			break;
		
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public void OnGameEnd(final EndGameAction a) {
		runOnUiThread(new Runnable() {
			final public EndGameAction action = a;
			@Override
			public void run() {
				GameView.SetGameState(false);
				switch(action){
				case AllPlayersDied:
					Toast.makeText(GameActivity.this, "All players have died...", Toast.LENGTH_SHORT).show();
					onBackPressed();
					break;
				case ConnectionDied:
					Toast.makeText(GameActivity.this, "Database connection failed!", Toast.LENGTH_SHORT).show();
					onBackPressed();
					break;
				case GameWon:
					Toast.makeText(GameActivity.this, "Victory!", Toast.LENGTH_SHORT).show();
					onBackPressed();
					break;
				case PlayerDied:
					Toast.makeText(GameActivity.this, "You died...", Toast.LENGTH_SHORT).show();
					onBackPressed();
					break;
				case Error:
					onBackPressed();
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void Show() {
	}
}
