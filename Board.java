package GameOfLifeDerivatives;

import java.util.*;
import java.awt.Point;
import GameOfLifeDerivatives.*;

public class Board {
    // direction matters for walls, and determining when they exist
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    // the board upon which the magic is done
    public ArrayList<ArrayList<Cell>> board;

    public boolean running = true;

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
                aCell.setX(i);
                aCell.setY(j);
                row.add(aCell);
            }
            board.add(row);
        }
    }
    
    public void debugCoordinates() {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                System.out.print(
                    getCell(i, j).getX() + "," + getCell(i, j).getY() + " "
                );
            }
            System.out.println();
        }
    }
    
    public void reset() {
        board = new ArrayList<ArrayList<Cell>>();
        
        // populate the rows
        for (int i=0; i<height; i++){
            ArrayList<Cell> row = new ArrayList<Cell>();
            // populate the columns in each row
            for (int j=0; j<width; j++){
                Cell aCell = new Cell(i,j);
                row.add(aCell);
            }
            board.add(row);
        }
    }

    // creates and returns an empty board object
    public ArrayList<ArrayList<Cell>> createEmptyBoard() {
        ArrayList<ArrayList<Cell>> temp = new ArrayList<ArrayList<Cell>>();
        
        // populate the rows
        for (int i=0; i<height; i++){
            ArrayList<Cell> row = new ArrayList<Cell>();
            // populate the columns in each row
            for (int j=0; j<width; j++){
                Cell aCell = new Cell(i,j);
                row.add(aCell);
            }
            temp.add(row);
        }

        return temp;
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
    
    // gathers ALL members of the group, ignoring neighbors
    public ArrayList<Cell> gatherGroupMembers(int groupID){
        // gather all members
        ArrayList<Cell> groupMembers =  new ArrayList<Cell>();
        // iterate through the whole board
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                Cell aCell = getCell(j,i);
                int theID = aCell.groupID;
                if (theID == groupID) { groupMembers.add(aCell); }
            }
        }
        // return the list of members
        return groupMembers;
    }

    // gathers ALL neighbors of a group
    public HashSet<Cell> gatherGroupNeighbors(int x, int y){
        // grab the cell and its groupID
        Cell thisCell = getCell(x, y);
        int groupID = thisCell.groupID;
        // gather the members of its group if not zero
        ArrayList<Cell> groupMembers = new ArrayList<Cell>();
        if (groupID != 0){
            groupMembers = gatherGroupMembers(groupID);
        }
        // gather the unique neighbors of every member
        HashSet<Cell> groupNeighbors = new HashSet<Cell>();
        // up loop
        for (int i=0; i<groupMembers.size(); i++){
            // grab a member
            Cell thisMember = groupMembers.get(i);
            // grab its coordinates
            int xLoc = thisMember.getX();
            int yLoc = thisMember.getY();
            // grab its neighbors
            HashSet<Cell> cellNeighbors = gatherCellNeighbors(xLoc, yLoc);
            groupNeighbors.addAll(cellNeighbors); 
        }      

        // remove dupes and return
        // groupNeighbors = removeDuplicates(groupNeighbors);
        return groupNeighbors;
    }
    
    // grabs the neighbors of an individual cell, except those in the same group
    public HashSet<Cell> gatherCellNeighbors(int x, int y){
        HashSet<Cell> neighbors = new HashSet<Cell>();
        // find the cell and groupID
        Cell thisCell = getCell(x, y);
        int thisGroupID = thisCell.groupID;

        // gather the cells
        Cell up = getCell(x, y-1);
        Cell down = getCell(x, y+1);
        Cell left = getCell(x-1, y);
        Cell right = getCell(x+1, y);

        Cell upLeft = getCell(x-1, y-1);
        Cell downLeft = getCell(x-1, y+1);
        Cell upRight = getCell(x+1, y-1);
        Cell downRight = getCell(x+1, y+1);

        // add neightbors when you can
        if (up.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(up); } 
        if (down.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(down); } 
        if (left.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(left); } 
        if (right.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(right); } 

        if (upLeft.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(upLeft); } 
        if (downLeft.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(downLeft); } 
        if (upRight.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(upRight); } 
        if (downRight.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(downRight); } 

        // remove dupes and return
        // neighbors = removeDuplicates(neighbors);

        return(neighbors);
    }

    /*
    // removes neighbors belonging to the same group
    public ArrayList<Cell> removeDuplicates(ArrayList<Cell> neighbors){
        // gather all of the elements and their numbers
        // make a new empty list
        ArrayList<Integer> neighborIDs = new ArrayList<Integer>();
        ArrayList<Integer[]> locs = new ArrayList<Integer[]>();
        ArrayList<Cell> newNeighbors = new ArrayList<Cell>();
        // add things to the empty list as they go along
        for (int i=0; i<neighbors.size(); i++){
            Cell thisOne = neighbors.get(i);
            Integer thisID = thisOne.groupID;
            Integer[] loc = thisOne.getLoc();
            // if it's a zero, check if it was included
            if (thisID == 0){ 
                if (!(locs.contains(loc))){ 
                    locs.add(loc); 
                    newNeighbors.add(thisOne);
                } 
            }
            // if not, check that it wasn't included already 
            else if (!(neighborIDs.contains(thisID))){
                if (!(locs.contains(loc))){ 
                    locs.add(loc);
                    newNeighbors.add(thisOne);
                    neighborIDs.add(thisID);
                    
                }
            }
        }
        return newNeighbors;
    }
    */ 

    // check the proportion of neighbors that are active
    public float checkProportion(int x, int y){
        // keep track of the cell
        Cell thisCell = getCell(x, y);
        HashSet<Cell> neighbors = new HashSet<Cell>();
        
        // grab the neighbors
        if (thisCell.groupID == 0)
            neighbors = gatherCellNeighbors(x, y);
        else
            neighbors = gatherGroupNeighbors(x, y);
        
        // calclate the proportion
        int numberActive = 0;
        for (Cell c : neighbors){
            if (c.isActive) { numberActive++; }
        }

        // return the proportion
        return ((float)numberActive / (float)neighbors.size());
    }

    // increases entropy of surroudning elements


    // steps with no entropy 
    public void step(){
        // create an empty board
        ArrayList<ArrayList<Cell>> temp = createEmptyBoard();

        // iterate through the rows
        for (int i=0; i<height; i++){
            // grab a row
            ArrayList<Cell> thisRow = temp.get(i);
            // iterate through its elements
            for (int j=0; j<width; j++){
                // gather information on current state
                boolean willBeAlive;
                float proportion = checkProportion(j, i); 
                Cell newCell = new Cell(getCell(j, i));
                
                /**
                 * if alive:
                 * less than 17%, 1.5/9, dies
                 * between 17%, 1.5/9, and 39%, 3.5/9, remains alive
                 * greater than 39%, 3.5/9, dies
                 */
                if (newCell.isActive){
                    if (0.39 > proportion && proportion >= 0.17){ willBeAlive = true; }
                    else{ willBeAlive = false; }
                }
                /**
                 * if dead:
                 * less than 28%, 2.5/9, remains dead
                 * between 28%, 2.5/9, and 39%, 3.5/9, becomes alive
                 * greater than 39%, 3.5/9, remains dead
                 */
                else{
                    if (0.39 > proportion && proportion >= 0.28){ willBeAlive = true; }
                    else{ willBeAlive = false; }
                }
                
                // grab the new cell and set it accordingly
                newCell.isActive = willBeAlive;
                thisRow.set(j,newCell);
            }
        }

        // replace the board!
        board = temp; 
    }
}