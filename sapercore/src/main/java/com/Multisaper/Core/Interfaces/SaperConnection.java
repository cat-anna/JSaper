package com.Multisaper.Core.Interfaces;

import com.Multisaper.Core.Interfaces.Controller.OutOfBoardException;
import com.Multisaper.Core.Logic.Board;


public abstract class SaperConnection {
	protected String GameID;
	protected int currentTime;
	
	public String getGameID() {
		return GameID;
	}
	
	public abstract Board GetBoardClient();
	public abstract void kill();
	public abstract void PerformClick(int x, int y) throws OutOfBoardException;
	public abstract void PerformSelectClick(int x, int y);
	public int getTime() { return currentTime; } 
}
