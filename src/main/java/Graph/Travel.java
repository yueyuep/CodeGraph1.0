package Graph;

import Graph.Base.EdegeType;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by lp on 2020/1/6
 * 遍历不同类型的节点
 */
@Data
public class Travel {

    private final String ClASS_NAME = "class com.github.javaparser.ast.stmt.";

    //private MutableValueGraph<Object, String> mutableValueGraph;
    private MutableNetwork<Object, String> mutableNetwork = NetworkBuilder.directed().allowsParallelEdges(true).allowsSelfLoops(true).build();
    List<Range> vistedNodes = new ArrayList<>();
    List<Node> tempPreNodes = new ArrayList<>();
    Collection<Node> preNodes = new ArrayList<>();
    Collection<Node> pContinueNodes = new ArrayList<>();
    Collection<Node> pBreakNodes = new ArrayList<>();

    private int edgeNumber;

    /**
     * Author:lp on 2020/1/6 10:38
     * Param:
     * return:
     * Description:遍历CFG和语句相关的节点
     */
    public Travel() {
        preNodes.clear();
        pBreakNodes.clear();
        pContinueNodes.clear();
    }

    public void travelNodeForCFG(Node rootnode) {   //methodDelacation
        if (!rootnode.getChildNodes().isEmpty()) {
            for (Node childNodes : rootnode.getChildNodes()) {
                String caseType = childNodes.getClass().toString().substring(ClASS_NAME.length());
                switch (childNodes.getClass().toString().substring(ClASS_NAME.length())) {
                    /*
                     case "BlockStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(childNodes);
                        BlockStmt blockStmt = (BlockStmt) childNodes;
                        blockStmt.getChildNodes().forEach(childnodes -> travelNodeForCFG(childnodes));
                        travelNodeForCFG(blockStmt);
                    }
                    break;
                    * */
                    case "ExpressionStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(childNodes);
                    }
                    break;
                    case "ForStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        ForStmt forStmt = (ForStmt) childNodes;
                        resetPreNodes(childNodes);
                        forStmt.getInitialization().forEach(init -> {
                            addNextEdgeForPreNodes(init);
                            resetPreNodes(init);
                        });
                        forStmt.getCompare().ifPresent(compare -> {
                            addNextEdgeForPreNodes(compare);
                            resetPreNodes(compare);
                        });
                        pBreakNodes.clear();
                        pContinueNodes.clear();
                        travelNodeForCFG(forStmt.getBody());
                        preNodes.addAll(pContinueNodes);
                        forStmt.getUpdate().forEach(update -> {
                            addNextEdgeForPreNodes(update);
                            resetPreNodes(update);
                        });
                        forStmt.getCompare().ifPresent(compare -> {
                            addNextEdgeForPreNodes(compare);
                            resetPreNodes(compare);
                        });
                        preNodes.addAll(pBreakNodes);
                    }

                    break;
                    case "WhileStmt": {
                        WhileStmt whileStmt = (WhileStmt) childNodes;
                        addNextEdgeForPreNodes(childNodes);
                        addNextEdge(childNodes, whileStmt.getCondition());
                        pBreakNodes.clear();
                        pContinueNodes.clear();
                        travelNodeForCFG(whileStmt.getBody());
                        preNodes.addAll(pContinueNodes);
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(whileStmt.getCondition());  //preNode设置是否合理，想想为什么？
                        preNodes.addAll(pBreakNodes);             //结束，加入break节点
                    }

                    break;
                    case "SwitchStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        SwitchStmt switchStmt = (SwitchStmt) childNodes;
                        Expression selector = switchStmt.getSelector();
                        addNextEdge(childNodes, selector);
                        preNodes.clear();
                        pBreakNodes.clear();
                        for (SwitchEntry entry : switchStmt.getEntries()) {
                            preNodes.add(selector);
                            addNextEdgeForPreNodes(entry);
                            entry.getLabels().forEach(label -> {
                                addNextEdge(entry, label);
                            });
                            resetPreNodes(entry);
                            travelNodeForCFG(entry);

                        }
                        preNodes.addAll(pBreakNodes);
                    }
                    break;
                    case "ForEachStmt": {
                        ForEachStmt forEachStmt = (ForEachStmt) childNodes;
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(childNodes);
                        addNextEdgeForPreNodes(forEachStmt.getIterable());
                        resetPreNodes(forEachStmt.getIterable());
                        addNextEdgeForPreNodes(forEachStmt.getVariable());
                        resetPreNodes(forEachStmt.getIterable());
                        travelNodeForCFG(forEachStmt.getBody());
                        addNextEdgeForPreNodes(forEachStmt.getIterable());
                        resetPreNodes(forEachStmt.getIterable());
                    }                         //注意语句的执行顺序

                    break;
                    case "AssertStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(childNodes);

                    }
                    break;
                    case "LabeledStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(childNodes);
                    }
                    break;
                    case "SynchronizedStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(childNodes);
                        SynchronizedStmt synchronizedStmt = (SynchronizedStmt) childNodes;
                        addNextEdgeForPreNodes(synchronizedStmt.getExpression());
                        resetPreNodes(synchronizedStmt.getExpression());
                        travelNodeForCFG(synchronizedStmt.getBody());
                    }
                    break;

                    case "BreakStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        pBreakNodes.clear();
                        pBreakNodes.add(childNodes);
                    }
                    break;
                    case "ContinueStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        pContinueNodes.clear();
                        pContinueNodes.add(childNodes);
                    }
                    break;
                    case "CatchClause": {
                        addNextEdgeForPreNodes(childNodes);
                        CatchClause catchClause = (CatchClause) childNodes;
                        addNextEdge(childNodes, catchClause.getParameter());
                        travelNodeForCFG(catchClause.getBody());
                        resetPreNodes(childNodes);
                    }
                    break;
                    case "ThrowStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(childNodes);
                    }
                    break;

                    case "TryStmt": {
                        addNextEdgeForPreNodes(childNodes);
                        resetPreNodes(childNodes);
                        TryStmt tryStmt = (TryStmt) childNodes;
                        travelNodeForCFG(tryStmt.getTryBlock());
                    }

                    break;

                    case "DoStmt": {
                        DoStmt doStmt = (DoStmt) childNodes;
                        travelNodeForCFG(doStmt.getBody());
                        addNextEdgeForPreNodes(doStmt.getCondition());//这里设置是否正确
                        resetPreNodes(doStmt.getCondition());
                    }
                    break;

                    case "IfStmt": {
                        handleIfStmt(childNodes);
                    }
                    break;
                    default: {
                        travelNodeForCFG(childNodes);
                    }
                    break;


                }


            }

        }

    }

    public <T extends Node> boolean travelAllNodes(T rootNode) {
        if (!rootNode.getRange().isPresent() || vistedNodes.contains(rootNode.getRange().get())) return false;
        List<Node> nodes = rootNode.findAll(Node.class);
        for (Node pnode : nodes) {
            if (!pnode.getRange().isPresent() || vistedNodes.contains(pnode.getRange().get())) continue;
            pnode.getRange().ifPresent(range -> vistedNodes.add(pnode.getRange().get()));

            //确定不同类型的节点进行处理
            String nodeClassPackage = pnode.getClass().toString();
            String nodeType = getNodeType(nodeClassPackage);
            pnode.removeComment();


        }
        return true;
    }

    public String getNodeType(String packageName) {
        String[] arrays = packageName.split(".");
        return arrays[arrays.length - 1];

    }

    public void addNextEdgeForPreNodes(Node childNode) {
        for (Node pre : preNodes) {
            addNextEdge(pre, childNode);

        }
    }

    public void addNextEdge(Node pre, Node succ) {
        WraperNode wraperPreNode = WraperNode.newinstance(pre);
        WraperNode wraperSuccNode = WraperNode.newinstance(succ);
        mutableNetwork.addEdge(wraperPreNode, wraperSuccNode, edgeNumber + "_" + EdegeType.NEXT_EDGE);
        edgeNumber++;

    }

    public void resetPreNodes(Node node) {
        preNodes.clear();
        preNodes.add(node);
    }

    public void handleIfStmt(Node childNode) {
        IfStmt ifStmt = (IfStmt) childNode;
        addNextEdgeForPreNodes(childNode);
        addNextEdge(childNode, ifStmt.getCondition());
        resetPreNodes(ifStmt.getCondition());
        travelNodeForCFG(ifStmt.getThenStmt());
        tempPreNodes.addAll(preNodes);
        resetPreNodes(ifStmt.getCondition());
        ifStmt.getElseStmt().ifPresent(eleseStmt -> {
            addNextEdgeForPreNodes(eleseStmt);
            if (eleseStmt.isIfStmt()) {
                handleIfStmt(eleseStmt);
            } else {
                resetPreNodes(eleseStmt);
                travelNodeForCFG(eleseStmt);
            }
        });

        preNodes.addAll(tempPreNodes);
        tempPreNodes.clear();
    }


}
