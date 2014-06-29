package com.stalepretzel.chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.stalepretzel.chess.abstractchessposition.AbstractChessPositionTests;
import com.stalepretzel.chess.chessmove.ChessMoveTests;
import com.stalepretzel.chess.piece.PieceTests;

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
