package player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chess.Board;
import chess.Piece;
import chess.Square;

public class BoardPieceValueHeuristic implements Heuristic<Board>{
    private static final Map<Piece.Type, Float> PIECE_VALUES;
    
    static {
        Map<Piece.Type, Float> pieceValues = new HashMap<Piece.Type, Float>(); 
        pieceValues.put(Piece.Type.PAWN, 1.0f);
        pieceValues.put(Piece.Type.KNIGHT, 3.0f);
        pieceValues.put(Piece.Type.BISHOP, 3.2f);
        pieceValues.put(Piece.Type.ROOK, 5.0f);
        pieceValues.put(Piece.Type.QUEEN, 9.0f);
        // TODO(jasonpr): Figure out what we should do about King's value.
        pieceValues.put(Piece.Type.KING, 1000.0f);
        PIECE_VALUES = Collections.unmodifiableMap(pieceValues);
    }
    
    @Override
    public float value(Board board) {
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
            mult = (p.getColor() == Piece.Color.WHITE) ? +1.0f : -1.0f;
            totalScore += pieceScore * mult;
        }
        return totalScore;
    }
}
