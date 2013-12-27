package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chess.Piece.Type;

/** A pawn-promotion chess move. */
public class PromotionMove extends ChessMove {
    private final Type promotedType;
    private static final Set<Type> PROMOTION_TYPES;

    static {
        Set<Type> promotionTypes = new HashSet<Type>();
        promotionTypes.add(Type.KNIGHT);
        promotionTypes.add(Type.BISHOP);
        promotionTypes.add(Type.ROOK);
        promotionTypes.add(Type.QUEEN);
        PROMOTION_TYPES = Collections.unmodifiableSet(promotionTypes);
    }

    /** Create a PromotionMove from a normal move and a promoted Piece.Type. */
    public PromotionMove(ChessMove move, Type promotedType) {
        super(move.getStart(), move.getEnd());
        this.promotedType = promotedType;
    }

    public Type getPromotedType() {
        return promotedType;
    }

    /**
     * Return all PromotionMoves that a Move could beget.
     * That is, create four PromotionMoves from one ChessMove,
     * one for each promotion type (knight, bishop, rook, queen).
     */
    public static Collection<PromotionMove> allPromotions(ChessMove move) {
        List<PromotionMove> allPromotions = new ArrayList<PromotionMove>();
        for (Type type : PROMOTION_TYPES) {
            allPromotions.add(new PromotionMove(move, type));
        }
        return allPromotions;
    }

    @Override
    public boolean isSane(ChessPosition board) {
        Piece movingPiece = board.getPiece(getStart());
        if (movingPiece == null || movingPiece.getType() != Piece.Type.PAWN ||
                !PROMOTION_TYPES.contains(getPromotedType())) {
            return false;
        }

        int promotableRank;
        if (movingPiece.getColor() == Piece.Color.WHITE) {
            promotableRank = 8;
        } else {
            promotableRank = 1;
        }

        if (getEnd().getRank() != promotableRank) {
            return false;
        }

        return super.isSane(board);
    }

    @Override
    public ChessPosition result(ChessPosition position) {
        // Make the pawn move to the last rank, normally.
        ChessPosition partlyMoved = super.result(position);

        // Convert it to its promoted type.
        // TODO: Do this without creating a second builder for the promotion
        // step.  (The first one was in the super.result step.)
        ChessPositionBuilder builder = new ChessPositionBuilder(partlyMoved);
        Piece promotedPiece = new Piece(getPromotedType(),
                                        partlyMoved.getPiece(getEnd()).getColor());
        builder.placePiece(promotedPiece, getEnd());

        return builder.build();
    }

    /** Serialize this PromotionMove as a 5-character string. */
    @Override
    public String serialized() {
        String coords = super.serialized();
        String type;
        switch (getPromotedType()) {
        case KNIGHT:
            type = "N";
            break;
        case BISHOP:
            type = "B";
            break;
        case ROOK:
            type = "R";
            break;
        case QUEEN:
            type = "Q";
            break;
        default:
            throw new RuntimeException("Invalid promotion type.");
        }
        return coords + type;
    }

    /** Deserialize this move from a 5-character String. */
    public static PromotionMove deserialized(String s) {
        assert s.length() == 5;
        char typeChar = s.charAt(4);
        Piece.Type type;
        if (typeChar == 'N') {
            type = Piece.Type.KNIGHT;
        } else if (typeChar == 'B') {
            type = Piece.Type.BISHOP;
        } else if (typeChar == 'R') {
            type = Piece.Type.ROOK;
        } else if (typeChar == 'Q') {
            type = Piece.Type.QUEEN;
        } else {
            throw new RuntimeException("Invalid promotion type.");
        }

        ChessMove basicMove = ChessMove.deserialized(s.substring(0, 4));
        return new PromotionMove(basicMove, type);
    }
}
