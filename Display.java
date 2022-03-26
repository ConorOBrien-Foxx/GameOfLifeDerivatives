package GameOfLifeDerivatives;

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
    private boolean controlHeld = false;
    private boolean blockFurtherMouseDown = false;
    
    public Display(Board b) {
        board = b;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    public void reset() {
        board.reset();
        repaint();
    }
    
    public void randomize(float density) {
        for(int i = 0; i < board.getWidth(); i++) {
            for(int j = 0; j < board.getHeight(); j++) {
                if(Math.random() > density) board.getCell(i, j).toggle();
            }
        }
    }
    
    public void toggleFromMouseLocation(MouseEvent me) {
        Point p = getCellCoordinateFromMouseLocation(me.getPoint());
        if(p != null && !stroke.contains(p)) {
            Cell cell = board.getCell(p);
            if(strokeButton == MouseEvent.BUTTON1) {
                if((me.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
                    cell.groupID = GROUP_ID_SETTING;
                    repaint();
                    String gidString = JOptionPane.showInputDialog(frame, "Please enter a group ID:", null);
                    strokeButton = -1;
                    try {
                        cell.groupID = Integer.parseInt(gidString);
                    }
                    catch(NumberFormatException ex) {
                        cell.groupID = 0;
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
                
        g2.setFont(new Font("Consolas", Font.PLAIN, 20));
        
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
                        borderColors.add(cellColor);
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
                        borderColors.add(cellColor);
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
    
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel displayPanel = new JPanel(new FlowLayout());
        displayPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        RLEParser parser;
        Board board;
        if(args.length > 0) {
            parser = RLEParser.fromFile(args[0]);
            if(parser == null) {
                System.out.println("Could not read file " + args[0]);
                return;
            }
            System.out.println("x    = " + parser.x);
            System.out.println("y    = " + parser.y);
            System.out.println("rule = " + parser.rule);
            board = parser.makeBoard();
        }
        else {
            board = new Board();
        }
        
        final Display comp = new Display(board);
        displayPanel.add(comp);
        
        JPanel buttonsPanel = new JPanel();
        JButton nextGenerationButton = new JButton("Next Generation");
        JButton runStopButton = new JButton("Run");
        JButton resetButton = new JButton("Reset");
        JButton randomButton = new JButton("Random Seed");
        JLabel densityLabel = new JLabel("Seed Density:");
        JTextField densityField = new JTextField("0.5", 3);
        JButton dev1 = new JButton("[DEV] Group ID Sample");
        buttonsPanel.add(nextGenerationButton);
        buttonsPanel.add(runStopButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(randomButton);
        buttonsPanel.add(densityLabel);
        // buttonsPanel.add(densityField);
        buttonsPanel.add(dev1);
        
        dev1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int gid = 0;
                for(int j = 0; j < Cell.DIMINISH_TIMES * 2; j++) {
                    for(int i = 0; i < Cell.STRATA_COUNT; i++) {
                        comp.board.getCell(i, j).groupID = gid++;
                        comp.repaint();
                    }
                }
            }
        });
        runStopButton.addActionListener(new ActionListener() {
            private boolean running = false;
            @Override
            public void actionPerformed(ActionEvent e) {
                runStopButton.setText(running ? "Run" : "Stop");
                running = !running;
            }
        });
        nextGenerationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TODO: advance the generation");
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