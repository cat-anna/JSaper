package com.Multisaper.Core.Common;


import com.Multisaper.Core.Interfaces.DrawableSpriteTexture;
import com.Multisaper.Core.Interfaces.GUIRenderer;
import com.Multisaper.Core.Interfaces.GuiSkin;
import com.Multisaper.Core.Logic.Board.FieldSelection;

public class ClassicGuiSkin extends GuiSkin {
    final static float BaseLightShadowColor[] = {0.9f, 0.9f, 0.9f};
    final static float BaseDarkShadowColor[] = {0.5f, 0.5f, 0.5f};
    final static float BaseInnerColor[] = {1, 1, 0};
    final static float BaseOuterColor[] = {0, 1, 1};
    final static float BaseFaceColor[] = {0.7f, 0.7f, 0.7f};

    final static int BaseOuterBorderSize = 2;
    final static int BaseUpperMenuHeight = 50;
    final static int BaseUpperMenuMarginSize = 5;
    final static int BaseMenuBorderSize = 2;

    final static int BaseBoardBorderSize = 2;
    final static int BaseBoardMarginSize = 5;

    final static int BaseFieldSize = 16;

    public ClassicGuiSkin(GUIRenderer guir, DrawableSpriteTexture fieldTexture, DrawableSpriteTexture counter, int SizeMultiplier) {
        super(guir);
        FieldTexture = fieldTexture;
        Counter = counter;

        LightShadowColor = BaseLightShadowColor;
        DarkShadowColor = BaseDarkShadowColor;
        InnerColor = BaseInnerColor;
        OuterColor = BaseOuterColor;
        FaceColor = BaseFaceColor;
        OuterBorderSize = SizeMultiplier * BaseOuterBorderSize;
        UpperMenuHeight = SizeMultiplier * BaseUpperMenuHeight;
        UpperMenuMarginSize = SizeMultiplier * BaseUpperMenuMarginSize;
        MenuBorderSize = SizeMultiplier * BaseMenuBorderSize;
        BoardBorderSize = SizeMultiplier * BaseBoardBorderSize;
        BoardMarginSize = SizeMultiplier * BaseBoardMarginSize;
        FieldSize = SizeMultiplier * BaseFieldSize;
    }

    float LightShadowColor[];
    float DarkShadowColor[];
    float InnerColor[];
    float OuterColor[];
    float FaceColor[];
    int OuterBorderSize;
    int UpperMenuHeight;
    int UpperMenuMarginSize;
    int MenuBorderSize;
    int BoardBorderSize;
    int BoardMarginSize;
    int FieldSize;

    private GUISize TimeCounterPos = new GUISize(1, 1);
    private GUISize BombCounterPos = new GUISize(1, 1);
    private GUIBox UpperMenu = new GUIBox();

    private DrawableSpriteTexture FieldTexture, Counter;

    public int GetFieldSize() {
        return (int) (FieldSize * FieldSizeFactor);
    }

    public void ResetSize() {
        int H = getH();
        int W = getW();

        UpperMenu.x = UpperMenuMarginSize + OuterBorderSize;
        UpperMenu.y = UpperMenuMarginSize + OuterBorderSize;
        if (ConstSize) {
            UpperMenu.w = W - UpperMenu.x;
        } else {
            UpperMenu.w = UpperMenu.x + BoardSize.Width - BoardBorderSize;
        }
        UpperMenu.h = UpperMenu.y + UpperMenuHeight;

        BoardBorder.x = OuterBorderSize + BoardMarginSize;
        BoardBorder.y = UpperMenu.h + 2 * BoardMarginSize;
        if (ConstSize) {
            BoardBorder.w = W - BoardBorder.x;
            BoardBorder.h = H - 2 * BoardMarginSize - OuterBorderSize;
        } else {
            BoardBorder.w = BoardBorder.x + BoardSize.Width - BoardBorderSize;
            BoardBorder.h = BoardSize.Height + 2 * BoardBorderSize + 1;//BoardBorder.y +  - BoardBorderSize;
        }
//		BoardSize.Height
        BoardDelta.Width = BoardBorder.x + BoardBorderSize;
        BoardDelta.Height = BoardBorder.y + BoardBorderSize + 1;

        TimeCounterPos.Height = BombCounterPos.Height = UpperMenuMarginSize + OuterBorderSize + (UpperMenuHeight - 13) / 2;
        TimeCounterPos.Width = UpperMenuMarginSize + OuterBorderSize + 20;
        BombCounterPos.Width = W - (UpperMenuMarginSize + OuterBorderSize + 20) - 3 * 13;
    }

