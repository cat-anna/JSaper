package com.JSaper.GUI;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.Multisaper.Core.Common.GUIBox;
import com.Multisaper.Core.DB.DBConn;
import com.Multisaper.Core.DB.DBConn.PlayeStats;
import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.GUIRenderer;
import com.Multisaper.Core.Interfaces.GuiSkin;
import com.Multisaper.Core.Logic.Board;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class StatisticsForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DialogData Output;
	
	JPanel plyerStatsPanel;
	
	public static class DialogData {
	};

	public static DialogData OpenDialog(JFrame Owner) {
		StatisticsForm ngf = new StatisticsForm(Owner, new DialogData());
		ngf.setModalityType(ModalityType.APPLICATION_MODAL);
		ngf.setVisible(true);
		return ngf.Output;
	}
	
	ChartPanel InitGameStats() {
		DefaultPieDataset dane = new DefaultPieDataset();
		dane.setValue("typ A", 20); //wartosci
		dane.setValue("typ B", 25);
		dane.setValue("typ C", 10);
		dane.setValue("typ D", 45);
		
		JFreeChart chart = ChartFactory.createPieChart
				("Wykres typu Pie ", // Tytu�
				dane, // Dane
				true, // Flaga - Legendy
				true, // Tultips � male opisy
				false // Configure chart to generate URLs?
			);
		return new ChartPanel(chart);
	}
	
	ChartPanel InitPlayerStats(String string) {
		DefaultPieDataset dane = new DefaultPieDataset();
		try {
			DBConn dbc = Controller.getInstance().getDBConnection();
			
			PlayeStats ps = dbc.getPlayerStats(Controller.getInstance().getPlayerName());
			dane.setValue("�mierci", ps.deaths); 
			dane.setValue("wygrane", ps.wins);
			dane.setValue("niekoko�czona rozgrywka",  ps.games - ps.deaths - ps.wins);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		JFreeChart chart = ChartFactory.createPieChart
				(string, // Tytu�
				dane, // Dane
				true, // Flaga - Legendy
				true, // Tultips � male opisy
				false // Configure chart to generate URLs?
			);
		return new ChartPanel(chart);
	}

	public StatisticsForm(JFrame Owner, DialogData dd) {
		super(Owner);		
		Output = dd;
		setTitle("Statystyki");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		tabbedPane.addTab("Statystyki gracza", null, InitPlayerStats("Statystyki gracza"), null);
		//tabbedPane.addTab("Statystyki Gry", null, InitGameStats(), null);
	}

	public static class GLGUIRenderer extends GUIRenderer {
        private com.jogamp.opengl.GL2 gl;
        private int w, h;

        public void SetGL(GL2 gl, int w, int h) {
            this.gl = gl;
            this.w = w;
            this.h = h;
        }

        public int getW() {
            return w;
        }

        public int getH() {
            return h;
        }

        public void QuickQuad(int x, int y, int w, int h) {
            gl.glPushMatrix();
            gl.glTranslatef(x, y, 0);
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2i(0, h);
            gl.glVertex2i(w, h);
            gl.glVertex2i(w, 0);
            gl.glVertex2i(0, 0);
            gl.glEnd();
            gl.glPopMatrix();
        }

        public void RenderBorders(GUIBox border, int size, float[] ColorLT, float[] ColorRB) {
            // left top shadow
            gl.glPushMatrix();
            gl.glTranslatef(border.x, border.y, 0);
            gl.glBegin(GL2.GL_QUAD_STRIP);
            gl.glColor3fv(ColorLT, 0);
            gl.glVertex2i(size, size);
            gl.glVertex2i(0, 0);
            gl.glVertex2i(size, border.h - size);
            gl.glVertex2i(0, border.h);
            gl.glVertex2i(border.w - size, border.h - size);
            gl.glVertex2i(border.w, border.h);
            gl.glEnd();
            // right bottom shadow
            gl.glBegin(GL2.GL_QUAD_STRIP);
            gl.glColor3fv(ColorRB, 0);
            gl.glVertex2i(size, size);
            gl.glVertex2i(0, 0);
            gl.glVertex2i(border.w - size, size);
            gl.glVertex2i(border.w, 0);
            gl.glVertex2i(border.w - size, border.h - size);
            gl.glVertex2i(border.w, border.h);
            gl.glEnd();
            gl.glPopMatrix();
        }

        public void DoRender(Board b, GuiSkin skin) {
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glTranslatef(0, getH(), 0);
            gl.glScalef(1, -1, 1);

            skin.RenderMainElements();
            if (b != null) {
    //			GUISize border = skin.getBoardDelta();

                b.EnumerateBoard(skin);

                skin.RenderCounters(Controller.getInstance().getTime(), b.getRemainBombs());
            }
            gl.glPopMatrix();
        }

        @Override
        public void FilledBorder(GUIBox border, int size, float[] Color, float[] ColorLT, float[] ColorRB) {
            RenderBorders(border, size, ColorLT, ColorRB);
            gl.glColor3fv(Color, 0);
            QuickQuad(border.x + size, border.y + size, border.w - 2 * size, border.h - 2 * size);
        }

        @Override
        public Object GetInstance() {
            return gl;
        }

        public GL2 getGl() {
            return gl;
        }

        public boolean IsReady() {
            return gl != null;
        }
    }
}
