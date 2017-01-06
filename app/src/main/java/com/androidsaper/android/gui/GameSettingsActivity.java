package com.androidsaper.android.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidsaper.R;

public class GameSettingsActivity extends Activity {

	private EditText etWidth, etHeight, etBombCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_settings);

		etWidth = (EditText) findViewById(R.id.editWidth);
		etHeight = (EditText) findViewById(R.id.editHeight);
		etBombCount = (EditText) findViewById(R.id.editBombs);
		((Button) findViewById(R.id.buttonOk))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						int w, h, b;
						try {
							w = Integer.parseInt(etWidth.getText().toString());
							h = Integer.parseInt(etHeight.getText().toString());
							b = Integer.parseInt(etBombCount.getText().toString());
						} catch (Exception e) {
							Toast.makeText(GameSettingsActivity.this, "Wpisz poprawne dane", Toast.LENGTH_SHORT).show();
							return;
						}
						Intent returnIntent = getIntent();//new Intent();
						returnIntent.putExtra("w", w);
						returnIntent.putExtra("h", h);
						returnIntent.putExtra("b", b);
						setResult(1, returnIntent);
						
						finish();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.game_settings, menu);
		return true;
	}
}
