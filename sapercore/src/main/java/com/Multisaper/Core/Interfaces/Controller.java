package com.Multisaper.Core.Interfaces;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.Multisaper.Core.Common.GUISize;
import com.Multisaper.Core.DB.DBConn;
import com.Multisaper.Core.Interfaces.MainWindow.EndGameAction;
import com.Multisaper.Core.Interfaces.MainWindow.NewGameAction;
import com.Multisaper.Core.Logic.Board;

public abstract class Controller {
	private static Controller _Instance;
	protected MainWindow mw;
	protected String Login, Password;
	protected GUISize BoardSize = null;
	protected int BombCount;
	protected com.Multisaper.Core.Interfaces.SaperConnection game;

	public Controller() {
		_Instance = this;
	}

	public boolean isConnected() {
		return com.Multisaper.Core.DB.DBConn.IsConnected();
	}

	public static class TheOneSaperMistakeException extends RuntimeException {
		private static final long serialVersionUID = -3247431047759137196L;
	}

	public static class OutOfBoardException extends RuntimeException {
		private static final long serialVersionUID = -3247431047759137195L;
	}

	public static class UnableToCreateServerException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public static class UnableToConnectException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public static class LoginFailureException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public static class ConnectionException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public static class ActionForbiddenException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public static class NotLoggedInException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public void SetMainWindow(MainWindow mw) {
		this.mw = mw;
	}

	public void BeginGame() {
		NewGame(10, 10, 10);
		mw.Show();
	}

	public void PlayerDied() {
		mw.OnGameEnd(EndGameAction.PlayerDied);
	}

	public GUISize GetBoardSize() {
		return BoardSize;
	}

	public int GetBombsCount() {
		return BombCount;
	}

	public Board GetBoard() {
		if(game == null) return null;
		return game.GetBoardClient();
	}

	public abstract void NewGame(int w, int h, int bombs);

	public abstract void MainWindowClosed();

	public enum GUINotificationReason {
		BoardChanged, ServerStartsNewGame, ConnectionLost, PlayerDied,
	}

	public enum GUINootificationResponse {
		Ok, Accept,
	}

	public abstract GUINootificationResponse NotifyGUI(
			GUINotificationReason reason);

	public enum WorkMode {
		Local, Server, Peer,
	}

	protected WorkMode workMode;

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void BoardClickField(int x, int y)
			throws TheOneSaperMistakeException {
		game.PerformClick(x, y);
	}

	public void BoardSelectField(int x, int y)
			throws OutOfBoardException {
		game.PerformSelectClick(x, y);
	}

	public abstract void SaveGameToFile(File f) throws IOException;

	public abstract void LoadGameFromFile(File f) throws IOException,
			ClassNotFoundException;

	public void JoinMultiplayerGame(String ServerName, int Port)
			throws NotLoggedInException, ConnectionException {
		TestConnection();
		releaseGame();
		game = new com.Multisaper.Core.Multi.SaperClient(this, Login, ServerName, Port);
		Board b = game.GetBoardClient();
		workMode = WorkMode.Peer;
		BoardSize = new GUISize(b.getWidth(), b.getHeight());
	}

	public void CreateMultiplayerGame(String ServerName, int Port, int W,
			int H, int B) throws UnableToCreateServerException,
			NotLoggedInException {
		TestConnection();
		releaseGame();
		game = new com.Multisaper.Core.Multi.SaperServer(ServerName, Login, Port, this, W, H, B);
		workMode = WorkMode.Server;
		BoardSize = new GUISize(W, H);
		BombCount = B;
	}

	protected void TestConnection() throws NotLoggedInException {
		if (!isConnected())
			throw new NotLoggedInException();
	}

	public void LogOff() {
		if (getWorkMode() != WorkMode.Local) {
			releaseGame();
			NewGame(10, 10, 10);
		}
	}

	public void LogIn(String User, String Password)
			throws LoginFailureException, ConnectionException {
		try {
			DBConn conn = DBConn.GetInstance();
			conn.ValidateUser(Password, User);
			this.Login = User;
			this.Password = Password;
			return;
		} catch (SQLException e) {
			throw new ConnectionException();
		} catch (com.Multisaper.Core.DB.DBConn.Failure e) {
			throw new LoginFailureException();
		}
	}

	public void CreateUser(String User, String Password)
			throws LoginFailureException, ConnectionException {
		try {
			DBConn conn = DBConn.GetInstance();
			conn.CreateUser(Password, User);
			this.Login = User;
			this.Password = Password;
			return;
		} catch (SQLException e) {
			throw new ConnectionException();
		} catch (com.Multisaper.Core.DB.DBConn.Failure e) {
			throw new LoginFailureException();
		}
	}

	protected void releaseGame() {
		if (game != null) {
			game.kill();
		}
		game = null;
	}

	public void ConnectionDied() {
		releaseGame();
		mw.OnGameEnd(EndGameAction.ConnectionDied);
	}

	public final int getTime() {
		if(game == null) return 0;
		return game.getTime();
	}

	public static final Controller getInstance() {
		return _Instance;
	}

	public void GameWon() {
		mw.OnGameEnd(EndGameAction.GameWon);
	}

	public DBConn getDBConnection() throws SQLException {
		return DBConn.GetInstance();
	}

	public String getPlayerName() {
		return Login;
	}
}
