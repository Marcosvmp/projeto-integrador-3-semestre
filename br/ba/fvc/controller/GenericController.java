package br.ba.fvc.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import br.ba.fvc.dao.GenericDAO;

public class GenericController {

	public GenericDAO dao;
	private DefaultTableModel tableModel;
	private String table;

	public GenericController(String table, String[] columns, String fields) {
		this.dao = new GenericDAO(table, fields);
		this.tableModel = new DefaultTableModel(null, columns);
		this.table = table;
	}

	public DefaultTableModel all() {
		ResultSet result = null;
		try {
			result = this.dao.all();

			this.addRow(result);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this.tableModel;
	}

	public DefaultTableModel store(Object[] data) {
		StringBuilder addQuotationMarks = new StringBuilder();
		try {
			for (Object values : data) {
				addQuotationMarks.append("'" + values + "'" + ",");
			}

			Boolean isVenda = this.table.contains("venda");

			Boolean isRouter = this.table.contains("rota");

			Boolean isVehicle = this.table.contains("veiculo");

			addQuotationMarks.deleteCharAt(addQuotationMarks.length() - 1);

			this.dao.store(addQuotationMarks);

			if (!isVenda || !isRouter || !isVehicle) {
				this.tableModel = this.all();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this.tableModel;
	}

	public DefaultTableModel filter(String field, String values) {
		ResultSet result = null;
		try {

			result = this.dao.filter(field, values);

			this.addRow(result);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this.tableModel;
	}

	public DefaultTableModel destroy(String id) {
		try {

			this.dao.destroy(id);

			Boolean isVenda = this.table.contains("venda");

			Boolean isRouter = this.table.contains("rota");

			Boolean isVehicle = this.table.contains("veiculo");

			if (!isVenda || !isRouter || !isVehicle) {
				this.tableModel = this.all();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this.tableModel;
	}

	public ResultSet index(String id) {
		ResultSet result = null;
		try {

			result = this.dao.index(id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public DefaultTableModel update(Object[] fields, Object[] data, String id) {
		StringBuilder removeKey = new StringBuilder();
		try {
			HashMap<Object, Object> values = new HashMap<>();

			int count = 0;
			for (Object object : data) {
				values.put(fields[count], "'" + object + "'");
				count++;
			}
			removeKey.append(values);
			removeKey.deleteCharAt(-0);
			removeKey.deleteCharAt(removeKey.length() - 1);

			this.dao.update(removeKey.toString(), id);

			Boolean isVehicle = this.table.contains("veiculo");

			Boolean isRouter = this.table.contains("rota");

			if (!isRouter || !isVehicle) {
				this.tableModel = this.all();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this.tableModel;
	}

	public DefaultTableModel addRow(ResultSet result) {
		this.tableModel.setRowCount(0);
		try {
			while (result.next()) {
				Object[] row = new Object[result.getMetaData().getColumnCount()];

				for (int i = 0; i < row.length; i++) {
					row[i] = result.getObject(i + 1);
				}
				this.tableModel.addRow(row);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this.tableModel;
	}

	public ArrayList<String> addCombobox(ResultSet result) {
		ArrayList<String> values = new ArrayList<>();
		try {
			while (result.next()) {
				values.add(result.getInt(0 + 1) + " = " + result.getString(2));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return values;
	}

	public static Boolean validateFieldsEmpty(Object[][] fields) {
		Boolean error = false;
		try {
			for (int i = 0; i < fields.length; i++) {
				for (int j = 0; j < fields[i].length; j++) {
					if (fields[i][j] == null || fields[i][j].toString().isEmpty() || fields[i][j].equals(-1)) {
						error = true;
						throw new Exception("Campo (" + fields[i][0].toString().toUpperCase() + ") obrigatório!");
					}
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ATENÇÃO ", JOptionPane.ERROR_MESSAGE);
		}
		return error;
	}

	public static String crypto(String password) {
		String cryptoPasssword = null;
		try {
			MessageDigest crypto = MessageDigest.getInstance("MD5"); 

			crypto.update(password.getBytes(), 0, password.length());

			cryptoPasssword = new BigInteger(1, crypto.digest()).toString(16);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ATENÇÃO ", JOptionPane.ERROR_MESSAGE);
		}
		return cryptoPasssword;
	}
}