package com.androidsaper.android.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.ConnectionException;
import com.Multisaper.Core.Interfaces.Controller.LoginFailureException;
import com.androidsaper.R;

public class LogInActivity extends Activity {

	private EditText etLogin, etPass;
	private ProgressDialog pDialog;
	
	class Worker extends AsyncTask<String, String, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
			pDialog.dismiss();
			super.onPostExecute(result);
		}
		String[] Params;
		@Override
		protected void onProgressUpdate(String... values) {
			if(values.length < 1) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							new Worker().execute(Params[0], Params[1], "dummy");
						}
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
				builder.setMessage("Such player does not exists. Do you want to crate new one?").setPositiveButton("Yes", dialogClickListener)
						.setNegativeButton("No", dialogClickListener).show();
				return;
			}
			
			Toast.makeText(LogInActivity.this, values[0], Toast.LENGTH_SHORT).show();
			super.onProgressUpdate(values);
		}

		@Override
		protected Integer doInBackground(String... params) {
			Params = params;
			if(params.length == 3) {
				try {
					Controller.getInstance().CreateUser(params[0], params[1]);
				} catch (Exception e1) {
					publishProgress("Database connection error!");
					return null;
				}
			} else {
				try {
					Controller.getInstance().LogIn(params[0], params[1]);
				} catch (LoginFailureException e) {
					publishProgress("Invalid user name or password");
					publishProgress();
					return null;
				} catch (ConnectionException e) {
					publishProgress("Database connection error!");
					return null;
				}
			}
			
			setResult(1, getIntent());
			finish();
			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);

		etLogin = (EditText) findViewById(R.id.editUserName);
		etPass = (EditText) findViewById(R.id.editPassword);
		((Button) findViewById(R.id.buttonOk)).setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String l, p;
				try {
					l = etLogin.getText().toString();
					p = etPass.getText().toString();
					if (p.length() < 1 || l.length() < 1)
						throw new Exception();
				} catch (Exception e) {
					Toast.makeText(LogInActivity.this, "Enter correct data", Toast.LENGTH_SHORT).show();
					return;
				}
				pDialog = ProgressDialog.show(LogInActivity.this, "Connecting to database..", "Please wait");
			    
				new Worker().execute(l, p);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}
}
