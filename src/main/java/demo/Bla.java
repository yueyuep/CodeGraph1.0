package demo;

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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/**
 * Create by lp on 2020/4/17
 */
public class Bla {
    public static void main(String[] args) throws IOException {

        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy().collect(Paths.get("").toAbsolutePath());

        for (SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
            sourceRoot.getParserConfiguration().setAttributeComments(false); // Ignore comments

            for (ParseResult<CompilationUnit> r : sourceRoot.tryToParse()) {
                r.getResult().ifPresent(compilationUnit -> {
                    for (ClassOrInterfaceDeclaration c : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)) {

                        System.out.println(c);
                        Set<MethodDeclaration> calledMethods = new HashSet<MethodDeclaration>();

                        ArrayDeque<MethodDeclaration> todo = new ArrayDeque<MethodDeclaration>();
                        c.findAll(MethodDeclaration.class).stream().forEach(methodDeclaration -> todo.add(methodDeclaration));

                        while (!todo.isEmpty()) {
                            MethodDeclaration m = todo.poll();

                            for (MethodCallExpr expr : m.findAll(MethodCallExpr.class)) {
                                ResolvedMethodDeclaration rmd = expr.resolve();

                                if (rmd instanceof JavaParserMethodDeclaration) {
                                    JavaParserMethodDeclaration jpmd = (JavaParserMethodDeclaration) rmd;
                                    MethodDeclaration mdec = jpmd.getWrappedNode();

                                    if (!calledMethods.contains(mdec)) {
                                        calledMethods.add(mdec);
                                        todo.add(mdec);
                                    }
                                }
                            }
                        }

                    }
                });
            }
        }
        System.out.println("Done");
    }
}
