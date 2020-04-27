package demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

/**
 * Create by lp on 2020/4/17
 */
public class Test {
    public static void main(String[] args) throws IOException {
        // You might want to set your own path instead of using the FileChooser
        Path path = openDirFileChooser().toPath();
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy().collect(path);
        for (SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
            sourceRoot.getParserConfiguration().setAttributeComments(false); // Ignore comments
            Path path1=sourceRoot.getRoot();
            List<ParseResult<CompilationUnit>> test = sourceRoot.tryToParse();
            for (ParseResult<CompilationUnit> r : sourceRoot.tryToParse()) {
                r.getResult().ifPresent(compilationUnit -> {
                    for (ClassOrInterfaceDeclaration c : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)) {

                        System.out.println(c.getNameAsString());
                        System.out.println(countCalledMethods(c));
                    }
                });
            }
        }
    }

    private static int countCalledMethods(ClassOrInterfaceDeclaration c) {
        List<MethodDeclaration> methods = c.getMethods();
        Set<MethodDeclaration> calledMethods = new HashSet<MethodDeclaration>(methods);
        ArrayDeque<MethodDeclaration> todo = new ArrayDeque<MethodDeclaration>(methods);
        while (!todo.isEmpty()) {
            MethodDeclaration m = todo.poll();
            for (MethodCallExpr expr : m.findAll(MethodCallExpr.class)) {
                ResolvedMethodDeclaration rmd = expr.resolve();

                if (rmd instanceof JavaParserMethodDeclaration) {
                    JavaParserMethodDeclaration jpmd = (JavaParserMethodDeclaration) rmd;
                    MethodDeclaration mdec = jpmd.getWrappedNode();
                    String className=jpmd.getQualifiedSignature();

                    if (!calledMethods.contains(mdec)) {
                        calledMethods.add(mdec);
                        todo.add(mdec);
                    }
                }
            }
        }

        return calledMethods.size();
    }

    private static File openDirFileChooser() {
        File file = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        return file;
    }
}
