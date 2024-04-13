package graphs.shortestpaths;
import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.DoubleMapMinPQ;
import priorityqueues.ExtrinsicMinPQ;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    // current method based on lecture 15 slide 15:
    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        Set<V> known = new HashSet<>();
        ExtrinsicMinPQ<V> pq = createMinPQ();
        Map<V, Double> distTo = new HashMap<>();
        Map<V, E> edgeTo = new HashMap<>();
        distTo.put(start, 0.0);
        pq.add(start, 0.0);

        while (!pq.isEmpty()) {
            V vertex = pq.removeMin();
            known.add(vertex);
            if (vertex.equals(end)) {
                return edgeTo;
            }
            for (E edge : graph.outgoingEdgesFrom(vertex)) {
                V newVertex = edge.to();
                if (!known.contains(newVertex)) {
                    double oldDist = Double.POSITIVE_INFINITY;
                    if (distTo.containsKey(newVertex)) {
                        oldDist = distTo.get(newVertex);
                    }
                    double newDist = distTo.get(vertex) + edge.weight();
                    if (newDist < oldDist) {
                        distTo.put(newVertex, newDist);
                        edgeTo.put(newVertex, edge);
                        if (pq.contains(newVertex)) {
                            pq.changePriority(newVertex, newDist);
                        } else {
                            pq.add(newVertex, newDist);
                        }
                    }
                }
            }
        }
        return edgeTo;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(end);
        }
        if (spt.containsKey(end)) {
            List<E> shortestPath = new ArrayList<>();
            V current = end;
            while (!current.equals(start)) {
                E edge = spt.get(current);
                shortestPath.add(edge);
                current = edge.from();
            }
            Collections.reverse(shortestPath);
            return new ShortestPath.Success<>(shortestPath);
        }
        return new ShortestPath.Failure<>();
    }
}
