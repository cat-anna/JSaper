package com.Multisaper.Core.Interfaces;



public interface MainWindow {
	void ResetWindow(int bw, int bh);
	
	enum NewGameAction {
		UserNewGame,
	};
	
	enum EndGameAction {
		GameWon,
		PlayerDied,
		ConnectionDied,
		AllPlayersDied,
	};
	
	void OnNewGame(NewGameAction action);
	void OnGameEnd(EndGameAction action);
	
	void Show();
}
