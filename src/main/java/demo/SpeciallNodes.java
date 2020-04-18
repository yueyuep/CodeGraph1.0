package demo;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

/**
 * Create by lp on 2020/1/6
 */
public class SpeciallNodes extends VoidVisitorAdapter<List<Node>> {
    private int level = 0;

    @Override
    public void visit(MethodCallExpr methodCallExpr, List<Node> nodes) {
        nodes.add(methodCallExpr);
        super.visit(methodCallExpr, nodes);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, List<Node> nodes) {
        nodes.add(methodDeclaration);
        super.visit(methodDeclaration, nodes);
    }
}
