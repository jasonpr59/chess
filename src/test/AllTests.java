package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.chess.ChessTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ChessTests.class
        })
public class AllTests {
}
