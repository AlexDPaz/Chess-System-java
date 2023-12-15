package chess;

import boardgame.Board;

public class ChessMatch {
	
	private Board board;
	
	public ChessMatch() {
		board = new Board(8, 8);
	}
	//Retorna uma matriz de peças correspondente a essa partida
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i<board.getRows(); i++) {
			for(int j = 0; j<board.getColumns(); i++) {
				mat[i][j] = (ChessPiece) board.piece(i, j) ; 
			}
		}
		return mat;
	}
}
