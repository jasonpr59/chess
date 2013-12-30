package test.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import test.TestUtil;
import chess.NormalChessMove;
import chess.Delta;
import chess.Square;
import chess.piece.Piece;

/** Tests for the chess.Square class. */
public class SquareTest {
    @Test
    public void testSquareAt() {
        // Assert that corners aren't problematic, just by
        // creating corner Squares.  (If these statements
        // don't throw exceptions, then we're happy.)
        Square.squareAt(1, 1);
        Square.squareAt(8, 8);

        // Assert that the static Square-getting methods
        // don't duplicate equal squares.
        Square d5 = Square.squareAt(4, 5);
        Square d5Again = Square.squareAt(4, 5);
        Square d5Algebraic = Square.algebraic("d5");
        assertSame(d5, d5Again);
        assertSame(d5, d5Algebraic);
    }

    @Test
    public void testEquality() {
        Square a2 = Square.squareAt(1, 2);
        Square a2Again = Square.squareAt(1, 2);
        Square c2 = Square.squareAt(3, 2);
        Square a4 = Square.squareAt(1, 4);

        assertEquals(a2, a2Again);
        assertEquals(a2.hashCode(), a2Again.hashCode());

        assertFalse(a2.equals(c2));
        assertFalse(a2.equals(a4));
    }

    @Test
    public void testAlgebraic() {
        Square a2 = Square.squareAt(1, 2);
        Square a2Algebraic = Square.algebraic("a2");
        assertEquals(a2, a2Algebraic);
    }

    @Test
    public void testGetters() {
        Square d5 = Square.squareAt(4, 5);
        assertEquals(4, d5.getFile());
        assertEquals(5, d5.getRank());
    }

    @Test
    public void testPlus() {
        Square d5 = Square.algebraic("d5");
        Delta delta = new Delta(-1, 2);
        Square c7 = Square.algebraic("c7");
        assertEquals(c7, d5.plus(delta));
    }

