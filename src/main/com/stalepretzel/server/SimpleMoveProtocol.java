package com.stalepretzel.server;

import com.stalepretzel.chess.CastlingMove;
import com.stalepretzel.chess.ChessMove;
import com.stalepretzel.chess.NormalChessMove;
import com.stalepretzel.chess.PromotionMove;
import com.stalepretzel.chess.Square;
import com.stalepretzel.chess.piece.Bishop;
import com.stalepretzel.chess.piece.Knight;
import com.stalepretzel.chess.piece.Piece;
import com.stalepretzel.chess.piece.Queen;
import com.stalepretzel.chess.piece.Rook;

/**
 * Utilities for Simple Move Protocol (de)serialization.
 *
 * A ChessMove is serialized to the following format:
 *   serialized ::=  square square extra? // The first square represents the start Square,
 *                                        // the second square represents the end Square.
 *   square ::= file rank
 *   file ::= "1" ... "8" // corresponds to "a" ... "h"
 *   rank ::= "1" ... "8"
 *   extra ::= castles | promoteType
 *   castles ::= C // included if the move is a CastlingMove
 *   promoteType ::= "N" | "B" | "R" | "Q" // included if the move is a PromotionMove.
 */
public class SimpleMoveProtocol {
    /** Serialize a ChessMove according to the Simple Move Protocol. */
    public static String serialized(ChessMove move) {
        Square start = move.getStart();
        Square end = move.getEnd();
        String serialized = "" + start.getFile() + start.getRank() + end.getFile() + end.getRank();

        // TODO: Get rid of these zillions of `instanceof` checks.
        if (move instanceof PromotionMove) {
            PromotionMove promotionMove = (PromotionMove) move;
            Piece promoted = promotionMove.getPromotedPiece();
            if (promoted instanceof Knight) {
                serialized += "N";
            } else if (promoted instanceof Bishop) {
                serialized += "B";
            } else if (promoted instanceof Rook) {
                serialized += "R";
            } else if (promoted instanceof Queen) {
                serialized += "Q";
            }
        } else if (move instanceof CastlingMove) {
            serialized += "C";
        }
        return serialized;
    }

    /** Deserialize a Simple-Move-Protocol-serialized string to a ChessMove. */
    public static ChessMove deserialized(String s) {
        s = s.trim();

        NormalChessMove normal = normalDeserialized(s.substring(0, 4));

        if (s.length() == 5) {
            if (s.charAt(4) == 'C') {
                return CastlingMove.fromNormalMove(normal);
            } else {
                Piece.Color color;
                if (normal.getStart().getRank() == 7) {
                    color = Piece.Color.WHITE;
                }  else {
                    color = Piece.Color.BLACK;
                }
                Piece promoted;
                char type = s.charAt(4);
                if (type == 'N') {
                    promoted = new Knight(color);
                } else if (type == 'B') {
                    promoted = new Bishop(color);
                } else if (type == 'R') {
                    promoted = new Rook(color);
                } else if (type == 'Q') {
                    promoted = new Queen(color);
                } else {
                    throw new IllegalArgumentException();
                }
                return new PromotionMove(normal, promoted);
            }
        } else {
            assert s.length() == 4;
            return normal;
        }
    }

    private static NormalChessMove normalDeserialized(String s) {
        assert s.length() == 4;
        int startFile = Integer.parseInt(s.substring(0,1));
        int startRank = Integer.parseInt(s.substring(1,2));
        int endFile = Integer.parseInt(s.substring(2,3));
        int endRank = Integer.parseInt(s.substring(3,4));
        return new NormalChessMove(Square.squareAt(startFile, startRank),
                Square.squareAt(endFile, endRank));
    }
}
