package application;

import chess.ChessMatch;

public class Program {

	public static void main(String[] args) {
		
		ChessMatch chessMatch = new ChessMatch();
		// Ciado uma função para imprir as peças da partida 
		UI.printBoard(chessMatch.getPieces());
	}

}
