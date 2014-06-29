package test.java.chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.java.chess.abstractchessposition.AbstractChessPositionTests;
import test.java.chess.chessmove.ChessMoveTests;
import test.java.chess.piece.PieceTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AbstractChessPositionTests.class,
        AlgebraicNotationTest.class,
        CastlingInfoTest.class,
        ChessMoveTests.class,
        ChessPositionBuilderTest.class,
        DeltaTest.class,
        GameTest.class,
        PieceTests.class,
        SquareTest.class
        })
public class ChessTests {
}
