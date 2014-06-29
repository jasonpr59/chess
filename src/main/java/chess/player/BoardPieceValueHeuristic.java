package main.java.chess.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import main.java.chess.ChessPosition;
import main.java.chess.Square;
import main.java.chess.piece.Bishop;
import main.java.chess.piece.King;
import main.java.chess.piece.Knight;
import main.java.chess.piece.Pawn;
import main.java.chess.piece.Piece;
import main.java.chess.piece.Queen;
import main.java.chess.piece.Rook;
import main.java.player.EstimatedScore;
import main.java.player.Heuristic;

public class BoardPieceValueHeuristic implements Heuristic<ChessPosition>{
    // TODO: Figure out how to restrict this to classes that extend Piece.
    private static final Map<Class<?>, Float> PIECE_VALUES;

    static {
        Map<Class<?>, Float> pieceValues = new HashMap<Class<?>, Float>();
        pieceValues.put(Pawn.class, 1.0f);
        pieceValues.put(Knight.class, 3.0f);
        pieceValues.put(Bishop.class, 3.2f);
        pieceValues.put(Rook.class, 5.0f);
        pieceValues.put(Queen.class, 9.0f);
        // TODO(jasonpr): Figure out what we should do about King's value.
        pieceValues.put(King.class, 1000.0f);
        PIECE_VALUES = Collections.unmodifiableMap(pieceValues);
    }

    @Override
    public EstimatedScore value(ChessPosition board) {
        float totalScore = 0.0f;
        Piece p;
        float pieceScore;
        float mult;
        for (Square square : Square.ALL) {
            p = board.getPiece(square);
            if (p == null) {
                continue;
            }
            pieceScore = PIECE_VALUES.get(p.getClass());
            mult = (p.getColor() == Piece.Color.WHITE) ? +1.0f : -1.0f;
            totalScore += pieceScore * mult;
        }
        return new EstimatedScore(totalScore);
    }
}
