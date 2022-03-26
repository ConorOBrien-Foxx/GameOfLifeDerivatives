package GameOfLifeDerivatives;

import java.util.ArrayList;
import GameOfLifeDerivatives.*;

public class Board{

    public ArrayList<ArrayList<Cell>> board;

    // height and width remain constant and are private
    private int height;
    private int width;

    public Board(){
        this(15, 15);
    }

    public Board(int w, int h){
        height = h;
        width = w;

        ArrayList<ArrayList<Cell>> board = new ArrayList<ArrayList<Cell>>();
        
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
        
        this.board = board;
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Cell getCell(int x, int y) { return board.get(y).get(x); }

    public int checkNeighbors(int w, int h){
        // gather the rows
        ArrayList<Cell> rowAbove = board.get((h-1+height) % height);
        ArrayList<Cell> row = board.get(h);
        ArrayList<Cell> rowBelow = board.get((h+1) % height);

        // gather the cells 
        return 0;

    }
}