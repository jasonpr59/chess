package test.chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.chess.abstractchessposition.AbstractChessPositionTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AbstractChessPositionTests.class,
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
