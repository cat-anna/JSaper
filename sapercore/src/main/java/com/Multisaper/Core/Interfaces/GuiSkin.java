package com.Multisaper.Core.Interfaces;

import com.Multisaper.Core.Common.GUIBorders;
import com.Multisaper.Core.Common.GUIBox;
import com.Multisaper.Core.Common.GUISize;
import com.Multisaper.Core.Logic.Board;
import com.Multisaper.Core.Logic.BoardEnumerator;

public abstract class GuiSkin implements BoardEnumerator {
	protected GUIRenderer guir;
	protected float scale = 1;
	protected GUIBox BoardBorder = new GUIBox();
	public GUIBox getBoardBorder() {
		return BoardBorder;
	}

	protected GUISize BoardSize = new GUISize(1, 1);
	protected GUISize BoardDelta = new GUISize(1, 1);
	
	protected GUISize BoardMove = new GUISize(0, 0);
	protected GUISize BoardViewSize = new GUISize(0, 0);
	protected float FieldSizeFactor = 1; 
	
	public GUISize getBoardMove() {
		return BoardMove;
	}

	public float getFieldSizeFactor() {
		return FieldSizeFactor;
	}

	public void setFieldSizeFactor(float fieldSizeFactor) {
		FieldSizeFactor = fieldSizeFactor;
	}

	public void MoveBoard(int x, int y) {
		BoardMove.Width += x;
		BoardMove.Height += y;
		
//		if(BoardMove.Width < 0) BoardMove.Width  = 0;
//		if(BoardMove.Height < 0) BoardMove.Height  = 0;
	}
	
	public boolean isConstSize() {
		return ConstSize;
	}

	public void setConstSize(boolean constSize) {
		ConstSize = constSize;
	}

	protected boolean ConstSize = false;
	
	public GUISize getBoardDelta() {
		return BoardDelta;
	}

	public GuiSkin(GUIRenderer guir) {
		this.guir = guir;
	}
	
	public void SetBoardSize(int fw, int fh, int SizeY, int SizeX) {
		BoardSize = new GUISize(fw * GetFieldSize(), fh * GetFieldSize());
		BoardMove = new GUISize(0, 0);
		if(ConstSize)
			BoardViewSize = new GUISize(SizeX * GetFieldSize(), SizeY * GetFieldSize());
		else
			BoardViewSize = new GUISize(BoardSize);
		ResetSize();
	}
	
	protected int getW() {
		return guir.getW();
	}
	protected int getH() {
		return guir.getH();
	}
	
	public void ResetSize()  { }
	
	public int GetFieldSize() {
		return (int)(FieldSizeFactor);
	}
	
	abstract public void RenderMainElements();
	abstract public void RenderCounters(int Time, int Bombs);
	
	abstract public void GetGUISize(GUIBorders borders, GUISize BoardSize);
}
