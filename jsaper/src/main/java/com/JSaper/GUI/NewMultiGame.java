package com.JSaper.GUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

public class NewMultiGame extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DialogData Output;
	
	public static class DialogData {
		String IP, Name;
		int Port;
	};

	public static DialogData OpenDialog(JFrame Owner) {
		DialogData dd = new DialogData();
		NewMultiGame ngf = new NewMultiGame(Owner, dd);
		ngf.setModalityType(ModalityType.APPLICATION_MODAL);
		ngf.setVisible(true);
		return ngf.Output;
	}
	private JSpinner PortSpinner;
	private JTextField IPTextField;
	
	private NewMultiGame(JFrame Owner, DialogData dd) {
		super(Owner);		
		Output = dd;
		setTitle("Nowa gra");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 248, 188);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		PortSpinner = new JSpinner();
		PortSpinner.setModel(new SpinnerNumberModel(1234, 1000, 65000, 1));
		PortSpinner.setBounds(103, 56, 73, 20);
		contentPane.add(PortSpinner);
		
		JLabel lblNewLabel = new JLabel("IP serwera");
		lblNewLabel.setBounds(26, 33, 67, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Port");
		lblNewLabel_1.setBounds(45, 59, 48, 14);
		contentPane.add(lblNewLabel_1);
		
		IPTextField = new JTextField();
		IPTextField.setText("127.0.0.1");
		IPTextField.setBounds(103, 30, 119, 20);
		contentPane.add(IPTextField);
		IPTextField.setColumns(10);

		JButton OKButton = new JButton("OK");
		OKButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				NewMultiGame.this.Output.Port = (Integer)PortSpinner.getValue();
				NewMultiGame.this.Output.IP = IPTextField.getText();
				setVisible(false);
			}
		});
		OKButton.setBounds(125, 112, 97, 23);
		contentPane.add(OKButton);
		
		JButton CancelButton = new JButton("Anuluj");
		CancelButton.setBounds(10, 112, 98, 23);
		CancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				NewMultiGame.this.Output = null;
				setVisible(false);
			}
		});
		contentPane.add(CancelButton);
	}
}
