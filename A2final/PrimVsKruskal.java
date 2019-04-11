/* PrimVsKruskal.java
   CSC 226 - Spring 2019
   Assignment 2 - Prim MST versus Kruskal MST Template
   
   The file includes the "import edu.princeton.cs.algs4.*;" so that yo can use
   any of the code in the algs4.jar file. You should be able to compile your program
   with the command
   
	javac -cp .;algs4.jar PrimVsKruskal.java
	
   To conveniently test the algorithm with a large input, create a text file
   containing a test graphs (in the format described below) and run
   the program with
   
	java -cp .;algs4.jar PrimVsKruskal file.txt
	
   where file.txt is replaced by the name of the text file.
   
   The input consists of a graph (as an adjacency matrix) in the following format:
   
    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>
	
   Entry G[i][j] >= 0.0 of the adjacency matrix gives the weight (as type double) of the edge from 
   vertex i to vertex j (if G[i][j] is 0.0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that G[i][j]
   is always equal to G[j][i].


   R. Little - 03/07/2019
*/

 import edu.princeton.cs.algs4.*;
 import java.util.Scanner;
 import java.io.File;
 import java.util.Arrays;
 import java.util.HashSet;

//Do not change the name of the PrimVsKruskal class
public class PrimVsKruskal {

	/* PrimVsKruskal(G)
		Given an adjacency matrix for connected graph G, with no self-loops or parallel edges,
		determine if the minimum spanning tree of G found by Prim's algorithm is equal to 
		the minimum spanning tree of G found by Kruskal's algorithm.
		
		If G[i][j] == 0.0, there is no edge between vertex i and vertex j
		If G[i][j] > 0.0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/
	static boolean PrimVsKruskal(double[][] G) {

		int n = G.length;
		double total_weight = 0.0;
		boolean pvk = true;

		/* Start of Kruskal's MST */

		// Initialize queues and union find object.
		Queue<Edge> mstKruskal = new Queue<Edge>();
		MinPQ<Edge> pq1 = new MinPQ<Edge>();
		HashSet<Double> uniqueSet = new HashSet<>();
		boolean unique = true;
		UF uf = new UF(n);

		// Insert all valid edges from the matrix G into the priority queue.
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (G[i][j] != 0) {
					pq1.insert(new Edge(i, j, G[i][j]));

					// If it is already in the set, the tree is not unique.
					if (uniqueSet.contains(G[i][j])) {
						unique = false;
					}
					uniqueSet.add(G[i][j]);
				}
			}
		}

		// Early detection for unique tree.
		if (unique == true) {
			return pvk;
		}

		// Run Kruskal's and add min. weight edge until priority queue is empty.
		while (!pq1.isEmpty() && mstKruskal.size() < n - 1) {
			Edge e = pq1.delMin();               // Get min. weight edge on pq and its vertices.
			int v = e.either(), w = e.other(v);
			if (uf.connected(v, w)) continue;    // Ignore ineligible edges.
			uf.union(v, w);                      // Merge components.
			mstKruskal.enqueue(e);               // Add edge to mst.
		}

		System.out.println("Printing MST by Kruskal's:");
		for (Edge edge2 : mstKruskal) {
			System.out.println(edge2);
			total_weight += edge2.weight();
		}
		System.out.println(total_weight + "\n");
		total_weight = 0.0;

		/* End of Kruskal's MST */
		
		/* Start of Prim's MST */

		// Initialize queues and arrays.
		Edge[] edgeTo = new Edge[n];
		double[] distTo = new double[n];
		boolean[] marked = new boolean[n];
		IndexMinPQ<Double> pq2 = new IndexMinPQ<Double>(n);
		Queue<Edge> tempQueue = new Queue<Edge>();
		double[][] mstPrim = new double[n][n];
		int v = 0;

		// Set distance to all vertices to infinity.
		for (int vert = 0; vert < n; vert++) {
			distTo[vert] = Double.POSITIVE_INFINITY;
		}

		distTo[0] = 0.0;
		pq2.insert(0, 0.0);		// Initialize pq2 with 0, weight 0.

		while (!pq2.isEmpty()) {  
			v = pq2.delMin();
			marked[v] = true;

			// Add each valid edge from the current vertex v to the temporary queue.
			for (int i = 0; i < n; i++) {
				if (G[v][i] != 0) {
					tempQueue.enqueue(new Edge(v, i, G[v][i]));
				}
			}

			// For each edge, get its adjacent vertices and determine the 
			// next lowest weight edge to add to the mst.
			for (Edge e : tempQueue) {
				int w = e.other(v);
				if (marked[w]) continue;	// v-w is ineligible.
				if (e.weight() < distTo[w]) {  
					// Edge e is new best connection from tree to w.
					edgeTo[w] = e;
					distTo[w] = e.weight();             
					if (pq2.contains(w)) pq2.change(w, distTo[w]);            
					else                 pq2.insert(w, distTo[w]);         
				}   
			}

			// Empty the queue so it can be filled with edges from a new vertex.
			while (!tempQueue.isEmpty()) {
				tempQueue.dequeue();
			}
		}

		// Create a new array of edges that doesn't include the first value.
		Edge[] edges = Arrays.copyOfRange(edgeTo, 1, edgeTo.length);

		System.out.println("Printing MST by Prim's:");
		for (Edge edge1 : edges) {
			mstPrim[edge1.either()][edge1.other(edge1.either())] = edge1.weight();
			System.out.println(edge1);
			total_weight += edge1.weight();
		}
		System.out.println(total_weight + "\n");
		total_weight = 0.0;

		/* End of Prim's MST */
		
		/* Determine if the MST by Prim equals the MST by Kruskal */

		// Check that each edge in mstKruskal is also in mstPrim using methods from the Edge class.
		for (Edge edge2 : mstKruskal) {
			if ((mstPrim[edge2.either()][edge2.other(edge2.either())] == 0) && (mstPrim[edge2.other(edge2.either())][edge2.either()] == 0)) {
				pvk = false;
			}
		}

		return pvk;
	}

	/* main()
	   Contains code to test the PrimVsKruskal function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
   public static void main(String[] args) {
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		int n = s.nextInt();
		double[][] G = new double[n][n];
		int valuesRead = 0;
		for (int i = 0; i < n && s.hasNextDouble(); i++){
			for (int j = 0; j < n && s.hasNextDouble(); j++){
				G[i][j] = s.nextDouble();
				if (i == j && G[i][j] != 0.0) {
					System.out.printf("Adjacency matrix contains self-loops.\n");
					return;
				}
				if (G[i][j] < 0.0) {
					System.out.printf("Adjacency matrix contains negative values.\n");
					return;
				}
				if (j < i && G[i][j] != G[j][i]) {
					System.out.printf("Adjacency matrix is not symmetric.\n");
					return;
				}
				valuesRead++;
			}
		}
		
		if (valuesRead < n*n){
			System.out.printf("Adjacency matrix for the graph contains too few values.\n");
			return;
		}	
		
        boolean pvk = PrimVsKruskal(G);
        System.out.printf("Does Prim MST = Kruskal MST? %b\n", pvk);
    }
}
