package GameOfLifeDerivatives;

import java.awt.Color;

public class Cell {
    
    public boolean isActive;
    public int[] entropies;
    public int groupID;

    // initializes to default values
    public Cell() {
        // alive by default
        isActive = true;
        // up, down, left, right
        entropies = new int[4];
        entropies[0] = 0;
        entropies[1] = 0;
        entropies[2] = 0;
        entropies[3] = 0;
        // default groupID is 0
        groupID = 0;
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
    
    private static Color[] ACTIVE_CELL_COLORS = {
        Color.RED,
        Color.ORANGE,
        Color.YELLOW,
        Color.GREEN,
        Color.BLUE,
        Color.CYAN,
        Color.MAGENTA,
    };
    public Color getColor() {
        if(groupID < ACTIVE_CELL_COLORS.length) {
            return ACTIVE_CELL_COLORS[groupID];
        }
        else {
            return Color.DARK_GRAY;
        }
    }
}