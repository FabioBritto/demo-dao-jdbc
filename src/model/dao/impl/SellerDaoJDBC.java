package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		//Aqui eu não preciso criar um objeto Connection, porque o nosso DAO, terá uma DEPENDÊNCIA COM A CONEXÃO
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " +
					"FROM seller INNER JOIN department " +
					"ON seller.DepartmentId = department.Id " +
					"WHERE Department.Id = ? " +
					"ORDER BY Name");
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			/*
			 * Aqui, diferente do método findById, eu estou buscando pelo ID do Department, ou seja,
			 * eu posso ter mais 0ZERO ou mais resultados. Por isso, eu troco o IF por um WHILE, pra
			 * garantir que acharei todos os correspondentes
			 */
			
			
			/*
			 * Para eu garantir que não estarei instanciando novos departamentes, eu faço o seguinte:
			 * 1 - Eu preciso criar um MAP pra garantir que haja apenas um correspondente entre o ID e
			 * a instância do Department em si.
			 * 2 - Eu declaro uma variável do tipo Department que recebe o retorno do método get() buscando
			 * o ID resultante do ResultSet naquela linha
			 * 3 - Caso o valor de dep seja null, ele adiciona esse dep ao map, para que não haja mais correspondências
			 */
			
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
		return null;
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
