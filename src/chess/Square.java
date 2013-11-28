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
        // TODO(jasonpr): Make this say "a4" instead of "Square(file: 1, rank: 4)".
        return "Square(file: " + file + ", rank: " + rank + ")";
    }
}
