package test.java;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.java.chess.ChessTests;
import test.java.player.PlayerTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ChessTests.class,
        PlayerTests.class
        })
public class AllTests {
}
