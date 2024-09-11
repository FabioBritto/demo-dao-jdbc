package model.dao;

import db.DB;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {

	//A classe expõe um método que retorna o tipo da interface, mas internamente ela
	//instacia uma implementação
	public static SellerDao createSellerDao() {
		//Ele recebe como parâmetro o método que abre a conexão com o banco de dados
		return new SellerDaoJDBC(DB.getConnection());
	}
}
