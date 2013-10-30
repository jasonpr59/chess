package chess;

public class Square {
    
    // Rank and file are both in [1...8].
    private final int rank;
    private final int file;
    
    public Square(int file, int rank) throws IllegalArgumentException{
        if (rank < 1 || rank > 8){
            throw new IllegalArgumentException("Illegal rank: " + rank);
        } else if (file < 1 || file >8) {
            throw new IllegalArgumentException("Illegal file: " + file);
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
}
