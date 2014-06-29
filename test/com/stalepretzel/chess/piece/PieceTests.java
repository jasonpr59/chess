package com.stalepretzel.chess.piece;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PieceTest.class,
        PawnTest.class,
        KnightTest.class,
        BishopTest.class,
        RookTest.class,
        QueenTest.class,
        KingTest.class
        })
public class PieceTests {
}
