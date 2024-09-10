package application;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Department department = new Department(1, "Livro");
		System.out.println(department);
		
		try {
			Seller seller = new Seller(1,"Fabio","fabio.tritono@gmail.com",sdf.parse("14/09/1996"),1250.90,department);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		/*
		 * Desta forma, o meu programa não conhece a implementação. Conhece somente a interface.
		 * É uma forma de fazer injeção de dependências
		 */
		SellerDao sellerDao = DaoFactory.createSellerDao();
		System.out.println(sellerDao);

	}

}
 