package br.ba.fvc.dao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import br.ba.fvc.database.connection.ConnectionMysql;

public class CidadeDAO {
    Connection connection = null;
    
    public CidadeDAO() {
		this.connection = new ConnectionMysql().getConnectionMysql();
	}


    public ResultSet verifyCidadeExists(String nome) {
		ResultSet result = null;

		try {
			Statement instance = this.connection.createStatement();
			
			String query = "SELECT * FROM cidade WHERE nome = UPPER('" + nome + "')";

			result = instance.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}
}
