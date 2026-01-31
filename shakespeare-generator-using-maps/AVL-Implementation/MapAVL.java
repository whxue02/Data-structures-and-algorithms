public class MapAVL<T> {
    // initialize vars
    int size;
    Node<T> root;

    // constructor
    MapAVL(){
        size = 0;
    }

    // size
    public int getSize() {
        return size;
    }

    // is empty?
    public boolean empty() {
        // if size = 0, then it's empty
        if (size == 0) {
            return true;
        }
        // otherwise it's false
        return false;
    }

    // insert
    public Node<T> insert(String k, T v) {
        // if the tree is empty, then insert
        if (size == 0){
            // create node
            Node<T> r = new Node<>(k, v);

            // insert it as root
            root = r;
            size++;
            return r;
        }

        // otherwise, try to find it in the tree
        Node<T> n = findHelper(k, root);

        // if the node is found, replace the value v
        if (n != null) {
            n.value = v;
            // return the new node
            return n;
        }
        else{
            // otherwise, insert it into the tree
            Node<T> inserted = insertHelper(k, v, root);

            // update size
            size++;

            // balance the tree
            balanceTree(inserted);
            
            // return the inserted node
            return inserted;
        }
        
    }

    // helper to insert recursively
    private Node<T> insertHelper(String k, T v, Node<T> n) {
        // if cur node is greater than key, go left
        if (n.key.compareTo(k) > 0) {
            // if no left child,
            if (n.leftChild == null) {
                // then insert
                Node<T> newNode = new Node<>(k, v);
                n.leftChild = newNode;
                newNode.parent = n;
                return newNode;
            } 
            // otherwise keep traversing the left subtree
            else {
                return insertHelper(k, v, n.leftChild);
            }
        } 
        // otherwise, go right
        else {
            // if no right child,
            if (n.rightChild == null) {
                // insert
                Node<T> newNode = new Node<>(k, v);
                n.rightChild = newNode;
                newNode.parent = n;
                return newNode;
            } 
            // otherwise traverse the right subtree
            else {
                return insertHelper(k, v, n.rightChild);
            }
        }
    }

    // delete, returns null if not found
    public Node<T> remove(String k) {
        // look for node to delete
        Node<T> n = findHelper(k, root);
        // if node not found
        if (n == null) {
            // return null
            return null;
        }

        // save original node data to return
        String origKey = k;
        T origValue = n.value;

        // call helper and get the parent
        Node<T> par = deleteHelper(n);

        // rebalance from parent up to root (only if parent exists)
        if (par != null) {
            balanceTree(par);
        }
        
        size--;
        return new Node<>(origKey, origValue);
    }
    
    // helper to remove the node and return the parent to start balancing
    private Node<T> deleteHelper(Node<T> n) {
        Node<T> parent = n.parent;

        // if the node has no children, just remove,
        if (n.leftChild == null && n.rightChild == null) {
            // if not root
            if (parent != null) {
                // check if the cur node is the left child
                if (parent.leftChild == n) {
                    // remove
                    parent.leftChild = null;
                }
                // otherwise it's the right child and remove
                else {
                    parent.rightChild = null;
                }
            } 
            // if it is the root,
            else {
                // delete and set root to null
                root = null; 
            }
        } 
        // if theres only the right child,
        else if (n.leftChild == null) {
            // if n isn't the root
            if (parent != null) {
                // check if n is a left child
                if (parent.leftChild == n) {
                    // then connect the par's  and the node's right child
                    parent.leftChild = n.rightChild;
                }
                // then n is the right child, connect
                else parent.rightChild = n.rightChild;
            } 
            // if n is the root,
            else {
                // set right child as the new root
                root = n.rightChild;
            }
            // connect the node's right child to n's parent
            n.rightChild.parent = parent;
        } 
        // if there is only the left child
        else if (n.rightChild == null) {
            // if it's not the root
            if (parent != null) {
                // then check if n is a left or right child
                if (parent.leftChild == n) {
                    // delete
                    parent.leftChild = n.leftChild;
                }
                // it's a right child
                else {
                    // delete
                    parent.rightChild = n.leftChild;
                }
            } 
            // otherwise if it's root
            else {
                // set left child as root
                root = n.leftChild;
            }
            // connect the child to n's parent
            n.leftChild.parent = parent;
        } 
        // last case, n has 2 children
        else {
            // replace with the minimum node of the right subtree
            Node<T> replacement = getMinNode(n.rightChild);

            // copy replacement's key and value to current node
            n.key = replacement.key;
            n.value = replacement.value;

            // delete the replacement node
            return deleteHelper(replacement);
        }

        // return parent for balanacing
        return parent; 
    }

    // helper to find minimum node in a subtree
    private Node<T> getMinNode(Node<T> n) {
        // traverse through left subtree until at leaf
        while (n.leftChild != null) {
            n = n.leftChild;
        }
        return n;
    }

    // find, returns null if not found
    public Node<T> find(String k) {
        // search for key w/ binary search
        Node<T> n = findHelper(k, root);

        // return the found value
        return n;
    }
    private Node<T> findHelper(String k, Node<T> n) {
        // base case
        if (n == null) {
            return null;
        }
        
        // if were at the right node, retun
        if (n.key.equals(k)) {
            return n;
        }

        // if current node is greater than the key
        if (k.compareTo(n.key) < 0) {
            // go to left node and search
            return findHelper(k, n.leftChild);
        } 
        // otherwise, node is less than key
        else {
            // and search the right subtree
            return findHelper(k, n.rightChild);
        }
    }

    // get keys through inorder traversal
    public String[] getKeys() {
        String[] keys = new String[size];
        
        // keep track of what index we're at to insert into the keys arr
        // uses arr, bc int is passed by value so it wouldnt work in recursion to keep track of index
        int[] index = {0};

        // call helper to recursively get keys in order
        getKeysHelper(root, keys, index);
        
        // return keys
        return keys;
    }

    private void getKeysHelper(Node<T> n, String[] keys, int[] index) {
        // base case, no node
        if (n == null) {
            return;
        }

        // in order traversal
        getKeysHelper(n.leftChild, keys, index); 

        // insert the current key at the current index
        keys[index[0]] = n.key;
        // increment index
        index[0] = index[0] + 1; 

        getKeysHelper(n.rightChild, keys, index);
    }

    // balance
    private void balanceTree(Node<T> n){
        // traverse through the tree upwards untill root
        while (n != null) {
            // update height first
            updateHeight(n);
            
            // calc the bf
            int bf = getBalanceFactor(n);
            
            // if bf > 1, then the tree is left heavy
            if (bf > 1) { 
                // if left child is also left heavy,
                if (getBalanceFactor(n.leftChild) >= 0) {
                    // then it's a LL case
                    n = LLRotation(n.leftChild, n);
                } 
                // otherwise
                else {
                    // its a LR case
                    n = LRRotation(n);
                }
            } 
            // if the bf is < -1, then it's right heavy
            else if (bf < -1) { 
                // if the right child is also right heavy
                if (getBalanceFactor(n.rightChild) <= 0) {
                    // then it's a RR case
                    n = RRRotation(n.rightChild, n);
                } 
                // otherwise
                else {
                    // its a RL case
                    n = RLRotation(n);
                }
            }
    
            // move up the tree
            if (n.parent == null) {
                // update root 
                root = n;
            }
            n = n.parent;
        }
    }

    // calculate balance factor + helper functions
    private static <T> int getHeight(Node<T> n) {
        return n == null ? 0 : n.height;
    }
    private static <T> int getBalanceFactor(Node<T> n) {
        return n == null ? 0 : getHeight(n.leftChild) - getHeight(n.rightChild);
    }
    private static <T> void updateHeight(Node<T> n) {
        if (n != null) {
            n.height = 1 + Math.max(getHeight(n.leftChild), getHeight(n.rightChild));
        }
    }

    // fix ll imbalance with a right rotation (both bf is positive), 
    private static <T> Node<T> LLRotation(Node<T> c, Node<T> par) {
        // first, child's right child becomes the parent's left child
        Node<T> cRight = c.rightChild;
        par.leftChild = cRight;
        // make sure the child exists before assigning it a parent
        if (cRight != null) {
            cRight.parent = par;
        }

        // second, child and parent swap
        Node<T> gp = par.parent;
        par.parent = c;
        c.rightChild = par;
        c.parent = gp;
        // connect grandparent to c
        if (gp != null) {
            if (gp.leftChild == par) {
                gp.leftChild = c;
            }
            else {
                gp.rightChild = c;
            }
        }

        // update heights
        updateHeight(par);
        updateHeight(c);

        // return the root of the subtree
        return c;
    }  

    // fix a rr imbalance with a left rotation (both bf is negative)
    private static <T> Node<T> RRRotation(Node<T> c, Node<T> par) {
        // first, c's left child becomes par's right child
        Node<T> cLeft = c.leftChild;
        par.rightChild = cLeft;
        // make sure the child exists before assigning it a parent
        if (cLeft != null) {
            cLeft.parent = par;
        }

        // second, par and c swap
        Node<T> gp = par.parent;
        par.parent = c;
        c.leftChild = par;
        c.parent = gp;
        // connect gp to c
        if (gp != null) {
            if (gp.leftChild == par) {
                gp.leftChild = c;
            }
            else {
                gp.rightChild = c;
            }
        }

        // update heights
        updateHeight(par);
        updateHeight(c);

        // return the root of the subtree
        return c;
    }

    // lr rotation (child is neg, parent is pos)
    private static <T> Node<T> LRRotation(Node<T> n) {
        // where n is the imbalanced node with bf >1
        Node<T> c = n.leftChild;

        // first, do left roation (fixing rr imbalance) on the node's left child and the child's right child
        RRRotation(c.rightChild, c);

        // then, do a right rotation (fixing ll imbalance) on the node and the new left child
        return LLRotation(n.leftChild, n);
    }

    // rl rotation (child is pos, parent is neg)
    private static <T> Node<T> RLRotation(Node<T> n) {
        // where n is the imbalanced node with bf >1
        Node<T> c = n.rightChild;

        // first, do right roation (fixing ll imbalance) on the node's right child and the child's left child
        LLRotation(c.leftChild, c);

        // then, do a left rotation (fixing rr imbalance) on the node and the new right child
        return RRRotation(n.rightChild, n);
    }

    public static void main(String[] args) {
    }

    public static class Node<T> {
        // intialize vars
        String key;
        T value;
        int height = 0; // leaf nodes start at height 0
        Node<T> parent = null;
        Node<T> leftChild = null;
        Node<T> rightChild = null;
    
        // constructor
        Node(String k, T v) {
            key = k;
            value = v;
        }
    
        // to string for testing
        // public String toString() {
        //     return "key: " + key + " value: " + value + " height: " + height;
        // }
    }
     
}