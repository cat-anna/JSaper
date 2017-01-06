package com.Multisaper.Core.Logic;

import java.util.Random;
import java.util.Stack;

import com.Multisaper.Core.Interfaces.Controller.OutOfBoardException;
import com.Multisaper.Core.Interfaces.Controller.TheOneSaperMistakeException;

public class Board implements java.io.Serializable {
	private static final long serialVersionUID = 55L;

	public enum FieldSelection {
		None, Bomb, Unknown, MaxValue,
	}

	private class Field implements java.io.Serializable {
		private static final long serialVersionUID = 66L;
		public boolean HasBomb;
		public boolean IsUncovered;
		public int BombsAround;
		private FieldSelection SelectionMode = FieldSelection.None;

		public Field() {
			HasBomb = IsUncovered = false;
			BombsAround = 0;
		}

		public Field(boolean HasBomb) {
			this.HasBomb = HasBomb;
			IsUncovered = false;
			BombsAround = 0;
		}

		public void ToggleSelection() {
			SelectionMode = FieldSelection.values()[SelectionMode.ordinal() + 1];
			if (SelectionMode == FieldSelection.MaxValue)
				SelectionMode = FieldSelection.None;
		}

		public boolean IsSelected() {
			return SelectionMode != FieldSelection.None;
		}

		public FieldSelection getSelectionMode() {
			return SelectionMode;
		}

		public void SetAsBombMarked() {
			SelectionMode = FieldSelection.Bomb;
		}

		public boolean isBombMarked() {
			return SelectionMode == FieldSelection.Bomb;
		}
	}

	private int Width, Height, BombCount;
	private Field[] Board = null;
	private int FieldNeighborhood[] = null;
	private int RemainBombs, CoveredFields;
	
	public boolean isGameEnded() {
		return RemainBombs == 0 && CoveredFields <= BombCount;
	}

	public Board(int Width, int Height, int BombCount) {
		this.Width = Width;
		this.Height = Height;
		this.BombCount = BombCount;

		int FieldNeighborhood[] = new int[] { -Width - 1, -Width, -Width + 1,
				-1, 0, +1, Width - 1, Width, Width + 1, };
		this.FieldNeighborhood = FieldNeighborhood;

		FlushBoard();
	}

	private void AnalyzeBoard() {
		RemainBombs = BombCount;
		int BoardSize = Width * Height;
		CoveredFields = 0; 
		for (int i = 0; i < BoardSize; ++i) {
			Field f = Board[i];
			// if (!f.IsUncovered)
			// continue;
			int rootX = i % Width;

			if (!f.HasBomb)
				continue;
			else
				if(!f.IsUncovered)
					CoveredFields++;
				
			if (f.isBombMarked() || f.IsUncovered) {
				--RemainBombs;
				continue;
			}

			int loneless = 0, validFields = 0;
			for (int coord : FieldNeighborhood) {
				int linear = coord + i;
				int dx = Math.abs(rootX - linear % Width);
				if (linear < 0 || linear >= BoardSize || dx > 1 || linear == i)
					continue;

				validFields++;
				if (Board[linear].IsUncovered)
					++loneless;
			}
			if (loneless == validFields) {
				f.SetAsBombMarked();
				--RemainBombs;
			}
		}
	}

	public int getWidth() {
		return Width;
	}

	public int getHeight() {
		return Height;
	}

	public int getBombCount() {
		return BombCount;
	}

	public boolean isFieldClickable(int x, int y) {
		if (x >= Width || y >= Height)
			throw new OutOfBoardException();
		if (x < 0 || y < 0)
			throw new OutOfBoardException();
		int linear = y * Width + x;
		Field f = Board[linear];
		return !(f.IsUncovered || f.IsSelected());
	}

	public int ClickField(int x, int y) throws TheOneSaperMistakeException,
			OutOfBoardException {
		if (x >= Width || y >= Height)
			throw new OutOfBoardException();
		if (x < 0 || y < 0)
			throw new OutOfBoardException();
		int linear = y * Width + x;
		Field f = Board[linear];
		if (f.IsUncovered || f.IsSelected())
			return 0;

		if (f.HasBomb) {
			f.IsUncovered = true;
			throw new TheOneSaperMistakeException();
		}
		return UncoverArea(linear);
	}

	private void FlushBoard() {
		int BoardSize = Width * Height;
		Board = new Field[BoardSize];
		for (int i = 0; i < BoardSize; ++i)
			Board[i] = new Field();

		Random generator = new Random();

		int RemainBombs = BombCount;
		while (RemainBombs > 0) {
			int f = generator.nextInt() % BoardSize;
			if (f < 0)
				f *= -1;

			if (Board[f].HasBomb)
				continue;

			Board[f].HasBomb = true;
			--RemainBombs;

			int rootX = f % Width;
			for (int coord : FieldNeighborhood) {
				int linear = coord + f;
				int dx = Math.abs(rootX - linear % Width);
				if (linear < 0 || linear >= BoardSize || dx > 1)
					continue;
				if (Board[linear].HasBomb)
					continue;
				Board[linear].BombsAround += 1;
			}
		}
		this.RemainBombs = BombCount;
	}

	private int UncoverArea(int ilinear) {
		Stack<Integer> stack = new Stack<Integer>();
		stack.add(new Integer(ilinear));
		int count = 0;
		while (!stack.empty()) {
			Integer linear = stack.pop();
			Field f = Board[linear];

			if (f.IsUncovered || f.HasBomb)
				continue;
			f.IsUncovered = true;
			++count;
			if (f.BombsAround > 0)
				continue;

			int x = linear % Width;
			if (x > 0)
				stack.push(new Integer(linear - 1));
			if (linear >= Width)
				stack.push(new Integer(linear - Width));
			if (x + 1 < Width)
				stack.push(new Integer(linear + 1));
			if (linear + Width < Board.length)
				stack.push(new Integer(linear + Width));
		}
		AnalyzeBoard();
		return count;
	}

	public void EnumerateBoard(BoardEnumerator be) {
		int size = Width * Height;
		for (int i = 0; i < size; ++i) {
			Field f = Board[i];
			int x = i % Width;
			int y = i / Width;
			if (f.IsUncovered) {
				if (f.HasBomb)
					be.ExplodedField(x, y);
				else
					be.UncoveredField(x, y, f.BombsAround);
			} else {
				if (f.IsSelected())
					be.CheckedField(x, y, f.getSelectionMode());
				else
					be.CoveredFiled(x, y);
			}// CheckedField
		}
	}

	public void SelectField(int x, int y) {
		if (x >= Width || y >= Height)
			throw new OutOfBoardException();
		if (x < 0 || y < 0)
			throw new OutOfBoardException();
		int linear = y * Width + x;
		Field f = Board[linear];
		if (f.IsUncovered)
			return;

		switch (f.getSelectionMode()) {
		case None:
			RemainBombs--;
			break;
		case Bomb:
			RemainBombs++;
			break;
		default:
			break;
		}
		f.ToggleSelection();
	}

	public int getRemainBombs() {
		return RemainBombs;
	}
}
