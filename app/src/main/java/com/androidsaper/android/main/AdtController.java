package com.androidsaper.android.main;

import java.io.File;
import java.io.IOException;

import com.Multisaper.Core.Common.GUISize;
import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.ConnectionException;
import com.Multisaper.Core.Interfaces.Controller.NotLoggedInException;
import com.Multisaper.Core.Interfaces.Controller.UnableToCreateServerException;
import com.Multisaper.Core.Interfaces.MainWindow.NewGameAction;
import com.Multisaper.Core.Logic.LocalGame;

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
	
	public void JoinMultiplayerGame(String ServerName, int Port)
			throws NotLoggedInException, ConnectionException {
		super.JoinMultiplayerGame(ServerName, Port);
		mw.ResetWindow(GetBoard().getWidth(), GetBoard().getHeight());
		mw.OnNewGame(NewGameAction.UserNewGame);
	}

	public void CreateMultiplayerGame(String ServerName, int Port, int W,
			int H, int B) throws UnableToCreateServerException,
			NotLoggedInException {
		super.CreateMultiplayerGame(ServerName, Port, W, H, B);
		mw.ResetWindow(W, H);
		mw.OnNewGame(NewGameAction.UserNewGame);
	}

}
