package br.ba.fvc.views;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
import br.ba.fvc.controller.RotaController;

import javax.swing.JScrollPane;

public class RotaView {

	private JFrame frame;
	private JFrame frame_fields;
	private JTextField input_filter;
	private JFormattedTextField date;
	private JTextField value_ticket;
	private JTable table;
	public DefaultTableModel list;
	public FuncionarioController funcionario;
	private RotaController router;
	private DefaultComboBoxModel<Object> cityModel;
	private JComboBox<Object> driver;
	private JComboBox<Object> vehicle;
	private JComboBox<Object> cityOrigin;
	private JComboBox<Object> cityDestiny;
	public ArrayList<String> comboboxEmployee;
	public ArrayList<String> comboboxOrigin;
	public ArrayList<String> comboBoxVehicle;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RotaView window = new RotaView(null);
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
	public RotaView(FuncionarioController fields) {
		this.funcionario = fields;
		this.router = new RotaController();
		this.list = this.router.all();
		this.comboboxEmployee = this.router.comboboxEmployee();
		this.comboboxOrigin = this.router.comboboxCity();
		this.comboBoxVehicle = this.router.comboboxVehicle();
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

	private void store() {
		try {
			Object[][] data = { { driver.getName(), driver.getSelectedIndex() },
					{ vehicle.getName(), vehicle.getSelectedIndex() }, { date.getName(), date.getValue() },
					{ cityOrigin.getName(), cityOrigin.getSelectedIndex() },
					{ cityDestiny.getName(), cityDestiny.getSelectedIndex() },
					{ value_ticket.getName(), value_ticket.getText() } };

			Boolean error = GenericController.validateFieldsEmpty(data);

			if (!error) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				LocalDateTime convertDate = LocalDateTime.parse(date.getText(), formatter);

				LocalDateTime now = LocalDateTime.now();

				if (now.compareTo(convertDate) > 0) {
					JOptionPane.showMessageDialog(null, "Data da partida menor que data atual!");
					return;
				}

				String id_origin = cityOrigin.getSelectedItem().toString().substring(0,
						cityOrigin.getSelectedItem().toString().indexOf('='));

				String id_destiny = cityDestiny.getSelectedItem().toString().substring(0,
						cityDestiny.getSelectedItem().toString().indexOf('='));

				String id_vehicle = vehicle.getSelectedItem().toString().substring(0,
						cityOrigin.getSelectedItem().toString().indexOf('='));

				String id_employee = driver.getSelectedItem().toString().substring(0,
						driver.getSelectedItem().toString().indexOf('='));

				this.router.setDate_match(convertDate.toLocalDate() + "=" + convertDate.toLocalTime());
				this.router.setDestiny(id_destiny);
				this.router.setId_vehicle(id_vehicle);
				this.router.setId_employee(id_employee);
				this.router.setOrigin(id_origin);
				this.router.setValue_ticket(Double.valueOf(value_ticket.getText()));
				this.list = router.incluir();

				if (this.list == null) {
					frame_fields.setVisible(true);
					return;
				}

				frame_fields.dispose();

				this.table.setModel(this.list);
				this.list.fireTableDataChanged();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void loadFieldsUpdate() {
		if (this.table.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(null, "Selecione um registro na tabela para alterar!");
			return;
		}
		fields("alterar");

		String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

		ResultSet result = null;

		try {
			result = this.router.loadFieldsUpdate(id);
			result.next();

			DateTimeFormatter formatterDatabase = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			LocalDateTime convertDateDatabase = LocalDateTime.parse(result.getString("data_partida"),
					formatterDatabase);

			DateTimeFormatter formatterDatePTBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

			LocalDateTime convertDatePTBR = LocalDateTime.parse(convertDateDatabase.format(formatterDatePTBR),
					formatterDatePTBR);

			date.setValue(formatterDatePTBR.format(convertDatePTBR));
			value_ticket.setText(result.getString("valor_passagem"));
			loadComboboxUpdate(this.comboboxEmployee, result, this.driver, "id_funcionario");
			loadComboboxUpdate(this.comboBoxVehicle, result, this.vehicle, "id_veiculo");
			loadComboboxUpdate(this.comboboxOrigin, result, this.cityOrigin, "cidade_origem");
			loadComboboxUpdate(this.comboboxOrigin, result, this.cityDestiny, "cidade_destino");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void update() {
		try {
			Object[][] data = { { driver.getName(), driver.getSelectedIndex() },
					{ vehicle.getName(), vehicle.getSelectedIndex() }, { date.getName(), date.getValue() },
					{ cityOrigin.getName(), cityOrigin.getSelectedIndex() },
					{ cityDestiny.getName(), cityDestiny.getSelectedIndex() },
					{ value_ticket.getName(), value_ticket.getText() } };

			Boolean error = GenericController.validateFieldsEmpty(data);

			if (!error) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				LocalDateTime convertDate = LocalDateTime.parse(date.getText(), formatter);

				LocalDateTime now = LocalDateTime.now();

				if (now.compareTo(convertDate) > 0) {
					JOptionPane.showMessageDialog(null, "Data da partida menor que data atual!");
					return;
				}

				String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

				String id_origin = cityOrigin.getSelectedItem().toString().substring(0,
						cityOrigin.getSelectedItem().toString().indexOf('='));

				String id_destiny = cityDestiny.getSelectedItem().toString().substring(0,
						cityDestiny.getSelectedItem().toString().indexOf('='));

				String id_vehicle = vehicle.getSelectedItem().toString().substring(0,
						cityOrigin.getSelectedItem().toString().indexOf('='));

				String id_employee = driver.getSelectedItem().toString().substring(0,
						driver.getSelectedItem().toString().indexOf('='));

				this.router.setDate_match(convertDate.toString());
				this.router.setDestiny(id_destiny);
				this.router.setId_vehicle(id_vehicle);
				this.router.setId_employee(id_employee);
				this.router.setOrigin(id_origin);
				this.router.setValue_ticket(Double.valueOf(value_ticket.getText()));
				this.list = router.update(id);

				if (this.list == null) {
					frame_fields.setVisible(true);
					return;
				}

				frame_fields.dispose();

				this.table.setModel(this.list);
				this.list.fireTableDataChanged();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void loadComboboxUpdate(ArrayList<String> list, ResultSet result, JComboBox<Object> combo, String key) {
		try {
			for (int i = 0; i < list.size(); i++) {
				String index = list.get(i);
				String id = index.toString().substring(0, index.toString().indexOf('='));
				if (Integer.parseInt(id.trim()) == result.getInt(key)) {
					combo.setSelectedItem(index);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void loadComboDestiny(String id) {
		this.comboboxOrigin = this.router.comboboxCity();

		cityModel = new DefaultComboBoxModel<Object>(this.comboboxOrigin.toArray());
		this.cityDestiny.setModel(cityModel);
		this.cityDestiny.removeItemAt(this.cityOrigin.getSelectedIndex());
		this.cityDestiny.getModel().setSelectedItem("SELECIONE");
	}

	private void destroy() {
		if (this.table.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(null, "Selecione um registro na tabela para excluir!");
			return;
		}

		int dialog = JOptionPane.showConfirmDialog(null, "Deseja excluir essa rota?", "Excluir rota",
				JOptionPane.YES_NO_OPTION);

		if (dialog == 0) {
			String id = this.table.getModel().getValueAt(this.table.getSelectedRow(), 0).toString();

			this.list = this.router.excluir(id);

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void filter() {
		Object[][] data = { { input_filter.getName(), input_filter.getText() } };

		Boolean error = GenericController.validateFieldsEmpty(data);

		if (!error) {
			this.router.setOrigin(input_filter.getText());

			this.list = this.router.filtrar(); 

			input_filter.setText("");

			this.table.setModel(this.list);
			this.list.fireTableDataChanged();
		}
	}

	private void clearFilter() {
		this.list = this.router.all();
		this.list.fireTableDataChanged();
	}

	private void fields(String criarOuAlterar) {
		try {
			frame_fields = new JFrame();
			frame_fields.setBounds(100, 100, 717, 389);
			frame_fields.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame_fields.getContentPane().setLayout(null);
			frame_fields.setResizable(false);
			frame_fields.setVisible(true);

			JSeparator separator = new JSeparator();
			separator.setBounds(10, 40, 681, 14);
			frame_fields.getContentPane().add(separator);

			JLabel label_nome_motorista = new JLabel("Nome Motorista");
			label_nome_motorista.setBounds(10, 62, 149, 14);
			frame_fields.getContentPane().add(label_nome_motorista);

			JLabel label_placa = new JLabel("Veículo");
			label_placa.setBounds(10, 116, 86, 14);
			frame_fields.getContentPane().add(label_placa);

			JLabel label_destino = new JLabel("Cidade Destino");
			label_destino.setBounds(360, 172, 120, 14);
			frame_fields.getContentPane().add(label_destino);

			JLabel label_origin = new JLabel("Cidade Origem");
			label_origin.setBounds(10, 172, 120, 14);
			frame_fields.getContentPane().add(label_origin);

			JLabel lblDataPartida = new JLabel("Data Partida / Hora partida");
			lblDataPartida.setBounds(360, 116, 160, 14);
			frame_fields.getContentPane().add(lblDataPartida);

			vehicle = new JComboBox<Object>(this.comboBoxVehicle.toArray());
			vehicle.setName("veiculo");
			vehicle.getModel().setSelectedItem("SELECIONE");
			vehicle.setBounds(10, 141, 316, 20);
			frame_fields.getContentPane().add(vehicle);

			date = new JFormattedTextField(new MaskFormatter("##/##/#### ##:##"));
			date.setName("date partida");
			date.setBounds(360, 141, 316, 20);
			frame_fields.getContentPane().add(date);
			date.setColumns(10);

			driver = new JComboBox<Object>(this.comboboxEmployee.toArray());
			driver.setName("Motorista");
			driver.getModel().setSelectedItem("SELECIONE");
			driver.setBounds(10, 87, 666, 22);
			frame_fields.getContentPane().add(driver);

			cityOrigin = new JComboBox<Object>(this.comboboxOrigin.toArray());
			cityOrigin.setName("cidade origem");
			cityOrigin.getModel().setSelectedItem("SELECIONE");
			cityOrigin.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if ((e.getStateChange() == ItemEvent.SELECTED)) {
						String id = cityOrigin.getSelectedItem().toString().substring(0,
								cityOrigin.getSelectedItem().toString().indexOf('='));
						loadComboDestiny(id);
					}
				}
			});
			cityOrigin.setBounds(10, 194, 316, 22);
			frame_fields.getContentPane().add(cityOrigin);

			cityDestiny = new JComboBox<Object>();
			cityDestiny.setName("cidade destino");
			cityDestiny.setBounds(360, 194, 316, 22);
			frame_fields.getContentPane().add(cityDestiny);

			value_ticket = new JTextField();
			value_ticket.setName("valor da passagem");
			value_ticket.setColumns(10);
			value_ticket.setBounds(10, 252, 316, 20);
			frame_fields.getContentPane().add(value_ticket);

			JLabel label_valor_passagem = new JLabel("Valor Passagem");
			label_valor_passagem.setBounds(10, 227, 120, 14);
			frame_fields.getContentPane().add(label_valor_passagem);

			if (criarOuAlterar.equals("cadastrar")) {
				JLabel lable_adicionar = new JLabel("Adicionar Rotas");
				lable_adicionar.setBounds(10, 20, 115, 14);
				frame_fields.getContentPane().add(lable_adicionar);

				JButton adicionar = new JButton("Adicionar");
				adicionar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						store();
					}
				});
				adicionar.setBounds(565, 258, 111, 23);
				frame_fields.getContentPane().add(adicionar);
			} else {
				JLabel lable_adicionar = new JLabel("Atualizar Rotas");
				lable_adicionar.setBounds(10, 20, 115, 14);
				frame_fields.getContentPane().add(lable_adicionar);

				JButton alterar = new JButton("Atualizar");
				alterar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						update();
					}
				});
				alterar.setBounds(589, 256, 94, 23);
				frame_fields.getContentPane().add(alterar);
			}

			JButton cancelar = new JButton("Cancelar");
			cancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame_fields.dispose();
				}
			});
			cancelar.setBounds(450, 258, 111, 23);
			frame_fields.getContentPane().add(cancelar);

			JLabel dark_logo_min = new JLabel("");
			URL logo = this.getClass().getResource("/public/dark_logo_min.png");
			dark_logo_min.setIcon(new ImageIcon(logo));
			dark_logo_min.setBounds(304, 313, 114, 14);
			frame_fields.getContentPane().add(dark_logo_min);

			frame_fields.setLocationRelativeTo(frame);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1001, 611);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 37, 942, 14);
		frame.getContentPane().add(separator_1);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 117, 951, 297);
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(this.table);

