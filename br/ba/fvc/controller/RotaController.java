package br.ba.fvc.controller;

import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import br.ba.fvc.dao.RotaDAO;

public class RotaController {

	private String date_match;
	private Double value_ticket;
	private String id_vehicle;
	private String id_employee;
	private String origin;
	private String destiny;
	private GenericController generic;
	private RotaDAO dao;
	private String table = "rota";
	private String[] columns = { "ID", "Nome motorista", "Placa do veiculo", "Data de partida", "Cidade origem",
			"Cidade destino" };
	private String field = "data_partida, valor_passagem, id_funcionario, id_veiculo, cidade_origem, cidade_destino";

	public RotaController() {
		this.generic = new GenericController(table, columns, field);
		this.dao = new RotaDAO();
		comboboxCity();
	}

	public DefaultTableModel all() {
		DefaultTableModel result = new DefaultTableModel();
		ResultSet resultSet = null;
		try {

			resultSet = this.dao.all();

			result = this.generic.addRow(resultSet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public DefaultTableModel incluir() {
		DefaultTableModel result = null;
		ResultSet resultSet = null;
		try {

			String date = date_match.toString().substring(0, date_match.toString().indexOf('='));
			
			resultSet = this.dao.verifyDateExists(date, id_vehicle);

			if (resultSet.next()) {
				if (resultSet.getString("data_partida").equals(date)) {
					throw new Exception("Veiculo já cadastrado para essa data!");
				}
			}

			Object[] data = { date_match, value_ticket, id_employee, id_vehicle, origin, destiny };
			result = this.generic.store(data);
			this.all();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public DefaultTableModel filtrar() {
		DefaultTableModel result = null;
		ResultSet resultSet = null;
		try {

			resultSet = this.dao.filterRouter(origin);

			result = this.generic.addRow(resultSet);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public ResultSet loadFieldsUpdate(String id) {
		ResultSet result = null;
		try {

			result = this.generic.index(id);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public DefaultTableModel excluir(String id) {
		DefaultTableModel result = null;
		try {

			result = this.generic.destroy(id);

			this.all();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public DefaultTableModel update(String id) {
		DefaultTableModel result = null;
		try {
			Object[] fields = { "data_partida", "valor_passagem", "id_funcionario", "id_veiculo", "cidade_origem",
					"cidade_destino" };
			Object[] data = { date_match, value_ticket, id_employee, id_vehicle, origin, destiny };

			result = this.generic.update(fields, data, id);
			this.all();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		return result;
	}

	public ArrayList<String> comboboxEmployee() {
		ArrayList<String> values = new ArrayList<>();
		ResultSet resultSet = null;
		try {
			resultSet = this.dao.comboboxemployee();

			values = this.generic.addCombobox(resultSet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return values;
	}

	public ArrayList<String> comboboxCity() {
		ArrayList<String> values = new ArrayList<>();
		ResultSet resultSet = null;
		try {
			resultSet = this.dao.comboboxCity();

			values = this.generic.addCombobox(resultSet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return values;
	}

	public ArrayList<String> comboboxVehicle() {
		ArrayList<String> values = new ArrayList<>();
		ResultSet resultSet = null;
		try {
			resultSet = this.dao.comboboxVehicle();

			values = this.generic.addCombobox(resultSet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return values;
	}

	public String getDate_match() {
		return date_match;
	}

	public void setDate_match(String date_match) {
		this.date_match = date_match;
	}

	public Double getValue_ticket() {
		return value_ticket;
	}

	public void setValue_ticket(Double value_ticket) {
		this.value_ticket = value_ticket;
	}

	public String getId_vehicle() {
		return id_vehicle;
	}

	public void setId_vehicle(String id_vehicle) {
		this.id_vehicle = id_vehicle;
	}

	public String getId_employee() {
		return id_employee;
	}

	public void setId_employee(String id_employee) {
		this.id_employee = id_employee;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestiny() {
		return destiny;
	}

	public void setDestiny(String destiny) {
		this.destiny = destiny;
	}
}
