package com.Multisaper.Core.Interfaces;

import com.Multisaper.Core.Common.GUIBox;
import com.Multisaper.Core.Common.GUISize;
import com.Multisaper.Core.Logic.Board;

public abstract class 	GUIRenderer {
	void Initialize() { }
	void Finalize() { }

	abstract public int getW();
	abstract public int getH();
	
	abstract public void DoRender(Board b, GuiSkin skin);
	abstract public void QuickQuad(int x, int y, int w, int h);
	abstract public void RenderBorders(GUIBox border, int size, float[] ColorLT, float[] ColorRB);
	abstract public void FilledBorder(GUIBox border, int size, float[] Color, float[] ColorLT, float[] ColorRB);
	abstract public Object GetInstance();
	abstract public boolean IsReady();
}
