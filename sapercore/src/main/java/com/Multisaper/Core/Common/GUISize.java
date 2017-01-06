package com.Multisaper.Core.Common;

public final class GUISize {
	public int Height, Width;

	public GUISize(int w, int h) {
		Height = h;
		Width = w;
	}

	public GUISize(GUISize other) {
		Height = other.Height;
		Width = other.Width;
	}

	public void Set(GUISize other) {
		Height = other.Height;
		Width = other.Width;
	}
}