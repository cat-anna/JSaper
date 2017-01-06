package com.JSaper.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;


import com.JSaper.Controller.PCController;
import com.JSaper.Util.ExtensionFilter;
import com.JSaper.Util.SpriteTexture;

import com.Multisaper.Core.Common.ClassicGuiSkin;
import com.Multisaper.Core.Common.GUIBorders;
import com.Multisaper.Core.Common.GUISize;
import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.ConnectionException;
import com.Multisaper.Core.Interfaces.Controller.LoginFailureException;
import com.Multisaper.Core.Interfaces.Controller.NotLoggedInException;
import com.Multisaper.Core.Interfaces.Controller.OutOfBoardException;
import com.Multisaper.Core.Interfaces.Controller.TheOneSaperMistakeException;
import com.Multisaper.Core.Interfaces.Controller.UnableToCreateServerException;
import com.Multisaper.Core.Interfaces.GuiSkin;
import com.Multisaper.Core.Logic.Board;
import com.jogamp.opengl.util.FPSAnimator;

public class MainForm extends JFrame implements com.Multisaper.Core.Interfaces.MainWindow, GLEventListener, WindowListener {
	private static final long serialVersionUID = -83350924438890944L;
	
	public static class ExitFunExeption extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public JFileChooser fileChooser = null;

	private StatisticsForm.GLGUIRenderer guir = null;
	private GuiSkin skin = null;
	private GLJPanel glPanel = null;

	private boolean m_EnableRendering = false;
	private boolean InGame = false;
	private boolean GameDisabled = false;

	private GUIBorders borders = new GUIBorders();
	private Point BoardSize = new Point();

	private JMenuItem mnLogin;

	private void DisableRendering() {
		m_EnableRendering = false;
	}

	private void EnableRendering() {
		m_EnableRendering = true;
	}

	private Point TransfromCoords(int x, int y) throws OutOfBoardException {
		if (x < 0 || y < 0)
			throw new OutOfBoardException();
		x -= borders.Left;
		y -= borders.Top;
		x /= skin.GetFieldSize();
		y /= skin.GetFieldSize();
		if (x >= BoardSize.x || y >= BoardSize.y)
			throw new OutOfBoardException();
		return new Point(x, y);
	}

	private void OnNewGame(ActionEvent arg0) {
		DisableRendering();
		NewGameFrame.DialogData Data = NewGameFrame.OpenDialog(MainForm.this);
		if (Data == null)
			return;
		Controller.getInstance().NewGame(Data.Width, Data.Height, Data.Bombs);
		EnableRendering();
	}

	private void OnNewMultiGame(ActionEvent arg0) {
		DisableRendering();
		try {
			ForceLoginStatus();
			NewMultiGame.DialogData ServData = NewMultiGame
					.OpenDialog(MainForm.this);
			if (ServData == null)
				throw new ExitFunExeption();
			NewGameFrame.DialogData NewGame = NewGameFrame
					.OpenDialog(MainForm.this);
			if (NewGame == null)
				throw new ExitFunExeption();
			Controller.getInstance()
					.CreateMultiplayerGame(ServData.Name, ServData.Port,
							NewGame.Width, NewGame.Height, NewGame.Bombs);
		} catch (ExitFunExeption e) {
		} catch (UnableToCreateServerException e) {
			JOptionPane.showMessageDialog(this, "B��d uruchamiania serwera!",
					"MultiSaper", JOptionPane.PLAIN_MESSAGE);
		} catch (NotLoggedInException e) {
			JOptionPane.showMessageDialog(this, "Musisz by� zalogowany!",
					"MultiSaper", JOptionPane.PLAIN_MESSAGE);
		}
		EnableRendering();
	}

	private void ForceLoginStatus() throws ExitFunExeption {
		if (Controller.getInstance().isConnected())
			return;
		int r = JOptionPane.showConfirmDialog(this,
				"Akcja wymaga zalogowania. Czy chcesz to teraz zrobi�?",
				"MultiSaper", JOptionPane.YES_NO_OPTION);

		if (r == JOptionPane.YES_OPTION)
			OnLoginClick(null);

		if (!Controller.getInstance().isConnected())
			throw new ExitFunExeption();
		return;
	}

	private void OnJoinMultiGame(ActionEvent arg0) {
		DisableRendering();
		try {
			ForceLoginStatus();
			NewMultiGame.DialogData ServData = NewMultiGame.OpenDialog(this);
			if (ServData == null)
				throw new ExitFunExeption();
			Controller.getInstance().JoinMultiplayerGame(ServData.IP,
					ServData.Port);
		} catch (ExitFunExeption e) {
		} catch (NotLoggedInException e) {
			JOptionPane.showMessageDialog(this, "Musisz by� zalogowany!",
					"MultiSaper", JOptionPane.PLAIN_MESSAGE);
		} catch (ConnectionException e) {
			JOptionPane.showMessageDialog(this,
					"Nie mo�esz do��cza� do gry wi�cej niz raz!", "MultiSaper",
					JOptionPane.PLAIN_MESSAGE);
		}
		EnableRendering();
	}

