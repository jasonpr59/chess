package test.chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.chess.abstractchessposition.AbstractChessPositionTests;
import test.chess.chessmove.CastlingMoveTest;
import test.chess.chessmove.ChessMoveTests;
import test.chess.piece.PieceTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AbstractChessPositionTests.class,
        AlgebraicNotationTest.class,
        CastlingInfoTest.class,
        CastlingMoveTest.class,
        ChessMoveTests.class,
        ChessPositionBuilderTest.class,
        DeltaTest.class,
        GameTest.class,
        PieceTests.class,
        SquareTest.class
        })
public class ChessTests {
}
