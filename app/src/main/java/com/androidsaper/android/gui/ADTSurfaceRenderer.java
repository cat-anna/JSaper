package com.androidsaper.android.gui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Display;
import android.view.SurfaceView;

import com.Multisaper.Core.Common.GUIBox;
import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.GUIRenderer;
import com.Multisaper.Core.Interfaces.GuiSkin;
import com.Multisaper.Core.Logic.Board;

public class ADTSurfaceRenderer extends GUIRenderer {

	private SurfaceView sf;
	private Canvas c;
	
	public ADTSurfaceRenderer(SurfaceView sf, Display display) {
		this.sf = sf;
		this.display = display;
	}
	
	public void SetCanvas(Canvas c) {
		this.c = c;
	}
	
	private Display display; 
	
	@Override
	public int getW() {
		return display.getWidth();
	}

	@Override
	public int getH() {
		return display.getHeight();
	}

	@Override
	public void DoRender(Board b, GuiSkin skin) {
		skin.RenderMainElements();
		if (b != null) {
			GUIBox delta = skin.getBoardBorder();
			skin.RenderCounters(Controller.getInstance().getTime(), b.getRemainBombs());
			c.clipRect(delta.x, delta.y, delta.w - 1, delta.h - 1);
			b.EnumerateBoard(skin);
		}
	}
	

	@Override
	public void QuickQuad(int x, int y, int w, int h) {
	}

	@Override
	public void RenderBorders(GUIBox border, int size, float[] ColorLT, float[] ColorRB) {
		Paint p1 = new Paint();
		p1.setARGB(255, (int)(255 * ColorLT[0]), (int)(255 * ColorLT[1]), (int)(255 * ColorLT[2]));
		p1.setStrokeWidth(size);
		
		int x1, x2 = border.w;
		int y1, y2 = border.h;
		
		c.drawLine(border.x, border.y, border.x , y2, p1);
		c.drawLine(border.x, border.y, x2, border.y, p1);
		p1.setARGB(255, (int)(255 * ColorRB[0]), (int)(255 * ColorRB[1]), (int)(255 * ColorRB[2]));	
		c.drawLine(x2, border.y, x2, y2, p1);
		c.drawLine(border.x, y2, x2, y2, p1);
	}

	@Override
	public void FilledBorder(GUIBox border, int size, float[] Color, float[] ColorLT, float[] ColorRB) {
		c.drawARGB(255, (int)(255 * Color[0]), (int)(255 * Color[1]), (int)(255 * Color[2]));
		RenderBorders(border, size, ColorLT, ColorRB);
	}

	@Override
	public Object GetInstance() {
		return c;
	}

	@Override
	public boolean IsReady() {
		return true;
	}
}
