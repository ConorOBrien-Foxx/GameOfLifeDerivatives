package GameOfLifeDerivatives;

import java.awt.Color;


public class Cell {
    
    // these must remain public
    public boolean isActive;
    public int[] entropies;
    public int groupID;
    // location
    private int xLoc;
    private int yLoc;
    
    // initializes to default values
    public Cell() {
        // alive by default
        this(false,-1,-1);
    }
    
    // initializes to default values
    public Cell(int x,int y) {
        // alive by default
        this(false,x,y);
    }

    // initializes to default values
    public Cell(boolean alive) {
        // alive by default
        this(alive,-1,-1);
    }

    public Cell(boolean alive, int x, int y) {
        isActive = alive;
        // up, down, left, right
        entropies = new int[4];
        entropies[0] = 0;
        entropies[1] = 0;
        entropies[2] = 0;
        entropies[3] = 0;
        // default groupID is 0
        groupID = 0;
        // locs
        yLoc = y;
        xLoc = x;
    }
    
    public Cell(Cell x) {
        isActive = x.isActive;
        entropies = new int[4];
        for(int i = 0; i < entropies.length; i++) {
            entropies[i] = x.entropies[i];
        }
        groupID = x.groupID;
        setX(x.getX());
        setY(x.getY());
    }

    // reduces the entropy of each item in the array
    public void reduceEntropy() {
        for (int i=0; i<3; i++){
            entropies[i] = entropies[i] - 1;
        }
    }
    
    public void toggle() {
        isActive = !isActive;
    }
    
    public static final int STRATA_COUNT = 7;
    public static final int DIMINISH_TIMES = 5;
    public static final int DARK_THRESHOLD = 13;
    public static final int UNIQUE_COUNT = STRATA_COUNT * DIMINISH_TIMES;
    public Color getColor() {
        // divide id into spectra
        int gid = groupID % UNIQUE_COUNT;
        float hue = gid % UNIQUE_COUNT % STRATA_COUNT * 1.0f / STRATA_COUNT;
        float brightness = 1.0f - (gid * 1.0f / STRATA_COUNT / DIMINISH_TIMES);
        /*
         * Options for saturation:
         * - brightness
         * - 1-(1-brightness)/2
         * - 1.0f
         */
        return Color.getHSBColor(hue, brightness, brightness);
    }
    
    public boolean isDark() {
        return groupID % UNIQUE_COUNT > DARK_THRESHOLD;
    }

    public int getX(){
        return xLoc;
    }

    public int getY(){
        return yLoc;
    }

    public void setX(int x){
        xLoc = x;
    }

    public void setY(int y){
        yLoc = y;
    }

    public Integer[] getLoc(){
        Integer[] loc = new Integer[2];
        loc[0] = xLoc;
        loc[1] = yLoc;  
        return loc;
    }

    public String toString(){
        return ("x - " + xLoc + ", y - " + yLoc + " , GroupID: " + groupID);
    }

    
    public boolean equals(Object o){
        if(!(o instanceof Cell)) return false;
        Cell another = (Cell) o;
        boolean xEqual = (this.getX() == another.getX());
        boolean yEqual = (this.getY() == another.getY());
        boolean idEqual = (this.groupID == another.groupID && this.groupID != 0);
        return (idEqual || (xEqual && yEqual)); 
    }
    
    // we need a lenient way to group Cells into sets
    public int hashCode() {
        return 1;
    }
}