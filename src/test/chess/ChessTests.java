package test.chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AbstractChessPositionTest.class,
        AlgebraicNotationTest.class,
        CastlingInfoTest.class,
        CastlingMoveTest.class,
        ChessMoveTest.class,
        ChessPositionBuilderTest.class,
        DeltaTest.class,
        GameTest.class,
        PieceTest.class,
        SquareTest.class
        })
public class ChessTests {
}