	private void OnStatisticsClick(ActionEvent arg0) {
		try {
			ForceLoginStatus();
			StatisticsForm.OpenDialog(this);
		} catch (ExitFunExeption e) {
		}
	}

	class LoginAction implements Runnable {
		private String LogIn, Password;
		public LoginAction(String logIn, String password) {
			super();
			LogIn = logIn;
			Password = password;
		}

		@Override
		public void run() {
			GameDisabled = true;
			try {
				try {
					Controller.getInstance().LogIn(LogIn, Password);
				} catch (LoginFailureException e) {
					int r = JOptionPane
							.showConfirmDialog(
									MainForm.this,
									"Z�a nazwa u�ytkownika lub has�o. Czy chcesz za�o�y� nowe konto?",
									"MultiSaper", JOptionPane.YES_NO_OPTION);
					if (r == JOptionPane.YES_OPTION) {
						Controller.getInstance().CreateUser(LogIn, Password);
					}
				}
			} catch (LoginFailureException e) {
				JOptionPane.showMessageDialog(MainForm.this,
						"U�ytkownik o tej nazwie ju� istnieje");
			} catch (ConnectionException e) {
				JOptionPane.showMessageDialog(MainForm.this, "B��d po��czenia z serwerem");
			} 
			if (Controller.getInstance().isConnected()) {
				JOptionPane.showMessageDialog(MainForm.this, "Zalogowano");
			}
			GameDisabled = false;
		}
	}
	
