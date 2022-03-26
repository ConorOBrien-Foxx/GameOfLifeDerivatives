package GameOfLifeDerivatives;

import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.*;

import GameOfLifeDerivatives.*;

public class Display extends JComponent {
    private Board board;
    private int cellDisplayWidth = 30;
    private int borderSize = 4;
    
    public Display(Board b) {
        board = b;
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                Point p = getCellCoordinateFromMouseLocation(me.getPoint());
                if(p != null) {
                    System.out.println("Clicked on cell " + p.x + ", " + p.y);
                    switch(me.getButton()) {
                        case MouseEvent.BUTTON1:
                            System.out.println("Toggling value!");
                            board.getCell(p).toggle();
                            break;
                        case MouseEvent.BUTTON3:
                            System.out.println("Incrementing group ID!");
                            board.getCell(p).groupID++;
                            break;
                    }
                    repaint();
                }
            }
        });
    }
    
    public Point getCellCoordinateFromMouseLocation(int cx, int cy) {
        // int clickOffsetX = cx % getCellDisplayWidthOffset();
        // int clickOffsetY = cy % getCellDisplayWidthOffset();
        
        // if(clickOffsetX >= cellDisplayWidth || clickOffsetY >= cellDisplayWidth) {
            // return null;
        // }
        
        int ix = cx / getCellDisplayWidthOffset();
        int iy = cy / getCellDisplayWidthOffset();
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
        for(int i = 0; i < board.getWidth(); i++) {
            for(int j = 0; j < board.getHeight(); j++) {
                int topLeftX = i * getCellDisplayWidthOffset();
                int topLeftY = j * getCellDisplayWidthOffset();
                Cell curCell = board.getCell(i, j);
                Color cellColor = curCell.groupID == 0 ? Color.RED : Color.GREEN;
                if(curCell.isActive) {
                    g.setColor(cellColor);
                    g.fillRect(topLeftX, topLeftY, getCellDisplayWidthOffset(), getCellDisplayWidthOffset());
                }
                
                if(i + 1 == board.getWidth()) {
                    if(board.hasBorderAt(i, j, Board.Direction.RIGHT)) {
                        g.setColor(Color.BLACK);
                    }
                    else {
                        g.setColor(cellColor);
                    }
                    // since we process j+1==board.getHeight() last, we guarantee this is correctly set
                    bottomRightRightBorder = new Line2D.Float(
                        topLeftX + borderSize / 2 + getCellDisplayWidthOffset(),
                        topLeftY + borderSize / 2,
                        topLeftX + borderSize / 2 + getCellDisplayWidthOffset(),
                        topLeftY + borderSize / 2 + getCellDisplayWidthOffset()
                    );
                    g2.draw(bottomRightRightBorder);
                }
                if(j + 1 == board.getHeight()) {
                    if(board.hasBorderAt(i, j, Board.Direction.DOWN)) {
                        g.setColor(Color.BLACK);
                    }
                    else {
                        g.setColor(cellColor);
                    }
                    g2.draw(new Line2D.Float(
                        topLeftX + borderSize / 2,
                        topLeftY + borderSize / 2 + getCellDisplayWidthOffset(),
                        topLeftX + borderSize / 2 +     getCellDisplayWidthOffset(),
                        topLeftY + borderSize / 2 + getCellDisplayWidthOffset()
                    ));
                }
                
                if(board.hasBorderAt(i, j, Board.Direction.UP)) {
                    g.setColor(Color.BLACK);
                    g2.draw(new Line2D.Float(
                        topLeftX + borderSize / 2,
                        topLeftY + borderSize / 2,
                        topLeftX + borderSize / 2 +     getCellDisplayWidthOffset(),
                        topLeftY + borderSize / 2
                    ));
                }
                if(board.hasBorderAt(i, j, Board.Direction.LEFT)) {
                    g.setColor(Color.BLACK);
                    g2.draw(new Line2D.Float(
                        topLeftX + borderSize / 2,
                        topLeftY + borderSize / 2,
                        topLeftX + borderSize / 2,
                        topLeftY + borderSize / 2 + getCellDisplayWidthOffset()
                    ));
                }
            }
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
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel displayPanel = new JPanel(new FlowLayout());
        displayPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        final Display comp = new Display(new Board(10, 15));
        displayPanel.add(comp);
        
        testFrame.getContentPane().add(displayPanel, BorderLayout.CENTER);
        
        testFrame.pack();
        testFrame.setVisible(true);
    }
    
    /*
    
    
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final Display comp = new Display();
        comp.setPreferredSize(new Dimension(320, 200));
        testFrame.getContentPane().add(comp, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        JButton newLineButton = new JButton("New Line");
        JButton clearButton = new JButton("Clear");
        buttonsPanel.add(newLineButton);
        buttonsPanel.add(clearButton);
        testFrame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        newLineButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int x1 = (int) (Math.random()*320);
                int x2 = (int) (Math.random()*320);
                int y1 = (int) (Math.random()*200);
                int y2 = (int) (Math.random()*200);
                Color randomColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
                comp.addLine(x1, y1, x2, y2, randomColor);
            }
        });
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                comp.clearLines();
            }
        });
        testFrame.pack();
        testFrame.setVisible(true);
    
    */
}