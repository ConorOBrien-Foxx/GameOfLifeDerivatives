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
    
    public void step() {
        System.out.println("Unimplemented");
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
        for(int i = 0; i < board.getWidth(); i++) {
            for(int j = 0; j < board.getHeight(); j++) {
                System.out.print(
                    board.getCell(i, j).getX() + "," + board.getCell(i, j).getY() + " "
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
    public ArrayList<Cell> gatherGroupNeighbors(int x, int y){
        // grab the cell and its groupID
        Cell thisCell = getCell(x, y);
        int groupID = thisCell.groupID;
        // gather the members of its group if not zero
        ArrayList<Cell> groupMembers = new ArrayList<Cell>();
        if (groupID != 0){
            groupMembers = gatherGroupMembers(groupID);
        }
        // gather the unique neighbors of every member
        ArrayList<Cell> groupNeighbors = new ArrayList<Cell>();
        // up loop
        for (int i=0; i<groupMembers.size(); i++){
            // grab a member
            Cell thisMember = groupMembers.get(i);
            // grab its coordinates
            int xLoc = thisMember.getX();
            int yLoc = thisMember.getY();
            // grab its neighbors
            ArrayList<Cell> cellNeighbors = gatherCellNeighbors(xLoc, yLoc);
            groupNeighbors.addAll(cellNeighbors); 
        }      

        // remove dupes and return
        groupNeighbors = removeDuplicates(groupNeighbors);
        return groupNeighbors;
    }

    // grabs the neighbors of an individual cell, except those in the same group
    public ArrayList<Cell> gatherCellNeighbors(int x, int y){
        ArrayList<Cell> neighbors = new ArrayList<Cell>();
        // find the cell and groupID
        Cell thisCell = getCell(x, y);
        int thisGroupID = thisCell.groupID;

        // gather the cells
        Cell up = getCell(x, y-1);
        Cell down = getCell(x, y+1);
        Cell left = getCell(x-1, y);
        Cell right = getCell(x+1, y);

        // add neightbors when you can
        if (up.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(up); } 
        if (down.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(down); } 
        if (left.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(left); } 
        if (right.groupID != thisGroupID || thisGroupID == 0) { neighbors.add(right); } 

        // remove dupes and return
        neighbors = removeDuplicates(neighbors);
        return(neighbors);
    }

    // removes neighbors belonging to the same group
    public ArrayList<Cell> removeDuplicates(ArrayList<Cell> neighbors){
        ArrayList<Integer> neighborIDs = new ArrayList<Integer>();
        ArrayList<Cell> newNeighbors = new ArrayList<Cell>();
        for (int i=0; i<neighbors.size(); i++){
            Cell thisOne = neighbors.get(i);
            Integer thisID = thisOne.groupID;
            if (thisID == 0){ newNeighbors.add(thisOne); }
            else if (neighborIDs.contains(thisID)){
                newNeighbors.add(thisOne);
                neighborIDs.add(thisID);
            }
        }
        return newNeighbors;
    }

    // check the proportion of neighbors that are active
    public float checkProportionOfNeighbors(int x, int y){
        // keep track of the cell
        Cell thisCell = getCell(x, y);
        ArrayList<Cell> neighbors = new ArrayList<Cell>();
        
        // grab the neighbors
        if (thisCell.groupID == 0)
            neighbors = gatherCellNeighbors(x, y);
        else
            neighbors = gatherGroupNeighbors(x, y);
        
        // calclate the proportion
        int numberActive = 0;
        for (int i=0; i<neighbors.size(); i++){
            Cell thisOne = neighbors.get(i);
            if (thisCell.isActive) { numberActive++; }
        }

        // return the proportion
        return ((float)numberActive / (float)neighbors.size());
    }
}