	private void OnLoginClick(ActionEvent arg0) {
		if (Controller.getInstance().isConnected()) {
			int r = JOptionPane.showConfirmDialog(this,
					"Jeste� zalogowany. Czy chcesz si� wylogowa�?",
					"MultiSaper", JOptionPane.YES_NO_OPTION);

			if (r == JOptionPane.YES_OPTION) {
				Controller.getInstance().LogOff();
			}
			return;
		}

		DisableRendering();
		try {
			LogInForm.DialogData LoginData = LogInForm.OpenDialog(this);
			if (LoginData == null)
				throw new ExitFunExeption();
			new Waiter(new LoginAction(LoginData.LogIn, LoginData.Password));
		} catch (ExitFunExeption e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		EnableRendering();
	}

	public MainForm() {
		fileChooser = new JFileChooser();
		ExtensionFilter type1 = new ExtensionFilter("Saper saved game", ".ssg");
		fileChooser.addChoosableFileFilter(type1);

		setTitle("Saper");
		setResizable(false);
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addWindowListener(this);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu mnGra = new JMenu("Gra");
		menuBar.add(mnGra);

		JMenuItem mntmNowaGra = new JMenuItem("Nowa gra");

		mntmNowaGra.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainForm.this.OnNewGame(arg0);
			}
		});
		mnGra.add(mntmNowaGra);

		JSeparator separator = new JSeparator();
		mnGra.add(separator);

		JMenuItem mntmZapiszGr = new JMenuItem("Zapisz gr\u0119");
		mnGra.add(mntmZapiszGr);
		mntmZapiszGr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DisableRendering();
				int returnVal = fileChooser.showSaveDialog(MainForm.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						Controller.getInstance().SaveGameToFile(
								fileChooser.getSelectedFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				EnableRendering();
			}
		});

		JMenuItem mntmWczytajGr = new JMenuItem("Wczytaj gr\u0119");
		mnGra.add(mntmWczytajGr);
		mntmWczytajGr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DisableRendering();
				int returnVal = fileChooser.showOpenDialog(MainForm.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						Controller.getInstance().LoadGameFromFile(
								fileChooser.getSelectedFile());
					} catch (IOException | ClassNotFoundException e) {
					}
				}
				EnableRendering();
			}
		});

		JSeparator separator_2 = new JSeparator();
		mnGra.add(separator_2);

		JMenuItem mntmNewMultiplayerGame = new JMenuItem("Nowa gra multiplayer");
		mnGra.add(mntmNewMultiplayerGame);
		mntmNewMultiplayerGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainForm.this.OnNewMultiGame(arg0);
			}
		});
		JMenuItem mntmJoinMultiplayerGame = new JMenuItem(
				"Do��cz do istniej�cej gry");
		mnGra.add(mntmJoinMultiplayerGame);
		mntmJoinMultiplayerGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainForm.this.OnJoinMultiGame(arg0);
			}
		});

		mnGra.add(new JSeparator());
		mnLogin = new JMenuItem("Zaloguj");
		mnGra.add(mnLogin);
		mnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OnLoginClick(arg0);
			}
		});

		mnGra.add(new JSeparator());
		JMenuItem mntmZakocz = new JMenuItem("Zako\u0144cz");
		mntmZakocz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DisableRendering();
				setVisible(false);
			}
		});
		mnGra.add(mntmZakocz);

		JMenu mnStatystyki = new JMenu("Statystyki");
		menuBar.add(mnStatystyki);

		JMenuItem ShowStats = new JMenuItem("Poka�");
		ShowStats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				OnStatisticsClick(arg0);
			}
		});
		mnStatystyki.add(ShowStats);

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);

		glPanel = new GLJPanel(glcapabilities);
		glPanel.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
				if (!InGame || GameDisabled)
					return;
				try {
					Point p = TransfromCoords(arg0.getX(), arg0.getY());
					if (arg0.getButton() == MouseEvent.BUTTON3)
						Controller.getInstance().BoardSelectField(p.x, p.y);
					else
						Controller.getInstance().BoardClickField(p.x, p.y);
				} catch (TheOneSaperMistakeException e) {
					Controller.getInstance().PlayerDied();
				} catch (Exception e) {

				}
			}

			public void mousePressed(MouseEvent arg0) { }
			public void mouseExited(MouseEvent arg0)  { }
			public void mouseEntered(MouseEvent arg0) { }
			public void mouseClicked(MouseEvent arg0) { }
		});
		glPanel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent arg0) { }
			public void mouseDragged(MouseEvent arg0) { }
		});
		getContentPane().add(glPanel, BorderLayout.CENTER);

		FPSAnimator animator = new FPSAnimator(glPanel, 60);
		animator.start();

		guir = new StatisticsForm.GLGUIRenderer();
		RecreateSkin();
	}
	
	void RecreateSkin() {
		SpriteTexture FieldTexture = null;
		SpriteTexture Counter = null;
		if(guir.getGl() != null) {
			FieldTexture = new SpriteTexture(guir.getGl(), "Board.bmp", 16);
			Counter = new SpriteTexture(guir.getGl(), "Counter.bmp", 12);
		}
		skin = new ClassicGuiSkin(guir, FieldTexture, Counter, 1);
		Board b = PCController.getInstance().GetBoard();
		skin.SetBoardSize(b.getWidth(), b.getHeight(), 0, 0);
	}

	public void Show() {
		glPanel.addGLEventListener(this);
		setVisible(true);
		m_EnableRendering = true;
	}

	@Override
	public void ResetWindow(int bw, int bh) {
		skin.SetBoardSize(bw, bh, 0, 0);
		BoardSize = new Point(bw, bh);
		GUISize bs = new GUISize(1, 1);
		skin.GetGUISize(borders, bs);
		int w = borders.Left + borders.Right + bs.Width;
		int h = borders.Top + borders.Bottom + bs.Height;
		Dimension ind;
		getContentPane().setPreferredSize(ind = new Dimension(w, h));
		pack();
		Dimension outd = getContentPane().getSize();
		if (!ind.equals(outd)) {
			JOptionPane.showMessageDialog(this,
					"Tw�j ekran jest zbyt ma�y aby wy�wietli� ca�� plansz�!");
			Controller.getInstance().NewGame(10, 10, 10);
		}
		skin.ResetSize();
	}

	@Override
	public void OnGameEnd(EndGameAction action) {
		InGame = false;
		switch (action) {
		case PlayerDied:
			JOptionPane.showMessageDialog(this, "Koniec gry!", "Multisaper",
					JOptionPane.PLAIN_MESSAGE);
			break;
		case AllPlayersDied:
			break;
		case ConnectionDied:
			JOptionPane.showMessageDialog(this,
					"Utracono po��czenie z serwerem!", "Multisaper",
					JOptionPane.PLAIN_MESSAGE);
			Controller.getInstance().NewGame(10, 10, 10);
			break;
		case GameWon:
			JOptionPane.showMessageDialog(this, "Wygrana!", "Multisaper",
					JOptionPane.PLAIN_MESSAGE);
			break;
		default:
			break;
		}
	}

	@Override
	public void OnNewGame(NewGameAction action) {
		EnableRendering();
		InGame = true;
	}

	// Window events
	public void windowOpened(WindowEvent arg0) { }
	public void windowIconified(WindowEvent arg0) { }
	public void windowDeiconified(WindowEvent arg0) { }
	public void windowDeactivated(WindowEvent arg0) { }
	public void windowClosing(WindowEvent arg0) {
		Controller.getInstance().MainWindowClosed();
	}

	public void windowClosed(WindowEvent arg0) { }
	public void windowActivated(WindowEvent arg0) { }
	// ---------------RENDERING----------------------

	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width,
			int height) {
		GL2 gl2 = glautodrawable.getGL().getGL2();
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		GLU glu = new GLU();
		glu.gluOrtho2D(0.0f, width, 0.0f, height);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		gl2.glViewport(0, 0, width, height);
		guir.SetGL(gl2, width, height);
		RecreateSkin();
	}

	@Override
	public void init(GLAutoDrawable glautodrawable) {
		glautodrawable.getGL().setSwapInterval(1);
	}

	@Override
	public void dispose(GLAutoDrawable glautodrawable) {
	}

	@Override
	public void display(GLAutoDrawable glautodrawable) {
		if (!m_EnableRendering)
			return;
		try {
			GL2 gl2 = glautodrawable.getGL().getGL2();
			gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
			gl2.glLoadIdentity();
			Controller c = Controller.getInstance();
			guir.DoRender(c.GetBoard(), skin);
		} catch(Exception e) {
			e.printStackTrace();
			m_EnableRendering = false;
		}
	}
}
