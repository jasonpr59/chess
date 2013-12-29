package test.chess;

import static org.junit.Assert.*;

import org.junit.Test;

import chess.NormalChessMove;
import chess.Delta;
import chess.Square;

/** Tests for the chess.Delta class. */
public class DeltaTest {

    @Test
    public void testGetters() {
        Delta d = new Delta(3, 4);
        assertEquals(3, d.getDeltaFile());
        assertEquals(4, d.getDeltaRank());
    }

    @Test
    public void testEquality() {
        Delta d1 = new Delta(3, 4);
        Delta d1Again = new Delta(3, 4);
        Delta d2 = new Delta(3, -4);
        Delta d3 = new Delta(-3, 4);

        assertEquals(d1, d1Again);
        assertEquals(d1.hashCode(), d1Again.hashCode());

        assertFalse(d1.equals(d2));
        assertFalse(d1.equals(d3));
    }

    @Test
    public void testConstructors() {
        // Make the same delta (1, 2) three ways, and assert
        // that they're all equal.
        Delta fromInts = new Delta(1, 2);
        Delta fromSquares = new Delta(Square.algebraic("b3"), Square.algebraic("c5"));
        Delta fromChessMove = new Delta(new NormalChessMove(Square.algebraic("d6"),
                                                      Square.algebraic("e8")));
        assertEquals(fromInts, fromSquares);
        assertEquals(fromInts, fromChessMove);
    }

    @Test
    public void testScaled() {
        Delta source = new Delta(1, 2);

        // Test positive scaling.
        Delta doubled = source.scaled(2);
        assertEquals(new Delta(2, 4), doubled);

        // Test zero scaling.
        Delta zero = source.scaled(0);
        assertEquals(new Delta(0, 0), zero);

        // Test negative scaling;
        Delta minusTripled = source.scaled(-3);
        assertEquals(new Delta(-3, -6), minusTripled);
    }

    @Test
    public void testUnitized() {
        // Test a basic vector.
        Delta bigSouth = Delta.SOUTH.scaled(3);
        assertEquals(Delta.SOUTH, bigSouth.unitized());

        // Test a diagonal vector.
        Delta bigNorthWest = Delta.NORTH_WEST.scaled(5);
        assertEquals(Delta.NORTH_WEST, bigNorthWest.unitized());
    }

    @Test
    public void testSum() {
        Delta augend = new Delta(1, 2);
        Delta addend = new Delta(3, -4);
        Delta sum = new Delta(4, -2);
        assertEquals(sum, Delta.sum(augend, addend));
    }
}
