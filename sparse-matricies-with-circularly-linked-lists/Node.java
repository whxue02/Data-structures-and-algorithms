// class for nodes used in the matrix
class Node{
    // initialize vars
    int col;
    int row;
    int value; // actual number in spot row, col
    Node nextRowNode; // pointer to next node in the row (left to right)
    Node nextColNode; // pointer to the next node in the col (top to bottom)

    // constructor
    Node(int row, int col, int value) {
        this.col = col;
        this.row = row;
        this.value = value;
        this.nextColNode = null;
        this.nextRowNode = null;
    }

    // to string
    public String toString() {
        return "row: " + row + " col: " + col + " value: " + value;
    }

    public static void main(String[] args) {
        Node n = new Node(1, 3,2);
        System.out.println("col: " + n.col + " row: " + n.row + " value: " + n.value);
    }
}



