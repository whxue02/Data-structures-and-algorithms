// resources:
//  - https://www.geeksforgeeks.org/java/java-io-bufferedreader-class-java/
//  - https://www.w3schools.com/java/ref_string_split.asp
// to run:
//  - javac CHWAPA1.java  
//  - java CHWAPA1 'FILENAME'

// imports
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CHWAPA1 {
    public static void main(String[] args) throws IOException {
        // make sure command was used correctly
        if (args.length != 1) {
            System.out.println("enter one arg (filename)");
            return;
        }
        // save file and open it
        String fileName = args[0];
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        
        // get first line (operation type and size of matrix)
        String line = br.readLine();
        String[] subline = line.split(",");
        String operation = subline[0];
        int size = Integer.parseInt(subline[1]);

        // create the matrix
        SparseMatrix m1 = new SparseMatrix(size);

        // get rid of empty line
        line = br.readLine();

        // read first matrix
        // iterate until end (null) or next matrix (",,")
        line = br.readLine();
        while (line != null && !line.equals(",,"))  {
            String[] lineParts = line.split(",");
            //to prevent errors
            if (lineParts.length == 3) {
                // get row, col, and value
                int row = Integer.parseInt(lineParts[0].trim());
                int col = Integer.parseInt(lineParts[1].trim());
                int value = Integer.parseInt(lineParts[2].trim());
                // create the node
                m1.insert(row, col, value);
            }
            line = br.readLine();
        }

        //create output matrix
        SparseMatrix output = null;

        // check for t (bc theres no second matrix in that case)
        if (operation.equals("T")) {
            output = m1.transpose();
        }

        // check for s, extract scalar
        else if (operation.equals("S")) {
            // get the scalar
            line = br.readLine();
            // convert to int and trim any possible whitespace
            String[] parts = line.split(",");
            int scalar = Integer.parseInt(parts[0].trim()); 
            output = m1.scalarMultiplication(scalar);
        }

        // otherwise its add/multiply, so read in second matrix
        else {
            // create second matrix
            SparseMatrix m2 = new SparseMatrix(size);

            // get second matrix
            line = br.readLine();
            // iterate until end (null) or next matrix (",,")
            while (line != null && !line.equals(",,")) {
                String[] lineParts = line.split(",");
                //to prevent errors
                if (lineParts.length == 3) {
                    // get row, col, and value
                    int row = Integer.parseInt(lineParts[0].trim());
                    int col = Integer.parseInt(lineParts[1].trim());
                    int value = Integer.parseInt(lineParts[2].trim());
                    // insert node
                    m2.insert(row, col, value);
                }
                line = br.readLine();
            }

            // if addition, do addition
            if (operation.equals("A")) {
                output = m1.add(m1, m2);
            }
            // otherwise multiplication
            else if (operation.equals("M")) {
                output = m1.multiplication(m1, m2);
            }
        }
        
        // done reading!
        br.close();

        // write output to csv
        String outputFileName = fileName.replace(".csv", "_output.csv");
        FileWriter file = new FileWriter(outputFileName);
        PrintWriter out = new PrintWriter(file);
        output.writeToCSV(out);
        out.close();
        
    } 
} 

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

    // to string for testing
    public String toString() {
        return "row: " + row + " col: " + col + " value: " + value;
    }

    public static void main(String[] args) {
        Node n = new Node(1, 3,2);
        System.out.println("col: " + n.col + " row: " + n.row + " value: " + n.value);
    }
}

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
        // iterate to create
        for(int i = 1; i <= n; i++) {
            // create new node and link it
            colHeaders[i] = new Node(0, i, -1);
            cur = colHeaders[i];
            prev.nextRowNode = cur;
            prev = cur;
            // make circular, node points to itself vertically at first
            colHeaders[i].nextColNode = colHeaders[i];
        }
        
        // last node points back to header to form the circular list
        if (cur != null) {
            cur.nextRowNode = header;
            // also point to itself
            cur.nextColNode = cur; 
        }

        // create left column (row headers)
        prev = header;
        // iterate to create row header 
        for(int i = 1; i <= n; i++) {
            // create node and link
            rowHeaders[i] = new Node(i, 0, -1);
            cur = rowHeaders[i];
            prev.nextColNode = cur;
            prev = cur;
            // link it to itself
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
                //insert and link
                insertedNode.nextColNode = colHeaders[col];
                cur.nextColNode = insertedNode;
                break;
            }
            // if we need to insert before the next node
            else if (cur.nextColNode.row > row) {
                // insert and link
                insertedNode.nextColNode = cur.nextColNode;
                cur.nextColNode = insertedNode;
                break;
            }
            cur = cur.nextColNode;
        }
    }

    // get a value based on row + col
    public int getValue(int row, int col) {
        Node cur = rowHeaders[row].nextRowNode;
        // while not at header again (beginning)
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
                // dont need to check for 0's bc insert function checks if its a 0 before inserting
                output.insert(i, j, sum);
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
            for (int j = 1; j <= n; j++) {
                // finding value of the i,j node in the new matrix
                int product = 0;
                for (int x = 1; x <= n; x++) {
                    // add up the dot product of the row of m1 and the col of m2
                    product += m1.getValue(i, x) * m2.getValue(x, j);
                }
                // dont need to check for 0's bc insert function checks if its a 0 before inserting
                output.insert(i, j, product);
            }
        }

        return output;
    }

    // write nodes to csv
    public void writeToCSV(PrintWriter out) {
        // iterate to find nodes
        for (int i = 1; i <= n; i++) {
            Node cur = rowHeaders[i].nextRowNode;
            while (cur != rowHeaders[i]) {
                // write it to file
                out.println(cur.row + "," + cur.col + "," + cur.value);
                cur = cur.nextRowNode;
            }
        }
    }

}

