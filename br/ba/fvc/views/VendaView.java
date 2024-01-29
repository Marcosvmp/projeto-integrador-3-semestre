package br.ba.fvc.views;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import br.ba.fvc.controller.FuncionarioController;
import br.ba.fvc.controller.GenericController;
import br.ba.fvc.controller.VendaController;

import javax.swing.JScrollPane;

public class VendaView {

	private JFrame frame;
	private JFrame frame_fields;
	private JFrame frame_result_filter;
	private JTable table;
	private JTextField nome;
	private JFormattedTextField cpf;
	private JTextField valor;
	private JTextField data;
	private JFormattedTextField input_de;
	private JFormattedTextField input_ate;
	private JTextField rota;
	private String id_rota;
	private String id_veiculo;
	private JLabel value;
	private JLabel label_de;
	private JLabel label_ate;
	private DefaultComboBoxModel<Object> armchairModel;
	private JComboBox<Object> poltrona;
	private JComboBox<Object> comboBox;
	public FuncionarioController funcionario;
	public VendaController venda;
	public DefaultTableModel list;
	public ArrayList<String> comboBoxRouter;
	public ArrayList<String> comboBoxPoltrona;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VendaView window = new VendaView(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	@SuppressWarnings("serial")
	public VendaView(FuncionarioController fields) {
		this.funcionario = fields;
		this.venda = new VendaController();
		this.list = this.venda.all();
		this.comboBoxRouter = this.venda.comboBoxRouter();
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

	private void loadComboBox(String id) {
		try {
			ArrayList<String> add_list = new ArrayList<>();
			ArrayList<String> index_remove = new ArrayList<>();

			ResultSet resultSet = this.venda.loadComboBox(id);
			ResultSet countArmchair = this.venda.countArmchair(id);
			this.comboBoxPoltrona = this.venda.comboboxArmchair(id);
			ResultSet result = this.venda.getCombobox(id);

			for (int i = 0; i <= result.getInt("quantidade_poltronas"); i++) {
				add_list.add("" + i + "");
				armchairModel = new DefaultComboBoxModel<Object>(add_list.toArray());
				this.poltrona.setModel(armchairModel);
				this.poltrona.getModel().setSelectedItem("SELECIONE");
				for (int j = 0; j < this.comboBoxPoltrona.size(); j++) {
					String index = this.comboBoxPoltrona.get(j);
					String id_armchair = index.toString().substring(index.lastIndexOf("=") + 1);
					if (this.comboBoxPoltrona.size() != 0) {
						if (Integer.parseInt(id_armchair.trim()) == Integer.parseInt(id_armchair.trim())) {
							index_remove.add(id_armchair.trim());
						}
						for (int a = 0; a <= j; a++) {
							this.poltrona.removeItem(index_remove.get(a));
						}
					}
				}
			}

			if (countArmchair.next()) {
				if (countArmchair.getInt("poltrona") > result.getInt("quantidade_poltronas")) {
					this.poltrona.getModel().setSelectedItem("PASSAGEM ESGOTADA!");
					JOptionPane.showMessageDialog(null, "Passagem esgotada, por favor selecione outra rota!");
				}
			}
			if (resultSet.next()) {
				data.setText(resultSet.getString("data_formatada"));

				String ticketValue = this.formatMoney(resultSet.getDouble("valor_passagem"));

				valor.setText(ticketValue);
				this.id_veiculo = resultSet.getString("id_veiculo");
				this.id_rota = id;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String formatMoney(Double value) {
		String money = null;
		Locale ptBr = new Locale("pt", "BR");

		DecimalFormatSymbols formatSymbolPTBR = new DecimalFormatSymbols(ptBr);

		DecimalFormat formatMoney = new DecimalFormat("¤ ###,###,##0.00", formatSymbolPTBR);

		money = formatMoney.format(value);
		return money;
	}

	private void cadastrar() {
		Object[][] data = { { nome.getName(), nome.getText() }, { cpf.getName(), cpf.getValue() },
				{ comboBox.getName(), comboBox.getSelectedIndex() },
				{ poltrona.getName(), poltrona.getSelectedIndex() }, };

		Boolean error = GenericController.validateFieldsEmpty(data);

		if (!error) {
			this.venda.setName_passenger(nome.getText());
			this.venda.setCpf(cpf.getText());
			this.venda.setArmchair(poltrona.getSelectedItem().toString());
			this.venda.setId_router(this.id_rota);
			this.venda.setId_vehicle(id_veiculo);
			this.list = venda.store();

			if (this.list == null) {
				frame_fields.setVisible(true);
				return;
			}
			frame_fields.dispose();

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
			this.table.getColumnModel().getColumn(0).setMaxWidth(50);
		}
	}

	private void excluir() {
		if (this.table.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(null, "Selecione um registro na tabela para cancelar passagem!");
			return;
		}

		int dialog = JOptionPane.showConfirmDialog(null, "Deseja cancelar passagem?", "Cancelar passagem",
				JOptionPane.YES_NO_OPTION);

		if (dialog == 0) {
			String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

			this.list = this.venda.destroy(id);

			if (this.list == null) {
				return;
			}

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void pesquisarRota() {
		Object[][] data = { { rota.getName(), rota.getText() } };

		Boolean error = GenericController.validateFieldsEmpty(data);
		if (!error) {

			this.venda.setRouter(rota.getText());

			this.list = this.venda.filterRouter();

			rota.setText("");

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void pesquisarPeriodo() {
		ResultSet result = null;

		Object[][] data = { { input_de.getName(), input_de.getValue() },
				{ input_ate.getName(), input_ate.getValue() } };

		Boolean error = GenericController.validateFieldsEmpty(data);
		try {

			if (!error) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDate de = LocalDate.parse(input_de.getText(), formatter);
				LocalDate ate = LocalDate.parse(input_ate.getText(), formatter);

				this.venda.setDe(de.toString());
				this.venda.setAte(ate.toString());

				resultFilter();

				result = venda.filterPeriod();
				result.next();

				double values = result.getDouble("valor_total");

				if (result.getString("valor_total") == null) {
					values = 0.0;
				}

				String valuePeriod = this.formatMoney(values);

				label_de.setText("Periodo De: " + input_de.getText());
				label_ate.setText("Periodo Até: " + input_ate.getText());
				value.setText("Valor total: " + valuePeriod);

				this.table.setModel(this.list);
				this.list.fireTableDataChanged();

				input_de.setValue("");
				input_ate.setValue("");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void limparFiltro() {
		this.list = venda.all();
		this.list.fireTableDataChanged();
	}

	private void resultFilter() {
		frame_result_filter = new JFrame();
		frame_result_filter.setBounds(100, 100, 332, 315);
		frame_result_filter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame_result_filter.getContentPane().setLayout(null);
		frame_result_filter.setResizable(false);

		frame_result_filter.setVisible(true);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 43, 678, 12);
		frame_result_filter.getContentPane().add(separator_1);

		label_de = new JLabel();
		label_de.setBounds(10, 87, 296, 14);
		frame_result_filter.getContentPane().add(label_de);

		label_ate = new JLabel();
		label_ate.setBounds(10, 136, 296, 14);
		frame_result_filter.getContentPane().add(label_ate);

		JLabel result_period = new JLabel("Resultado filtro por periodo");
		result_period.setBounds(10, 18, 170, 14);
		frame_result_filter.getContentPane().add(result_period);

		value = new JLabel();
		value.setBounds(10, 184, 249, 14);
		frame_result_filter.getContentPane().add(value);

		JButton cancelar = new JButton("Cancelar");
		cancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame_result_filter.dispose();
			}
		});
		cancelar.setBounds(84, 242, 138, 23);
		frame_result_filter.getContentPane().add(cancelar);

		frame_result_filter.setLocationRelativeTo(frame);
	}

	private void campos() {
		try {
			frame_fields = new JFrame();
			frame_fields.setBounds(100, 100, 714, 391);
			frame_fields.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame_fields.getContentPane().setLayout(null);
			frame_fields.setResizable(false);
			frame_fields.setVisible(true);

			JSeparator separator_1 = new JSeparator();
			separator_1.setBounds(10, 43, 678, 12);
			frame_fields.getContentPane().add(separator_1);

			JLabel label_cpf = new JLabel("CPF");
			label_cpf.setBounds(361, 53, 46, 14);
			frame_fields.getContentPane().add(label_cpf);

			cpf = new JFormattedTextField(new MaskFormatter("###.###.###-##"));
			cpf.setName("cpf");
			cpf.setBounds(361, 78, 327, 20);
			frame_fields.getContentPane().add(cpf);
			cpf.setColumns(10);

			JLabel label_data_partida = new JLabel("Data Partida / Hora Partida");
			label_data_partida.setBounds(10, 167, 200, 14);
			frame_fields.getContentPane().add(label_data_partida);

			data = new JTextField();
			data.setBounds(10, 184, 327, 17);
			frame_fields.getContentPane().add(data);
			data.setColumns(10);
			data.setEditable(false);

			JLabel label_nome = new JLabel("Nome");
			label_nome.setBounds(10, 53, 46, 14);
			frame_fields.getContentPane().add(label_nome);

			nome = new JTextField();
			nome.setName("nome");
			nome.setBounds(10, 78, 327, 20);
			frame_fields.getContentPane().add(nome);
			nome.setColumns(10);

			JLabel label_poltrona = new JLabel("Poltrona");
			label_poltrona.setBounds(10, 209, 70, 14);
			frame_fields.getContentPane().add(label_poltrona);

			poltrona = new JComboBox<Object>();
			poltrona.setName("poltrona");
			poltrona.setBounds(10, 231, 327, 20);
			frame_fields.getContentPane().add(poltrona);

			JLabel label_valor_passagem = new JLabel("Valor Passagem");
			label_valor_passagem.setBounds(349, 163, 102, 23);
			frame_fields.getContentPane().add(label_valor_passagem);

			valor = new JTextField();
			valor.setBounds(349, 183, 339, 18);
			frame_fields.getContentPane().add(valor);
			valor.setColumns(10);
			valor.setEditable(false);

			JLabel label_cidade_origim = new JLabel("Rota");
			label_cidade_origim.setBounds(10, 109, 122, 20);
			frame_fields.getContentPane().add(label_cidade_origim);

			comboBox = new JComboBox<Object>(this.comboBoxRouter.toArray());
			comboBox.setName("rota");
			comboBox.getModel().setSelectedItem("SELECIONE");
			comboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if ((e.getStateChange() == ItemEvent.SELECTED)) {
						String id = comboBox.getSelectedItem().toString().substring(0,
								comboBox.getSelectedItem().toString().indexOf('='));
						loadComboBox(id);
					}
				}
			});
			comboBox.setBounds(10, 134, 678, 22);
			frame_fields.getContentPane().add(comboBox);

			JLabel label_adicionar = new JLabel("Adicionar Vendas");
			label_adicionar.setBounds(10, 18, 145, 14);
			frame_fields.getContentPane().add(label_adicionar);

			JButton adicionar = new JButton("Concluir venda");
			adicionar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cadastrar();
				}
			});
			adicionar.setBounds(550, 279, 140, 23);
			frame_fields.getContentPane().add(adicionar);

			JButton Cancelar = new JButton("Cancelar");
			Cancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame_fields.dispose();
				}
			});
			Cancelar.setBounds(402, 279, 138, 23);
			frame_fields.getContentPane().add(Cancelar);

