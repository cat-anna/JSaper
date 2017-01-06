package com.Multisaper.Core.Interfaces;

public interface DrawableSpriteTexture {
	public void Render(GUIRenderer Renderer, int x, int y, int w, int h, int index);
	public void Render(GUIRenderer Renderer, int x, int y, int w, int h, byte[] indexes);
}
