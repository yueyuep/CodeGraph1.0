package Graph;
import Graph.Base.DataToJson;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.lang.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
/**
 * Create by lp on 2020/2/5
 * 主程序的入口
 */
public class GraphParse {
    //函数名-类名.类名-参数类型1-参数类型2
    private String fileName;
    private String version;
    private List<String> callMethodName = new ArrayList<>();
    private String methodName;
    public GraphParse() {
        //无参构造函数；
    }
    public static void main(String[] args) throws Exception {
        System.out.println("开始解析！");
        String Version = "0.9.22";
        String SVersion = "s0.9.22";
        String SourceCat = "../CodeGraph1.0/src/main/resources/SourceData/" + Version + "/";
        String SaveCat = "../CodeGraph1.0/src/main/resources/Savedata/" + SVersion + "/";
        File dir = new File(SourceCat);
        ExtractJavaFile javafile = new ExtractJavaFile(SourceCat);
        javafile.getFileList(dir);
        File[] fileList = javafile.getFile();
        ProcessMultiFile(fileList, SaveCat);
    }

    /*文件处理的入口程序*/
    public static void ProcessMultiFile(File[] fileList, String SaveCat) throws Exception {
        /*MethodDeclation中的CallMethod的类型*/
        HashMap<String, HashMap<MethodDeclaration, String>> callMethod;
        // 获得所有文件的内部类函数和外部类函数
        List<HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>>> fileMethodDeclarationMap =
                Utils.getfileMethodDeclarationMap(fileList);
        for (File pfile : fileList) {//循环遍历文件处理
            AST2Graph ast2Graph = new AST2Graph(pfile.getPath());
            // 不包含 new 类{ 函数 }的情况
            List<MethodDeclaration> methodDeclarations = ast2Graph.getMethodDeclarations();
            //写入当前文件的头文件信息
            new GraphParse().headOfJson(pfile, methodDeclarations, SaveCat + pfile.getName() + ".txt");
            //获得当前文件的外部类、内部类函数
            HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>> outclassMethods = fileMethodDeclarationMap.get(1).get(pfile);
            HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>> innerclassMethods = fileMethodDeclarationMap.get(0).get(pfile);
            //循环遍历函数声明处理
            for (MethodDeclaration methodDeclaration : methodDeclarations) {
                //  函数申明在外部类或者内部类中m
                if (Utils.containMethod(methodDeclaration, outclassMethods, innerclassMethods)) {
                    //目前只处理外部类和内部类中的函数
                    callMethod = Utils.getcallMethods(pfile, methodDeclaration, fileMethodDeclarationMap);
                    try {
                        new GraphParse().methodOfJson(pfile, methodDeclaration, callMethod, SaveCat + pfile.getName() + ".txt");
//                        String classNameOrMethod = FunctionParse.getClassOfMethod(methodDeclaration);//获得类名_函数名
//                        PreocessingMethod(ast2Graph, methodDeclaration, classNameOrMethod.concat("_") + methodDeclaration.getNameAsString(), pfile, callMethod, SaveCat);
                    } catch (NumberFormatException e) {
                        System.out.println(methodDeclaration.getNameAsString() + "\t:内外部类函数构造异常");
                        continue;
                    }

                } else {
                    System.out.println("函数声明的其他情况发生");

                    //  函数申明不在内部类外部类函数中
                    // 代码中实例化的函数,比如在新建接口，需要对接口中的方法进行实现，有可能直接在new花括号中直接实现。这部分会被
                    // 这部分会被当成函数调用，注意这种格式一般是函数被重写(目前是按照这种格式来处理的)
//                    if (methodDeclaration.getParentNode().isPresent() && methodDeclaration.getParentNode().get() instanceof ObjectCreationExpr) {
//                        //这个是new实例化中的方法重写
//                        //标记方法用新建对象的new A(){}中的A作为我们的类对象。
//                        String newClassName = ((ObjectCreationExpr) methodDeclaration.getParentNode().get()).getTypeAsString();
//                        //获得类名
//                        String classNameOfMethod = PareClassOrInterfaces.concatName(newClassName);
//                        //  在OuterClassMethod2Json中完成了函数名的划分
//                        callMethod = Utils.getcallMethods(pfile, methodDeclaration, fileMethodDeclarationMap);//<文件名，<函数申明，类名_>>
//                        try {
//                            new GraphParse().methodOfJson(pfile, methodDeclaration, SaveCat + pfile.getName() + ".txt");
////                            PreocessingMethod(ast2Graph, methodDeclaration, classNameOfMethod.concat("_") + methodDeclaration.getNameAsString(), pfile, callMethod, SaveCat);
//                        } catch (NumberFormatException e) {
//                            System.out.println(methodDeclaration.getNameAsString() + "\t:实例化函数构造异常");
//                            continue;
//                        }
//
//
//                    } else {
//                        // TODO 可能存在其他的情况，还没想到
//                    }


                }

            }
        }
    }
    public void headOfJson(File file, List<MethodDeclaration> methodDeclarations, String saveFilePath) {
        System.out.println("测试点"+file.getPath());
        String[] array = file.getPath().split("\\\\");
        this.fileName = file.getName();
        this.version = array[4];
        // 函数名-类名.类名-参数类型-参数类型
        // 无参函数： 函数名-类名.类名-
        methodDeclarations.forEach(methodDeclaration -> this.callMethodName.add(
                methodDeclaration.getNameAsString() + "-" +
                        getClassNameOfMethod(methodDeclaration) + "-" +
                        getMethodParameter(methodDeclaration)
        ));
        Utils.saveToJsonFile(new DataToJson.Head(this.fileName, this.version, this.callMethodName), saveFilePath);
    }