			frame_fields.setLocationRelativeTo(frame);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			this.table.getColumnModel().getColumn(0).setMaxWidth(50);
			frame = new JFrame();
			frame.setBounds(100, 100, 862, 613);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			frame.setResizable(false);

			JSeparator separator_1 = new JSeparator();
			separator_1.setBounds(10, 37, 826, 14);
			frame.getContentPane().add(separator_1);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 192, 828, 278);
			frame.getContentPane().add(scrollPane);
			scrollPane.setViewportView(this.table);

			JLabel label_listar_venda = new JLabel("Listar Vendas");
			label_listar_venda.setBounds(394, 12, 129, 14);
			frame.getContentPane().add(label_listar_venda);

			JLabel lblPesquisarPorPeriodo = new JLabel("Pesquisar por periodo");
			lblPesquisarPorPeriodo.setBounds(10, 54, 162, 14);
			frame.getContentPane().add(lblPesquisarPorPeriodo);

			input_de = new JFormattedTextField((new MaskFormatter("##/##/####")));
			input_de.setName("periodo de");
			input_de.setBounds(10, 79, 129, 20);
			frame.getContentPane().add(input_de);
			input_de.setColumns(10);

			input_ate = new JFormattedTextField((new MaskFormatter("##/##/####")));
			input_ate.setName("periodo ate");
			input_ate.setBounds(157, 79, 129, 20);
			frame.getContentPane().add(input_ate);
			input_ate.setColumns(10);

			JButton pesquisar = new JButton("Pesquisar");
			pesquisar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pesquisarPeriodo();
				}
			});
			pesquisar.setBounds(299, 78, 129, 23);
			frame.getContentPane().add(pesquisar);

			JLabel lblPesquisarPorRoteiro = new JLabel("Pesquisar por rota");
			lblPesquisarPorRoteiro.setBounds(10, 106, 162, 14);
			frame.getContentPane().add(lblPesquisarPorRoteiro);

			rota = new JTextField();
			rota.setName("rota");
			rota.setBounds(10, 131, 276, 20);
			frame.getContentPane().add(rota);
			rota.setColumns(10);

			JButton pesquisar_rota = new JButton("Pesquisar");
			pesquisar_rota.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pesquisarRota();
				}
			});
			pesquisar_rota.setBounds(299, 130, 129, 23);
			frame.getContentPane().add(pesquisar_rota);

			JButton limpar_filtros = new JButton("Limpar Filtro");
			limpar_filtros.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					limparFiltro();
				}
			});
			limpar_filtros.setBounds(436, 130, 129, 23);
			frame.getContentPane().add(limpar_filtros);

			JButton home = new JButton("Home");
			home.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					home();
				}
			});
			home.setBounds(10, 3, 89, 23);
			frame.getContentPane().add(home);

			JButton cadastrar = new JButton("Comprar Passagem");
			cadastrar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					campos();
				}
			});
			cadastrar.setBounds(674, 481, 162, 23);
			frame.getContentPane().add(cadastrar);

			JButton cancelar_passagem = new JButton("Cancelar Passagem");
			if (!this.funcionario.getCargoLogado().equals("ADMINISTRADOR")) {
				cancelar_passagem.setVisible(false);
			}
			cancelar_passagem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelar_passagem.setEnabled(false);
					if (funcionario.getCargoLogado().equals("ADMINISTRADOR")) {
						cancelar_passagem.setEnabled(true);
						excluir();
					}
				}
			});
			cancelar_passagem.setBounds(500, 481, 162, 23);
			frame.getContentPane().add(cancelar_passagem);

			JLabel dark_logo_min = new JLabel("");
			URL logo = this.getClass().getResource("/public/dark_logo_min.png");
			dark_logo_min.setIcon(new ImageIcon(logo));
			dark_logo_min.setBounds(367, 523, 137, 14);
			frame.getContentPane().add(dark_logo_min);

			frame.setLocationRelativeTo(frame);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
}
