package Graph;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Create by lp on 2020/1/13
 */
public class AST2GraphTest {

    @Test
    public void constructNetwork() {
        try {
            AST2Graph ast2Graph = new AST2Graph("H:\\CodeGraph1.0\\src\\main\\java\\data\\A.java");
            List<MethodDeclaration> methodDeclarations = ast2Graph.getCompilationUnit().findAll(MethodDeclaration.class);
            ast2Graph.constructNetwork(methodDeclarations.get(0));
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}