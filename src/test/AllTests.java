package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.chess.ChessTests;
import test.player.PlayerTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ChessTests.class,
        PlayerTests.class
        })
public class AllTests {
}
