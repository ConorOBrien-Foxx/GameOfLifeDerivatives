package GameOfLifeDerivatives;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import GameOfLifeDerivatives.*;

public class Display extends JComponent {
    private static Board board;
    public Display(Board b) {
        board = b;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        int width = 10;
        g.setColor(Color.RED);
        for(int i = 0; i < board.getWidth(); i++) {
            for(int j = 0; j < board.getHeight(); j++) {
                if(board.getCell(i, j).isActive) {
                    g.fillRect(10 + j * (width+1), 10 + i * (width+1), width, width);
                }
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final Display comp = new Display(new Board());
        comp.setPreferredSize(new Dimension(256, 256));
        testFrame.getContentPane().add(comp, BorderLayout.CENTER);
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