package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.concrete.dictionaries.KVPair;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import misc.Sorter;
import misc.exceptions.NoPathExistsException;


/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    private IDictionary<V, IList<E>> dictionary;
    private IList<E> edges;

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * Note that each edge in 'edges' represents a unique edge. For example, if 'edges'
     * contains an entry for '(A,B)' and for '(B,A)', that means there are two parallel
     * edges between vertex 'A' and vertex 'B'.
     *
     * @throws IllegalArgumentException if any edges have a negative weight
     * @throws IllegalArgumentException if any edges connect to a vertex not present in 'vertices'
     * @throws IllegalArgumentException if 'vertices' or 'edges' are null or contain null
     * @throws IllegalArgumentException if 'vertices' contains duplicates
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        this.dictionary = new ChainedHashDictionary<>();
        this.edges = edges;

        if (vertices == null) {
            throw new IllegalArgumentException();
        }

        if (edges == null) {
            throw new IllegalArgumentException();
        }

        for (V vertex : vertices) {
            if (vertex == null) {
                throw new IllegalArgumentException();
            }
            this.dictionary.put(vertex, new DoubleLinkedList<>());
        }

        for (E edge : edges) {
            if (edge == null) {
                throw new IllegalArgumentException();
            }

            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }

            if (!vertices.contains(edge.getVertex1()) || !vertices.contains(edge.getVertex2())) {
                throw new IllegalArgumentException();
            }

            this.dictionary.get(edge.getVertex1()).add(edge);
            this.dictionary.get(edge.getVertex2()).add(edge);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.dictionary.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> mst = new ChainedHashSet<>();
        IDisjointSet<V> vertexSet = new ArrayDisjointSet<>();

        for (KVPair<V, IList<E>> vertex : this.dictionary) {
            vertexSet.makeSet(vertex.getKey());
        }

        IList<E> edgeSortList = Sorter.topKSort(numEdges(), this.edges);

        for (E edge : edgeSortList) {
            if (vertexSet.findSet(edge.getVertex1()) != vertexSet.findSet(edge.getVertex2())) {
                vertexSet.union(edge.getVertex1(), edge.getVertex2());
                mst.add(edge);
            }
        }

        return mst;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null or not in the graph
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        IList<E> result = new DoubleLinkedList<>();

        if (start == null || end == null || !this.dictionary.containsKey(start) || !this.dictionary.containsKey(end)) {
            throw new IllegalArgumentException();
        }

        if (start.equals(end)) {
            return result;
        }

        ISet<V> visited = new ChainedHashSet<>();
        IDictionary<V, Path<V, E>> vertexPath = new ChainedHashDictionary<>();

        // Initialize Distance of each Vertices
        for (KVPair<V, IList<E>> pair : this.dictionary) {
            V eachVertex = pair.getKey();

            // Source Vertex Distance = 0
            if (eachVertex.equals(start)) {
                vertexPath.put(start, new Path<>(start, 0.0, new DoubleLinkedList<>()));

                // Other Vertices Distance = INFINITY
            } else {
                vertexPath.put(eachVertex, new Path<>(eachVertex));
            }
        }

        // Initialize Minimum Priority Queue
        IPriorityQueue<Path<V, E>> heap = new ArrayHeap<>();
        heap.add(vertexPath.get(start));

        // Dijkstra's Algorithm:
        while (!heap.isEmpty() && heap.peekMin().vertex != end) {
            Path<V, E> current = heap.removeMin();

            for (E eachEdge : this.dictionary.get(current.vertex)) {
                if (!visited.contains(eachEdge.getOtherVertex(current.vertex))) {
                    V otherVertex = eachEdge.getOtherVertex(current.vertex);
                    double oldDist = vertexPath.get(otherVertex).dist;
                    double newDist = current.dist + eachEdge.getWeight();

                    if (newDist < oldDist) {
                        IList<E> currentPath = new DoubleLinkedList<>();
                        for (E edge : current.path) {
                            currentPath.add(edge);
                        }
                        currentPath.add(eachEdge);

                        Path<V, E> oldPath = vertexPath.get(otherVertex);
                        Path<V, E> newPath = new Path<>(otherVertex, newDist, currentPath);

                        vertexPath.put(otherVertex, newPath);

                        if (oldDist == Double.POSITIVE_INFINITY) {
                            heap.add(newPath);
                        } else {
                            heap.replace(oldPath, newPath);
                        }
                    }
                }
            }
            visited.add(current.vertex);
        }

        if (heap.isEmpty()){
            throw new NoPathExistsException();
        }

        result = heap.peekMin().path;

        return result;
    }

    private static class Path<V, E> implements Comparable<Path<V, E>> {
        private final V vertex;
        private final Double dist;
        private final IList<E> path;

        public Path(V vertex) {
            this.vertex = vertex;
            this.dist = Double.POSITIVE_INFINITY;
            this.path = new DoubleLinkedList<>();
        }

        public Path(V vertex, Double dist, IList<E> path) {
            this.vertex = vertex;
            this.dist = dist;
            this.path = path;
        }

        public int compareTo(Path<V, E> other) {
            return this.dist.compareTo(other.dist);
        }

    }
}
