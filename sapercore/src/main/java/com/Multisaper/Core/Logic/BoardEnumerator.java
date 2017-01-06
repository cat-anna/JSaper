package com.Multisaper.Core.Logic;

public interface BoardEnumerator {
	void CoveredFiled(int x, int y);
	void UncoveredField(int x, int y, int BombCount);
	void CheckedField(int x, int y, Board.FieldSelection CheckMode);
	void ExplodedField(int x, int y);
}
