package com.androidsaper.android.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.Multisaper.Core.Interfaces.DrawableSpriteTexture;
import com.Multisaper.Core.Interfaces.GUIRenderer;

public class AdtBitmap implements DrawableSpriteTexture {
	private Bitmap texture;
	private int hMax;
	
	public AdtBitmap(Bitmap tex, int hMax) {
		this.hMax = hMax;
		this.texture = tex;
	}
	
	public void Render(GUIRenderer Renderer, int x, int y, int w, int h, byte[] indexes) {
		for(int i = 0; i < indexes.length; ++i) {
			Render(Renderer, x, y, w, h, indexes[i]);
			x += w;//texture.getWidth();
		}
	}
	
	private static final Paint painter = new Paint(); 
	public void Render(GUIRenderer Renderer, int x, int y, int w, int h, int index) {
		Canvas c = (Canvas)Renderer.GetInstance();
		Rect src = new Rect(0, hMax * index, texture.getWidth(), hMax * (index + 1));
		Rect dst = new Rect(x, y, x + w, y + h);
		c.drawBitmap(texture, src, dst, painter);
//		texture.bind(gl2);
//		float u = index / (float)hMax;
//		float v = (index + 1) / (float)hMax;
//		gl2.glPushMatrix();
//		gl2.glEnable(GL2.GL_TEXTURE_2D);		
//		gl2.glTranslatef(x, y, 0);
//		gl2.glDisable(GL2.GL_TEXTURE_2D);		
//		gl2.glPopMatrix();				
	}

}
