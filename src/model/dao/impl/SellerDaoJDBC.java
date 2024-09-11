package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	//Dessa forma, o conn fica disponível pra classe inteira
	private Connection conn;
	
	//Pra forçar a Injeção de Dependência, crio um construtor
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) {
		
		
	}

	@Override
	public void update(Seller obj) {
		
		
	}

	@Override
	public void deleteById(Integer id) {
		
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		//Aqui eu não preciso criar um objeto Connection, porque o nosso DAO, terá uma DEPENDÊNCIA COM A CONEXÃO
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " +
					"FROM seller INNER JOIN department " +
					"ON seller.DepartmentId = department.Id " +
					"WHERE seller.Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			/*
			 * Agora preciso armazenar esses dados recebidos em forma de tabela em um OBJETO SELLER
			 * --Quando eu uso o ResultSet, por natureza, ele aponta para a posiçao 0ZERO, como se
			 * fosse o cabeçalho da tabela. Lá, não há os dados da pesquisa. Preciso avançar para a
			 * posição .
			 * Portanto, preciso testar se essa busca me retornou algum resultado, usando rs.next()
			 */
			if(rs.next()) {
				//Aqui eu estou criando uma variável temporária do tipo Department, para que
				//eu possa armazenar o resultado do setId(). Esse método recebendo como parâmetro
				//o rs.getInt(), me permite passar UMA STRING com o nome da coluna que quero pegar o dado
				Department department = new Department();
				department.setId(rs.getInt("DepartmentId"));
				department.setName(rs.getString("DepName"));
				Seller seller = new Seller();
				seller.setId(rs.getInt("Id"));
				seller.setName(rs.getString("Name"));
				seller.setEmail(rs.getString("Email"));
				seller.setBaseSalary(rs.getDouble("BaseSalary"));
				seller.setBirthDate(rs.getDate("BirthDate"));
				seller.setDepartment(department);
				return seller;
			}
			return null;			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeRestultSet(rs);
		}
	}

	@Override
	public List<Seller> findAll() {
		
		return null;
	}

	
}
