package GameOfLifeDerivatives;

import java.io.*;
import java.util.ArrayList;

import GameOfLifeDerivatives.*;

// parses .rle (standard golly files) and .rlegold (our own file extension)
public class RLEParser {
    public int x;
    public int y;
    public String rule = null;
    public String engine = null;
    public ArrayList<ArrayList<Cell>> buildGrid = new ArrayList<>();
    public ArrayList<Cell> buildRow = new ArrayList<>();
    
    
    private boolean frozen = false;
    private boolean goldMode = false;
    private int repeatCount = 1;
    private int maxWidth = 0;
    private Cell buildCell = new Cell(0, 0);
    
    public RLEParser() {
        
    }
    
    public void padGrid() {
        for(int i = 0; i < buildGrid.size(); i++) {
            ArrayList<Cell> row = buildGrid.get(i);
            while(row.size() < maxWidth) {
                row.add(new Cell(false, row.size(), i));
            }
        }
    }
    
    public Board makeBoard() {
        padGrid();
        if(engine == null || engine.equals("gol")) {
            return new Board(this.buildGrid);
        }
        if(engine.equals("sandpile")) {
            return new SandpileBoard(this.buildGrid);
        }
        return null;
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
        if(line.startsWith("#GOLD_RLEX")) {
            goldMode = true;
        }
        if(line.startsWith("#")) {
            // other kind of comment, ignore
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
                else if(first.equals("engine")) {
                    engine = second;
                }
            }
        }
        else {
            char lastCur = 0;
            for(int i = 0; i < line.length(); i++) {
                char cur = line.charAt(i);
                if(cur == 'b') {
                    buildCell.isActive = false;
                    addRepeatCell(buildCell);
                    repeatCount = 1;
                    buildCell.groupID = 0;
                }
                else if(cur == 'o') {
                    buildCell.isActive = true;
                    addRepeatCell(buildCell);
                    repeatCount = 1;
                    buildCell.groupID = 0;
                }
                else if(cur == '$') {
                    addRow();
                }
                else if(cur == '&') {
                    if(!goldMode) {
                        System.out.println("Warning: Gold mode not enabled, so & is ignored.");
                    }
                }
                else if(Character.isDigit(cur)) {
                    int buildInt = cur - '0';
                    while(Character.isDigit(cur = line.charAt(++i))) {
                        buildInt *= 10;
                        buildInt += cur - '0';
                    }
                    i--;
                    if(lastCur == '&' && goldMode) {
                        // custom: set group id
                        buildCell.groupID = buildInt;
                    }
                    else {
                        repeatCount = buildInt;
                    }
                }
                else if(cur == '!') {
                    freeze();
                    return;
                }
                lastCur = line.charAt(i);
            }
        }
    }
    
    public void freeze() {
        if(buildRow.size() >= 1) {
            addRow();
        }
        frozen = true;
    }
    
    public void dump() {
        System.out.println("x      = " + x);
        System.out.println("y      = " + y);
        System.out.println("rule   = " + rule);
        System.out.println("engine = " + engine);
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