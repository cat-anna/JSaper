package com.JSaper.GUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class NewGameFrame extends JDialog {

	private JPanel contentPane;
	private DialogData Output;
	
	public static class DialogData {
		int Width, Height, Bombs;
	}

	public static DialogData OpenDialog(JFrame Owner) {
		DialogData dd = new DialogData();
		NewGameFrame ngf = new NewGameFrame(Owner, dd);
		ngf.setModalityType(ModalityType.APPLICATION_MODAL);
		ngf.setVisible(true);
		return ngf.Output;
	}
	
	private JSpinner BoardWidthSpinner;
	private JSpinner BoardHeightSpinner;
	private JSpinner BombCountSpinner;

	private NewGameFrame(JFrame Owner, DialogData dd) {
		super(Owner);		
		Output = dd;
		setTitle("Nowa gra");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 248, 247);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		BoardWidthSpinner = new JSpinner();
		BoardWidthSpinner.setModel(new SpinnerNumberModel(10, 5, 1000, 1));
		BoardWidthSpinner.setBounds(125, 30, 51, 20);
		contentPane.add(BoardWidthSpinner);
		
		BoardHeightSpinner = new JSpinner();
		BoardHeightSpinner.setModel(new SpinnerNumberModel(10, 10, 1000, 1));
		BoardHeightSpinner.setBounds(125, 56, 51, 20);
		contentPane.add(BoardHeightSpinner);
		
		BombCountSpinner = new JSpinner();
		BombCountSpinner.setModel(new SpinnerNumberModel(new Integer(10), new Integer(1), null, new Integer(1)));
		BombCountSpinner.setBounds(125, 97, 51, 20);
		contentPane.add(BombCountSpinner);		
		
		JLabel lblNewLabel = new JLabel("Szeroko\u015B\u0107");
		lblNewLabel.setBounds(10, 33, 83, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Wysoko\u015B\u0107");
		lblNewLabel_1.setBounds(10, 59, 83, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Ilo\u015B\u0107 bomb");
		lblNewLabel_2.setBounds(10, 100, 86, 14);
		contentPane.add(lblNewLabel_2);
		
		JButton OKButton = new JButton("OK");
		OKButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int w = (Integer)BoardWidthSpinner.getValue();
				int h = (Integer)BoardHeightSpinner.getValue();
				int c = (Integer)BombCountSpinner.getValue();
				NewGameFrame.this.Output.Width = w;
				NewGameFrame.this.Output.Height = h;
				NewGameFrame.this.Output.Bombs = c;
				setVisible(false);
			}
		});
		OKButton.setBounds(125, 163, 97, 23);
		contentPane.add(OKButton);
		
		JButton CancelButton = new JButton("Anuluj");
		CancelButton.setBounds(10, 163, 98, 23);
		CancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				NewGameFrame.this.Output = null;
				setVisible(false);
			}
		});
		contentPane.add(CancelButton);
	}
}
