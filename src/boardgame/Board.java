package boardgame;

import chess.ChessPiece;

public class Board {
	
	private int rows;
	private int columns;
	private Piece[][] pieces;
	
	public Board(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		//Instaciar a matriz de peças na quantidade de linha informadas e colunas
		pieces = new Piece[rows][columns];
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	//Criando o metodo para retornar a peça dado uma linha e uma coluna 
	public ChessPiece piece(int row, int column) {
		return (ChessPiece) pieces[row][column];
	}
	//Criando uma sobrecargar do metodo anterior recebendo Position position para retorna a peça pela positição
	public Piece piece(Position position) {
		return pieces[position.getRow()][position.getColumn()];
	}
	
}
