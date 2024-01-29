package br.ba.fvc.views;

import java.awt.EventQueue;
import javax.swing.ImageIcon;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.ResultSet;

import javax.swing.JTextField;

import br.ba.fvc.controller.FuncionarioController;
import br.ba.fvc.controller.GenericController;

import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JPasswordField;

public class LoginView {

	private JFrame frame;
	private JTextField email;
	private JPasswordField senha;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginView window = new LoginView();
					window.frame.setVisible(true);
					window.frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginView() {
		initialize();
	}

	private void View() {
		try {
			FuncionarioController fields = new FuncionarioController();

			char[] senhaChar = senha.getPassword();

			String convertChar = String.valueOf(senhaChar);

			Object[][] data = { { email.getName(), email.getText() }, { senha.getName(), convertChar }, };

			Boolean error = GenericController.validateFieldsEmpty(data);

			String password = GenericController.crypto(convertChar);

			if (!error) {

				fields.setEmail(email.getText());

				fields.setSenha(password);

				ResultSet result = fields.login();

				if (result.next()) {
					fields.setIdLogado(result.getString("id"));
					fields.setLogado(result.getString("nome"));
					fields.setNome(result.getString("nome"));
					fields.setCargo(result.getString("cargo"));
					fields.setCargoLogado(result.getString("cargo"));
					fields.setEmail(result.getString("email"));
					fields.setSenha(result.getString("senha"));

					MenuView windowMenu = new MenuView(fields);
					windowMenu.setVisible(true);

					frame.dispose();
				} else {
					JOptionPane.showMessageDialog(null, "LOGIN OU SENHA INVALIDOS!");
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 862, 613);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		JLabel label_email = new JLabel("Email");
		label_email.setFont(new Font("Tahoma", Font.PLAIN, 15));
		label_email.setBounds(293, 175, 141, 40);
		frame.getContentPane().add(label_email);

		email = new JTextField("");
		email.setName("email");
		email.setBounds(293, 219, 295, 34);
		frame.getContentPane().add(email);
		email.setColumns(10);

		JLabel label_senha = new JLabel("Senha");
		label_senha.setFont(new Font("Tahoma", Font.PLAIN, 15));
		label_senha.setBounds(293, 264, 46, 14);
		frame.getContentPane().add(label_senha);

		JButton btnEntrar = new JButton("Entrar");
		btnEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				View();
			}
		});

		btnEntrar.setBounds(293, 348, 295, 34);
		frame.getContentPane().add(btnEntrar);

		JLabel lblNewLabel_2 = new JLabel();
		lblNewLabel_2.setBackground(new Color(192, 192, 192));
		lblNewLabel_2.setForeground(new Color(192, 192, 192));

		lblNewLabel_2.setBounds(102, 26, 243, 59);

		frame.getContentPane().add(lblNewLabel_2);

		senha = new JPasswordField("");
		senha.setName("senha");
		senha.setBounds(293, 290, 295, 34);
		frame.getContentPane().add(senha);

		JLabel lblNewLabel_3 = new JLabel();
		URL urlToImage = this.getClass().getResource("/public/logo-dark.png");
		lblNewLabel_3.setIcon(new ImageIcon(urlToImage));

		frame.setLocationRelativeTo(frame);

		lblNewLabel_3.setBounds(324, 94, 522, 59);
		frame.getContentPane().add(lblNewLabel_3);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
}
