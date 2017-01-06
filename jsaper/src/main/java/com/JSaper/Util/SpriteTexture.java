package com.JSaper.Util;

import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL2;

import com.Multisaper.Core.Interfaces.DrawableSpriteTexture;
import com.Multisaper.Core.Interfaces.GUIRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class SpriteTexture implements DrawableSpriteTexture {
	private Texture texture;
	private int hMax;
	
	public SpriteTexture(GL2 gl2, String FileName, int hMax) {
		this.hMax = hMax;
        try { 
        	texture = TextureIO.newTexture(new File(FileName), false); 
        	texture.setTexParameteri(gl2, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST); 
        	texture.setTexParameteri(gl2, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST); 
        	texture.setTexParameteri(gl2, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE); 
        	texture.setTexParameteri(gl2, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE); 
        } catch (IOException e) { 
            System.out.println("Unable to load texure: " + FileName);
        } 
	}
	
	public void Render(GUIRenderer Renderer, int x, int y, int w, int h, byte[] indexes) {
		for(int i = 0; i < indexes.length; ++i) {
			Render(Renderer, x, y, w, h, indexes[i]);
			x += w;
		}
	}
	
	public void Render(GUIRenderer Renderer, int x, int y, int w, int h, int index) {
		GL2 gl2 = (GL2)Renderer.GetInstance();
		texture.bind(gl2);
		float u = index / (float)hMax;
		float v = (index + 1) / (float)hMax;
		gl2.glPushMatrix();
		gl2.glEnable(GL2.GL_TEXTURE_2D);		
		gl2.glTranslatef(x, y, 0);
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glTexCoord2f(0,v);  gl2.glVertex2i(0, h);	
		gl2.glTexCoord2f(1,v);  gl2.glVertex2i(w, h);	
		gl2.glTexCoord2f(1,u);  gl2.glVertex2i(w, 0);			
		gl2.glTexCoord2f(0,u);  gl2.glVertex2i(0, 0);				
		gl2.glEnd();	
		gl2.glDisable(GL2.GL_TEXTURE_2D);		
		gl2.glPopMatrix();				
	}
}
