package chess;

import java.util.ArrayList;
import java.util.List;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	
	List<Piece> piecesOnTheBoard = new ArrayList<>();
	List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initalsetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getcurrentPlayer() {
		return currentPlayer;
	}

	// Retorna uma matriz de peças correspondente a essa partida
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public ChessPiece performChessPiece(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePostion(source);
		validateTargetPostion(source, target);
		Piece capturePiece = makeMove(source, target);
		nextTurn();//chamada para trucar o turno 
		return (ChessPiece)capturePiece;
		}
	
	//PAra indicar as possiveis direçoes das peças 
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position postion = sourcePosition.toPosition();// converntendo a posição de xadrez em uma posição de matriz normal
		validateSourcePostion(postion);//validar a posição após o usuaria entra com ela
		return board.piece(postion).possibleMove();//para poder imprimir as posiçoes possivel apartir da posição de origem
	}
	
	private Piece makeMove(Position source, Position target) {
		Piece p = board.removedPiece(source);
		Piece capturedPiece = board.removedPiece(target);
		board.placePiece(p, target);
		
		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
		return capturedPiece;
	}
	
	private void validateSourcePostion(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("there is no piece on source porition");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece isn't yours");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There no possible moves for the chosen piece ");
		}
	}
	
	private void validateTargetPostion(Position source, Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to traget possition ");
		}
	}
	
	//Metodo para troca de turno
	public void nextTurn() {
		turn++; // incrementando o turno 
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
		// expressâo condiçional ternaria para mudar a vez do jogador 
		//? = então, : caso contrario
	}
	

	// Metodo para instaciar as peças do xadrez informando as cordenadas no sistema
	// do xadrez e não no sistema da matriz
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	// Metodo para o setup inicial da partida de xadrez
	private void initalsetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
		placeNewPiece('c', 2, new Rook(board, Color.WHITE));
		placeNewPiece('d', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 1, new Rook(board, Color.WHITE));
		placeNewPiece('d', 1, new King(board, Color.WHITE));

		placeNewPiece('c', 7, new Rook(board, Color.BLACK));
		placeNewPiece('c', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
