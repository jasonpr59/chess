package com.stalepretzel.chess.chessmove;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NormalChessMoveTest.class,
        CastlingMoveTest.class,
        PromotionMoveTest.class
        })
public class ChessMoveTests {
}
