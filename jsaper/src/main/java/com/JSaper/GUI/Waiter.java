package com.JSaper.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Waiter extends JDialog implements WindowListener {
	private static final long serialVersionUID = 1L;

	private Thread th;
	private Runnable action;
	
	public Waiter(Runnable action) {
		setTitle("Multisaper");
		setResizable(false);
		addWindowListener(this);
		setType(Type.POPUP);
		this.action = action;
		setBounds(100, 100, 280, 100);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				buttonPane.add(cancelButton);
				{
					JLabel lblNewLabel = new JLabel("\u0141\u0105czenie z baz\u0105. Prosz\u0119 czela\u0107...");
					lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
					getContentPane().add(lblNewLabel, BorderLayout.CENTER);
				}
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						th.interrupt();
						setVisible(false);
					}
				});
			}
		}
		setVisible(true);
		th = new Thread(new Runnable() {
			@Override
			public void run() {
				Waiter.this.action.run();
				setVisible(false);
			}
		});
		th.setDaemon(true);
		th.start();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		th.interrupt();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
