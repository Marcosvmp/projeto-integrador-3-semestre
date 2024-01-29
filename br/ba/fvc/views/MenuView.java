package br.ba.fvc.views;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import br.ba.fvc.controller.FuncionarioController;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.net.URL;

public class MenuView {

	private JFrame frame;
	public FuncionarioController user;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuView window = new MenuView(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MenuView(FuncionarioController fields) {
		this.user = fields;
		initialize();
	}

	private void windowEmployee() {
		FuncionarioView windowEmployee = new FuncionarioView(this.user);
		windowEmployee.setVisible(true);
		frame.dispose();
	}

	private void windowVehicle() {
		VeiculoView windowVehicle = new VeiculoView(this.user);
		windowVehicle.setVisible(true);
		frame.dispose();
	}

	private void windowCity() {
		CidadeView windowCity = new CidadeView(this.user);
		windowCity.setVisible(true);
		frame.dispose();
	}

	private void windowRouter() {
		RotaView windowRouter = new RotaView(this.user);
		windowRouter.setVisible(true);
		frame.dispose();
	}

	private void windowSale() {
		VendaView windowSale = new VendaView(this.user);
		windowSale.setVisible(true);
		frame.dispose();
	}
	
	private void windowLogin() {
		LoginView windowLogin = new LoginView();
		windowLogin.setVisible(true);
		frame.dispose();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 337);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		JLabel lblNewLabel = new JLabel("Funcionario: " + this.user.getLogado().toUpperCase());
		lblNewLabel.setBounds(136, 48, 248, 14);
		frame.getContentPane().add(lblNewLabel);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 73, 414, 2);
		frame.getContentPane().add(separator);

		JButton btn_funcionario = new JButton("");
		if (!this.user.getCargoLogado().equals("ADMINISTRADOR")) {
			btn_funcionario.setBackground(Color.gray);
			btn_funcionario.setToolTipText("Você não ter permissão para acessar o modulo de funcionário.");
		}
		btn_funcionario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_funcionario.setEnabled(true);
				if (user.getCargoLogado().equals("ADMINISTRADOR")) {
					btn_funcionario.setEnabled(false);
					windowEmployee();
				}
			}
		});
		URL funcionario_image = this.getClass().getResource("/public/funcionario_menu.png");
		btn_funcionario.setIcon(new ImageIcon(funcionario_image));
		btn_funcionario.setBounds(20, 99, 97, 73);
		frame.getContentPane().add(btn_funcionario);

		JButton btn_veiculo = new JButton("");
		if (!this.user.getCargoLogado().equals("ADMINISTRADOR")) {
			btn_veiculo.setBackground(Color.gray);
			btn_veiculo.setToolTipText("Você não ter permissão para acessar o modulo de veiculo.");
		}
		btn_veiculo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_funcionario.setEnabled(true);
				if (user.getCargoLogado().equals("ADMINISTRADOR")) {
					btn_funcionario.setEnabled(false);
					windowVehicle();
				}
			}
		});
		URL onibus_image = this.getClass().getResource("/public/veiculo_menu.png");
		btn_veiculo.setIcon(new ImageIcon(onibus_image));
		btn_veiculo.setBounds(20, 216, 97, 73);
		frame.getContentPane().add(btn_veiculo);

		JButton btn_cidade = new JButton("");
		if (!this.user.getCargoLogado().equals("ADMINISTRADOR")) {
			btn_cidade.setBackground(Color.gray);
			btn_cidade.setToolTipText("Você não ter permissão para acessar o modulo de cidade.");
		}
		btn_cidade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_funcionario.setEnabled(true);
				if (user.getCargoLogado().equals("ADMINISTRADOR")) {
					btn_funcionario.setEnabled(false);
					windowCity();
				}
			}
		});
		URL cidade_image = this.getClass().getResource("/public/cidade_menu.png");
		btn_cidade.setIcon(new ImageIcon(cidade_image));
		btn_cidade.setBounds(300, 99, 97, 73);
		frame.getContentPane().add(btn_cidade);

		JButton btn_rota = new JButton("");
		if (!this.user.getCargoLogado().equals("ADMINISTRADOR")) {
			btn_rota.setBackground(Color.gray);
			btn_rota.setToolTipText("Você não ter permissão para acessar o modulo de rota.");
		}
		btn_rota.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_funcionario.setEnabled(true);
				if (user.getCargoLogado().equals("ADMINISTRADOR")) {
					btn_funcionario.setEnabled(false);
					windowRouter();
				}
			}
		});
		URL rota_image = this.getClass().getResource("/public/rota_menu.png");
		btn_rota.setIcon(new ImageIcon(rota_image));
		btn_rota.setBounds(300, 216, 97, 73);
		frame.getContentPane().add(btn_rota);

		JButton btn_venda = new JButton("");
		btn_venda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowSale();
			}
		});
		URL venda_image = this.getClass().getResource("/public/venda_menu.png");
		btn_venda.setIcon(new ImageIcon(venda_image));
		btn_venda.setBounds(165, 139, 97, 73);
		frame.getContentPane().add(btn_venda);

		JButton btn_logout = new JButton("Sair");
		btn_logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowLogin();
			}
		});
		btn_logout.setBounds(337, 11, 89, 23);
		frame.getContentPane().add(btn_logout);

		frame.setLocationRelativeTo(frame);
	}

	public void setVisible(Boolean visible) {
		frame.setVisible(visible);
	}
}
