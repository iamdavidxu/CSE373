package disjointsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A quick-union-by-size data structure with path compression.
 * @see DisjointSets for more documentation.
 */
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    // Do NOT rename or delete this field. We will be inspecting it directly in our private tests.
    List<Integer> pointers;
    public HashSet<T> newSet;
    public List<Integer> size;
    public List<Integer> id;
    /*
    However, feel free to add more fields and private helper methods. You will probably need to
    add one or two more fields in order to successfully implement this class.
    */

    public UnionBySizeCompressingDisjointSets() {
        pointers = new ArrayList<>();
        newSet = new HashSet<>();
        size = new ArrayList<>();
        id = new ArrayList<>();
    }

    @Override
    public void makeSet(T item) {
        pointers.add(newSet.size() - 1);
        newSet.add(item);
        size.add(1);
        id.add(id.size());

    }

    @Override
    public int findSet(T item) {
        int index = new ArrayList<>(newSet).indexOf(item);
        if (index < 0) {
            throw new IllegalArgumentException("Index smaller than 0");
        }
        int root = index;
        while (root != id.get(root)) {
            id.set(root, id.get(id.get(root)));
            root = id.get(root);
        }
        return root;
    }

    @Override
    public boolean union(T item1, T item2) {
        int index1 = new ArrayList<>(newSet).indexOf(item1);
        int index2 = new ArrayList<>(newSet).indexOf(item2);
        if (index1 < 0 || index2 < 0) {
            throw new IllegalArgumentException("Not in disjoint set");
        }
        int root1 = findSet(item1);
        int root2 = findSet(item2);
        if (root1 == root2) {
            return false;
        }
        if (size.get(root1) > size.get(root2)) {
            id.set(root2, root1);
            size.set(root1, size.get(root1) + size.get(root2));
            pointers.set(root1, root1);
        } else {
            id.set(root1, root2);
            size.set(root2, size.get(root2) + size.get(root1));
            pointers.set(root2, root2);
        }
        return true;
    }
}
