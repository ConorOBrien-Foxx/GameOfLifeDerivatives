from cell import *

class board:

    def __init__(self):
        board = []
        
        length = 15
        width = 15

        for _ in range(length):
            row = []
            for _ in range(width):
                item = Cell()
                row.append(item)
            board.append(row)
        
            
            