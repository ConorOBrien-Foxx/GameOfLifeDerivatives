class Cell:

    def __init__(self):
        self.isActive = True
        
        # entropies[0] = up
        # entropies[1] = down
        # entropies[2] = left
        # entropies[3] = right
        self.entropies = [0, 0, 0, 0]
        
        self.groupID = 0

    def reduceEntropy(self):
        for entry in range(len(self.entropies)):
            self.entropies[entry] = self.entropies[entry] - 1

    def __str__(self):
        if self.isActive:
            return "on"
        else:
            return "off"