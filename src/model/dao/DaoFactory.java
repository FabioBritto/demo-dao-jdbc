package model.dao;

import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {

	//A classe expõe um método que retorna o tipo da interface, mas internamente ela
	//instacia uma implementação
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC();
	}
}
