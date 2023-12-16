package boardgame;

public class BoardExpection extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public BoardExpection (String msg) {
		super(msg);
	}
}
