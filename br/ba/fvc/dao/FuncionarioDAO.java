package br.ba.fvc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import br.ba.fvc.database.connection.ConnectionMysql;

public class FuncionarioDAO {

	Connection connection = null;

	public FuncionarioDAO() {
		this.connection = new ConnectionMysql().getConnectionMysql();
	}

	public ResultSet login(String email, String senha) {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();

			String query = "SELECT * FROM funcionario WHERE email = '" + email + "' AND senha = '" + senha + "' ";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public ResultSet verifyCpfExists(String cpf, String email) {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();

			String query = "SELECT cpf, email FROM funcionario WHERE cpf = " + "'" + cpf + "' OR email = '" + email + "' ";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}
}