		JLabel listar_rota = new JLabel("Listar  Rotas");
		listar_rota.setFont(new Font("Tahoma", Font.BOLD, 12));
		listar_rota.setBounds(456, 20, 185, 14);
		frame.getContentPane().add(listar_rota);

		JButton alterar = new JButton("Alterar");
		alterar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFieldsUpdate();
			}
		});
		alterar.setBounds(705, 425, 123, 23);
		frame.getContentPane().add(alterar);

		JButton excluir = new JButton("Excluir");
		excluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroy();
			}
		});
		excluir.setBounds(572, 425, 123, 23);
		frame.getContentPane().add(excluir);

		JLabel lblPesquisarPorCidade = new JLabel(" Pesquisar por cidade origem");
		lblPesquisarPorCidade.setBounds(10, 50, 190, 14);
		frame.getContentPane().add(lblPesquisarPorCidade);

		input_filter = new JTextField();
		input_filter.setName("pesquisar por cidade origem");
		input_filter.setBounds(10, 70, 424, 20);
		frame.getContentPane().add(input_filter);
		input_filter.setColumns(10);

		JButton cadastrar = new JButton("Cadastrar");
		cadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fields("cadastrar");
			}
		});
		cadastrar.setBounds(838, 425, 123, 23);
		frame.getContentPane().add(cadastrar);

		JButton pesquisar = new JButton("Pesquisar");
		pesquisar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filter();
			}
		});
		pesquisar.setBounds(444, 69, 137, 23);
		frame.getContentPane().add(pesquisar);

		JButton btnLimparFiltro = new JButton("Limpar filtro");
		btnLimparFiltro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearFilter();
			}
		});
		btnLimparFiltro.setBounds(591, 69, 137, 23);
		frame.getContentPane().add(btnLimparFiltro);

		JButton home = new JButton("Home");
		home.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				home();
			}
		});
		home.setBounds(10, 3, 137, 23);
		frame.getContentPane().add(home);

		JLabel dark_logo_min = new JLabel("");
		URL logo = this.getClass().getResource("/public/dark_logo_min.png");
		dark_logo_min.setIcon(new ImageIcon(logo));
		dark_logo_min.setBounds(456, 524, 137, 14);
		frame.getContentPane().add(dark_logo_min);

		frame.setLocationRelativeTo(frame);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
}
