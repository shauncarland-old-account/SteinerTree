/*steiner tree
by shaun carland
*/
package student;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import graph.*;
import steinerTree.SteinerTreeTester;

/* 
 * This Student class is meant to contain the algorithm.
 * 
 * The inputs are:
 *   1. Graph object, which has:
 *      an ArrayList of all vertices - use graph.vertexIterator()
 *      an ArrayList of all edges - use graph.edgeIterator()
 *      each vertex has an ArrayList of its edges - use vertex.edgeIterator()
 *      see the documentation for: Graph, Vertex, and Edge for more details
 *   2. An ArrayList of vertices that are the targeted vertices for inclusion
 *      in your Steiner tree.  The mark fields are also already set in the graph
 *      for these vertices.  
 */ 



public class SteinerTree
{
	public static ArrayList<Integer> reverse(ArrayList<Integer> list) {
	    if(list.size() > 1) {                   
	        int value = list.remove(0);
	        reverse(list);
	        list.add(value);
	    }
	    return list;
	}
	
	public static ArrayList<Integer> constPath(int start, int end, int [][] path){
		ArrayList<Integer> pathList = new ArrayList<Integer>();
		int cur = end;
		pathList.add(cur);
		while(cur != start){
			cur = path[start][end];
			pathList.add(cur);
			end = cur;
			
		}
		return pathList;
	}

