package graphs.minspantrees;

import disjointsets.DisjointSets;
import disjointsets.UnionBySizeCompressingDisjointSets;
import graphs.BaseEdge;
import graphs.KruskalGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Computes minimum spanning trees using Kruskal's algorithm.
 * @see MinimumSpanningTreeFinder for more documentation.
 */
public class KruskalMinimumSpanningTreeFinder<G extends KruskalGraph<V, E>, V, E extends BaseEdge<V, E>>
    implements MinimumSpanningTreeFinder<G, V, E> {

    protected DisjointSets<V> createDisjointSets() {
        //return new QuickFindDisjointSets<>();
        /*
        Disable the line above and enable the one below after you've finished implementing
        your `UnionBySizeCompressingDisjointSets`.
         */
        return new UnionBySizeCompressingDisjointSets<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    public MinimumSpanningTree<V, E> findMinimumSpanningTree(G graph) {
        List<E> edges = new ArrayList<>(graph.allEdges());
        edges.sort(Comparator.comparingDouble(E::weight));
        DisjointSets<V> disjointSets = createDisjointSets();

        for (V vertex: graph.allVertices()) {
            disjointSets.makeSet(vertex);
        }

        List<E> treeEdge = new ArrayList<>();
        for (E edge: edges) {
            V start = edge.from();
            V end = edge.to();
            if (disjointSets.findSet(start) != disjointSets.findSet(end)) {
                disjointSets.union(start, end);
                treeEdge.add(edge);
            }
        }

        if (graph.allVertices().isEmpty()) {
            return new MinimumSpanningTree.Success<>(treeEdge);
        }

        if (treeEdge.size() != graph.allVertices().size() - 1) {
            return new MinimumSpanningTree.Failure<>();
        } else {
            return new MinimumSpanningTree.Success<>(treeEdge);
        }
    }
}

