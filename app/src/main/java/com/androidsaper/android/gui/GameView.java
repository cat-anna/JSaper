package com.androidsaper.android.gui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.Multisaper.Core.Common.ClassicGuiSkin;
import com.Multisaper.Core.Common.GUISize;
import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.TheOneSaperMistakeException;
import com.Multisaper.Core.Interfaces.DrawableSpriteTexture;
import com.Multisaper.Core.Interfaces.GuiSkin;
import com.Multisaper.Core.Logic.Board;

import com.androidsaper.R;

class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private GameThread thread;
	// Bitmap bitmapDroid;
	ADTSurfaceRenderer Renderer;
	GuiSkin skin;
	Display disp;

	boolean IsMultiGame;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);

		disp = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Renderer = new ADTSurfaceRenderer(this, disp);

		IsMultiGame = false;
		thread = null;
		setFocusable(true);
		RecreateSkin();
	}

	public void RecreateSkin() {
		DrawableSpriteTexture FieldTexture = null;
		DrawableSpriteTexture Counter = null;
		if (Renderer.IsReady()) {
			FieldTexture = new AdtBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.board), 16);
			Counter = new AdtBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.counter), 23);
		}
		skin = new ClassicGuiSkin(Renderer, FieldTexture, Counter, 2);
		skin.setConstSize(true);
		skin.setFieldSizeFactor(2);
		Board b = Controller.getInstance().GetBoard();
		skin.SetBoardSize(b.getWidth(), b.getHeight(), 10, 10);
	}

	boolean moved = false, InGame = true, Clicked = false, SelChange = false;
	long time = 0;
	Point ClickPoint = new Point();
	Point BoardPoint = new Point();
	
	public void SetGameState(boolean state) {
		Log.d("dupa", "game: " + state);
		InGame = state;
		IsMultiGame = Controller.getInstance().IsMultiGame();
	}

	Point transformPoint(int x, int y) {
		GUISize delta = skin.getBoardDelta();
		GUISize move = skin.getBoardMove();
		int fs = skin.GetFieldSize();
		x -= delta.Width;
		y -= delta.Height;
//		if(x < fs) x = -fs;
//		else
			x -= move.Width;	
//		if(y < fs) y = -fs;
//		else
			y -= move.Height;		
		x /= fs;
		y /= fs;
		return new Point(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int x = (int) event.getX();
		int y = (int) event.getY();
		final Point p = transformPoint(x, y);
		int act = event.getAction();
		long currtime = SystemClock.elapsedRealtime();
		Log.d("dupa", String.format("%d  %d %d", act, p.x, p.y));
		switch (act) {
		case MotionEvent.ACTION_UP: {
			Clicked = false;
			if (!moved) {
				if(!InGame) {
					if(IsMultiGame)
						Toast.makeText(getContext(), "You are dead!", Toast.LENGTH_SHORT).show();
					return true;
				}
				if(!SelChange) {
					new Thread(new Runnable() {
						public void run() {
							try {
								Controller.getInstance().BoardClickField(p.x, p.y);
							} catch (TheOneSaperMistakeException e) {
								Controller.getInstance().PlayerDied();
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
				time = currtime;
			} else {
			}
			moved = false;
			SelChange = false;
			break;
		}
		case MotionEvent.ACTION_DOWN: {
			Clicked = true;
			time = SystemClock.elapsedRealtime();
			BoardPoint = p;
			ClickPoint.x = x;
			ClickPoint.y = y;
			SelChange = false;
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			Log.d("dupa", String.format("dpos %s     %d  %d", String.valueOf(moved),  x - ClickPoint.x, y - ClickPoint.y));
			boolean tosmall = false;
			if(Math.abs(ClickPoint.x - x) < 15 && Math.abs(ClickPoint.y - y) < 15)
				tosmall = true;
			if(!tosmall) {
				if(moved) {
					int dx = x - ClickPoint.x;
					int dy = y - ClickPoint.y;
					synchronized (getHolder()) {
						skin.MoveBoard(dx, dy);
					}
				} 
				moved = true;
			}
			ClickPoint.x = x;
			ClickPoint.y = y;
			break;
		}

		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d("dupa", width + "-" + height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceDestroyed(holder);
		thread = new GameThread(getHolder());
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(thread != null) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			thread = null;
		}	
	}
	
	boolean RenderingEnabled = false;
	public void SetRenderingState(boolean state) {
		RenderingEnabled = state;
	}

	class GameThread extends Thread {
		private SurfaceHolder surfaceHolder;

		public GameThread(SurfaceHolder surfaceHolder) {
			this.surfaceHolder = surfaceHolder;
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				if(!RenderingEnabled) {
					continue;
				}
				Canvas c = null;
				long currtime = SystemClock.elapsedRealtime();
				if(!SelChange && Clicked && !moved && currtime - time > 1000) {
					time = currtime;
					SelChange = true;
					try {
						Controller.getInstance().BoardSelectField(BoardPoint.x, BoardPoint.y);
					} catch(Exception e) {
					}
				}
				
				try {
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {
						Renderer.SetCanvas(c);
						Renderer.DoRender(Controller.getInstance().GetBoard(), skin);
					}
				} finally {
					if (c != null) {
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
}