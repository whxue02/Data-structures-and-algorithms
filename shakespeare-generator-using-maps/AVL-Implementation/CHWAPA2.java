import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CHWAPA2 {
    // create the map
    MapAVL<CharDistribution> map = new MapAVL<>();
    int windowSize;
    int totalChars;
    
    public static void main(String[] args) throws IOException {
        // create class object
        CHWAPA2 obj = new CHWAPA2();
        
        // get user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter window size: ");
        obj.windowSize = scanner.nextInt();

        // error if window size is less than 1
        if (obj.windowSize < 1) {
            System.err.println("error window size must be greater than 0");
            System.exit(-1);
        }

        System.out.print("Enter output length: ");
        int outputLength = scanner.nextInt();
        scanner.close();

        // error if output size is less than 1
        if (outputLength < 1) {
            System.err.println("error output size must be greater than 0");
            System.exit(-1);
        }
        
        // process the file and build the map
        String text = obj.processFile("merchant.txt");
        
        // generate output text
        String output = obj.generateShakespeare(text, outputLength);

        // write it to an output file
        obj.writeFile("output.txt", output);
        
    }

    public String processFile(String filename) throws IOException {
        try {
            // total text
            String text = "";
            // read file
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            // iterate until end of file, combining all text into one string
            while(line != null) {
                // add current line to total text
                // and add extra space at end for newline since readline doesnt include it
                text = text + line + " ";
                line = br.readLine();
            }
            br.close();

            // save the char length
            totalChars = text.length();

            // if window is larger than total chars, break and error
            if (totalChars < windowSize) {
                System.err.println("error: window size too large, try again");
                System.exit(-1);
            }
            
            // iterate through the text with a sliding window
            for (int i = 0; i < text.length() - windowSize; i++) {
                // get the current window
                String window = text.substring(i, i + windowSize);
                // get the character that follows the window
                char nextChar = text.charAt(i + windowSize);
                
                // find the corresponding node for this window
                MapAVL.Node<CharDistribution> n = map.find(window);
                CharDistribution dist;

                // if it doesnt exist
                if (n == null) {
                    // create new distribution for it
                    dist = new CharDistribution();
                    map.insert(window, dist);
                }
                // if it does, then get the value
                else{
                    dist = n.value;
                }
                
                // record that nextChar follows this window
                dist.occurs(nextChar);
            }
            return text;
        } 
        catch (Exception e) {
            throw new IOException(e);
        }
        
    }

    public String generateShakespeare(String inputText, int length) {
        String output = "";
        
        // start with first windowSize characters from input
        String currentWindow = inputText.substring(0, windowSize);
        output = output + currentWindow;
        
        // generate remaining characters
        while (output.length() < length) {
            // look up the distribution for current window
            MapAVL.Node<CharDistribution> n = map.find(currentWindow);
            CharDistribution dist;
            
            // if window not found
            if (n == null) {
                // break (shouldn't happen with proper input)
                break;
            }
            
            dist = n.value;
            // get next random character from distribution
            String nextChar = dist.getRandomChar();
            output = output + nextChar;
            
            // slide the window forward
            currentWindow = currentWindow.substring(1) + nextChar;
        }
        
        return output;
    }

    public void writeFile(String filename, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write(content);
        bw.close();
    }

    class CharDistribution {
        private int[] counters;  // array of 27 ints for a-z and space
        private int totalCount;   // sum of all counters
        
        public CharDistribution() {
            counters = new int[27];
            totalCount = 0;
        }
        
        // record that character c occurred after this window
        public void occurs(char c) {
            int index = charToIndex(c);
            counters[index]++;
            totalCount++;
        }
        
        // get a random character based on the distribution
        public String getRandomChar() {
            // generate random number from 1 to totalCount
            int random = (int)(Math.random() * totalCount) + 1;
            
            int total = 0;
            
            // scan through counters to find which character to return4
            for (int i = 0; i < 27; i++) {
                total += counters[i];
                if (total >= random) {
                    return indexToChar(i);
                }
            }
            
            // shouldn't reach here, but return space as failsafe
            return " ";
        }
        
        // convert character to index (a=0, b=1, ..., z=25, space=26)
        private int charToIndex(char c) {
            // find the index for the char
            String characters = "abcdefghijklmnopqrstuvwxyz ";
            int index = characters.indexOf(c);

            // if not found for some reason
            if (index == -1) {
                // default to space i guess
                return 26;
            }
            return index;
        }
        
        // convert index back to character
        private String indexToChar(int index) {
            String[] characters = {"a", "b", "c", "d", "e", "f", "g","h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", " "};
            return characters[index];
        }
    }
}