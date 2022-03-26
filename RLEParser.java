package GameOfLifeDerivatives;

import java.io.*;
import java.util.ArrayList;

import GameOfLifeDerivatives.*;

// parses .rle (standard golly files) and .rlegx (our own file extension)
public class RLEParser {
    public int x;
    public int y;
    public String rule = null;
    public ArrayList<ArrayList<Cell>> buildGrid = new ArrayList<>();
    public ArrayList<Cell> buildRow = new ArrayList<>();
    
    
    private boolean frozen = false;
    private int repeatCount = 1;
    private int maxWidth = 0;
    
    public RLEParser() {
        
    }
    
    public void padGrid() {
        for(ArrayList<Cell> row : buildGrid) {
            while(row.size() < maxWidth) {
                row.add(new Cell(false));
            }
        }
    }
    
    public Board makeBoard() {
        padGrid();
        return new Board(this.buildGrid);
    }
    
    public void addRepeatCell(Cell cell) {
        for(int i = 0; i < repeatCount; i++) {
            buildRow.add(new Cell(cell));
        }
    }
    
    public void addRow() {
        if(buildRow.size() > maxWidth) {
            maxWidth = buildRow.size();
        }
        buildGrid.add(new ArrayList<>(buildRow));
        buildRow.clear();
    }
    
    public void feedLine(String line) {
        if(frozen) return;
        if(line == null) return;
        if(line.startsWith("#")) {
            // comment, ignore
            return;
        }
        if(line.startsWith("x ") || line.startsWith("x=")) {
            // xyrule line
            String[] sections = line.split(",");
            for(int i = 0; i < sections.length; i++) {
                String[] parts = sections[i].split("=");
                String first = parts[0].trim();
                String second = parts[1].trim();
                if(first.equals("x")) {
                    x = Integer.parseInt(second);
                }
                else if(first.equals("y")) {
                    y = Integer.parseInt(second);
                }
                else if(first.equals("rule")) {
                    rule = second;
                }
            }
        }
        else {
            Cell buildCell = new Cell();
            for(int i = 0; i < line.length(); i++) {
                char cur = line.charAt(i);
                if(cur == 'b') {
                    buildCell.isActive = false;
                    addRepeatCell(buildCell);
                    repeatCount = 1;
                }
                else if(cur == 'o') {
                    buildCell.isActive = true;
                    addRepeatCell(buildCell);
                    repeatCount = 1;
                }
                else if(cur == '$') {
                    addRow();
                }
                else if(Character.isDigit(cur)) {
                    int buildInt = cur - '0';
                    while(Character.isDigit(cur = line.charAt(++i))) {
                        buildInt *= 10;
                        buildInt += cur - '0';
                    }
                    i--;
                    repeatCount = buildInt;
                }
                else if(cur == '!') {
                    freeze();
                    return;
                }
            }
        }
    }
    
    public void freeze() {
        addRow();
        frozen = true;
    }
    
    public static RLEParser fromFile(String path) {
        RLEParser parser = new RLEParser();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            do {
                line = br.readLine();
                parser.feedLine(line);
            } while(line != null);
        }
        catch(IOException ex) {
            return null;
        }
        
        parser.freeze();
        
        return parser;
    }
}