package com.JSaper.GUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class LogInForm extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private DialogData Output;
	private JTextField LoginField;
	private JPasswordField passwordField;
	
	public static class DialogData {
		String LogIn, Password;
	};

	public static DialogData OpenDialog(JFrame Owner) {
		LogInForm ngf = new LogInForm(Owner, new DialogData());
		ngf.setModalityType(ModalityType.APPLICATION_MODAL);
		ngf.setVisible(true);
		return ngf.Output;
	}

	public LogInForm(JFrame Owner, DialogData dd) {
		super(Owner);		
		Output = dd;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 248, 178);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Nazwa u\u017Cytkownika");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(10, 33, 98, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Has\u0142o");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(45, 59, 63, 14);
		contentPane.add(lblNewLabel_1);
		
		JButton OKButton = new JButton("OK");
		OKButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String l = LoginField.getText();
				@SuppressWarnings("deprecation")
				String p = passwordField.getText();
				
				if(l.isEmpty() || p.isEmpty()) {
					JOptionPane.showMessageDialog(LogInForm.this, "Wszystkie pola s� wymagane", "Multisaper",
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				LogInForm.this.Output.LogIn = l;
				LogInForm.this.Output.Password = p;
				setVisible(false);
			}
		});
		OKButton.setBounds(118, 110, 97, 23);
		contentPane.add(OKButton);
		
		JButton CancelButton = new JButton("Anuluj");
		CancelButton.setBounds(10, 110, 98, 23);
		CancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				LogInForm.this.Output = null;
				setVisible(false);
			}
		});
		contentPane.add(CancelButton);
		
		LoginField = new JTextField();
		LoginField.setBounds(118, 30, 84, 20);
		contentPane.add(LoginField);
		LoginField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(118, 56, 84, 20);
		contentPane.add(passwordField);
	}
}
