package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import exceptions.NonexistantSquareException;

public class Square {
    
    // Rank and file are both in [1...8].
    private final int rank;
    private final int file;
    // The space of all squares.
    public static final Iterable<Square> ALL;
    
    
    static {
        List<Square> all = new ArrayList<Square>();
        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                all.add(new Square(file, rank));
            }
        }
        ALL = Collections.unmodifiableList(all);
    }
    
    public Square(int file, int rank) throws NonexistantSquareException{
        if (rank < 1 || rank > 8){
            throw new NonexistantSquareException("Illegal rank: " + rank);
        } else if (file < 1 || file >8) {
            throw new NonexistantSquareException("Illegal file: " + file);
        }
        this.rank = rank;
        this.file = file;
    }
    
    /**
     * Create a square from its algebraic representation.
     * @param algRep The algebraic representation of the square, using
     *  a lowercase letter to represent the file.
     *  E.g. Square("d1") is the white queen's original square.
     */
    public static Square algebraic(String algRep) {
        assert algRep.length() == 2;
        char file = algRep.charAt(0);
        int fileNum = file - 'a' + 1;
        int rankNum = Integer.parseInt(algRep.substring(1, 2));
        return new Square(fileNum, rankNum);
    }

    public int getRank() {
        return rank;
    }

    public int getFile() {
        return file;
    }

    public Square plus(Delta delta) {
        return new Square(file + delta.getDeltaFile(), rank + delta.getDeltaRank());
    }
    
    public static Square mean(Square a, Square b) {
        int fileSum = a.getFile() + b.getFile();
        int rankSum = a.getRank() + b.getRank();
        if (fileSum % 2 != 0 || rankSum % 2 != 0) {
            throw new IllegalArgumentException("Input squares " + a + " and " + b +
                                               " do not have a mean that's a valid square.");
        }
        
        return new Square(fileSum / 2, rankSum / 2);
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
        // TODO(jasonpr): Do this in one line,
        // without making Java angry about int vs. char types.
        char fileChar = 'a';
        fileChar -= 1;
        fileChar += file;

        return Character.toString(fileChar) + rank;
    }
    
    /**
     * Return all squares that are in any specified direction from this square.
     * @param directions a set of quasi-unit Deltas, specifying directions to explore.
     * @return
     */
    public Collection<Square> explore(Collection<Delta> directions) {
        // Moving more than 7 in *any* direction will land you
        // off the board.
        return explore(directions, 7);
    }
    
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
                } catch (NonexistantSquareException e) {
                    break;
                }
                foundSquares.add(foundSquare);
                factor++;
            }
        }
        return foundSquares;
    }
    
    public Collection<Move> distributeOverEnds(Collection<Square> ends) {
        Collection<Move> moves = new ArrayList<Move>();
        for (Square end : ends) {
            moves.add(new Move(this, end));
        }
        return moves;
    }
    
    public static Collection<Square> line(char clue) {
        if ('1' <= clue && clue <= '8') {
            return rank(clue);
        } else if ('a' <= clue && clue <= 'h') {
            return file(clue);
        } else {
            throw new IllegalArgumentException("Illegal line clue " + clue);
        }
    }
    
    private static Collection<Square> rank(char asciiRank) {
        Collection<Square> squares = new ArrayList<Square>();
        char rank = (char) (asciiRank - '1' + 1);
        for (char file = 1; file <= 8; file++) {
            squares.add(new Square(file, rank));
        }
        return squares;
    }
    
    private static Collection<Square> file(char asciiFile) {
        Collection<Square> squares = new ArrayList<Square>();
        char file = (char) (asciiFile - 'a' + 1);
        for (char rank = 1; rank <= 8; rank++) {
            squares.add(new Square(file, rank));
        }
        return squares;
    }
}
