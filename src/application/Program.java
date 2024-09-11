package application;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		
		/*
		 * Desta forma, o meu programa não conhece a implementação. Conhece somente a interface.
		 * É uma forma de fazer injeção de dependências
		 */
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		//Isso aqui é SENSACIONAL. Porque eu já instancio um objeto Seller que recebe o retorno
		//do método sellerDao.findById(int id) que já faz TODO o trabalho de receber os dados
		//em um ResultSet e armazenar num objeto do tipo seller. 
		Seller seller = sellerDao.findById(3);
		
		System.out.println(sellerDao);

	}

}
 