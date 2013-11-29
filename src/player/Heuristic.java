package player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chess.Board;
import chess.Piece;
import chess.Square;

public class Heuristic {
    private static final Map<Piece.PieceType, Float> PIECE_VALUES;
    
    static {
        Map<Piece.PieceType, Float> pieceValues = new HashMap<Piece.PieceType, Float>(); 
        pieceValues.put(Piece.PieceType.PAWN, 1.0f);
        pieceValues.put(Piece.PieceType.KNIGHT, 3.0f);
        pieceValues.put(Piece.PieceType.BISHOP, 3.2f);
        pieceValues.put(Piece.PieceType.ROOK, 5.0f);
        pieceValues.put(Piece.PieceType.QUEEN, 9.0f);
        // TODO(jasonpr): Figure out what we should do about King's value.
        pieceValues.put(Piece.PieceType.KING, 1000.0f);
        PIECE_VALUES = Collections.unmodifiableMap(pieceValues);
    }
    
    public static float pieceValueHeuristic(Board board) {
        float totalScore = 0.0f;
        Piece p;
        float pieceScore;
        float mult;
        for (Square square : Square.ALL) {
            p = board.getPiece(square);
            if (p == null) {
                continue;
            }
            pieceScore = PIECE_VALUES.get(p.getType());
            mult = (p.getPieceColor() == Piece.PieceColor.WHITE) ? +1.0f : -1.0f;
            totalScore += pieceScore * mult;
        }
        return totalScore;
    }
}
