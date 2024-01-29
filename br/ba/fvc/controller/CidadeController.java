package br.ba.fvc.controller;

import java.sql.ResultSet;

import javax.swing.JOptionPane;

import br.ba.fvc.dao.CidadeDAO;

import javax.swing.table.DefaultTableModel;

public class CidadeController {

	private String nome;
	private String uf;
	private GenericController generic;
	private String tabela = "cidade";
	public String[] colunas = { "ID", "Nome", "UF" };
	private String campos = "nome, uf";
	private CidadeDAO dao;

	public CidadeController() {
		this.generic = new GenericController(tabela, colunas, campos);
		this.dao = new CidadeDAO();

	}

	public DefaultTableModel listar() {
		DefaultTableModel result = null;
		try {

			result = this.generic.all();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public DefaultTableModel incluir() {
		DefaultTableModel result = null;
		ResultSet resultSet = null;
		try {
			resultSet = this.dao.verifyCidadeExists(nome);

			if (resultSet.next()) {
				if (resultSet.getString("nome").equals(nome.toUpperCase())) {
					throw new Exception("Cidade já cadastrada!");
				}
			}

			Object[] data = { nome.toUpperCase(), uf.toUpperCase() };
			result = this.generic.store(data);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public DefaultTableModel excluir(String id) {
		DefaultTableModel result = null;
		try {

			result = this.generic.destroy(id);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public DefaultTableModel filtrar() {
		DefaultTableModel result = null;
		try {

			String campo_filtro = "nome";
			
			result = this.generic.filter(campo_filtro, nome);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public DefaultTableModel alterar(String id) {
		DefaultTableModel result = null;
		try {
			Object[] fields = { "nome", "uf" };
			Object[] data = { nome.toUpperCase(), uf.toUpperCase() };

			result = this.generic.update(fields, data, id);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		return result;
	}

	public ResultSet carregaCamposAlterar(String id) {
		ResultSet result = null;
		try {

			result = this.generic.index(id);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}