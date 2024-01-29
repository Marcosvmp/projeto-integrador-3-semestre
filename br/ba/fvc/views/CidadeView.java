package br.ba.fvc.views;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.ResultSet;

import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import br.ba.fvc.controller.CidadeController;
import br.ba.fvc.controller.FuncionarioController;
import br.ba.fvc.controller.GenericController;

import javax.swing.JScrollPane;

public class CidadeView {

	private JFrame frame;
	private JFrame frame_fields;
	private JTextField input_filtrar;
	private JTable table;
	private JTextField nome;
	private JTextField uf;
	private JLabel label_cidade;
	private JLabel label_uf;
	public DefaultTableModel list;
	public FuncionarioController funcionario;
	public CidadeController cidade;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CidadeView window = new CidadeView(null);
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
	 */
	@SuppressWarnings("serial")
	public CidadeView(FuncionarioController fields) {
		this.cidade = new CidadeController();
		this.list = this.cidade.listar();
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
		Object[][] data = { { nome.getName(), nome.getText() }, { uf.getName(), uf.getText() } };

		Boolean error = GenericController.validateFieldsEmpty(data);

		if (!error) {
			this.cidade.setNome(nome.getText());
			this.cidade.setUf(uf.getText());
			this.list = cidade.incluir();

			if (this.list == null) {
				frame_fields.setVisible(true);
				return;
			}

			frame_fields.dispose();

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void filtrar() {

		Object[][] data = { { input_filtrar.getName(), input_filtrar.getText() } };

		Boolean error = GenericController.validateFieldsEmpty(data);

		if (!error) {
			this.cidade.setNome(input_filtrar.getText());

			this.list = this.cidade.filtrar();

			input_filtrar.setText("");

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void limparFiltro() {
		this.list = cidade.listar();
		this.list.fireTableDataChanged();
	}

	private void excluir() {
		if (this.table.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(null, "Selecione um registro na tabela para excluir!");
			return;
		}

		int dialog = JOptionPane.showConfirmDialog(null, "Deseja excluir essa cidade?", "Excluir cidade", JOptionPane.YES_NO_OPTION);
		
		if (dialog == 0) {
			String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

			this.list = this.cidade.excluir(id);

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void carregarCamposAlterar() {
		if (this.table.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(null, "Selecione um registro na tabela para alterar!");
			return;
		}
		campos("alterar");

		String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

		ResultSet result = null;

		try {
			result = cidade.carregaCamposAlterar(id);
			result.next();

			nome.setText(result.getString("nome"));
			uf.setText(result.getString("uf"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void alterar() {
		Object[][] data = { { nome.getName(), nome.getText() }, { uf.getName(), uf.getText() } };

		Boolean error = GenericController.validateFieldsEmpty(data);

		if (!error) {
			String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

			this.cidade.setNome(nome.getText());
			this.cidade.setUf(uf.getText());

			this.list = cidade.alterar(id);

			frame_fields.setVisible(false);

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void campos(String criarOuAlterar) {
		frame_fields = new JFrame();
		frame_fields.setBounds(100, 100, 494, 353);
		frame_fields.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame_fields.getContentPane().setLayout(null);
		frame_fields.setResizable(false);
		frame_fields.setVisible(true);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 45, 458, 9);
		frame_fields.getContentPane().add(separator);

		nome = new JTextField("");
		nome.setName("Cidade");
		nome.setBounds(10, 85, 274, 20);
		frame_fields.getContentPane().add(nome);
		nome.setColumns(10);

		uf = new JTextField("");
		uf.setName("uf");
		uf.setBounds(10, 141, 86, 20);
		frame_fields.getContentPane().add(uf);
		uf.setColumns(10);

		label_cidade = new JLabel("Cidade");
		label_cidade.setBounds(10, 65, 46, 14);
		frame_fields.getContentPane().add(label_cidade);

		label_uf = new JLabel("UF");
		label_uf.setBounds(10, 116, 46, 14);
		frame_fields.getContentPane().add(label_uf);

		if (criarOuAlterar.equals("cadastrar")) {
			JLabel adicionar_func_label = new JLabel("Adicionar Cidade");
			adicionar_func_label.setFont(new Font("Tahoma", Font.BOLD, 12));
			adicionar_func_label.setBounds(10, 13, 156, 14);
			frame_fields.getContentPane().add(adicionar_func_label);

			JButton adicionar = new JButton("Adicionar");
			adicionar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cadastrar();
				}
			});
			adicionar.setBounds(366, 220, 97, 23);
			frame_fields.getContentPane().add(adicionar);
		} else {
			JLabel atualizar_func_label = new JLabel("Atualizar Cidade");
			atualizar_func_label.setFont(new Font("Tahoma", Font.BOLD, 12));
			atualizar_func_label.setBounds(10, 13, 156, 14);
			frame_fields.getContentPane().add(atualizar_func_label);

			JButton alterar = new JButton("Atualizar");
			alterar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					alterar();
				}
			});
			alterar.setBounds(366, 220, 97, 23);
			frame_fields.getContentPane().add(alterar);
		}

		JButton cancelar = new JButton("Cancelar");
		cancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame_fields.dispose();
			}
		});
		cancelar.setBounds(267, 220, 89, 23);
		frame_fields.getContentPane().add(cancelar);

		JLabel logo_cidade = new JLabel("");
		URL cidade_logo = this.getClass().getResource("/public/cidade.png");
		logo_cidade.setIcon(new ImageIcon(cidade_logo));
		logo_cidade.setBounds(346, 61, 122, 126);
		frame_fields.getContentPane().add(logo_cidade);

		JLabel logo_dark_min = new JLabel("");
		URL logo = this.getClass().getResource("/public/dark_logo_min.png");
		logo_dark_min.setIcon(new ImageIcon(logo));
		logo_dark_min.setBounds(197, 276, 114, 14);
		frame_fields.getContentPane().add(logo_dark_min);

		frame_fields.setLocationRelativeTo(frame);
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

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 37, 826, 14);
		frame.getContentPane().add(separator_1);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 124, 561, 355);
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(this.table);

		JLabel listar_cidade = new JLabel("Listar  Cidades");
		listar_cidade.setFont(new Font("Tahoma", Font.BOLD, 12));
		listar_cidade.setBounds(319, 19, 185, 14);
		frame.getContentPane().add(listar_cidade);

		JLabel logo_cidade_list = new JLabel("");
		URL urlToImage = this.getClass().getResource("/public/cidade.png");
		logo_cidade_list.setIcon(new ImageIcon(urlToImage));
		logo_cidade_list.setBounds(668, 147, 105, 130);
		frame.getContentPane().add(logo_cidade_list);

		JLabel lbl_pesquisar = new JLabel("Pesquisar por nome");
		lbl_pesquisar.setBounds(10, 47, 180, 14);
		frame.getContentPane().add(lbl_pesquisar);

		input_filtrar = new JTextField();
		input_filtrar.setName("Pesquisar por nome");
		input_filtrar.setBounds(10, 72, 475, 20);
		frame.getContentPane().add(input_filtrar);
		input_filtrar.setColumns(10);

		JButton alterar = new JButton("Alterar");
		alterar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carregarCamposAlterar();
			}
		});
		alterar.setBounds(642, 397, 158, 23);
		frame.getContentPane().add(alterar);

		JButton pesquisar = new JButton("Pesquisar");
		pesquisar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filtrar();
			}
		});
		pesquisar.setBounds(495, 71, 127, 23);
		frame.getContentPane().add(pesquisar);

		JButton cadastrar = new JButton("Cadastrar");
		cadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				campos("cadastrar");
			}
		});
		cadastrar.setBounds(642, 363, 158, 23);
		frame.getContentPane().add(cadastrar);

		JButton home = new JButton("Home");
		home.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				home();
			}
		});
		home.setBounds(10, 11, 89, 23);
		frame.getContentPane().add(home);

		JButton excluir = new JButton("Excluir");
		excluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				excluir();
			}
		});
		excluir.setBounds(642, 431, 158, 23);
		frame.getContentPane().add(excluir);

		JLabel logo_dark_min_two = new JLabel("");
		URL logo = this.getClass().getResource("/public/dark_logo_min.png");
		logo_dark_min_two.setIcon(new ImageIcon(logo));
		logo_dark_min_two.setBounds(367, 523, 137, 14);
		frame.getContentPane().add(logo_dark_min_two);

		JButton limpar_filtro = new JButton("Limpar filtro");
		limpar_filtro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				limparFiltro();
			}
		});
		limpar_filtro.setBounds(624, 71, 125, 23);
		frame.getContentPane().add(limpar_filtro);

		frame.setLocationRelativeTo(frame);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

}
