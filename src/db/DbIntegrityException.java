package db;

public class DbIntegrityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/*
	 * Classe criada pra lidar com a integridade dos dados no ato de delação.
	 * Ou seja, pra que eu não tenha o erro de deletar algo que é referenciado
	 * em outro ponto do banco de dados e da aplicação
	 */
	
	public DbIntegrityException(String msg) {
		super(msg);
	}
}
