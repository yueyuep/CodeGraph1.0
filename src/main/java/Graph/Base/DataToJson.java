package Graph.Base;

import Graph.AST2Graph;
import Graph.GraphParse;
import Graph.WraperNode;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.google.common.graph.MutableNetwork;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by lp on 2020/2/9
 */
public class DataToJson {
    /*head序列化的字段信息
     * fileName:
     * version:
     * hasMethodName:
     */
    public static class Head {
        @Expose
        @SerializedName(value = "fileName")
        private String fileName;
        @Expose
        @SerializedName(value = "version")
        private String version;
        @Expose
        @SerializedName(value = "hasMethodName")
        private List<String> callMethodName = new ArrayList<>();

        public Head(String fileName, String version, List<String> callMethodName) {
            this.fileName = fileName;
            this.version = version;
            this.callMethodName = callMethodName;
        }
    }

    /*body序列化字段信息
     * fileName:
     * version:
     * methodName:
     * callMethodNameReferTo:
     * num:
     * succs:
     * attribute:*/
    public static class Body {
        private AST2Graph ast2Graph;
        private MethodDeclaration methodDeclaration;
        private HashMap<String, HashMap<MethodDeclaration, String>> calledMethod = new HashMap<>();

        @Expose
        @SerializedName(value = "fileName")
        private String fileName;
        @Expose
        @SerializedName(value = "version")
        private String version;
        @Expose
        @SerializedName(value = "methodName")
        private String methodName;
        @Expose
        @SerializedName(value = "callMethodNameReferTo")
        private Map<Integer, String> callMethodNameReferTo = new HashMap<>();
        @Expose
        @SerializedName(value = "num")
        private int nodeNumber;
        @Expose
        @SerializedName(value = "succs")
        private List<List<Integer>> successors = new ArrayList<>();
        @Expose
        @SerializedName(value = "attribute")


        private List<String> nodeAttribute = new ArrayList<>();

        public Body(File file, String fileName, String version, String methodName, MethodDeclaration methodDeclaration,
                    HashMap<String, HashMap<MethodDeclaration, String>> calledMethod) {
            this.ast2Graph = AST2Graph.newInstance(file.getPath());
            this.methodDeclaration = methodDeclaration;
            this.calledMethod = calledMethod;

            this.fileName = fileName;
            this.version = version;
            this.methodName = methodName;
        }


        /*填充body字段信息*/
        public void addFeatureMethodOfJson() {
            this.ast2Graph.initNetwork();
            this.ast2Graph.constructNetwork(this.methodDeclaration);
            MutableNetwork<Object, String> mutableNetwork = this.ast2Graph.getNetwork();
            Map<Object, Integer> vistedMethodCallex = new HashMap<>();
            Map<Object, Integer> nodeMap = new HashMap<>();
            int nodeIndex = 0;
            // 添加函数调用字段
            for (Object node : mutableNetwork.nodes()) {
                nodeMap.put(node, nodeIndex);
                nodeIndex++;
                //+++++++++++++++++++++++++++++++++++++++++构建节点调用函数位置关系++++++++++++++++++++++++++++++++++++++++
                if (!vistedMethodCallex.containsKey(node) && node instanceof WraperNode) {
                    //当callMethod得到的结果为零的时候，一定是不存在函数调用的。直接过滤掉。
                    if (((WraperNode) node).getPnode() instanceof MethodCallExpr) {
                        MethodCallExpr methodCallExpr = ((MethodCallExpr) ((WraperNode) node).getPnode()).asMethodCallExpr();
                        //如果当前结点是函数调用的结点
                        int index = nodeMap.get(node);
                        vistedMethodCallex.put(methodCallExpr, index);
                        for (String fileName : this.calledMethod.keySet()) {
                            for (MethodDeclaration methodDeclaration : this.calledMethod.get(fileName).keySet()) {
                                // TODO
                                //  MethodCallExpr和 MethodDeclation的比较这部分逻辑问题
                                //  目前： 仅仅通过函数名，和参数个数来进行匹配
                                //  待完善： 通过函数名和参数类型来进行匹配
                                if (methodDeclaration.getNameAsString().equals(methodCallExpr.getNameAsString()) && methodDeclaration.getParameters().size() == methodCallExpr.getArguments().size()) {
                                    String className = this.calledMethod.get(fileName).get(methodDeclaration);
                                    String res = fileName.concat("-").concat(methodDeclaration.getNameAsString()).concat("-").concat(className).concat("-").concat(new GraphParse().getMethodParameter(methodDeclaration));
                                    this.callMethodNameReferTo.put(index, res);
                                }
                            }
                        }
                    }
                }
            }

            // 添加节点总数字段
            this.nodeNumber = nodeIndex;

            // 添加节点关系（后继）字段
            for (Object node : mutableNetwork.nodes()) {
                List<Integer> tempNode = mutableNetwork.successors(node).stream()
                        .map(n -> nodeMap.get(n))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                Integer index = nodeMap.get(node);
                tempNode = tempNode.stream().filter(n -> n != index).collect(Collectors.toList());
                this.successors.add(tempNode);
                addAttribute(node, this.nodeAttribute);
            }

        }

        public void addAttribute(Object node, List<String> nodeAttribute) {
            if (node instanceof WraperNode) {
                nodeAttribute.add(new Graph2Json().travelNode(((WraperNode) node).getPnode()));
            } else if (node instanceof String) {
                nodeAttribute.add(node.toString());
            } else {
                nodeAttribute.add(node.toString());
            }
        }


    }

}
