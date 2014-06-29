package com.stalepretzel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.stalepretzel.chess.ChessTests;
import com.stalepretzel.player.PlayerTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ChessTests.class,
        PlayerTests.class
        })
public class AllTests {
}
