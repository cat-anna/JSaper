package com.Multisaper.Core.Logic;

import java.io.IOException;

import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.OutOfBoardException;
import com.Multisaper.Core.Interfaces.Controller.TheOneSaperMistakeException;
import com.Multisaper.Core.Interfaces.SaperConnection;

public class LocalGame extends SaperConnection {
	private Board board;
	
	private Incrementor incr;
	
	class Incrementor extends Thread {
		public Incrementor() {
			setDaemon(true);
			if(incr != null) {
				incr.interrupt();
			}
			incr = this;
			start();
		}
		@Override
		public void run() {
			while(!isInterrupted()) {
				currentTime += 1;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	
	public LocalGame(Board board) {
		super();
		this.board = board;
		new Incrementor();
	}

	public LocalGame(int w, int h, int b) {
		board = new Board(w, h, b);
		new Incrementor();
	}
	
	@Override
	public Board GetBoardClient() {
		return board;
	}

	@Override
	public void kill() {
		if(incr != null) {
			incr.interrupt();
		}
		incr = null;
	}

	@Override
	public void PerformClick(int x, int y) throws OutOfBoardException {
		try {
			board.ClickField(x, y);
		}
		catch (TheOneSaperMistakeException e){
			if(incr != null) incr.interrupt();
			throw e;
		}
		if(board.isGameEnded()) {
			if(incr != null) incr.interrupt();
			Controller.getInstance().GameWon();
		}
	}

	public Board getBoard() {
		return board;
	}

	@Override
	public void PerformSelectClick(int x, int y) {
		board.SelectField(x, y);
		if(board.isGameEnded()) {
			if(incr != null) incr.interrupt();
			Controller.getInstance().GameWon();
		}
	}
}