    @Test
    public void testMean() {
        Square d5 = Square.algebraic("d5");
        Square c3 = Square.algebraic("c3");
        Square e7 = Square.algebraic("e7");

        assertEquals(d5, Square.mean(c3, e7));

        Square e8 = Square.algebraic("e8");
        try {
            Square.mean(c3, e8);
            fail("Did not fail to take mean of squares separated by " +
                 "odd number of ranks.");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testExploreNoDirections() {
        // No directions.
        Square start = Square.algebraic("d5");
        Collection<Delta> noDirs = new HashSet<Delta>();
        Collection<Square> found = start.explore(noDirs, 5);
        Collection<Square> noSquares = new HashSet<Square>();
        TestUtil.assertSameElements(noSquares, found);
    }

    @Test
    public void testExploreMaxDistZero() {
        Square start = Square.algebraic("d5");
        Collection<Delta> dirs = new HashSet<Delta>();
        dirs.add(Delta.NORTH_EAST);
        Collection<Square> found = start.explore(dirs, 0);
        Collection<Square> noSquares = new HashSet<Square>();
        TestUtil.assertSameElements(noSquares, found);
    }

    @Test
    public void testExploreMaxDistOne() {
        Collection<Delta> dirs = new HashSet<Delta>();
        dirs.add(Delta.NORTH_EAST);
        dirs.add(Delta.WEST);

        Square start = Square.algebraic("d5");
        Collection<Square> found = start.explore(dirs, 1);

        Collection<Square> expected = new HashSet<Square>();
        expected.add(Square.algebraic("e6"));
        expected.add(Square.algebraic("c5"));

        TestUtil.assertSameElements(expected, found);
    }

    @Test
    public void testExploreMaxDistFew() {
        Collection<Delta> dirs = new HashSet<Delta>();
        dirs.add(Delta.NORTH_EAST);

        Square start = Square.algebraic("c4");
        Collection<Square> found = start.explore(dirs, 3);

        Collection<Square> expected = new HashSet<Square>();
        expected.add(Square.algebraic("d5"));
        expected.add(Square.algebraic("e6"));
        expected.add(Square.algebraic("f7"));

        TestUtil.assertSameElements(expected, found);
    }

    @Test
    public void testExploreFewDirs() {
        Square start = Square.algebraic("d5");
        Collection<Square> found = start.explore(Delta.BASIC_DIRS, 1);

        Collection<Square> expected = new HashSet<Square>();
        expected.add(Square.algebraic("c5"));
        expected.add(Square.algebraic("d6"));
        expected.add(Square.algebraic("e5"));
        expected.add(Square.algebraic("d4"));

        TestUtil.assertSameElements(expected, found);
    }

    @Test
    public void testExploreCutoff() {
        // Ensure that all correct squares are explored
        // even if some of the exploration directions
        // are cut off by the edge of the board prematurely.
        Square start = Square.algebraic("b2");
        Collection<Square> found = start.explore(Delta.BASIC_DIRS, 2);

        Collection<Square> expected = new HashSet<Square>();
        expected.add(Square.algebraic("a2"));
        expected.add(Square.algebraic("b1"));
        expected.add(Square.algebraic("c2"));
        expected.add(Square.algebraic("d2"));
        expected.add(Square.algebraic("b3"));
        expected.add(Square.algebraic("b4"));

        TestUtil.assertSameElements(expected, found);
    }

    @Test
    public void testExploreNoMaxDist() {
        Collection<Delta> dirs = new HashSet<Delta>();
        dirs.add(Delta.NORTH_WEST);
        dirs.add(Delta.SOUTH_EAST);

        Square start = Square.algebraic("b4");
        Collection<Square> found = start.explore(dirs);

        Collection<Square> expected = new HashSet<Square>();
        expected.add(Square.algebraic("a5"));
        expected.add(Square.algebraic("c3"));
        expected.add(Square.algebraic("d2"));
        expected.add(Square.algebraic("e1"));

        TestUtil.assertSameElements(expected, found);
    }

    @Test
    public void testDistributeOverEnds() {
        Square start = Square.algebraic("d5");

        Collection<Square> ends = new HashSet<Square>();
        ends.add(Square.algebraic("e7"));
        ends.add(Square.algebraic("f4"));
        Collection<NormalChessMove> distributed = start.distributeOverEnds(ends);

        Collection<NormalChessMove> expected = new HashSet<NormalChessMove>();
        expected.add(new NormalChessMove(Square.algebraic("d5"),
                                   Square.algebraic("e7")));
        expected.add(new NormalChessMove(Square.algebraic("d5"),
                                   Square.algebraic("f4")));

        TestUtil.assertSameElements(expected, distributed);
    }

    @Test
    public void testIsOnPawnHomeRank() {
        Square whiteQueenPawnHome = Square.algebraic("d2");
        Square blackKingPawnHome = Square.algebraic("e7");
        Square nonPawnHome = Square.algebraic("g5");

        assertTrue(whiteQueenPawnHome.isOnPawnHomeRank(Piece.Color.WHITE));
        assertFalse(whiteQueenPawnHome.isOnPawnHomeRank(Piece.Color.BLACK));

        assertTrue(blackKingPawnHome.isOnPawnHomeRank(Piece.Color.BLACK));
        assertFalse(blackKingPawnHome.isOnPawnHomeRank(Piece.Color.WHITE));

        assertFalse(nonPawnHome.isOnPawnHomeRank(Piece.Color.WHITE));
        assertFalse(nonPawnHome.isOnPawnHomeRank(Piece.Color.BLACK));
    }

    @Test
    public void testLine() {
        Collection<Square> bFile = new HashSet<Square>();
        for (char rank = '1'; rank <= '8'; rank++) {
            bFile.add(Square.algebraic("b" + rank));
        }
        TestUtil.assertSameElements(bFile, Square.line('b'));

        Collection<Square> secondRank = new HashSet<Square>();
        for (char file = 'a'; file <= 'h'; file++) {
            secondRank.add(Square.algebraic(file + "2"));
        }
        TestUtil.assertSameElements(secondRank, Square.line('2'));
    }
}
