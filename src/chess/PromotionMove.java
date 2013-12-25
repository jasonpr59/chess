package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chess.Piece.Type;

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
    public PromotionMove(Square start, Square end, Type promotedType) {
        super(start, end);
        this.promotedType = promotedType;
    }

    public PromotionMove(Square start, Delta delta, Type promotedType) {
        super(start, delta);
        this.promotedType = promotedType;
    }

    public PromotionMove(ChessMove move, Type promotedType) {
        super(move.getStart(), move.getEnd());
        this.promotedType = promotedType;
    }

    public Type getPromotedType() {
        return promotedType;
    }

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
            !PROMOTION_TYPES.contains(movingPiece.getType())) {
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
}
