package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;


	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
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

	/*public ChessPiece performChessPiece(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePostion(source);
		validateTargetPostion(source, target);
		Piece capturePiece = makeMove(source, target);

		// teste para ver se movimento feito não colocou em check o jogador
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturePiece);
			throw new ChessException("You can't put yourself in check");
		}
		

		check = (testCheck(opponent(currentPlayer))) ? true : false; // feito um expressão condicional ternaria para
																		// verificar se o oponente ficou em check

		if (testCheckMate(opponent(currentPlayer))) { // testar se a jogar realizada deixou o oponente em checkmate o
														// jogo tem que acabar se não a partida continua
			checkMate = true;
		} else {
			nextTurn();// chamada para trucar o turno
		}
		return (ChessPiece) capturePiece;
	}*/

	// PAra indicar as possiveis direçoes das peças
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();// converntendo a posição de xadrez em uma posição de matriz
														// normal
		validateSourcePostion(position);// validar a posição após o usuaria entra com ela
		return board.piece(position).possibleMove();// para poder imprimir as posiçoes possivel apartir da posição de
													// origem
	}


	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePostion(source);
		validateTargetPostion(source, target);
		Piece capturedPiece = makeMove(source, target);

		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);

		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		else {
			nextTurn();			
		}
		
		//#specialmove en passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() -2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		}
		else {
			enPassantVulnerable = null;
		}

		return (ChessPiece)capturedPiece;
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.removedPiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removedPiece(target);
		board.placePiece(p, target);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}

		// #specialmove castling kingsize rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removedPiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		// #specialmove castling queensize rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removedPiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		// #specialmove en passant
		if (p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removedPiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}

		return capturedPiece;
	}

	// methodo para desfazer caso o usuario se coloq ue em check
	private void undoMove(Position source, Position target, Piece capturePiece) {
		ChessPiece p = (ChessPiece)board.removedPiece(target);// remove a peça que foi ovido para destino
		p.decreaseMoveCount();
		board.placePiece(p, source);// devolve para posião de origem
		// laço para devolver a peça capiturada para posição de destino
		if (capturePiece != null) {
			board.placePiece(capturePiece, target);
			capturedPieces.remove(capturePiece);// tira a peça da lista de peças capturada e devolve para lista de peças
												// do tabuleiro
			piecesOnTheBoard.add(capturePiece);
		}

		// #specialmove castling kingsize rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removedPiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}

		// #specialmove castling queensize rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removedPiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		// #specialmove en passant
				if (p instanceof Pawn) {
					if(source.getColumn() != target.getColumn() && capturePiece == enPassantVulnerable) {
						ChessPiece pawn = (ChessPiece)board.removedPiece(target);
						Position pawnPosition;
						if (p.getColor() == Color.WHITE) {
							pawnPosition = new Position(3, target.getColumn());
						}
						else {
							pawnPosition = new Position(4, target.getColumn());
						}
						board.placePiece(pawn, pawnPosition);
					}
				}
	}

	private void validateSourcePostion(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece isn't yours");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There no possible moves for the chosen piece ");
		}
	}

	private void validateTargetPostion(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to traget possition ");
		}
	}

	// Metodo para troca de turno
	public void nextTurn() {
		turn++; // incrementando o turno
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
		// expressâo condiçional ternaria para mudar a vez do jogador
		// ? = então, : caso contrario
	}

	// metodo para devolver o oponente de uma cor
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is not" + color + "King on the board");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentePieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentePieces) {
			boolean[][] mat = p.possibleMove();// tenho a matriz de movimentos possiveis desta peça adversaria p
			// laço para verificar de o rei esta em check
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMove();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) { // laço para verificar se tem movimento possivel e se o movimento tira do check
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true; // esgotando laço e não encontrar nenhum movimento possivel para sair do check
						// então retornar True para checkmate
	}

	// Metodo para instaciar as peças do xadrez informando as cordenadas no sistema
	// do xadrez e não no sistema da matriz
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	// Metodo para o setup inicial da partida de xadrez
	private void initialSetup() {

		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

		/*
		 * placeNewPiece('c', 1, new Rook(board, Color.WHITE)); placeNewPiece('c', 2,
		 * new Rook(board, Color.WHITE)); placeNewPiece('d', 2, new Rook(board,
		 * Color.WHITE)); placeNewPiece('e', 2, new Rook(board, Color.WHITE));
		 * placeNewPiece('e', 1, new Rook(board, Color.WHITE)); placeNewPiece('d', 1,
		 * new King(board, Color.WHITE));
		 * 
		 * placeNewPiece('c', 7, new Rook(board, Color.BLACK)); placeNewPiece('c', 8,
		 * new Rook(board, Color.BLACK)); placeNewPiece('d', 7, new Rook(board,
		 * Color.BLACK)); placeNewPiece('e', 7, new Rook(board, Color.BLACK));
		 * placeNewPiece('e', 8, new Rook(board, Color.BLACK)); placeNewPiece('d', 8,
		 * new King(board, Color.BLACK));
		 */
	}
}
