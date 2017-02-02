package com.androidsaper.android.main;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.Multisaper.Core.Common.GUISize;
import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.ConnectionException;
import com.Multisaper.Core.Interfaces.Controller.NotLoggedInException;
import com.Multisaper.Core.Interfaces.Controller.UnableToCreateServerException;
import com.Multisaper.Core.Interfaces.MainWindow;
import com.Multisaper.Core.Interfaces.MainWindow.NewGameAction;
import com.Multisaper.Core.Logic.LocalGame;
import com.androidsaper.android.gui.MainActivity;
import com.androidsaper.android.gui.MultiGameActivity;

public class AdtController extends Controller {
	
	public AdtController(){
		super();
	}

	public void NewGame(int w, int h, int bombs) {
		releaseGame();
		BoardSize = new GUISize(w, h);
		BombCount = bombs;
		game = new LocalGame(w, h, bombs);
		mw.ResetWindow(w, h);
		mw.OnNewGame(NewGameAction.UserNewGame);
	}

	@Override
	public void MainWindowClosed() {
	}

	@Override
	public GUINootificationResponse NotifyGUI(GUINotificationReason reason) {
		return null;
	}

	@Override
	public void SaveGameToFile(File f) throws IOException {
	}

	@Override
	public void LoadGameFromFile(File f) throws IOException, ClassNotFoundException {
	}


	private class JoinMultiplayerGameArgs {
		public String ServerName;
		public int Port;
		public ProgressDialog dialog;

		public JoinMultiplayerGameArgs(String serverName, int port) {
			ServerName = serverName;
			Port = port;
		}
	};
	private class JoinMultiplayerGameTask extends AsyncTask<JoinMultiplayerGameArgs, Void, Exception> {
		JoinMultiplayerGameArgs arg;
		protected Exception doInBackground(JoinMultiplayerGameArgs ...args) {
			arg = args[0];
			try {
				AdtController.this.JoinMultiplayerGameDbCall(arg.ServerName, arg.Port);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return e;
			}
			return null;
		}

		protected void onPostExecute(Exception result) {
			arg.dialog.dismiss();

			if(result == null) {
				mw.ResetWindow(GetBoard().getWidth(), GetBoard().getHeight());
				mw.OnNewGame(NewGameAction.UserNewGame);
				return;
			}

			result.printStackTrace();
			mw.OnGameEnd(MainWindow.EndGameAction.Error);
			Toast.makeText(arg.dialog.getContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void JoinMultiplayerGameDbCall(String ServerName, int Port)
			throws NotLoggedInException, ConnectionException {
		super.JoinMultiplayerGame(ServerName, Port);
	}

	public void JoinMultiplayerGame(String ServerName, int Port)
			throws NotLoggedInException, ConnectionException {
		JoinMultiplayerGameArgs arg = new JoinMultiplayerGameArgs(ServerName, Port);
		arg.dialog = ProgressDialog.show((Context)mw, "Please wait", "Connecting...");
		new JoinMultiplayerGameTask().execute(arg);
	}

	private class CreateMultiplayerGameArgs {
		public String ServerName;
		public int Port;
		public int W;
		public int H;
		public int B;
		public ProgressDialog dialog;

		public CreateMultiplayerGameArgs(String serverName, int port, int w, int h, int b) {
			ServerName = serverName;
			Port = port;
			W = w;
			H = h;
			B = b;
		}
	};
	private class CreateMultiplayerGameTask extends AsyncTask<CreateMultiplayerGameArgs, Void, Exception> {
		CreateMultiplayerGameArgs arg;
		protected Exception doInBackground(CreateMultiplayerGameArgs ...args) {
			arg = args[0];
			try {
				AdtController.this.CreateMultiplayerGameDbCall(arg.ServerName, arg.Port, arg.W, arg.H, arg.B);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return e;
			}
			return null;
		}

		protected void onPostExecute(Exception result) {
			if(result == null) {
				mw.ResetWindow(arg.W, arg.H);
				mw.OnNewGame(NewGameAction.UserNewGame);
				arg.dialog.dismiss();
				return;
			}

			result.printStackTrace();
			arg.dialog.dismiss();
			mw.OnGameEnd(MainWindow.EndGameAction.Error);

		}
	}

	protected void CreateMultiplayerGameDbCall(String ServerName, int Port, int W, int H, int B)
			throws UnableToCreateServerException, NotLoggedInException {
		super.CreateMultiplayerGame( ServerName, Port, W, H, B);
	}

	public void CreateMultiplayerGame(String ServerName, int Port, int W,
			int H, int B) throws UnableToCreateServerException,
			NotLoggedInException {
		CreateMultiplayerGameArgs arg = new CreateMultiplayerGameArgs(ServerName, Port, W, H, B);
		arg.dialog = ProgressDialog.show((Context)mw, "Please wait", "Talking to database...");
		new CreateMultiplayerGameTask().execute(arg);
	}

}
