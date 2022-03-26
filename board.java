import java.util.ArrayList;
import cell;

public class Board{

    public ArrayList board;

    // height and width remain constant and are private
    private static int height;
    private static int width; 

    public Board(){
        height = 15;
        width = 15;
        
        // populate the rows
        for (int i=0; i<height; i++){
            ArrayList row;
            // populate the columns in each row
            for (int i=0; i<width; i++){
                aCell = new Cell();
                row.add(aCell);
            }
            board.add(row);
        }
    }

    public Board(int w, int h){
        height = h;
        width = w;
        
        // populate the rows
        for (int i=0; i<height; i++){
            ArrayList row;
            // populate the columns in each row
            for (int i=0; i<width; i++){
                aCell = new Cell();
                row.add(aCell);
            }
            board.add(row);
        }
    }

    public int checkNeighbors(int w, int h){
        // gather the cells
        upCell = board[w][(h + 1) % height]
        downCell = board[w][(h - 1 + height) % height]
        leftCell = board[(w - 1 + width) % width][h]
        rightCell = board[(w + 1) % height][h]

    }
}