
// class for the sparse matrix
class SparseMatrix {
    int n; // size of matrix
    Node header;
    Node[] rowHeaders;
    Node[] colHeaders;

    // constructor
    public SparseMatrix(int n) {
        this.n = n;
        this.rowHeaders = new Node[n+1];
        this.colHeaders = new Node[n+1];
        createMatrix();
    }

    // function to create the matrix headers
    private void createMatrix() {
        // create header node
        header = new Node(n, n, -1);

        // create col headers and link them --> way
        Node prev = header;
        Node cur = null;
        for(int i = 1; i <= n; i++) {
            colHeaders[i] = new Node(0, i, -1);
            cur = colHeaders[i];
            prev.nextRowNode = cur;
            prev = cur;
            // node points to itself vertically at first
            colHeaders[i].nextColNode = colHeaders[i];
        }
        
        // last node points back to header to form the circular list
        if (cur != null) {
            cur.nextRowNode = header;
            cur.nextColNode = cur; 
        }

        // create left column (row headers)
        prev = header;
        for(int i = 1; i <= n; i++) {
            rowHeaders[i] = new Node(i, 0, -1);
            cur = rowHeaders[i];
            prev.nextColNode = cur;
            prev = cur;
            rowHeaders[i].nextRowNode = rowHeaders[i];
        }
        
        // last node points back to header to form the circular list
        if (cur != null) {
            cur.nextColNode = header;
            cur.nextRowNode = cur;
        }

        // testing
        // for (int i = 1; i <= n; i++) {
        //     System.out.println(rowHeaders[i]);
        // }
    }

    // function to insert a node into the matrix
    public void insert(int row, int col, int value) {
        // if value = 0, dont insert into matrix
        if (value == 0) {
            return;
        }

        // the node to be inserted
        Node insertedNode = new Node(row, col, value);

        // connect the node horizontally
        Node cur = rowHeaders[row];
        // iterate to find the node's spot
        while (cur.col < col) {
            // if end of row (bc looping back to header node)
            if (cur.nextRowNode == rowHeaders[row]) {
                // then its time to insert
                insertedNode.nextRowNode = rowHeaders[row];
                cur.nextRowNode = insertedNode;
                break;
            }
            // if we need to insert before the next node (bc row node is supposed to be after the node to be inserted)
            else if (cur.nextRowNode.col > col) {
                // insert
                insertedNode.nextRowNode = cur.nextRowNode;
                cur.nextRowNode = insertedNode;
                break;
            }
            // otherwise iterate
            cur = cur.nextRowNode;
        }

        // connect node vertically
        cur = colHeaders[col];
        while (cur.row < row) {
            // end of col since were going back to header
            if (cur.nextColNode == colHeaders[col]) {
                //insert
                insertedNode.nextColNode = colHeaders[col];
                cur.nextColNode = insertedNode;
                break;
            }
            // if we need to insert before the next node
            else if (cur.nextColNode.row > row) {
                insertedNode.nextColNode = cur.nextColNode;
                cur.nextColNode = insertedNode;
                break;
            }
            cur = cur.nextColNode;
        }
    }


    public void printMatrixGrid() {
        System.out.println("Matrix " + n + "x" + n + " in grid format:");
        System.out.print("    ");
        // Print column headers
        for (int j = 1; j <= n; j++) {
            System.out.printf("%4d", j);
        }
        System.out.println();
        
        // Print each row
        for (int i = 1; i <= n; i++) {
            System.out.printf("%2d: ", i);
            for (int j = 1; j <= n; j++) {
                int value = getValue(i, j);
                System.out.printf("%4d", value);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printSparseFormat() {
        System.out.println("Sparse matrix format (row, col, value):");
        
        for (int i = 1; i <= n; i++) {
            Node cur = rowHeaders[i].nextRowNode;
            while (cur != rowHeaders[i]) {
                System.out.println(cur.row + ", " + cur.col + ", " + cur.value);
                cur = cur.nextRowNode;
            }
        }
        System.out.println();
    }
    
    public int getValue(int row, int col) {
        Node cur = rowHeaders[row].nextRowNode;
        // while not at the beginning of list again
        while (cur != rowHeaders[row]) {
            // if found, return
            if (cur.col == col) {
                return cur.value;
            } 
            // if pass the col index and still didnt find, break bc node isnt here
            else if (cur.col > col) {
                break;
            }
            // otherwise iterate
            cur = cur.nextRowNode;
        }
        // not found, so return 0
        return 0; 
    }

    // do scalar multiplication on the matrix and return a new matrix
    public SparseMatrix scalarMultiplication(int scalar) {
        // create new matrix for the product
        SparseMatrix output = new SparseMatrix(n);
        // traverse non-zero entries in matrix
        for (int i = 1; i <= n; i++) {
            Node cur = rowHeaders[i].nextRowNode;
            // while we're not done looping until back to row header
            while (cur != rowHeaders[i]) {
                // multiple all values by scalar
                output.insert(cur.row, cur.col, cur.value*scalar);
                cur = cur.nextRowNode;
            }
        }
        
        return output;
    }

    // transpose the matrix and return a new matrix
    public SparseMatrix transpose() {
        // create new matrix for the product
        SparseMatrix output = new SparseMatrix(n);
        // traverse non-zero entries in matrix
        for (int i = 1; i <= n; i++) {
            Node cur = rowHeaders[i].nextRowNode;
            // while we're not done looping until back to row header
            while (cur != rowHeaders[i]) {
                // reverse col and row to transpose
                output.insert(cur.col, cur.row, cur.value);
                cur = cur.nextRowNode;
            }
        }
        
        return output;
    }

    // add two matricies in a new matrix
    public SparseMatrix add(SparseMatrix m1, SparseMatrix m2) {
        // create new matrix for the output
        SparseMatrix output = new SparseMatrix(n);

        // brute force it (lol) and get every element and add them
        // ik its not the best time complexity wise but the assignment doesnt mention optimizing and this is simpler...
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                int sum = m1.getValue(i, j) + m2.getValue(i, j);
                if (sum != 0 ) {
                    output.insert(i, j, sum);
                } 

            }
        }

        return output;
    }

    // multiply two matricies into a new matrix
    public SparseMatrix multiplication(SparseMatrix m1, SparseMatrix m2) {
        // create new matrix for the output
        SparseMatrix output = new SparseMatrix(n);
        
        // brute force w/ 3(!!) nested loops (super unoptimal sorry)
        for (int i = 1; i <= n; i++) {
            for (int j = 1; i <=1; i++) {
                
            }
        }

        return output;
    }
    

    public static void main(String[] args) {
        SparseMatrix s = new SparseMatrix(5);
        System.out.println("header: " + s.header);
        s.insert(1, 2, 10);
        s.insert(2, 3, 20);
        s.insert(1, 4, 30);
        s.insert(3, 1, 40);
        s.insert(5, 5, 50);
        s.insert(2, 1, 15);
        s.printMatrixGrid();
        SparseMatrix output = s.scalarMultiplication(2);
        output.insert(5, 1, 40);
        output.insert(4, 5, 50);
        output.printMatrixGrid();
        SparseMatrix output2 = output.add(s, output);
        output2.printMatrixGrid();
    }

}