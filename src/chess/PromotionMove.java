package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chess.Piece.PieceType;

public class PromotionMove extends Move {
    private final PieceType promotedType;
    private static final Set<PieceType> PROMOTION_TYPES;
    
    static {
        Set<PieceType> promotionTypes = new HashSet<PieceType>();
        promotionTypes.add(PieceType.KNIGHT);
        promotionTypes.add(PieceType.BISHOP);
        promotionTypes.add(PieceType.ROOK);
        promotionTypes.add(PieceType.QUEEN);
        PROMOTION_TYPES = Collections.unmodifiableSet(promotionTypes);
    }
    public PromotionMove(Square start, Square end, PieceType promotedType) {
        super(start, end);
        this.promotedType = promotedType;
    }

    public PromotionMove(Square start, Delta delta, PieceType promotedType) {
        super(start, delta);
        this.promotedType = promotedType;
    }
    
    public PromotionMove(Move move, PieceType promotedType) {
        super(move.getStart(), move.getEnd());
        this.promotedType = promotedType;
    }
    
    public PieceType getPromotedType() {
        return promotedType;
    }

    public static Collection<PromotionMove> allPromotions(Move move) {
        List<PromotionMove> allPromotions = new ArrayList<PromotionMove>();
        for (PieceType type : PROMOTION_TYPES) {
            allPromotions.add(new PromotionMove(move, type));
        }
        return allPromotions;
    }

    @Override
    public boolean isSane(Board board) {
        Piece movingPiece = board.getPiece(getStart());
        if (movingPiece == null || movingPiece.getType() != Piece.PieceType.PAWN ||
            !PROMOTION_TYPES.contains(movingPiece.getType())) {
            return false;
        }

        int promotableRank;
        if (movingPiece.getPieceColor() == Piece.PieceColor.WHITE) {
            promotableRank = 8;
        } else {
            promotableRank = 1;
        }
        
        if (getEnd().getRank() != promotableRank) {
            return false;
        }
        
        return super.isSane(board);
    }
}
