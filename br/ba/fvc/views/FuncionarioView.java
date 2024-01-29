package br.ba.fvc.views;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import br.ba.fvc.controller.FuncionarioController;
import br.ba.fvc.controller.GenericController;

import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.sql.ResultSet;

public class FuncionarioView {

	private JFrame frame;
	private JFrame frame_fields;
	private JTable table;
	private JTextField nome;
	private JFormattedTextField cpf;
	private JTextField email;
	private JComboBox<Object> cargo;
	private JTextField input_filtrar;
	private JPasswordField senha;
	public DefaultTableModel list;
	private FuncionarioController funcionario;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FuncionarioView window = new FuncionarioView(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @param fields
	 */
	@SuppressWarnings("serial")
	public FuncionarioView(FuncionarioController fields) {
		this.funcionario = new FuncionarioController();
		this.list = this.funcionario.listar();
		this.funcionario = fields;
		this.table = new JTable(this.list) {
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				return false;
			}
		};
		initialize();
	}

	private void home() {
		MenuView menu = new MenuView(this.funcionario);
		menu.setVisible(true);
		frame.dispose();
	}

	private void cadastrar() {
		try {

			char[] senhaChar = senha.getPassword();

			String convertChar = String.valueOf(senhaChar);

			Object[] passwordEmpty = { senha.getName(), convertChar };
			Object[] objectEmpty = {};
			if (cargo.getSelectedItem().equals("MOTORISTA")) {
				passwordEmpty = null;
			}

			Object[][] data = { { nome.getName(), nome.getText() }, { cpf.getName(), cpf.getValue() },
					{ cargo.getName(), cargo.getSelectedIndex() }, { email.getName(), email.getText() },
					passwordEmpty == null ? objectEmpty : passwordEmpty };

			Boolean error = GenericController.validateFieldsEmpty(data);

			String password = GenericController.crypto(convertChar);

			if (!error) {
				this.funcionario.setNome(nome.getText());
				this.funcionario.setCpf(cpf.getText());
				this.funcionario.setCargo(cargo.getSelectedItem().toString());
				this.funcionario.setEmail(email.getText());
				this.funcionario.setSenha(password);
				this.list = funcionario.incluir();

				if (this.list == null) {
					frame_fields.setVisible(true);
					return;
				}
				frame_fields.dispose();

				this.table.setModel(this.list);
				this.list.fireTableDataChanged();
				this.sizeCell();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void filtrar() {
		Object[][] data = { { input_filtrar.getName(), input_filtrar.getText() } };

		Boolean error = GenericController.validateFieldsEmpty(data);

		if (!error) {
			this.funcionario.setNome(input_filtrar.getText());

			this.list = this.funcionario.filtrar();

			input_filtrar.setText("");

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void limparFiltro() {
		this.list = funcionario.listar();
		this.list.fireTableDataChanged();
		this.sizeCell();
	}

	private void excluir() {
		if (this.table.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(null, "Selecione um registro na tabela para excluir!");
			return;
		}

		int dialog = JOptionPane.showConfirmDialog(null, "Deseja excluir esse funcionário?", "Excluir funcionário",
				JOptionPane.YES_NO_OPTION);

		if (dialog == 0) {
			String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

			if (id.equals(this.funcionario.getIdLogado())) {
				JOptionPane.showMessageDialog(null, "Não é possível excluir a conta logada!");
				return;
			}

			if (Integer.parseInt(id) == 1) {
				JOptionPane.showMessageDialog(null, "Não é possível excluir o (ADMINISTRADOR PADRÃO)");
				return;
			}

			this.list = this.funcionario.excluir(id);

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
			this.sizeCell();
		}
	}

	private void carregarCamposAlterar() {
		if (this.table.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(null, "Selecione um registro na tabela para alterar!");
			return;
		}

		String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

		if (Integer.parseInt(this.funcionario.getIdLogado()) != 1 && Integer.parseInt(id) == 1) {
			JOptionPane.showMessageDialog(null, "Você não tem permissão para alterar esse funcionário!");
			return;
		}

		campos("alterar");

		if (id.equals(this.funcionario.getIdLogado())) {
			cargo.setEnabled(false);
		}

		if (Integer.parseInt(id) == 1) {
			cargo.setEnabled(false);
		}

		ResultSet result = null;

		try {
			result = funcionario.carregaCamposAlterar(id);
			result.next();

			nome.setText(result.getString("nome"));
			cpf.setValue(result.getString("cpf"));
			cargo.setSelectedItem(result.getString("cargo"));
			email.setText(result.getString("email"));
			senha.setText(result.getString("senha"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void sizeCell() {
		this.table.getColumnModel().getColumn(0).setMaxWidth(80);
		this.table.getColumnModel().getColumn(1).setMaxWidth(400);
		this.table.getColumnModel().getColumn(2).setMaxWidth(140);
		this.table.getColumnModel().getColumn(3).setMaxWidth(520);
		this.table.getColumnModel().getColumn(4).setMaxWidth(170);
	}

	private void alterar() {
		String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

		char[] senhaChar = senha.getPassword();

		String convertChar = String.valueOf(senhaChar);

		Object[] passwordEmpty = { senha.getName(), convertChar };
		Object[] objectEmpty = {};
		
		if (cargo.getSelectedItem().equals("MOTORISTA")) {
			passwordEmpty = null;
		}

		Object[][] data = { { nome.getName(), nome.getText() }, { cpf.getName(), cpf.getValue() },
				{ cargo.getName(), cargo.getSelectedIndex() }, { email.getName(), email.getText() },
				passwordEmpty == null ? objectEmpty : passwordEmpty };

		Boolean error = GenericController.validateFieldsEmpty(data);

		String password = GenericController.crypto(convertChar);

		if (!error) {
			this.funcionario.setNome(nome.getText());
			this.funcionario.setCpf(cpf.getText());
			this.funcionario.setCargo(cargo.getSelectedItem().toString());
			this.funcionario.setEmail(email.getText());
			this.funcionario.setSenha(password);
			this.list = funcionario.alterar(id);

			frame_fields.dispose();

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
			this.sizeCell();
		}
	}

	private void campos(String criarOuAlterar) {
		try {
			frame_fields = new JFrame();
			frame_fields.setBounds(100, 100, 722, 353);
			frame_fields.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame_fields.getContentPane().setLayout(null);
			frame_fields.setResizable(false);
			frame_fields.setVisible(true);

			JSeparator separator = new JSeparator();
			separator.setBounds(10, 45, 686, 9);
			frame_fields.getContentPane().add(separator);

			JLabel nome_label = new JLabel("Nome completo");
			nome_label.setBounds(49, 53, 114, 14);
			frame_fields.getContentPane().add(nome_label);

			JLabel cpf_label = new JLabel("CPF");
			cpf_label.setBounds(49, 109, 46, 14);
			frame_fields.getContentPane().add(cpf_label);

			JLabel cargo_label = new JLabel("Cargo");
			cargo_label.setBounds(387, 109, 46, 14);
			frame_fields.getContentPane().add(cargo_label);

			JLabel label_email = new JLabel("Email");
			label_email.setBounds(49, 167, 46, 14);
			frame_fields.getContentPane().add(label_email);

			JLabel senha_label = new JLabel("Senha");
			senha_label.setBounds(387, 167, 46, 14);
			frame_fields.getContentPane().add(senha_label);

			nome = new JTextField("");
			nome.setName("nome");
			nome.getText();
			nome.setBounds(49, 78, 634, 20);
			frame_fields.getContentPane().add(nome);
			nome.setColumns(10);

			cpf = new JFormattedTextField(new MaskFormatter("###.###.###-##"));
			cpf.setName("cpf");
			cpf.setBounds(49, 130, 310, 20);
			frame_fields.getContentPane().add(cpf);
			cpf.setColumns(10);

			senha = new JPasswordField("");
			senha.setName("senha");
			senha.setBounds(389, 194, 294, 18);
			frame_fields.getContentPane().add(senha);

			String[] cargos = { "ADMINISTRADOR", "OPERADOR", "MOTORISTA" };
			cargo = new JComboBox<Object>(cargos);
			cargo.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if ((e.getStateChange() == ItemEvent.SELECTED)) {
						if (cargo.getSelectedItem().equals("MOTORISTA")) {
							senha.setBackground(Color.gray);
							senha.setEnabled(false);
							senha.setText("");
						} else {
							senha.setBackground(Color.white);
							senha.setEnabled(true);
						}
					}
				}
			});
			cargo.setEnabled(true);
			cargo.getModel().setSelectedItem("SELECIONE");
			cargo.setName("cargo");
			cargo.setBounds(387, 130, 294, 20);
			frame_fields.getContentPane().add(cargo);

			email = new JTextField("");
			email.setName("email");
			email.setBounds(49, 193, 310, 20);
			frame_fields.getContentPane().add(email);
			email.setColumns(10);

			JLabel dark_logo = new JLabel("");
			URL logo = this.getClass().getResource("/public/dark_logo_min.png");
			dark_logo.setIcon(new ImageIcon(logo));
			dark_logo.setBounds(302, 289, 114, 14);
			frame_fields.getContentPane().add(dark_logo);

			if (criarOuAlterar.equals("cadastrar")) {
				JLabel adicionar_func_label = new JLabel("Adicionar Funcionários");
				adicionar_func_label.setFont(new Font("Tahoma", Font.BOLD, 12));
				adicionar_func_label.setBounds(10, 13, 156, 14);
				frame_fields.getContentPane().add(adicionar_func_label);

				JButton adicionar = new JButton("Adicionar");
				adicionar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cadastrar();
					}
				});
				adicionar.setBounds(589, 256, 94, 23);
				frame_fields.getContentPane().add(adicionar);
			} else {
				JLabel atualizar_func_label = new JLabel("Atualizar Funcionários");
				atualizar_func_label.setFont(new Font("Tahoma", Font.BOLD, 12));
				atualizar_func_label.setBounds(10, 13, 156, 14);
				frame_fields.getContentPane().add(atualizar_func_label);

				JButton alterar = new JButton("Atualizar");
				alterar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						alterar();
					}
				});
				alterar.setBounds(589, 256, 94, 23);
				frame_fields.getContentPane().add(alterar);
			}

			JButton btn_cancelar = new JButton("Cancelar");
			btn_cancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame_fields.dispose();
				}
			});
			btn_cancelar.setBounds(476, 256, 89, 23);
			frame_fields.getContentPane().add(btn_cancelar);

			frame_fields.setLocationRelativeTo(frame);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		this.sizeCell();
		frame = new JFrame();
		frame.setBounds(100, 100, 862, 613);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 150, 662, 321);
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(this.table);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 44, 807, 7);
		frame.getContentPane().add(separator);

		JLabel listar_func_label = new JLabel("Listar  Funcionário");
		listar_func_label.setFont(new Font("Tahoma", Font.BOLD, 12));
		listar_func_label.setBounds(319, 19, 185, 14);
		frame.getContentPane().add(listar_func_label);

		input_filtrar = new JTextField("");
		input_filtrar.setName("pesquisar por nome");
		input_filtrar.setBounds(20, 87, 447, 20);
		frame.getContentPane().add(input_filtrar);
		input_filtrar.setColumns(10);

		JLabel btn_pesquisar = new JLabel("Pesquisar por nome");
		btn_pesquisar.setBounds(20, 65, 130, 14);
		frame.getContentPane().add(btn_pesquisar);

		JLabel img_listar_func = new JLabel("");
		URL urlToImage = this.getClass().getResource("/public/listagem_funcionario.png");
		img_listar_func.setIcon(new ImageIcon(urlToImage));
		img_listar_func.setBounds(711, 164, 106, 130);
		frame.getContentPane().add(img_listar_func);

		JLabel img_dark_logo = new JLabel("");
		URL logo = this.getClass().getResource("/public/dark_logo_min.png");
		img_dark_logo.setIcon(new ImageIcon(logo));
		img_dark_logo.setBounds(349, 519, 106, 14);
		frame.getContentPane().add(img_dark_logo);

		JButton alterar = new JButton("Alterar");
		alterar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carregarCamposAlterar();
			}
		});
		alterar.setBounds(692, 375, 125, 23);
		frame.getContentPane().add(alterar);

		JButton excluir = new JButton("Excluir");
		excluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				excluir();
			}
		});
		excluir.setBounds(692, 409, 125, 23);
		frame.getContentPane().add(excluir);

		JButton home = new JButton("Home");
		home.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				home();
			}
		});

		home.setBounds(7, 16, 89, 23);
		frame.getContentPane().add(home);

		JButton cadastrar = new JButton("Cadastrar");
		cadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				campos("cadastrar");
			}
		});
		cadastrar.setBounds(692, 341, 125, 23);
		frame.getContentPane().add(cadastrar);

		JButton pesquisar = new JButton("Pesquisar");
		pesquisar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filtrar();
			}
		});
		pesquisar.setBounds(480, 86, 125, 23);
		frame.getContentPane().add(pesquisar);

		JButton limpar_filtro = new JButton("Limpar filtro");
		limpar_filtro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				limparFiltro();
			}
		});
		limpar_filtro.setBounds(613, 86, 125, 23);
		frame.getContentPane().add(limpar_filtro);

		frame.setLocationRelativeTo(frame);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
}
