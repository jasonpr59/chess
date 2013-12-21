package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CastlingTest.class,
        GameTest.class,
        BoardTest.class})
public class AllTests {
}
