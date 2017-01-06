package com.JSaper.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.JSaper.GUI.MainForm;
import com.Multisaper.Core.Common.GUISize;
import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.MainWindow.NewGameAction;
import com.Multisaper.Core.Logic.Board;
import com.Multisaper.Core.Logic.LocalGame;

public class PCController extends Controller {

	public void NewGame(int w, int h, int bombs) {
		releaseGame();
		BoardSize = new GUISize(w, h);
		BombCount = bombs;
		game = new LocalGame(w, h, bombs);
		if (mw == null) {
			SetMainWindow(new MainForm());
		}
		mw.ResetWindow(w, h);
		mw.OnNewGame(NewGameAction.UserNewGame);
	}

	public void SaveGameToFile(File f) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(f);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(game.GetBoardClient());
		out.close();
		fileOut.close();
	}

	public void LoadGameFromFile(File f) throws IOException,
			ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(f);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		workMode = WorkMode.Local;
		game = new LocalGame((Board) in.readObject());
		in.close();
		fileIn.close();
		Board b = game.GetBoardClient();
		mw.ResetWindow(b.getWidth(), b.getHeight());
		mw.OnNewGame(NewGameAction.UserNewGame);
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

	public void MainWindowClosed() {
		releaseGame();
	}

	public GUINootificationResponse NotifyGUI(GUINotificationReason reason) {
		return null;
	}
}
