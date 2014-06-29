package com.stalepretzel.player;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.stalepretzel.player.EstimatedScore;
import com.stalepretzel.player.Player;
import com.stalepretzel.player.Score;
import com.stalepretzel.player.TerminalScore;

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
    public void testUnequalEstimateMixedWithWinsScores() {
        Score maxWins = TerminalScore.wins(Player.MAXIMIZER, 10);
        Score positiveEstimate = new EstimatedScore(100.0f);
        Score negativeEstimate = new EstimatedScore(-100.0f);
        Score minWins = TerminalScore.wins(Player.MINIMIZER, 10);

        doGreaterAssertions(maxWins, positiveEstimate);
        doGreaterAssertions(maxWins, negativeEstimate);
        doGreaterAssertions(positiveEstimate, minWins);
        doGreaterAssertions(negativeEstimate, minWins);
    }

    @Test
    public void testUnequalEstimateMixedWithDrawScores() {
        Score positiveEstimate = new EstimatedScore(100.0f);
        Score draw = TerminalScore.draw(10);
        Score negativeEstimate = new EstimatedScore(-100.f);

        doGreaterAssertions(positiveEstimate, draw);
        doGreaterAssertions(draw, negativeEstimate);
    }

    @Test
    public void testEqualTerminalScores() {
        doEqualAssertions(TerminalScore.wins(Player.MAXIMIZER, 9),
                          TerminalScore.wins(Player.MAXIMIZER, 9));

        doEqualAssertions(TerminalScore.wins(Player.MAXIMIZER, 9),
                          TerminalScore.loses(Player.MINIMIZER, 9));
    }

    @Test
    public void testUnequalTerminalScores() {
        Score maxWinSoon = TerminalScore.wins(Player.MAXIMIZER, 1);
        Score maxWinInAWhile = TerminalScore.wins(Player.MAXIMIZER, 11);
        Score draw = TerminalScore.draw(6);
        Score minWinInAWhile = TerminalScore.wins(Player.MINIMIZER, 5);
        Score minWinSoon = TerminalScore.wins(Player.MINIMIZER, 1);

        doGreaterAssertions(maxWinSoon, maxWinInAWhile);
        doGreaterAssertions(maxWinInAWhile, draw);
        doGreaterAssertions(draw, minWinInAWhile);
        doGreaterAssertions(minWinInAWhile, minWinSoon);
    }

    @Test
    public void testEstimatedGetters() {
        EstimatedScore es = new EstimatedScore(1.5f);
        assertEquals(1.5f, es.getValue(), EQUALITY_EPSILON);
    }

    @Test
    public void testTerminalGetters() {
        TerminalScore maxWins = TerminalScore.wins(Player.MAXIMIZER, 5);
        assertEquals(Float.POSITIVE_INFINITY, maxWins.getValue(), EQUALITY_EPSILON);
        assertEquals(5, maxWins.getPliesUntilEnd());

        TerminalScore minWins = TerminalScore.wins(Player.MINIMIZER, 3);
        assertEquals(Float.NEGATIVE_INFINITY,minWins.getValue(), EQUALITY_EPSILON);
        assertEquals(3, minWins.getPliesUntilEnd());
    }
}
