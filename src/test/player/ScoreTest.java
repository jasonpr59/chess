package test.player;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import player.EstimatedScore;
import player.Player;
import player.Score;
import player.TerminalScore;

public class ScoreTest {

    // TODO: Determine what makes a good epsilon value.
    private static float EQUALITY_EPSILON = 0.0001f;

    /** Assert that two Scores compare in a specific way. */
    private static void doComparisonAssertions(Score first, Score second,
        boolean shouldEqual, boolean firstGreater, boolean firstLess) {
        assertEquals(shouldEqual, first.equals(second));
        assertEquals(shouldEqual, second.equals(first));
        if (shouldEqual) {
            assertEquals(first.hashCode(), second.hashCode());
        }

        assertEquals(firstGreater, first.greaterThan(second));
        assertEquals(firstLess, second.greaterThan(first));
        assertEquals(firstLess, first.lessThan(second));
        assertEquals(firstGreater, second.lessThan(first));
    }

    private static void doEqualAssertions(Score first, Score second) {
        boolean shouldEqual = true;
        boolean firstGreater = false;
        boolean firstLess = false;
        doComparisonAssertions(first, second, shouldEqual, firstGreater, firstLess);
    }

    private static void doGreaterAssertions(Score greater, Score lesser) {
        boolean shouldEqual = false;
        boolean firstGreater = true;
        boolean firstLess = false;
        doComparisonAssertions(greater, lesser, shouldEqual, firstGreater, firstLess);
    }

    @Test
    public void testEqualEstimates() {
        doEqualAssertions(new EstimatedScore(1.0f), new EstimatedScore(1.0f));
    }

    @Test
    public void testUnequalEstimates() {
        Score bigPos = new EstimatedScore(1.0f);
        Score littlePos = new EstimatedScore(0.5f);
        Score littleNeg = new EstimatedScore(-0.5f);
        Score bigNeg = new EstimatedScore(-1.0f);

        doGreaterAssertions(bigPos, littlePos);
        doGreaterAssertions(littlePos, littleNeg);
        doGreaterAssertions(littleNeg, bigNeg);
    }

    @Test
    public void testUnequalEstimateMixedScores() {
        Score maxWins = new TerminalScore(Player.MAXIMIZER, 10);
        Score positiveEstimate = new EstimatedScore(100.0f);
        Score negativeEstimate = new EstimatedScore(-100.0f);
        Score minWins = new TerminalScore(Player.MINIMIZER, 10);

        doGreaterAssertions(maxWins, positiveEstimate);
        doGreaterAssertions(maxWins, negativeEstimate);
        doGreaterAssertions(positiveEstimate, minWins);
        doGreaterAssertions(negativeEstimate, minWins);
    }

    @Test
    public void testEqualTerminalScores() {
        doEqualAssertions(new TerminalScore(Player.MAXIMIZER, 9),
                          new TerminalScore(Player.MAXIMIZER, 9));
    }

    @Test
    public void testUnequalTerminalScores() {
        Score maxWinSoon = new TerminalScore(Player.MAXIMIZER, 1);
        Score maxWinInAWhile = new TerminalScore(Player.MAXIMIZER, 11);
        Score minWinInAWhile = new TerminalScore(Player.MINIMIZER, 5);
        Score minWinSoon = new TerminalScore(Player.MINIMIZER, 1);

        doGreaterAssertions(maxWinSoon, maxWinInAWhile);
        doGreaterAssertions(maxWinInAWhile, minWinInAWhile);
        doGreaterAssertions(minWinInAWhile, minWinSoon);
    }

    @Test
    public void testEstimatedGetters() {
        EstimatedScore es = new EstimatedScore(1.5f);
        assertEquals(1.5f, es.getValue(), EQUALITY_EPSILON);
    }

    @Test
    public void testTerminalGetters() {
        TerminalScore maxWins = new TerminalScore(Player.MAXIMIZER, 5);
        assertEquals(Float.POSITIVE_INFINITY, maxWins.getValue(), EQUALITY_EPSILON);
        assertEquals(5, maxWins.getPliesUntilWin());

        TerminalScore minWins = new TerminalScore(Player.MINIMIZER, 3);
        assertEquals(Float.NEGATIVE_INFINITY,minWins.getValue(), EQUALITY_EPSILON);
        assertEquals(3, minWins.getPliesUntilWin());
    }
}
