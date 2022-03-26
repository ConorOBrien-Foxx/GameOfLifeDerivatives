package GameOfLifeDerivatives;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.FlowLayout;
import java.awt.Point;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.*;

import GameOfLifeDerivatives.*;

public class Display extends JComponent {
    private Board board;
    private int cellDisplayWidth = 30;
    private int borderSize = 3;
    
    public Display(Board b) {
        board = b;
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                Point p = getClickedCellCoordinate(me.getPoint());
                if(p != null) {
                    System.out.println("Clicked on cell " + p.x + ", " + p.y);
                    board.toggleCell(p);
                    repaint();
                }
            }
        });
    }
    
    public Point getClickedCellCoordinate(int cx, int cy) {
        int clickOffsetX = cx % getCellDisplayWidthOffset();
        int clickOffsetY = cy % getCellDisplayWidthOffset();
        
        if(clickOffsetX >= cellDisplayWidth || clickOffsetY >= cellDisplayWidth) {
            return null;
        }
        
        int ix = cx / getCellDisplayWidthOffset();
        int iy = cy / getCellDisplayWidthOffset();
        return new Point(ix, iy);
    }
    public Point getClickedCellCoordinate(Point p) {
        return getClickedCellCoordinate(p.x, p.y);
    }
    
    public int getFrameWidth() {
        return getCellDisplayWidthOffset() * board.getWidth();
    }
    public int getFrameHeight() {
        return getCellDisplayWidthOffset() * board.getHeight();
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
        g.setColor(Color.RED);
        for(int i = 0; i < board.getWidth(); i++) {
            for(int j = 0; j < board.getHeight(); j++) {
                if(board.getCell(i, j).isActive) {
                    g.fillRect(
                        i * getCellDisplayWidthOffset(),
                        j * getCellDisplayWidthOffset(),
                        cellDisplayWidth,
                        cellDisplayWidth
                    );
                }
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