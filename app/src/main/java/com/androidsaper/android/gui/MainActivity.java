package com.androidsaper.android.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.ConnectionException;
import com.Multisaper.Core.Interfaces.Controller.NotLoggedInException;
import com.Multisaper.Core.Interfaces.Controller.UnableToCreateServerException;

import com.androidsaper.R;

public class MainActivity extends Activity {
	private Button BackButton;
	private TextView InfoText;

	final static protected int NewGameRequest = 10;
	final static protected int CreateGameRequest = 20;
	final static protected int JoinGameRequest = 30;
	final static protected int StatisticsRequest = 40;

	protected void onNewGameClick(View arg0) {
		Intent intent = new Intent(this, GameLevelSelection.class);
		startActivityForResult(intent, NewGameRequest);
	}

	protected void onCreateGameClick(View arg0) {
		Log.d("dupa", "create");
		if (Controller.getInstance().isConnected()) {
			onActivityResult(CreateGameRequest, 1, null);
		} else {
			Intent intent = new Intent(this, LogInActivity.class);
			startActivityForResult(intent, CreateGameRequest);
		}
	}

	protected void onJoinGameClick(View arg0) {
		Log.d("dupa", "join");
		if (Controller.getInstance().isConnected()) {
			onActivityResult(JoinGameRequest, 1, null);
		} else {
			Intent intent = new Intent(this, LogInActivity.class);
			startActivityForResult(intent, JoinGameRequest);
		}
	}

	public void OnShowStatistics(View v) {
		Log.d("dupa", "stats");
		if (Controller.getInstance().isConnected()) {
			onActivityResult(StatisticsRequest, 1, null);
		} else {
			Intent intent = new Intent(this, LogInActivity.class);
			startActivityForResult(intent, StatisticsRequest);
		}
	}

	protected void onReturnToGamelick(View arg0) {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode <= 0) return;
		Log.d("dupa", "rc:" + requestCode);

		switch(requestCode) {
		case NewGameRequest: {
			int w = data.getIntExtra("w", 10);
			int h = data.getIntExtra("h", 10);
			int b = data.getIntExtra("b", 10);
			Controller.getInstance().NewGame(w, h, b);
			finish();
			break;
		}

		case CreateGameRequest: {
			Intent intent = new Intent(this, MultiGameActivity.class);
			startActivityForResult(intent, CreateGameRequest+1);
			break;
		}
		case CreateGameRequest+1: {
			data.setClass(this, GameLevelSelection.class);
			startActivityForResult(data, CreateGameRequest+2);
			break;
		}
		case CreateGameRequest+2: {
			int w = data.getIntExtra("w", 10);
			int h = data.getIntExtra("h", 10);
			int b = data.getIntExtra("b", 10);
			String Name = data.getStringExtra("s");
			int Port = data.getIntExtra("p", 1234);
			try {
				Controller.getInstance().CreateMultiplayerGame(Name, Port, w, h, b);
			} catch (UnableToCreateServerException e) {
			} catch (NotLoggedInException e) {
			}
			finish();
			break;
		}		
		
		
		case JoinGameRequest: {
			Intent intent = new Intent(this, MultiGameActivity.class);
			startActivityForResult(intent, JoinGameRequest+1);
			break;
		}
		case JoinGameRequest+1: {
			String Name = data.getStringExtra("s");
			int Port = data.getIntExtra("p", 1234);
			try {
				Controller.getInstance().JoinMultiplayerGame(Name, Port);
			} catch (NotLoggedInException e) {
				e.printStackTrace();
				return;
			} catch (ConnectionException e) {
				e.printStackTrace();
				Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
				return;
			}
			finish();
			break;
		}

		case StatisticsRequest: {
			Intent intent = new Intent(this, ChartActivity.class);
			startActivityForResult(intent, StatisticsRequest);
			break;
		}

		}
//		super.onActivityResult(requestCode, resultCode, data);
	}
//awdsfegdhjf
	@Override
	protected void onResume() {
		if(Controller.getInstance().isConnected()) {
			InfoText.setText("Hello " + Controller.getInstance().getPlayerName());
		} else {
			InfoText.setText("Hello stranger, please log in");
		}
		
		BackButton.setEnabled(Controller.getInstance().GetBoard() != null);
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainscreen);
		
		InfoText = (TextView)findViewById(R.id.textinfo);

		((Button) (findViewById(R.id.buttonNewGame))).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.this.onNewGameClick(arg0);
			}
		});

		((Button) (findViewById(R.id.buttonJoinGame))).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.this.onJoinGameClick(arg0);
			}
		});

		((Button) (findViewById(R.id.buttonCreateGame))).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.this.onCreateGameClick(arg0);
			}
		});

		(BackButton = (Button) (findViewById(R.id.buttonReturnToGame))).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.this.onReturnToGamelick(arg0);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the
		return true;
	}

}
