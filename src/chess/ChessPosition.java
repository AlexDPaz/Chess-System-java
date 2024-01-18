package chess;

import boardgame.Position;

public class ChessPosition {
	
	private char column;
	private int row;
	
	public ChessPosition() {
	}

	public ChessPosition(char column, int row) {
		// Criando programação defenciva
		if(column < 'a'  || column > 'h' || row < 0 || row > 8) {
			throw new ChessException("Error instantiating position, valid values are from a1 to h8. ");
		}
		this.column = column;
		this.row = row;
	}

	public char getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	
	protected Position toPosition() {
		return new Position(8 - row, column - 'a');
	}
	
	protected static ChessPosition fromPosition(Position posion) {
		return new ChessPosition((char)('a' + posion.getColumn()), 8 - posion.getRow());
	}
	
	@Override
	public String toString() {
		return "" + column + row;
		//Macete para concatenar String de forma automatica 
	}

}