	public static int [][] buildInitPath(int [][] adj){
		int n = adj[0].length;
		int infinity = Integer.MAX_VALUE;
		int [][] p = new int [n][n];
		
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				if (adj[i][j] != 0 && adj[i][j] != infinity){
					p[i][j] = i;
				}
				else {
					p[i][j] = -1;
				}
			}
		}
	return p;
	}
	
	public static Vertex [] sort(Vertex [] arr){	
		for (int i = 0; i < arr.length; i++) {
`	        for (int j = i + 1; j < arr.length; j++) {
	            Vertex tmp;
	            if (arr[i].getId() > arr[j].getId()) {
	                tmp = arr[i];
	                arr[i] = arr[j];
	                arr[j] = tmp;
	            }
	        }
	    }
		return arr;
	}
		
	
	
	public static int findShortestPath(int [][] SP, ArrayList<Vertex> targets, Graph g, ArrayList<Integer> pathList, int [][] path){
		/*make an Array of the vertices*/
		int k;
		int i;
		int j;
		int num_vertices = SP[0].length;
	    	int [] tarInd = new int [targets.size()];
	    
		Vertex [] vert = new Vertex[num_vertices];
		Iterator <Vertex> vtr = g.vertexIterator();
		Iterator <Vertex> t_vtr = targets.iterator();
		
		for (k = 0; k < num_vertices;k++){
			vert[k] = vtr.next();
		}
		
		/*Put the targets into an array*/
		Vertex [] tar = new Vertex [targets.size()];
		
		for (int x = 0; x < targets.size();x++){
			tar[x] = t_vtr.next();
		}
		
		/*Sort the targets*/
		tar = sort(tar);
		
		/*Build Target Index Array*/
		for (k = 0; k < tar.length; k++){
			tarInd[k] = tar[k].getId();
		}
		
		int dist = 0;
		
		/*find A path*/
		int distance = 0;
		int curD;
		for ( i = 0; i < tar.length - 1; i++){
			curD = SP[tarInd[i]][tarInd[i+1]];
			distance = distance + curD ;
		}
		
		/*Rebuild Path*/
		ArrayList<Integer> returnPath = new ArrayList<Integer>();
		for (i = 0; i < tar.length - 1; i++){
			int st = tarInd[i];
			int fi = tarInd[i+1];
			
			returnPath = constPath(st,fi,path);
			reverse(returnPath);
			
			pathList.addAll(returnPath);
			returnPath.clear();
			
		}
		/*remove duplicates*/
		Set<Integer> s = new LinkedHashSet<Integer>(pathList);
		pathList.clear();
		Iterator itr = s.iterator();
		
		while(itr.hasNext()){
			pathList.add((Integer) itr.next());
		}
		
		/*Iterate through every target*/
		System.out.println();
		return distance;
	}
	
	public static int [][] buildShortestPathTable(int [][] adj, int [][] path){
		int i;
		int j;
		int k;
		int infinity = Integer.MAX_VALUE;
		int len = adj.length;
		int n = adj.length;
		int [][] A = new int [len][len];
		
		/*copy adj into SP*/
		for (i = 0; i < len; i++){
			A[i] = adj[i].clone();
		}
	
		for (k = 0; k < n; k++){
			for (i = 0; i < n; i++){
				for (j = 0; j < n; j++){
					if (A[i][k] == infinity || A[k][j] == infinity){
						continue;
					}
					
					if (A[i][k] + A[k][j] < A[i][j]){
						A[i][j] = A[i][k] + A[k][j];
						path[i][j] = path[k][j];
					}
				}
			}
		}
		return A;
	}
	
	/************************************************************
	Method:
	Input:
	Output:
	Description:
	*************************************************************/
	
	/************************************************************
	Method: buildAdjMatrix
	Input: Graph g to build matrix on, ArrayList of targets
	Output: An adjacency matrix (adj) representing the graph
	Notes:
		adj[i][j] == 0 if node i & j do not connect OR i == j
		adj[i][j] == the weight of the edge between node i & j, if they connect
	*************************************************************/
	public static int [][] buildAdjMatrix(Graph g, ArrayList<Vertex> targets){
		int infinity = Integer.MAX_VALUE;
		int num_targ = targets.size();
		int num_vertices = g.numVertices();
		int [][] adj = new int [num_vertices][num_vertices];
		
		// sort each vertex's edges shortest first
		g.sortVertexEdgeLists(new Graph.CompareEdgesSmallestFirst());
		
		/*make an Array of the vertices*/
		Vertex [] vert = new Vertex[num_vertices];
		Iterator <Vertex> vtr = g.vertexIterator();
		for (int k = 0; k < num_vertices;k++){
			vert[k] = vtr.next();
		}
		
		/*reset the iterator*/
		vtr = g.vertexIterator();
		/*For each vertex in g*/
		for (int i = 0; i < num_vertices; i++){
			Vertex v = vert[i];
			/*check the other vertices*/
			for (int j = 0; j < num_vertices; j++){
				Vertex v2 = vert[j];
				/*CASE: v is the same as V2.  */
				if (v.equals(v2)){
					adj[i][j] = 0;
				}
				/*check to see if v can reach v2*/
				else {
					/*iterate over v's edges*/
					int flag = 0;
					Iterator<Edge> evtr= v.edgeIterator();
					v.sortEdges(new Graph.CompareEdgesSmallestFirst());
					while (evtr.hasNext()){
						Edge e = evtr.next();
						Vertex v3 = e.getOppositeVertexOf(v);
						if (v3.equals(v2)){
							flag = 1;
							adj[i][j] = e.getWeight();
							break;
						}
					}
					
					/*check to see if these vertices aren't adj*/
					if (flag == 0){
						adj[i][j] = infinity;
					}
					
				}
			
			}
		}
		return adj;
	}
	
	/************************************************************
	Method:
	Input:
	Output:
	Description:
	*************************************************************/
	public static void printTable(int [][] table){
		int infinity = Integer.MAX_VALUE;
		int n = table[0].length;
		int i;
		int j;
		char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		
		for (i = 0; i < n; i++){
			if ( i ==0){
				System.out.print("  A ");
			}
			else{
			System.out.print(alphabet[i] + " ");
			}
		}
		System.out.println();
		
		for (i = 0; i < n; i++){
			System.out.print(alphabet[i] + " ");
			for(j = 0; j < n; j++){
				if (table[i][j] == infinity){
					System.out.print("I ");
				}
				else {
					System.out.print(table[i][j] + " ");	
				}
			}
			System.out.println();
		}
		
	}
	// Simple example routine that just does a depth first search until it reaches
	// all of the target vertices.
	public static int steinerTree(Graph g, ArrayList<Vertex> targets)						
	{			
		int [][] adj = buildAdjMatrix(g,targets);
		int [][] path =buildInitPath(adj);
		int [][] SP = buildShortestPathTable(adj,path);
		ArrayList<Integer> pathList = new ArrayList<Integer>();
		int dist = findShortestPath(SP,targets,g,pathList, path);
		g.sortVertexEdgeLists(new Graph.CompareEdgesSmallestFirst());
		ArrayList<Integer> path1 = constPath(0,5,path);
		
		System.out.println();
		
		/*Put the targets into an array*/
		Vertex [] vert = new Vertex[g.numVertices()];
		Iterator <Vertex> vtr = g.vertexIterator();
		Iterator <Vertex> t_vtr = targets.iterator();
	    int [] tarInd = new int [targets.size()];
		Vertex [] tar = new Vertex [targets.size()];
		
		for (int x = 0; x < targets.size();x++){
			tar[x] = t_vtr.next();
		}
		
		/*Sort the targets*/
		tar = sort(tar);
		
		/*Build Target Index Array*/
		for (int k = 0; k < tar.length; k++){
			tarInd[k] = tar[k].getId();
		}
		
		ArrayList<Integer> visited = new ArrayList<Integer>();
		
		/*initialize the targets*/
		Vertex firstTar = targets.get(0);
		visited.add(firstTar.getId());
		
		Iterator<Vertex> vitr = g.vertexIterator();
		/*array of vertexes*/
		Vertex [] vertexArr = new Vertex [g.numVertices()];
		
		Vertex v1;
		for (int i = 0; i < vertexArr.length; i++){
			v1 = vitr.next();
			vertexArr[i] = v1;
			
		}
		
		int curDist;
		int retDist = 0;
		
		for (int i = 0; i < targets.size()-1; i++){
			int [] sourceSink = new int [2];
			ArrayList<Integer> thePath = new ArrayList<Integer>();
			curDist = prims(SP,path,tarInd,visited,sourceSink);
			retDist = retDist  + curDist;
			thePath = constPath( sourceSink[0],sourceSink[1], path);
			drawPath(g, sourceSink[0], sourceSink[1], thePath, vertexArr);			
		}
		return retDist;
		
		
		
		
	}

	
	public static int  prims(int [][] sp, int [][] path, int [] tarInd, ArrayList<Integer> visitedTar, int [] sourceSink){
		
		int curTar;
		int curDist;
		int infinity = Integer.MAX_VALUE;
		int sourceTar;
		int [] localSolutions = new int [tarInd.length];
		int [] localNextTar = new int [tarInd.length];
		int [] source = new int [tarInd.length];
		int [] sink = new int [tarInd.length];
		
		int sinkV = -1;
		
		int minDist;
		int nextTar;
		int flag = 0;
		int k = 0;
		/*For each targets in visitedTar*/
		
		Iterator<Integer> itr = visitedTar.iterator();
		while (itr.hasNext()){
			flag = 0;
			minDist = infinity;
			nextTar = -1;
			sourceTar = itr.next();
			/*Calculate the distance between this target and other targets*/
			for (int i = 0; i < tarInd.length; i++){
				curTar = tarInd[i];
			
				if (visitedTar.contains(curTar)){
					/* 
					localSolutions[i] = -1;
					localNextTar[i] = -1;
				*/
				//	System.out.println();
				}
				else {
					
					curDist = sp[sourceTar][tarInd[i]];
					if (curDist < minDist && curDist !=0){
						minDist = curDist;
						nextTar = tarInd[i];
						sinkV = tarInd[i];
						
					
					}
				}
				
			}
			
			localSolutions[k] = minDist;
			sink[k] = sinkV;
			//source[k] = tarInd[sourceTar];
			source[k] = sourceTar;
			localNextTar[k] = nextTar;
			k++;
		
		}
		/*check to see which gave us the best solution*/
		
		minDist = infinity;
		int minInd = -1;
		
		for (int i = 0; i < localSolutions.length; i++){
			if (localSolutions[i] < minDist && localSolutions[i] != 0){
				minDist = localSolutions[i];
				minInd = i;
			}
			
		}
		
		/*localSolutions[minInd] gives us the opt value*/
		/**/
		
		/* 
		 * source: minInd
		 * sink: localNextTar[minInd]
		 * */
		int newSink = localNextTar[minInd];
		int newSource = source[minInd];
		visitedTar.add(newSink);
		
		sourceSink[0] = newSource;
		sourceSink[1] = newSink;
		
		//return minDist;
		return localSolutions[minInd];
	
	}
	
	
	public static void drawPath(Graph g, int source, int sink, ArrayList<Integer> path, Vertex [] vertList){
		reverse(path);
		
		/*Start on the source*/

		Vertex v1 = vertList[source];
		Iterator<Integer> pathItr  = path.iterator();
		pathItr.next();
		Vertex nextV;
		Vertex v2; 
		Edge e;
		while (true){
			if (v1.getId() == sink){
				break;
			}
			
			
			nextV = vertList[pathItr.next()];
			
			Iterator<Edge> eitr = v1.edgeIterator();
			while (eitr.hasNext()){
				e = eitr.next();
				v2 = e.getOppositeVertexOf(v1);
				
				if (v2 == nextV){
					e.setMark(1);              // we are considering
					e.setColor(Color.GREEN);     // color it green for animation)
					SteinerTreeTester.show(g);
					v1 = v2;
					break;
				}
				
			}
			
		//	nextV = vertList[pathItr.next()];
			
		}
		
	}
	
	
	// Recursively depth first search the graph until all targets are reached.
	// As it searches, it tags vertices as reached by setting their value field to 1.
	// It sets the mark field for edges that were used to reach new target vertices.
	// We can tell this occurred when we return from a recursive search of an edge and 
	// the number of targets remaining has decreased.
	//
	// We exit the dfs early once all targets have been found.
	//
	// returns: the number of targets still remaining 
	public static int dfs(Graph g, Vertex v, int targetsRemaining) {
		v.setValue(1);             // set value to indicated this vertex has been reached
		if (v.getMark() == 1)
			targetsRemaining--;    // we found a target vertex
		if (targetsRemaining == 0)
			return 0;				// all targets found, we are done
		
		//Iterator<Edge> itr = v.edgeIterator();  // iterate over all edges out of this vertex
		//while (itr.hasNext()) {
		//	Edge e = itr.next();
//		for (Edge e : (Iterable<Edge>) v) {
		for (Edge e : v) {
			Vertex newv = e.getOppositeVertexOf(v);
			if (newv.getValue() == 0) { // found an unreached vertex
				e.setMark(1);              // we are considering
				e.setColor(Color.GREEN);     // color it green for animation)
				SteinerTreeTester.show(g);
				int newRemaining = dfs(g, newv, targetsRemaining); // recursively search
				if (newRemaining < targetsRemaining) { // did this edge lead to any new targets
					targetsRemaining = newRemaining;
					e.setMark(1);                 // mark this edge as part of solution
					SteinerTreeTester.show(g);    // for animation show the graph at this point
				}
				else {
					e.setMark(0);                 // unmark, this lead to nothing used
					e.setColor(Color.RED);        // draw in red
					SteinerTreeTester.show(g);
				}
				if (targetsRemaining == 0)
					return 0;				// all targets found, we are done
			}
		}

		return targetsRemaining;
	}
}


