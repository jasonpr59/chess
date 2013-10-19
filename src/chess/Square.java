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
