package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller " +
					"(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
					"VALUES " +
					"(?,?,?,?,?)",
					//Aqui eu estou retornando o meu ID do novo Seller
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			//Faço isso pra descobrir se alguma linha no banco foi de fato alterada
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					//Aqui eu já estou na primeira linha com dados. O 1UM passado como parâmetro
					//serve para que eu acesse o primeiro dado, que é justamente o ID
					int id = rs.getInt(1);
					/*
					 * Aqui eu seto o valor de ID que é gerado AUTOMATICAMENTE no banco de dados,
					 * e seto como o id do objeto Seller que estou trabalhando no momento.
					 * Como o dado de ID não é gerado por mim e sim pelo banco, preciso fazer isso pra setar
					 */
					obj.setId(id);
				}
				DB.closeRestultSet(rs);
			}
			else {
				throw new DbException("Erro Inesperado. Nenhuma linha do banco foi afetada!");
			}
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller " +
					"SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " +
					"WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected == 0) {
				System.out.println("Nenhuma linha afetada!");
			}
			else {
				System.out.println("Deletado com sucesso");
			}
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
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
				Department department = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs, department);
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
	public List<Seller> findByDepartment(Department department){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " +
					"FROM seller INNER JOIN department " +
					"ON seller.DepartmentId = department.Id " +
					"WHERE Department.Id = ? " +
					"ORDER BY Name");
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			List<Seller> sellerList = new ArrayList<>();
			Map<Integer,Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				if(dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				sellerList.add(instantiateSeller(rs, dep));
			}
			return sellerList;			
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
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " +
					"FROM seller INNER JOIN department " +
					"ON seller.DepartmentId = department.Id " +
					"ORDER by Name");
			
			rs = st.executeQuery();
			List<Seller> sellerList = new ArrayList<>();
			Map<Integer,Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				if(dep == null) {
					 dep = instantiateDepartment(rs);
					 map.put(rs.getInt("DepartmentId"), dep);
				}
				sellerList.add(instantiateSeller(rs, dep));
			}
			return sellerList;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeRestultSet(rs);
		}
	}
	
	/*
	 * --Nos dois métodos a seguir, eu passo como parâmentro o ResultSet, porque ele carrega
	 * a tabela com os dados que preciso.
	 * ------ATENÇÃO------
	 * ALÉM DISSO...
	 * Esse método a princípio geraria uma EXCEÇÃO. Mas eu não quero retratá-la aqui. Eu só preciso
	 * lançá-la, pois o método que a chamou já a trata
	 */
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		/*
		 * Aqui eu estou criando uma variável temporária do tipo Department, para que
		 * eu possa armazenar o resultado do setId(). Esse método recebendo como parâmetro
		 *o rs.getInt(), me permite passar UMA STRING com o nome da coluna que quero pegar o dado
		 */
		Department department = new Department();
		department.setId(rs.getInt("DepartmentId"));
		department.setName(rs.getString("DepName"));
		return department;
	}
	
	//Esse método pode ser PRIVATE, porque ele será chamado APENAS por meio de um método público
	private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setDepartment(department);
		return seller;
	}

	
}