    @Override
    public void GetGUISize(GUIBorders borders, GUISize BoardSize) {
        ResetSize();
        if (ConstSize) {
//			borders.Left = OuterBorderSize + BoardMarginSize + BoardBorderSize;
//			borders.Right = borders.Left;
//			borders.Bottom = borders.Left;
//			borders.Top = OuterBorderSize + UpperMenuMarginSize * 2 + UpperMenuHeight + BoardMarginSize + BoardBorderSize;
        } else {
            borders.Left = OuterBorderSize + BoardMarginSize + BoardBorderSize;
            borders.Right = borders.Left;
            borders.Bottom = borders.Left;
            borders.Top = OuterBorderSize + UpperMenuMarginSize * 2 + UpperMenuHeight + BoardMarginSize + BoardBorderSize;

        }
        BoardSize.Set(this.BoardSize);
    }

    // -------------------------------------------------------------------

    @Override
    public void CoveredFiled(int x, int y) {
        int fs = GetFieldSize();
        x = x * fs + BoardMove.Width + BoardDelta.Width;
        y = y * fs + BoardMove.Height + BoardDelta.Height;
        FieldTexture.Render(guir, x, y, fs, fs, 0);
    }

    @Override
    public void UncoveredField(int x, int y, int BombCount) {
        int fs = GetFieldSize();
        x = x * fs + BoardMove.Width + BoardDelta.Width;
        y = y * fs + BoardMove.Height + BoardDelta.Height;
        FieldTexture.Render(guir, x, y, fs, fs, 15 - BombCount);
    }

    @Override
    public void CheckedField(int x, int y, FieldSelection CheckMode) {
        int fs = GetFieldSize();
        x = x * fs + BoardMove.Width + BoardDelta.Width;
        y = y * fs + BoardMove.Height + BoardDelta.Height;
        int index;
        switch (CheckMode) {
            case Bomb:
                index = 1;
                break;
            case None:
                index = 0;
                break;
            case Unknown:
                index = 2;
                break;
            default:
                index = 6;
                break;
        }
        FieldTexture.Render(guir, x, y, fs, fs, index);
    }

    @Override
    public void ExplodedField(int x, int y) {
        int fs = GetFieldSize();
        x = x * fs + BoardMove.Width + BoardDelta.Width;
        y = y * fs + BoardMove.Height + BoardDelta.Height;
        FieldTexture.Render(guir, x, y, fs, fs, 3);
    }

    @Override
    public void RenderMainElements() {
        guir.FilledBorder(new GUIBox(0, 0, getW(), getH()), OuterBorderSize, FaceColor, DarkShadowColor, LightShadowColor);
        guir.RenderBorders(UpperMenu, MenuBorderSize, DarkShadowColor, LightShadowColor);
        guir.RenderBorders(BoardBorder, BoardBorderSize, DarkShadowColor, LightShadowColor);
    }

    private byte ProcessIndex(char c) {
        byte index = 1;
        if (c >= '0' && c <= '9') {
            index = (byte) (11 - (c - '0'));
        } else if (c == '-') index = 0;
        return index;
    }

    @Override
    public void RenderCounters(int Time, int Bombs) {
        String Tstr = Integer.toString(Time);
        String Bstr = Integer.toString(Bombs);

        while (Tstr.length() < 3) Tstr = " " + Tstr;
        while (Bstr.length() < 3) Bstr = " " + Bstr;
        byte[] temp = new byte[3];

        for (int i = 0; i < 3; ++i) temp[i] = ProcessIndex(Tstr.charAt(i));
        Counter.Render(guir, BombCounterPos.Width, BombCounterPos.Height, 13, 23, temp);
        for (int i = 0; i < 3; ++i) temp[i] = ProcessIndex(Bstr.charAt(i));
        Counter.Render(guir, TimeCounterPos.Width, TimeCounterPos.Height, 13, 23, temp);
    }

}
