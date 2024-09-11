package application;

import java.util.Date;
import java.util.List;

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
		//do método (return Seller) sellerDao.findById(int id) que já faz TODO o trabalho de receber os dados
		//em um ResultSet e armazenar num objeto do tipo seller. 
		Seller seller = sellerDao.findById(3);
		
		System.out.println(seller);
		
		Department dep = new Department(2,null);
		List<Seller> list = sellerDao.findByDepartment(dep);
		for(Seller s : list) {
			System.out.println(s);
		}
		
		List<Seller> allSellers = sellerDao.findAll();
		for(Seller s : allSellers) {
			System.out.println(s);
		}
		
		System.out.println("---------------------------- TESTE INSERT ----------------------------------");
		
		Seller vendedor = new Seller(null, "Ana Britto","ana.tritono@gmail.com",new Date(), 3500.90,new Department(1,null));
		sellerDao.insert(vendedor);
	}

}
 