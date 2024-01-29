package br.ba.fvc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import br.ba.fvc.database.connection.ConnectionMysql;

public class RotaDAO {
	Connection connection = null;

	public RotaDAO() {
		this.connection = new ConnectionMysql().getConnectionMysql();
	}

	public ResultSet all() {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();

			String query = "SELECT rota.id, funcionario.nome, veiculo.placa, DATE_FORMAT(rota.data_partida, '%d/%m/%Y %H:%i') AS data_formatada, origem.nome AS origem, destino.nome AS destino FROM rota "
					+ "INNER JOIN funcionario ON rota.id_funcionario = funcionario.id "
					+ "INNER JOIN veiculo ON rota.id_veiculo = veiculo.id "
					+ "INNER JOIN cidade AS origem ON rota.cidade_origem = origem.id "
					+ "INNER JOIN cidade AS destino ON rota.cidade_destino = destino.id "
					+ "WHERE funcionario.cargo = UPPER('motorista') ORDER BY id DESC";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public ResultSet comboboxemployee() {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();

			String query = "SELECT id, nome FROM funcionario WHERE cargo = UPPER('motorista')";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public ResultSet comboboxCity() {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();

			String query = "SELECT id, nome FROM cidade";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public ResultSet comboboxVehicle() {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();

			String query = "SELECT id, CONCAT(numero ,' / ', placa, ' / ', modelo) veiculo FROM veiculo";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public ResultSet verifyDateExists(String date, String id_vehicle) {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();

			String query = "SELECT DATE_FORMAT(data_partida, '%Y-%m-%d') AS data_partida, id_veiculo FROM rota WHERE DATE_FORMAT(data_partida, '%Y-%m-%d') = "
					+ "'" + date + "' AND id_veiculo = '" + id_vehicle + "' ";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public ResultSet filterRouter(String origin) {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();

			String query = "SELECT rota.id, funcionario.nome, veiculo.placa, DATE_FORMAT(rota.data_partida, '%d/%m/%Y %T') AS data_formatada, origem.nome AS origem, destino.nome AS destino FROM rota "
					+ "INNER JOIN funcionario ON rota.id_funcionario = funcionario.id "
					+ "INNER JOIN veiculo ON rota.id_veiculo = veiculo.id "
					+ "INNER JOIN cidade AS origem ON rota.cidade_origem = origem.id "
					+ "INNER JOIN cidade AS destino ON rota.cidade_destino = destino.id " + " WHERE origem.nome LIKE "
					+ "'%" + origin + "%' AND funcionario.cargo = UPPER('motorista')";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}
}
