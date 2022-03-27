package GameOfLifeDerivatives;

import java.io.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
// import java.awt.event.KeyListener;
import java.awt.geom.Line2D;

import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.JOptionPane;

import GameOfLifeDerivatives.*;

public class Display extends JComponent
                     implements MouseListener, MouseMotionListener, 
                     MouseWheelListener {
    public static JFrame frame;
    public final static int GROUP_ID_SETTING = -2;
    
    protected Board board;
    private int cellDisplayWidth = 26;
    private int borderSize = 4;
    private Set<Point> stroke = new HashSet<Point>();
    private int strokeButton = -1;
    private boolean blockFurtherMouseDown = false;
    
    public Display(Board b) {
        board = b;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    public void setBoard(Board b) {
        board = b;
        
        System.out.println(getFrameWidth() + " / " + getFrameHeight());
        setSize(getFrameWidth(), getFrameHeight());
        repaint();
        frame.pack();
    }
    
    public void step() {
        board.step();
        repaint();
    }
    
    public void reset() {
        board.reset();
        repaint();
    }
    
    public void randomize(float density) {
        for(int i = 0; i < board.getWidth(); i++) {
            for(int j = 0; j < board.getHeight(); j++) {
                if(Math.random() < density) board.getCell(i, j).toggle();
            }
        }
    }
    
    public void toggleFromMouseLocation(MouseEvent me) {
        Point p = getCellCoordinateFromMouseLocation(me.getPoint());
        if(p != null && !stroke.contains(p)) {
            Cell cell = board.getCell(p);
            if(strokeButton == MouseEvent.BUTTON1) {
                if((me.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
                    int previousID = cell.groupID;
                    strokeButton = -1;
                    cell.groupID = GROUP_ID_SETTING;
                    repaint();
                    String gidString = JOptionPane.showInputDialog(frame, "Please enter a Group ID:", previousID + "");
                    try {
                        cell.groupID = Integer.parseInt(gidString);
                    }
                    catch(NumberFormatException ex) {
                        cell.groupID = previousID;
                    }
                    repaint();
                    return;
                }
                else {
                    cell.toggle();
                }
            }
            else if(strokeButton == MouseEvent.BUTTON3) {
                cell.groupID++;
            }
            else {
                return;
            }
            stroke.add(p);
            repaint();
        }
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent me) {
        Point p = getCellCoordinateFromMouseLocation(me.getPoint());
        if(p != null) {
            if(me.getWheelRotation() < 0) {
                board.getCell(p).groupID++;
            }
            else if(board.getCell(p).groupID > 0) {
                board.getCell(p).groupID--;
            }
            repaint();
        }
    }
    
    @Override
    public void mousePressed(MouseEvent me) {
        stroke.clear();
        strokeButton = me.getButton();
        toggleFromMouseLocation(me);
    }
    
    @Override
    public void mouseMoved(MouseEvent me) {
        toggleFromMouseLocation(me);
    }
    
    @Override
    public void mouseDragged(MouseEvent me) {
        toggleFromMouseLocation(me);
    }
    
    @Override
    public void mouseExited(MouseEvent me) {
        // setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    @Override
    public void mouseEntered(MouseEvent me) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    @Override
    public void mouseReleased(MouseEvent me) {
        strokeButton = -1;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
    }
    
    public Point getCellCoordinateFromMouseLocation(int cx, int cy) {
        if(cx < 0 || cy < 0) {
            return null;
        }
        int ix = cx / getCellDisplayWidthOffset();
        int iy = cy / getCellDisplayWidthOffset();
        if(ix >= board.getWidth() || iy >= board.getHeight()) {
            return null;
        }
        return new Point(ix, iy);
    }
    public Point getCellCoordinateFromMouseLocation(Point p) {
        return getCellCoordinateFromMouseLocation(p.x, p.y);
    }
    
    // include enough room to draw outer border
    public int getFrameWidth() {
        return borderSize * 2 + getCellDisplayWidthOffset() * board.getWidth();
    }
    public int getFrameHeight() {
        return borderSize * 2 + getCellDisplayWidthOffset() * board.getHeight();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getFrameWidth(), getFrameHeight());
    }
    
    public int getCellDisplayWidthOffset() {
        return cellDisplayWidth + borderSize;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(borderSize));
        Line2D.Float bottomRightRightBorder = null;
        ArrayList<Line2D.Float> borders = new ArrayList<>();
        ArrayList<Color> borderColors = new ArrayList<>();
        
        Font large = new Font("Consolas", Font.PLAIN, 20);
        Font small = new Font("Consolas", Font.PLAIN, 12);
        
        
        for(int i = 0; i < board.getWidth(); i++) {
            for(int j = 0; j < board.getHeight(); j++) {
                int topLeftX = i * getCellDisplayWidthOffset();
                int topLeftY = j * getCellDisplayWidthOffset();
                Cell curCell = board.getCell(i, j);
                Color cellColor = curCell.getColor();
                if(curCell.isActive) {
                    g.setColor(cellColor);
                    g.fillRect(topLeftX, topLeftY, getCellDisplayWidthOffset(), getCellDisplayWidthOffset());
                }
                
                if(curCell.isDark() && curCell.isActive) {
                    g.setColor(Color.WHITE);
                }
                else {
                    g.setColor(Color.BLACK);
                }
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if(curCell.groupID > 99) {
                    g2.setFont(small);
                }
                else {
                    g2.setFont(large);
                }
                g2.drawString(
                    curCell.groupID == GROUP_ID_SETTING
                        ? "--"
                        : curCell.groupID + "",
                    topLeftX + 3 * borderSize / 2,
                    topLeftY + getCellDisplayWidthOffset() - borderSize / 2
                ); 
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                
                if(i + 1 == board.getWidth()) {
                    if(board.hasBorderAt(i, j, Board.Direction.RIGHT)) {
                        borderColors.add(Color.BLACK);
                    }
                    else {
                        if(curCell.groupID == board.getCell(i+1, j).groupID && !curCell.isActive) {
                            borderColors.add(new Color(0,0,0,0));
                        }
                        else {
                            borderColors.add(cellColor);
                        }
                    }
                    // since we process j+1==board.getHeight() last, we guarantee this is correctly set
                    bottomRightRightBorder = new Line2D.Float(
                        topLeftX + borderSize / 2 + getCellDisplayWidthOffset(),
                        topLeftY + borderSize / 2,
                        topLeftX + borderSize / 2 + getCellDisplayWidthOffset(),
                        topLeftY + borderSize / 2 + getCellDisplayWidthOffset()
                    );
                    borders.add(bottomRightRightBorder);
                }
                if(j + 1 == board.getHeight()) {
                    if(board.hasBorderAt(i, j, Board.Direction.DOWN)) {
                        borderColors.add(Color.BLACK);
                    }
                    else {
                        if(curCell.groupID == board.getCell(i, j+1).groupID && !curCell.isActive) {
                            borderColors.add(new Color(0,0,0,0));
                        }
                        else {
                            borderColors.add(cellColor);
                        }
                    }
                    borders.add(new Line2D.Float(
                        topLeftX + borderSize / 2,
                        topLeftY + borderSize / 2 + getCellDisplayWidthOffset(),
                        topLeftX + borderSize / 2 +     getCellDisplayWidthOffset(),
                        topLeftY + borderSize / 2 + getCellDisplayWidthOffset()
                    ));
                }
                
                if(board.hasBorderAt(i, j, Board.Direction.UP)) {
                    borderColors.add(Color.BLACK);
                    borders.add(new Line2D.Float(
                        topLeftX + borderSize / 2,
                        topLeftY + borderSize / 2,
                        topLeftX + borderSize / 2 +     getCellDisplayWidthOffset(),
                        topLeftY + borderSize / 2
                    ));
                }
                if(board.hasBorderAt(i, j, Board.Direction.LEFT)) {
                    borderColors.add(Color.BLACK);
                    borders.add(new Line2D.Float(
                        topLeftX + borderSize / 2,
                        topLeftY + borderSize / 2,
                        topLeftX + borderSize / 2,
                        topLeftY + borderSize / 2 + getCellDisplayWidthOffset()
                    ));
                }
            }
        }
        
        for(int i = 0; i < borders.size(); i++) {
            g.setColor(borderColors.get(i));
            g2.draw(borders.get(i));
        }
        
        // edge case: bottom right border
        if(board.hasBorderAt(-1, -1, Board.Direction.RIGHT) && !board.hasBorderAt(-1, -1, Board.Direction.DOWN)) {
            // redraw border
            if(bottomRightRightBorder != null) {
                g.setColor(Color.BLACK);
                g2.draw(bottomRightRightBorder);
            }
        }
    }
    
    public static int stepDelay = 100;
    public static void updateStepDelay(JTextField stepField) {
        stepDelay = Integer.parseInt(stepField.getText());
    }
    
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel displayPanel = new JPanel(new FlowLayout());
        displayPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        Board board;
        {
            RLEParser parser = null;
            if(args.length > 0) {
                parser = RLEParser.fromFile(args[0]);
                if(parser == null) {
                    System.out.println("Could not read file " + args[0]);
                    return;
                }
                parser.dump();
                board = parser.makeBoard();
            }
            else {
                board = new Board();
            }
        }
        
        final Display comp = new Display(board);
        displayPanel.add(comp);
        
        JPanel buttonsPanel = new JPanel();
        JButton helpButton = new JButton("Help");
        JButton nextGenerationButton = new JButton("Next Generation");
        JButton runStopButton = new JButton("Run");
        JLabel stepLabel = new JLabel("step/ms:");
        JTextField stepField = new JTextField("300", 3);
        JButton resetButton = new JButton("Reset");
        JButton randomButton = new JButton("Random Seed");
        JLabel densityLabel = new JLabel("Seed Density:");
        JTextField densityField = new JTextField("0.5", 3);
        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        String[] modeOptions = { "Entropy of Life", "Sandpiles" };
        JComboBox modeSelect = new JComboBox<>(modeOptions);
        
        final JFileChooser fc = new JFileChooser("./");
        
        // JButton dev1 = new JButton("[DEV] Group ID Sample");
        buttonsPanel.add(helpButton);
        buttonsPanel.add(modeSelect);
        buttonsPanel.add(nextGenerationButton);
        buttonsPanel.add(runStopButton);
        buttonsPanel.add(stepLabel);
        buttonsPanel.add(stepField);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(randomButton);
        buttonsPanel.add(densityLabel);
        buttonsPanel.add(densityField);
        buttonsPanel.add(exportButton);
        buttonsPanel.add(importButton);
        
        modeSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String mode = (String)cb.getSelectedItem();
                if(mode.equals("Entropy of Life")) {
                    comp.board = new Board(comp.board.board);
                }
                else {
                    comp.board = new SandpileBoard(comp.board.board);
                }
                System.out.println("Oh? " + (comp.board instanceof SandpileBoard));
                comp.repaint();
            }
        });
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int retval = fc.showSaveDialog(frame);
                if(retval == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    // System.out.println("Save to: " + file.getName());
                    comp.board.dumpRLEToFile(file.getName());
                }
            }
        });
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int retval = fc.showOpenDialog(frame);
                if(retval == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    
                    RLEParser parser = RLEParser.fromFile(file.getName());
                    if(parser == null) {
                        System.out.println("Could not read file " + args[0]);
                        return;
                    }
                    parser.dump();
                    comp.setBoard(parser.makeBoard());
                }
            }
        });
        stepField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStepDelay(stepField);
            }
        });
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Left Click to toggle a cell's alive state\nRight Click to increment a cell's Group ID\nMouse Wheel Up/Down to increase/decrease a cell's Group ID by 1\n", "Controls", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        runStopButton.addActionListener(new ActionListener() {
            class RunThread extends Thread {
                public void run() {
                    while(running) {
                        comp.step();
                        if(!comp.board.running) {
                            // stop running
                            actionPerformed(null);
                            break;
                        }
                        try {
                            Thread.sleep(stepDelay);
                        }
                        catch(InterruptedException ex) {
                            break;
                        }
                    }
                }
            }
            
            private boolean running = false;
            RunThread thread = null;
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStepDelay(stepField);
                runStopButton.setText(running ? "Run" : "Stop");
                running = !running;
                
                if(running) {
                    comp.board.running = true;
                    thread = new RunThread();
                    thread.start();
                }
                else {
                    thread = null;
                }
            }
        });
        nextGenerationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.step();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.reset();
            }
        });
        randomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.reset();
                float f = Float.parseFloat(densityField.getText());
                comp.randomize(f);
            }
        });
        
        frame.getContentPane().add(displayPanel, BorderLayout.CENTER);
        frame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        
        frame.pack();
        frame.setVisible(true);
    }
}