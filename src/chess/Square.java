package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.exceptions.NonexistentSquareException;


/**
 * A square on a chess board.
 * This class is immutable.
 */
public class Square {

    // Rank and file are both between 1 and 8, inclusive.
    private final int rank;
    private final int file;

    // A grid of all 64 squares.
    // e.g. GRID[1][3] = "b4".
    private static final Square[][] GRID;

    // The space of all squares.
    public static final Iterable<Square> ALL;

    static {
        GRID = new Square[8][8];
        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                GRID[file - 1][rank - 1] = new Square(file, rank);
            }
        }

        List<Square> all = new ArrayList<Square>();
        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                all.add(squareAt(file, rank));
            }
        }
        ALL = Collections.unmodifiableList(all);
    }

    /** Create a new Square at the given rank and file.
     *
     * Rank and file are both integers in the range [1, 8].
     * For file, [1, 8] maps to [a, h].
     *
     * For example, new Square(3, 5) represents "c5".
     *
     * This constructor should be used VERY rarely.  Prefer the static
     * method Square.squareAt, which returns an already-existent Square.
     *
     * @throws NonexistantSquareException If the rank or file
     *     falls outside the legal range.
     */
    private Square(int file, int rank) throws NonexistentSquareException{
        if (rank < 1 || rank > 8){
            throw new NonexistentSquareException("Illegal rank: " + rank);
        } else if (file < 1 || file >8) {
            throw new NonexistentSquareException("Illegal file: " + file);
        }
        this.rank = rank;
        this.file = file;
    }

    /**
     * Create a square from its algebraic representation.
     * @param algRep The algebraic representation of the square, using
     *     a lowercase letter to represent the file.
     *     For example, Square("d1") is the white queen's original square.
     */
    public static Square algebraic(String algRep) {
        assert algRep.length() == 2;
        char file = algRep.charAt(0);
        assert 'a' <= file && file <= 'h';
        int fileNum = file - 'a' + 1;
        int rankNum = Integer.parseInt(algRep.substring(1, 2));
        return squareAt(fileNum, rankNum);
    }

    public int getRank() {
        return rank;
    }

    public int getFile() {
        return file;
    }

    /** Get the Square offset from this one by a given Delta. */
    public Square plus(Delta delta) {
        return squareAt(file + delta.getDeltaFile(), rank + delta.getDeltaRank());
    }

    /**
     * Return the square exactly between two squares.
     * Requires that such a Square exists-- that is, that the two input
     * squares are separated by an even number of ranks AND files.
     *
     * @throws IllegalArgumentExcption If the input squares are separated
     *     by an odd number of ranks or files.
     */
    public static Square mean(Square a, Square b) {
        int fileSum = a.getFile() + b.getFile();
        int rankSum = a.getRank() + b.getRank();
        if (fileSum % 2 != 0 || rankSum % 2 != 0) {
            throw new IllegalArgumentException("Input squares " + a + " and " + b +
                                               " do not have a mean that's a valid square.");
        }

        return squareAt(fileSum / 2, rankSum / 2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        }

        Square that = (Square) obj;
        return file == that.getFile() && rank == that.getRank();
    }

    @Override
    public int hashCode() {
        return 17 * file + rank;
    }

    @Override
    public String toString(){
        char fileChar = (char) ('a' - 1 + file);
        return Character.toString(fileChar) + rank;
    }

    /**
     * Return all Squares that are in any specified direction from this Square.
     * @param directions a set of quasi-unit Deltas, specifying directions to explore.
     * TODO: Make a subclass of Delta, QuasiUnitDelta, which enforces the quasi-unit
     *     condition.
     * See explore(Collection<Delta> directions, int maxDist) for more info.
     */
    public Collection<Square> explore(Collection<Delta> directions) {
        // Moving more than 7 in *any* direction will land you
        // off the board.
        return explore(directions, 7);
    }

    /**
     * Return all nearby Squares in certain directions.
     * More specifically: Generate a set of Squares by "exploring" in
     * different directions from this source Square.  For each direction
     * of exploration, move in that direction, one step at a time, adding
     * each newly encountered square to the set of explored Squares, until
     * you have traveled some maximum distance from the original square.
     *
     * This method might be used in generating candidate moves: a bishop
     * might explore in the diagonal directions as part of enumerating which
     * squares it could move to.
     *
     * @param directions a set of quasi-unit Deltas, specifying directions to explore.
     * @param maxDist The maximum number of steps to take in any direction while exploring.
     */
    public Collection<Square> explore(Collection<Delta> directions, int maxDist) {
        Collection<Square> foundSquares = new ArrayList<Square>();
        int factor;
        Square foundSquare;
        for (Delta d : directions) {
            factor = 1;
            // We should always break by attempting to create a bad square,
            // but keep factor below 8, just in case.
            // TODO: Throw a RuntimeException if factor grows beyond 8?
            // TODO: Remove use of Exceptions for control flow?
            while (factor <= maxDist) {
                try {
                    foundSquare = this.plus(d.scaled(factor));
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
                foundSquares.add(foundSquare);
                factor++;
            }
        }
        return foundSquares;
    }

    /**
     * Return a set of Moves with this square as the start.
     * For each Square in ends, create a Move from this Square to that Square.
     */
    public Collection<ChessMove> distributeOverEnds(Collection<Square> ends) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        for (Square end : ends) {
            moves.add(new ChessMove(this, end));
        }
        return moves;
    }

    /** Return whether the square is on the color's pawns' home rank. */
    public boolean isOnPawnHomeRank(Piece.Color color) {
        if (color == Piece.Color.WHITE) {
            return rank == 2;
        } else {
            return rank == 7;
        }
    }

    /**
     * Return the set of Squares in a given rank or file.
     * @param clue The rank or file, as a char in the range ['1', '8']
     *      or in the range ['a', 'h'].
     */
    public static Collection<Square> line(char clue) {
        if ('1' <= clue && clue <= '8') {
            return rank(clue);
        } else if ('a' <= clue && clue <= 'h') {
            return file(clue);
        } else {
            throw new IllegalArgumentException("Illegal line clue " + clue);
        }
    }

    /** Return the set of Squares in a given rank. */
    private static Collection<Square> rank(char asciiRank) {
        Collection<Square> squares = new ArrayList<Square>();
        char rank = (char) (asciiRank - '1' + 1);
        for (char file = 1; file <= 8; file++) {
            squares.add(squareAt(file, rank));
        }
        return squares;
    }

    /** Return the set of Squares in a given file. */
    private static Collection<Square> file(char asciiFile) {
        Collection<Square> squares = new ArrayList<Square>();
        char file = (char) (asciiFile - 'a' + 1);
        for (char rank = 1; rank <= 8; rank++) {
            squares.add(squareAt(file, rank));
        }
        return squares;
    }

    /**
     * Return the Square at a given file and rank.
     * @param file The file, as an integer in the range [1, 8].
     *      [1, 8] maps to ['a', 'h'].
     * @param rank The rank, as an integer in [1, 8].
     *
     * This is the preferred way to get a "new" Square, because it
     * returns a reference to an already-created, immutable Square.
     * The prevents us from storing an extra object time we want a
     * Square.
     *
     */
    public static Square squareAt(int file, int rank) {
        return GRID[file - 1][rank - 1];
    }
}
