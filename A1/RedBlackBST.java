import java.util.NoSuchElementException;
import java.lang.Math;
import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;


public class RedBlackBST {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private Node root;     // root of the BST

    // BST helper node data type
    private class Node {
        private int key;           // key
        private Node left, right;  // links to left and right subtrees
        private boolean color;     // color of parent link
        private int size;          // subtree count
        private int num_reds;      // subtree count for red nodes

        public Node(int key, boolean color, int size, int num_reds) {
            this.key = key;
            this.color = color;
            this.size = size;
            this.num_reds = num_reds;
        }
    }

	public RedBlackBST() {

	}
   /***************************************************************************
    *  Node helper methods.
    ***************************************************************************/
    // is node x red; false if x is null ?
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    // number of red nodes in subtree rooted at x; 0 if x is null
    private int num_reds(Node x) {
        if (x == null) return 0;
        return x.num_reds;
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(Node x) {
        if (x == null) return 0;
        return x.size;
    } 

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return size(root);
    }

   /**
     * Is this symbol table empty?
     * @return {@code true} if this symbol table is empty and {@code false} otherwise
     */
    public boolean isEmpty() {
        return root == null;
    }

   /***************************************************************************
    *  Red-black tree insertion.
    ***************************************************************************/

    /**
     * Inserts the specified key-value pair into the symbol table, overwriting the old 
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is {@code null}.
     *
     * @param key the key
     * @param val the value
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void put(int key) {

        root = put(root, key);
        if (isRed(root)) {
            root.num_reds = root.num_reds - 1;
        }
        root.color = BLACK;
    }

    // insert the key-value pair in the subtree rooted at h
    private Node put(Node h, int key) { 
        if (h == null) return new Node(key, RED, 1, 1);

        int cmp = key - h.key;
        if      (cmp < 0) h.left  = put(h.left,  key); 
        else if (cmp > 0) h.right = put(h.right, key); 
        else              h.key   = key;

        // fix-up any right-leaning links
        if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
        if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left)  &&  isRed(h.right))     flipColors(h);
        h.size = size(h.left) + size(h.right) + 1;
        h.num_reds = num_reds(h.left) + num_reds(h.right);
        if (isRed(h)) h.num_reds = h.num_reds + 1;

        return h;
    }


   /***************************************************************************
    *  Red-black tree helper functions.
    ***************************************************************************/

    // make a left-leaning link lean to the right
    private Node rotateRight(Node h) {
        // assert (h != null) && isRed(h.left);
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        x.size = h.size;
        x.num_reds = h.num_reds + 1;
        h.size = size(h.left) + size(h.right) + 1;  
        h.num_reds = num_reds(h.left) + num_reds(h.right);
        if (isRed(h)) h.num_reds = h.num_reds + 1;

        return x;
    }

    // make a right-leaning link lean to the left
    private Node rotateLeft(Node h) {
        // assert (h != null) && isRed(h.right);
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        x.size = h.size;
        x.num_reds = h.num_reds + 1;
        h.size = size(h.left) + size(h.right) + 1;
        h.num_reds = num_reds(h.left) + num_reds(h.right);
        if (isRed(h)) h.num_reds = h.num_reds + 1;

        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {
        // h must have opposite color of its two children
        // assert (h != null) && (h.left != null) && (h.right != null);
        // assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
        //    || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;

        h.left.num_reds = h.left.num_reds - 1;
        h.right.num_reds = h.right.num_reds - 1;
    }

    // use the stored values num_reds and size in the root to 
    // calculate the percentage of the nodes in the tree that are red
    public double percentRed() {
        int reds = num_reds(root);
        int tree_size = size(root);
        double percentRed = ((double)reds / (double)tree_size);

        return percentRed;
    }


    /**
     * Unit tests the {@code RedBlackBST} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) { 

        // if no input_size for the tree is given, the program will not run
        if (args.length == 0) {
            System.err.println("Please enter how many random numbers should be inserted into the red-black tree.");
            return;
        }

        // sanitize the input
        int input_size = 0;
        try {
           input_size = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e)
        {
            System.err.println("Please enter how many random numbers should be inserted into the red-black tree."); 
            return;
        }

        // adds numbers from 1 to input_size to a list
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < input_size; i++) {
            numbers.add(i + 1);
        }

        // randomize the list and instantiate a red-black tree
        Collections.shuffle(numbers);
        RedBlackBST st = new RedBlackBST();

        // add the keys from 1 to input_size to the tree in the randomized order
        for (int j = 0; j < input_size; j++) {
            st.put(numbers.get(j));
        } 

        // print the results of percentRed to the console for the user to view
        if (st.size() > 0) {
    		System.out.println("The size of the tree is " + st.size() + ".");
            double red_ratio = st.percentRed();
            int percent_red = (int)(Math.round(100 * red_ratio));
            System.out.println("The percentage of red nodes in the tree is " + percent_red + " percent.");
        } else {
            System.out.println("There are no nodes in the red-black tree");
        }
    }
        
 
}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/