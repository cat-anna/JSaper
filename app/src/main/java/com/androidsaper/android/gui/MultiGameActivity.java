package com.androidsaper.android.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidsaper.android.gui.LogInActivity.Worker;
import com.androidsaper.R;

public class MultiGameActivity extends Activity {

	private EditText etPort;
	private EditText etServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_game);
		etPort = (EditText) findViewById(R.id.editPort);
		etServer = (EditText) findViewById(R.id.editServer);
		((Button) findViewById(R.id.buttonOk)).setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String s;
				int p;
				try {
					p = Integer.parseInt(etPort.getText().toString());
					s = etServer.getText().toString();
					if (s.length() < 1)
						throw new Exception();
				} catch (Exception e) {
					Toast.makeText(MultiGameActivity.this, "Wpisz poprawne dane", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent returnIntent = getIntent();//new Intent();
				returnIntent.putExtra("p", p);
				returnIntent.putExtra("s", s);
				setResult(1, returnIntent);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.multi_game, menu);
		return true;
	}

}
