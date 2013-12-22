package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** The difference between two Squares. */
public class Delta{
    private final int deltaFile;
    private final int deltaRank;
    public static final Delta NORTH;
    public static final Delta EAST;
    public static final Delta SOUTH;
    public static final Delta WEST;
    public static final Delta NORTH_EAST;
    public static final Delta NORTH_WEST;
    public static final Delta SOUTH_EAST;
    public static final Delta SOUTH_WEST;
    
    public static final Collection<Delta> BASIC_DIRS;
    public static final Collection<Delta> DIAGONAL_DIRS;
    public static final Collection<Delta> QUEEN_DIRS;
    
    static {
        NORTH = new Delta(0, 1);
        EAST = new Delta(1, 0); 
        SOUTH = NORTH.scaled(-1);
        WEST = EAST.scaled(-1);
        
        List<Delta> basicDirs = new ArrayList<Delta>();
        basicDirs.add(NORTH);
        basicDirs.add(EAST);
        basicDirs.add(SOUTH);
        basicDirs.add(WEST);
        BASIC_DIRS = Collections.unmodifiableList(basicDirs);
        
        NORTH_EAST = sum(NORTH, EAST);
        NORTH_WEST = sum(NORTH, WEST);
        SOUTH_EAST = sum(SOUTH, EAST);
        SOUTH_WEST = sum(SOUTH, WEST);
        
        List<Delta> diagonalDirs = new ArrayList<Delta>();
        diagonalDirs.add(NORTH_EAST);
        diagonalDirs.add(NORTH_WEST);
        diagonalDirs.add(SOUTH_EAST);
        diagonalDirs.add(SOUTH_WEST);
        DIAGONAL_DIRS = Collections.unmodifiableList(diagonalDirs);
        
        List<Delta> queenDirs = new ArrayList<Delta>();
        queenDirs.addAll(BASIC_DIRS);
        queenDirs.addAll(DIAGONAL_DIRS);
        QUEEN_DIRS = Collections.unmodifiableList(queenDirs);
                
    }

    /** Create the delta between a Move's start and end Squares. */
    public Delta(Move move) {
        this(move.getStart(), move.getEnd());
    }

    /** Create a Delta with specified changes in file and rank. */
    public Delta(int deltaFile, int deltaRank) {
        this.deltaFile = deltaFile;
        this.deltaRank = deltaRank;
    }
    
    /** Create a delta from one Square to another. */
    public Delta(Square start, Square end) {
        this(end.getFile() - start.getFile(),
             end.getRank() - start.getRank());
    }
    
    public int getDeltaFile() {
        return deltaFile;
    }
    
    public int getDeltaRank() {
        return deltaRank;
    }
    
    /** Return a scaled-up copy of this Delta. */
    public Delta scaled(int scale) {
        return new Delta(deltaFile * scale, deltaRank * scale);
    }

    /**
     * Return a quasi-unitized version of this Delta.
     * That is, return a quasi-unit Delta, d, such that scaling d
     * up by some factor would yield a Delta equal to this one.
     *
     * A quasi-unit Delta is a Delta for which deltaRank <= 1 and
     * deltaFile <= 1, but they're not both zero.  That is, it's a
     * Delta that represents the difference between adjacent or
     * diagonally adjacent Squares.
     */
    public Delta unitized() {
        // TODO(jasonpr): Check that is diagonal or is basic.
        // This will require some refactoring, since currently
        // isDiagonal and isBasic are methods of Move.
        int length = Math.max(Math.abs(deltaFile), Math.abs(deltaRank));
        return new Delta(deltaFile / length, deltaRank / length);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        Delta that = (Delta) obj;
        return that.getDeltaFile() == deltaFile && that.getDeltaRank() == deltaRank;
    }

    @Override
    public int hashCode() {
        return 17 * deltaFile + deltaRank;
    }
    
    @Override
    public String toString() {
        return "Delta(file: " + deltaFile + ", rank: " + deltaRank + ")";
    }
    
    /**
     * Return the sum of two Deltas.
     * Delta sums are computed by doing component-wise addition.
     */
    public static Delta sum(Delta a, Delta b) {
        return new Delta(a.getDeltaFile() + b.getDeltaFile(),
                         a.getDeltaRank() + b.getDeltaRank());
    }
}
