package GameOfLifeDerivatives;

import java.util.ArrayList;
import java.awt.Point;
import GameOfLifeDerivatives.*;

public class SandpileBoard extends Board {
    public SandpileBoard() {
        super();
        fillActive();
    }
    
    public SandpileBoard(ArrayList<ArrayList<Cell>> grid) {
        super(grid);
        fillActive();
    }
    
    public void fillActive() {
        for(int i = 0; i < getHeight(); i++) {
            for(int j = 0; j < getWidth(); j++) {
                getCell(j, i).isActive = true;
            }
        }
    }
    
    public static final int SANDPILE_OVERFLOW = 4;
    @Override
    public void step() {
        running = true;
        ArrayList<Point> toppleCandidates = new ArrayList<>();
        for(int i = 0; i < getHeight(); i++) {
            for(int j = 0; j < getWidth(); j++) {
                if(getCell(j, i).groupID >= SANDPILE_OVERFLOW) {
                    toppleCandidates.add(new Point(j, i));
                }
            }
        }
        
        if(toppleCandidates.size() == 0) {
            // stable configuration
            running = false;
            return;
        }
        
        int chosenIndex = (int)(Math.random() * toppleCandidates.size());
        
        topple(toppleCandidates.get(chosenIndex));
    }
    
    void topple(Point p) {
        getCell(p.x, p.y).groupID -= SANDPILE_OVERFLOW;
        getCell(p.x - 1, p.y).groupID++;
        getCell(p.x + 1, p.y).groupID++;
        getCell(p.x, p.y - 1).groupID++;
        getCell(p.x, p.y + 1).groupID++;
    }
}