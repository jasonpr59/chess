package chess;

public class Delta{
    private int deltaFile;
    private int deltaRank;
    public Delta(Move move) {
        Square start = move.getStart();
        Square end = move.getEnd();
        deltaFile = end.getFile() - start.getFile();
        deltaRank = end.getRank() - start.getRank();
    }
        
    public Delta(int deltaFile, int deltaRank) {
        this.deltaFile = deltaFile;
        this.deltaRank = deltaRank;
    }
    
    public int getDeltaFile() {
        return deltaFile;
    }
    
    public int getDeltaRank() {
        return deltaRank;
    }
    
    public Delta scaled(int scale) {
        return new Delta(deltaFile * scale, deltaRank * scale);
    }

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
    
}
