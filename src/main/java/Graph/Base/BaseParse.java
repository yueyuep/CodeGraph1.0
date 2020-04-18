package Graph.Base;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.Data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


/**
 * * Create by lp on 2020/1/13
 * *
 * *
 **/

@Data
public class BaseParse {
    private String pfilePath;
    private CompilationUnit compilationUnit;
    private List<MethodDeclaration> methodDeclarations;

    public BaseParse() {
    }

    public BaseParse(String pfilePath) throws FileNotFoundException {
        this.pfilePath = pfilePath;
        try {

            this.compilationUnit = StaticJavaParser.parse(new FileInputStream(pfilePath));
            this.methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);

        } catch (Exception e) {

            System.out.println("Exception：" + pfilePath);

        }
    }

}
