//package demo;
//
//import com.github.javaparser.StaticJavaParser;
//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.Node;
//import com.github.javaparser.ast.expr.MethodCallExpr;
//import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
//import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
//import com.github.javaparser.symbolsolver.JavaSymbolSolver;
//import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
//import com.github.javaparser.utils.SourceRoot;
//import com.google.gson.internal.$Gson$Preconditions;
//
//import java.io.File;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Create by lp on 2020/1/4
// */
//public class MethodCallInfo {
//    /*解析的java文件*/
//
//
//
//    private static final String FILE_PATH = "H:\\CodeGraph1.0\\src\\main\\java\\ExampleData\\data\\A.java";
//
//    //注意事项，需要将我们的源码放在java目录下，检索范围是要在项目路径即可，不需要在包路径下
//    public static void main(String[] args) throws Exception {
//        int[] dp = new int[5];
//        System.out.println(dp[2]);
//        System.out.println("donr");
//        TypeSolver typeSolver = new CombinedTypeSolver(
//                new ReflectionTypeSolver(),
//                new JavaParserTypeSolver(new File("H:\\CodeGraph1.0\\src\\main\\java\\ExampleData\\data\\"))
//
//        );
//        //SourceRoot sourceRoot=new SourceRoot(Paths.get("H:\\CodeGraph1.0\\src\\main\\java\\ProjectDir\\MethodCallTest"));
//        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
//        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
//        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));
//        List<MethodCallExpr> methodCallExprList = cu.findAll(MethodCallExpr.class);
//        for (MethodCallExpr methodCallExpr : methodCallExprList) {
//            ResolvedMethodDeclaration rmd = methodCallExpr.resolve();
//        }
//
//        /*cu.findAll(MethodCallExpr.class).forEach(mce ->
//                System.out.println(mce.resolve().getQualifiedSignature()));
//        List<Node> nodes = new ArrayList<>();
//        VoidVisitorAdapter<List<Node>> speciallNodes = new SpeciallNodes();
//        speciallNodes.visit(cu, nodes);
//        System.out.println("done");*/
//
//    }
//}
