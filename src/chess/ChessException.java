package chess;

import boardgame.BoardExpection;

public class ChessException extends BoardExpection{

	private static final long serialVersionUID = 1L;
	
	public ChessException(String  msg) {
		super(msg);
	}

}
