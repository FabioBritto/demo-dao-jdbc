package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {

	/*
	 * Métodos ESTÁTICOS auxiliares para abertura e fechamento do Banco de Dados
	 * Primeiro eu crio um método auxiliar pra carregar as propriedades contidas no arquivo "db.properties"
	 * Os métodos são estáticos porque não faz sentido lidar com instâncias nesse caso
	 * 
	 * -------------ATENÇÃO-----------------
	 * !!!CONECTAR COM O BANCO DE DADOS NO JDBC É INSTANCIAR UM OBJETO DO TIPO CONNECTION!!!
	 */
	
	private static Connection conn = null;
	
	public static Connection getConnection() {
		if(conn == null) {
			try {
				//Se for nulo, quer dizer que não está conectado com o banco, preciso pegar as PROPRIEDADES DE CONEXÇÃO
				//Abaixo, eu crio um objeto do tipo Properties, que recebe o return do método loadProperties()
				Properties props = loadProperties();
				//Aqui eu armazeno numa String a url tal qual está em "db.properties"
				String url = props.getProperty("dburl");
				//Atribuo à minha variável "conn" o valor de DriverManager.getConnection() que recebe como parâmetros
				//a String url(valor do conteúdo "dburl") e o objeto props (return do método loadProperties())
				conn = DriverManager.getConnection(url,props);
			}
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}
	
	//Método que fecha a conexão
	public static void closeConnection() {
		if(conn != null) {
			try {
				conn.close();
			}
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	//O método é PRIVATE porque só é utilizado aqui
	private static Properties loadProperties() {
		
		try(FileInputStream fs = new FileInputStream("db.properties")){
			Properties props = new Properties();
			//O comando "load()", faz a leitura do arquivo Properties apontado pelo meu Input fs
			//E guarda os dados dentro do objeto props
			props.load(fs);
			return props;
		}
		catch(IOException e) {
			//PORQUE EU FAÇO ISSO MESMO?
			//----------------------- RESPOSTA: ------------------------
			//A minha DbException deriva de RUNTIMEEXCEPTION, então meu programa não precisará ficar colocando TRY CATCH toda hora, só quando necessário
			//Além disso, posso ter uma Exceção Personalizada
			throw new DbException(e.getMessage());
		}
	}
	
	public static void closeStatement(Statement st) {
		if(st != null) {
			try {
				st.close();
			}
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	public static void closeRestultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			}
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
}
