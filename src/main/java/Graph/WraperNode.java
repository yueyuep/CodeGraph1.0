package Graph;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import java.util.HashMap;
import java.util.Optional;
/**
 * Create by lp on 2020/1/6
 */
public class WraperNode {
    private Node pnode;
    protected Optional<Range> poptionRanges;
    private static HashMap<String, WraperNode> wraperNodes = new HashMap<>();
    private static HashMap<Range, WraperNode> rangeWraperNods = new HashMap<>();

    public WraperNode(Node pnode) {
        this.pnode = pnode;
        this.poptionRanges = pnode.getRange();
    }
    public static void nodeCacheClear() {
        wraperNodes.clear();
    }

    public static WraperNode newinstance(Node pnode) {
        String hashKey = String.valueOf(pnode.hashCode());
        if (pnode.getRange().isPresent()) {
            if (rangeWraperNods.keySet().contains(pnode.getRange().get()))
                return rangeWraperNods.get(pnode.getRange().get());
            else {
                WraperNode wraperNode = new WraperNode(pnode);
                rangeWraperNods.put(pnode.getRange().get(), wraperNode);
                return rangeWraperNods.get(pnode.getRange().get());
            }
        } else {
            WraperNode wraperNode = new WraperNode(pnode);
            wraperNodes.put(hashKey, wraperNode);
            return wraperNodes.get(hashKey);
        }
    }

    public static HashMap<Range, WraperNode> getRangeWraperNods() {
        return rangeWraperNods;
    }
    public static HashMap<String, WraperNode> getWraperNodes() {
        return wraperNodes;
    }
    public Node getPnode() { return pnode; }
}
