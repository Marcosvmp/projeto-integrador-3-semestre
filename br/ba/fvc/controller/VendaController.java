package br.ba.fvc.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import br.ba.fvc.dao.VendaDAO;

public class VendaController {

	private String name_passenger;
	private String cpf;
	private String router;
	private String id_router;
	private String id_vehicle;
	private String armchair;
	private String de;
	private String ate;
	private VendaDAO dao;
	private GenericController generic;
	private String table = "venda";
	private String[] columns = { "ID", "Nome", "CPF", "Origem", "Destino", "Data Partida" };
	private String fields = "nome_passageiro, cpf, poltrona, id_rota";

	public VendaController() {
		this.generic = new GenericController(table, columns, fields);
		this.dao = new VendaDAO();
	}

	public DefaultTableModel all() {
		DefaultTableModel result = new DefaultTableModel();
		ResultSet resultSet = null;
		try {

			resultSet = this.dao.allSale();

			result = this.generic.addRow(resultSet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public ArrayList<String> comboBoxRouter() {
		ArrayList<String> values = new ArrayList<>();
		ResultSet resultSet = null;
		try {
			resultSet = this.dao.comboBoxRouter();

			values = this.generic.addCombobox(resultSet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return values;
	}

	public ResultSet loadComboBox(String id) {
		ResultSet result = null;
		try {

			result = this.dao.loadComboBox(id);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public ResultSet countArmchair(String id) {
		ResultSet result = null;
		try {

			result = this.dao.countArmchair(id);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public ArrayList<String> comboboxArmchair(String id) {
		ArrayList<String> values = new ArrayList<>();
		ResultSet resultSet = null;
		try {
			resultSet = this.dao.comboboxArmchair(id);

			values = this.generic.addCombobox(resultSet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return values;
	}

	public ResultSet getCombobox(String id) {
		ResultSet result = null;
		try {

			result = this.dao.getArmchair(id);
			result.next();
			result = this.dao.getVehicle(result.getString("id_veiculo"));
			result.next();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public DefaultTableModel store() {
		DefaultTableModel result = null;
		ResultSet model = null;
		try {
			model = this.dao.where("veiculo", "id", id_vehicle);

			if (model.next()) {
				if (Integer.parseInt(this.armchair) > Integer.parseInt(model.getString("quantidade_poltronas"))) {
					throw new Exception("Número da poltrona informado é maior que o número total para o veiculo!");
				}
			}

			Object[] data = { name_passenger, cpf, this.armchair, id_router };
			result = this.generic.store(data);

			this.all();
			this.generatePdf();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public void generatePdf() {
		ResultSet result = null;
		ResultSet sale = null;

		Rectangle sizePage = new Rectangle(320, 280);
		Document document = new Document(sizePage);
		Paragraph paragraph = new Paragraph();

		try {
			result = this.dao.where("venda", "cpf", cpf);

			if (result.next()) {
				sale = this.dao.getSale(result.getString("id"));
				if (sale.next()) {

					Timestamp name_file = new Timestamp(System.currentTimeMillis());
					File currentPath = new File("");
					String path = currentPath.getAbsolutePath();

					if (!Files.exists(Paths.get(path + "\\src\\public\\storage\\"))) {
						Files.createDirectories(Paths.get(path + "\\src\\public\\storage\\"));
					}

					PdfWriter.getInstance(document,
							new FileOutputStream(path + "\\src\\public\\storage\\" + name_file.getTime() + ".pdf"));
					document.open();

					Image figura = Image.getInstance(path + "\\src\\public\\logo-dark.png");
					figura.setAlignment(Element.ALIGN_CENTER);
					document.add(figura);

					paragraph.add("Olá: " + sale.getString("nome_passageiro"));
					paragraph.setAlignment(Element.ALIGN_CENTER);
					document.add(paragraph);
					document.add(Chunk.NEWLINE);
					document.add(new Paragraph("Origem: " + sale.getString("origem")));
					document.add(new Paragraph("Destino: " + sale.getString("destino")));
					document.add(new Paragraph("Data / Hora partida: " + sale.getString("hora_data")));
					document.add(new Paragraph("Poltrona: " + sale.getString("poltrona")));
					document.add(new Paragraph("Número Veiculo: " + sale.getString("numero")));

					URI url = currentPath.toURI();

					Desktop.getDesktop()
							.browse(new URI(url.toString() + "src/public/storage/" + name_file.getTime() + ".pdf"));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		document.close();
	}

	public DefaultTableModel destroy(String id) {
		DefaultTableModel result = null;
		try {

			ResultSet resultSet = this.dao.getSale(id);

			if (resultSet.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				LocalDateTime convertDate = LocalDateTime.parse(resultSet.getString("hora_data"), formatter);

				LocalDateTime now = LocalDateTime.now();

				if (now.compareTo(convertDate) > 0) {
					throw new Exception("Passagem não pode ser cancelada, data da viagem já passou!");
				}
			}
			result = this.generic.destroy(id);
			this.all();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;

	}

	public DefaultTableModel filterRouter() {
		DefaultTableModel result = null;
		ResultSet resultSet = null;
		try {

			resultSet = this.dao.filterRouter(router);

			result = this.generic.addRow(resultSet);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public ResultSet filterPeriod() {
		ResultSet result = null;
		try {

			result = this.dao.filterPeriod(de, ate);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return result;
	}

	public String getName_passenger() {
		return name_passenger;
	}

	public void setName_passenger(String name_passenger) {
		this.name_passenger = name_passenger;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getId_router() {
		return id_router;
	}

	public void setId_router(String id_router) {
		this.id_router = id_router;
	}

	public String getId_vehicle() {
		return id_vehicle;
	}

	public void setId_vehicle(String id_vehicle) {
		this.id_vehicle = id_vehicle;
	}

	public String getArmchair() {
		return armchair;
	}

	public void setArmchair(String armchair) {
		this.armchair = armchair;
	}

	public String getRouter() {
		return router;
	}

	public void setRouter(String router) {
		this.router = router;
	}

	public String getDe() {
		return de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	public String getAte() {
		return ate;
	}

	public void setAte(String ate) {
		this.ate = ate;
	}
}
