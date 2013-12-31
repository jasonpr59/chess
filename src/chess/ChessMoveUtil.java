package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ChessMoveUtil {
    /** Return the subset of sane moves from a set of moves. */
    public static Collection<ChessMove> filterSane(Collection<ChessMove> candidates,
                                                   ChessPosition position) {
        Set<ChessMove> saneMoves = new HashSet<ChessMove>();
        for (ChessMove c : candidates) {
            if (c.isSane(position)) {
                saneMoves.add(c);
            }
        }
        return saneMoves;
    }
}
