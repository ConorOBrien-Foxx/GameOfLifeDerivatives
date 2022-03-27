package GameOfLifeDerivatives;

import java.io.*;
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
    public static final int DEFAULT_ENTROPY = 2;
    
    // height and width remain constant and are private
    private int height;
    private int width;
    private int entropyLimit;
    private int highestGroup;
    private LinkedList<Integer> unusedSizes = new LinkedList<>();

    public Board() {
        this(55, 25);
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
                aCell.setX(j);
                aCell.setY(i);
                row.add(aCell);
            }
            board.add(row);
        }

        entropyLimit = DEFAULT_ENTROPY;
        highestGroup = 0;
    }
    
    private int makeGroupID() {
        // if(unusedSizes.size() != 0) {
            // return unusedSizes.pollLast();
        // }
        // else {
            return ++highestGroup;
        // }
    }
    
    public String getEngine() {
        return "gol";
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

        highestGroup = 0;
        entropyLimit = DEFAULT_ENTROPY;
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

        /*
        for (Cell c : neighbors){
            System.out.println(c);
        }
        */

        // calclate the proportion
        int numberActive = 0;
        for (Cell c : neighbors){
            if (c.isActive) { numberActive++; }
        }
        
        // return the proportion
        // System.out.println("X: " + x + ", Y: " + y + " Neighbors: " + neighbors.size());
        // System.out.println("X: " + x + ", Y: " + y + " Number Active: " + numberActive);
        return ((float)numberActive / (float)neighbors.size());
    }

    // increases entropy of surroudning elements


    // steps with no entropy 
    public void step(){
        // create an empty board
        ArrayList<ArrayList<Cell>> temp = createEmptyBoard();
        ArrayList<Cell> entropizingCells = new ArrayList<Cell>();
        
        boolean anyAlive = false;

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
                
                //System.out.println("====" + newCell + ": " + proportion + "===="); 
                
                /**
                 * if alive:
                 * less than 17%, 1.5/9, dies
                 * between 17%, 1.5/9, and 39%, 3.5/9, remains alive
                 * greater than 39%, 3.5/9, dies
                 */
                if (newCell.isActive){
                    if (0.39 > proportion && proportion >= 0.17) {
                        newCell.isActive = true;
                        anyAlive = true;
                    }
                    else{ newCell.isActive = false; }
                }
                /**
                 * if dead:
                 * less than 28%, 2.5/9, remains dead
                 * between 28%, 2.5/9, and 39%, 3.5/9, becomes alive
                 * greater than 39%, 3.5/9, remains dead
                 */
                else{ 
                    if (0.39 > proportion && proportion >= 0.28){ 
                        newCell.isActive = true; 
                        anyAlive = true;
                        // we mark it to increase entropy later
                        entropizingCells.add(newCell);
                    }
                    else{ newCell.isActive = false; }
                }
                
                // grab the new cell and set it accordingly
                thisRow.set(j,newCell);
            }
        }

        // replace the board!
        board = temp; 
        // now we increase the entropy on the indicated cells
        /* 
        for (Cell thisCell : entropizingCells){
            System.out.println("Entropizing: " + thisCell);
        }
        */ 
        increaseEntropy(entropizingCells);
        
        if(!anyAlive) running = false;
    }
    
    // increases entropy of entropizing cells
    private void increaseEntropy(ArrayList<Cell> entropizingCells){
        for (Cell thisCell : entropizingCells){
            // cursed
            int y = thisCell.getX();
            int x = thisCell.getY();

            Cell up = getCell(x, y-1);
            Cell down = getCell(x, y+1);
            Cell left = getCell(x-1, y);
            Cell right = getCell(x+1, y);

            /*
            System.out.println("up: " + up);
            System.out.println("down: " + down);
            System.out.println("left: " + left);
            System.out.println("right: " + right);
            */

            // up, down, left, right
            down.entropies[0]++;
            up.entropies[1]++;
            right.entropies[2]++;
            left.entropies[3]++;
            
            // System.out.println("down.entropies[0]: " + down.entropies[0]);
            // System.out.println("entropyLimit: " + entropyLimit);
            if (down.entropies[0] > entropyLimit){
                merge(thisCell, up);
            }
            if (up.entropies[1] > entropyLimit){
                merge(thisCell, down);
            }
            if (right.entropies[2] > entropyLimit){
                merge(thisCell, left);
            }
            if (left.entropies[3] > entropyLimit){
                merge(thisCell, right);
            }
        }
    }

    private void merge(Cell cell1, Cell cell2){
        // System.out.println("Cell1 merge: " + cell1);
        // System.out.println("Cell2 merge: " + cell2);

        int group1 = cell1.groupID;
        int group2 = cell2.groupID;
        cell1.isActive = true;
        cell2.isActive = true;
        // if they're both 0, make a new group
        if (group1 == 0 && group2 == 0){
            // System.out.println("Case 1");
            cell1.groupID = makeGroupID();
            cell2.groupID = cell1.groupID;
        }
        // if one is 0, set it to the other
        else if (group1 == 0){
            // System.out.println("Case 2A");
            cell1.groupID = group2;
        }
        else if (group2 == 0){
            // System.out.println("Case 2B");
            cell2.groupID = group1;
        }
        // else, fix all group1 to group2
        else{
            // System.out.println("Case 3");
            for (int i=0; i<height; i++){
                for (int j=0; j<width; j++){
                    // grab a cell
                    Cell testCell = getCell(j,i);
                    // check if it has a certain ID
                    int testID = testCell.groupID;
                    if (testID == group1 || testID == group2){
                        // if it does, fix it
                        testCell.groupID = group2;
                        testCell.isActive = true;
                    }  
                }
            }
            unusedSizes.addFirst(group1);
        }
    }

    public void dumpRLEToFile(String path) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(path), "utf-8"))) {
            writer.write("#GOLD_RLEX\n");
            writer.write("x = 0, y = 0, engine = " + getEngine() + '\n');
            Cell previous = null, current;
            int runLength = 0;
            for(int i = 0; i < height; i++) {
                for(int j = 0; j < width; j++) {
                    current = getCell(j, i);
                    if(previous == null) {
                        previous = current;
                        runLength = 1;
                    }
                    else if(current.groupID != previous.groupID || current.isActive != previous.isActive) {
                        writer.write(runLength + "&" + previous.groupID);
                        writer.write(previous.isActive ? 'o' : 'b');
                        runLength = 1;
                    }
                    else {
                        runLength++;
                    }
                    previous = current;
                }
                writer.write(runLength + "&" + previous.groupID);
                writer.write(previous.isActive ? 'o' : 'b');
                previous = null;
                writer.write("$\n");
            }
        }
        catch(IOException ex) {
            System.out.println("unable to save file");
        }
    }
}