    public void methodOfJson(File file, MethodDeclaration methodDeclaration, HashMap<String, HashMap<MethodDeclaration, String>> CalledMethod, String savaFilePath) {
        this.fileName = file.getName();
        this.version = file.getParent().split(File.separator)[4];
        this.methodName = methodDeclaration.getNameAsString() + "-" + getClassNameOfMethod(methodDeclaration) + "-" + getMethodParameter(methodDeclaration);

        DataToJson.Body body = new DataToJson.Body(file, this.fileName, this.version, this.methodName, methodDeclaration, CalledMethod);
        body.addFeatureMethodOfJson();

        Utils.saveToJsonFile(body, savaFilePath);
    }

    /**
     * @Description: 返回函数的类名，多层嵌套
     * @Param:
     * @return:
     * @Author: Kangaroo
     * @Date: 2019/10/22
     */
    public String getClassNameOfMethod(Node methodDeclaration) {
        List<String> allClassName = new ArrayList<>();

        while (methodDeclaration.getParentNode().isPresent() && !(methodDeclaration.getParentNode().get() instanceof CompilationUnit)) {

            if (methodDeclaration.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
                allClassName.add(((ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get()).getName().toString());
            } else if (methodDeclaration.getParentNode().get() instanceof ObjectCreationExpr) {
                // TODO
                // 函数定义在 new 类名(){}中的情况暂不完善
                //
//                allClassName.add(((ObjectCreationExpr)methodDeclaration.getParentNode().get()).getTypeAsString());

            } else {
                // TODO
                // 第二种情况再往上遍历时，会找到其他类型的节点

//                System.out.println("此情况未考虑");
//                System.exit(0);
            }
            methodDeclaration = methodDeclaration.getParentNode().get();
        }

        Collections.reverse(allClassName);
        return StringUtils.join(allClassName.toArray(), ".");
    }

    /**
     * @Description: 获取带参数类型的函数名
     * @Param:
     * @return: String
     * @Author: Kangaroo
     * @Date: 2019/10/22
     */
    public String getMethodParameter(MethodDeclaration methodDeclaration) {
        List<String> res = new ArrayList<>();

        for (Parameter parameter : methodDeclaration.getParameters()) {
            Type type = parameter.getType();
            String string = new String();

            if (type.isArrayType()) {
                string = parameter.getType().asArrayType().asString();

            } else if (type.isClassOrInterfaceType()) {
                string = parameter.getType().asClassOrInterfaceType().asString();

            } else if (type.isIntersectionType()) {
                string = parameter.getType().asIntersectionType().asString();

            } else if (type.isPrimitiveType()) {
                string = parameter.getType().asPrimitiveType().asString();

            } else if (type.isReferenceType()) {
                System.out.println("ReferenceType");
                // pass

            } else if (type.isTypeParameter()) {
                string = parameter.getType().asTypeParameter().asString();

            } else if (type.isUnionType()) {
                string = parameter.getType().asUnionType().asString();

            } else if (type.isUnknownType()) {
                string = parameter.getType().asUnknownType().asString();

            } else if (type.isVarType()) {
                string = parameter.getType().asVarType().asString();

            } else if (type.isVoidType()) {
                string = parameter.getType().asVoidType().asString();

            } else if (type.isWildcardType()) {
                string = parameter.getType().asWildcardType().asString();

            } else {
                System.out.println("Wrong!");
                System.exit(0);
            }
            res.add(string);
        }

        return StringUtils.join(res.toArray(), "-");
    }

}




