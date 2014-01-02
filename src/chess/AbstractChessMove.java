package chess;

public abstract class AbstractChessMove implements ChessMove {
    @Override
    public boolean isLegal(ChessPosition board) {
        // All legal Moves are sane.
        if (!isSane(board)) {
            return false;
        }
        ChessPosition resultBoard = result(board);
        return !resultBoard.checked(board.getToMoveColor());
    }
}
