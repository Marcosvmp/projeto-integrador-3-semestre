package br.ba.fvc.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMysql {

	public Connection getConnectionMysql() {
		Connection connection = null;
		try {

			String url = "jdbc:mysql://localhost/ticket_bus?user=root&password=";
			connection = DriverManager.getConnection(url);
		} catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return connection;
	}

}
