package GameOfLifeDerivatives;

import java.util.ArrayList;
import java.awt.Point;
import GameOfLifeDerivatives.*;

public class Board {
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public ArrayList<ArrayList<Cell>> board;

    // height and width remain constant and are private
    private int height;
    private int width;

    public Board() {
        this(15, 15);
    }

    public Board(int w, int h) {
        height = h;
        width = w;
        
        reset();
    }
    
    public Board(ArrayList<ArrayList<Cell>> grid) {
        board = new ArrayList<ArrayList<Cell>>();
        // populate the rows
        height = grid.size();
        width = height == 0 ? 0 : grid.get(0).size();
        for (int i=0; i<height; i++){
            ArrayList<Cell> row = new ArrayList<Cell>();
            // populate the columns in each row
            for (int j=0; j<width; j++){
                Cell aCell = grid.get(i).get(j);
                row.add(aCell);
            }
            board.add(row);
        }
    }
    
    public void reset() {
        board = new ArrayList<ArrayList<Cell>>();
        
        // populate the rows
        for (int i=0; i<height; i++){
            ArrayList<Cell> row = new ArrayList<Cell>();
            // populate the columns in each row
            for (int j=0; j<width; j++){
                Cell aCell = new Cell();
                row.add(aCell);
            }
            board.add(row);
        }
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public Cell getCell(int x, int y) {
        if(y < 0)       y = height - (height - y) % height;
        if(y >= height) y %= height;
        if(x < 0)       x = width - (width -x ) % width;
        if(x >= width)  x %= width;
        return board.get(y).get(x);
    }
    public Cell getCell(Point p) { return getCell(p.x, p.y); }
    
    public boolean hasBorderAt(int x, int y, Direction d) {
        int bx, by;
        switch(d) {
            case UP:    bx = x; by = y - 1; break;
            case DOWN:  bx = x; by = y + 1; break;
            case LEFT:  bx = x - 1; by = y; break;
            case RIGHT: bx = x + 1; by = y; break;
            default:    return false;
        }
        Cell target = getCell(x, y);
        Cell toCheck = getCell(bx, by);
        return target.groupID == 0 || target.groupID != toCheck.groupID;
    }
    
    public int checkNeighbors(int w, int h){
        // gather the rows
        ArrayList<Cell> rowAbove = board.get((h-1+height) % height);
        ArrayList<Cell> row = board.get(h);
        ArrayList<Cell> rowBelow = board.get((h+1) % height);

        // gather the cells 
        return 0;

    }
}