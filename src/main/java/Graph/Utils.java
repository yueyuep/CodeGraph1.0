package Graph;
import Graph.Base.FunctionParse;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Create by lp on 2020/2/5
 * 工具包
 */
public class Utils {
    public static List<HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>>> getfileMethodDeclarationMap(File[] files) {
        // 获得所有文件的内部函数声明和外部函数声明
        List<HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>>> fileMethodDeclarationMap = new ArrayList<>();
        //<外部类，外部类中所有的方法声明>
        HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>> allOutclassMethods = new HashMap<>();
        //<内部类，内部类中所有的方法声明>
        HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>> allInnerclassMethods = new HashMap<>();
        for (File file : files) {
            FunctionParse functionParse = new FunctionParse(file);
            functionParse.PareMethod();
            allOutclassMethods.put(file, functionParse.getOutclassMethods());
            allInnerclassMethods.put(file, functionParse.getInnerclassMethods());
        }
        fileMethodDeclarationMap.add(allInnerclassMethods);
        fileMethodDeclarationMap.add(allOutclassMethods);
        return fileMethodDeclarationMap;
    }
    public static boolean containMethod(MethodDeclaration methodDeclaration, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>> outclassMethods, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>> innertclassMethods) {
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : outclassMethods.keySet()) {
            if (outclassMethods.get(classOrInterfaceDeclaration).contains(methodDeclaration)) {
                return true;
            }
        }
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : innertclassMethods.keySet()) {
            if (innertclassMethods.get(classOrInterfaceDeclaration).contains(methodDeclaration)) {
                return true;
            }
        }
        return false;
    }
    public static HashMap<String, HashMap<MethodDeclaration, String>> getcallMethods(File pfile, MethodDeclaration methodDeclaration, List<HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>>> fileMethodDeclarationMap) throws Exception {
        /*

         找到所有函数调用所在的文件名，类名、函数申明,修订版本，
         通过javaSampleSolver来解决。这个可以直接获得函数调用得所在得包名，需要进行进一步解析。
        */
        //工程项目所在的工程路径
        String path = "H:\\CodeGraph1.0\\src\\main\\resources\\SourceData\\0.9.22\\android-demo\\";
        HashMap<String, HashMap<MethodDeclaration, String>> CalledMethod = new HashMap<>();
        List<MethodCallExpr> methodCallExprList = methodDeclaration.findAll(MethodCallExpr.class);
        //获得函数调用得具体位置
        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(new File(path)));
        JavaSymbolSolver javaSymbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(javaSymbolSolver);
        CompilationUnit cu = StaticJavaParser.parse(new File(pfile.getPath()));
        //cu.findAll(MethodCallExpr.class).forEach(mce ->
        // System.out.println(mce.resolve().getQualifiedSignature()));
        //遍历所有的函数调用，
        for (MethodCallExpr methodCallExpr : methodCallExprList) {
            System.out.println(methodCallExpr.resolve().getQualifiedSignature());
        }
        return CalledMethod;
    }
    /*save json to file*/
    public static void saveToJsonFile(Object object, String fileName) {
        Gson gson = new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String jsonString = gson.toJson(object).replace("\\", "");//去掉转移字符
        saveToFile(jsonString, fileName);
    }

    /*save  text to file*/
    public static void saveToFile(String text, String fileName) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
            out.write(text + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
