package GUM;

import com.github.gumtreediff.gen.SyntaxException;
import com.github.gumtreediff.gen.javaparser.JavaParserGenerator;
import com.github.gumtreediff.gen.javaparser.JavaParserVisitor;
import com.github.gumtreediff.io.LineReader;
import com.github.gumtreediff.tree.TreeContext;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

/**
 * Create by lp on 2020/1/16
 */
public class MethodDeclationVisitor extends JavaParserGenerator {

    HashMap<String, TreeContext> treeContextHashMap = new HashMap<>();


    public MethodDeclationVisitor() {
        super();
    }

    public HashMap<String, TreeContext> generateTreeContextList(Reader r) throws IOException {
        LineReader lr = new LineReader(r);

        try {
            CompilationUnit cu = StaticJavaParser.parse(lr);

            List<MethodDeclaration> mcu = cu.findAll(MethodDeclaration.class);
            for (MethodDeclaration methodDeclaration : mcu) {
                JavaParserVisitor v = new JavaParserVisitor(lr);
                v.visitPreOrder(methodDeclaration);
                treeContextHashMap.put(methodDeclaration.getNameAsString(), v.getTreeContext());

            }
            return treeContextHashMap;
        } catch (ParseProblemException var5) {
            throw new SyntaxException(this, r);
        }

    }

    public HashMap<String, TreeContext> getTreeContextHashMap() {
        return treeContextHashMap;
    }
}
