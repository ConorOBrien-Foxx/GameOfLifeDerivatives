package GameOfLifeDerivatives;

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

}