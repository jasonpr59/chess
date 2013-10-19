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
}
