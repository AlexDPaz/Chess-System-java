package boardgame;

import chess.ChessPiece;

public class Board {
	
	private int rows;
	private int columns;
	private Piece[][] pieces;
	
	public Board(int rows, int columns) {
		// criando programação defensivos
		if (rows < 1 || columns < 1) {
			throw new BoardExpection("Error createing board: there must be at least 1 row and 1 column");
		}
		this.rows = rows;
		this.columns = columns;
		//Instaciar a matriz de peças na quantidade de linha informadas e colunas
		pieces = new Piece[rows][columns];
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}
	//Criando o metodo para retornar a peça dado uma linha e uma coluna 
	public Piece piece(int row, int column) {
		// criando programação defensivos
		if(!positionExists(row, column)) {
		throw new BoardExpection("Position not on the board ");
		}
		return (ChessPiece) pieces[row][column];
	}
	
	//Criando uma sobrecargar do metodo anterior recebendo Position position para retorna a peça pela positição
	public Piece piece(Position position) {
		if(!positionExists(position)) {
			throw new BoardExpection("Position not on the board ");
			}
		return pieces[position.getRow()][position.getColumn()];
	}
	
	//Metodo para posicionar a peça no tabuleiro 
	public void placePiece(Piece piece, Position position) {
		if (thereIsAPiece(position)) {
			throw new BoardExpection("Thre is already a piece on position " + position);
		}
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;	
	}
	
	public Piece removedPiece(Position position) {
		if(!positionExists(position)) {
			throw new BoardExpection("Position not on the board");
		}
		if (piece(position) == null) {
			return null;
		}
		Piece aux = piece(position);
		aux.position = null;
		pieces[position.getRow()][position.getColumn()] = null;
		return aux;
	}
	//Metodo auxiliar para dado momento que sera mais facil testar pela linha e pela coluna
	private boolean positionExists(int row, int column) {
		return row >=0 && row < rows && column >=0 && column < columns; 
	}
	
	public boolean positionExists(Position position) {
		return positionExists(position.getRow(), position.getColumn());
	}
	
	public boolean thereIsAPiece(Position position) {
		if(!positionExists(position)) {
			throw new BoardExpection("Position not on the board ");
			}
		return piece(position) != null;
	}
	
	
}
