public class Cell{
    
    public boolean isActive;
    public Array entropies;
    public int groupID;

    // initializes to default values
    public Cell(){
        // alive by default
        isActive = true;
        // up, down, left, right
        entropies = [0,0,0,0];
        // default groupID is 0
        groupID = 0;
    }

    // reduces the entropy of each item in the array
    public void reduceEntropy(){
        for (int i=0; i<3; i++){
            entropies[i] = entropies[i] - 1;
        }
    }

}