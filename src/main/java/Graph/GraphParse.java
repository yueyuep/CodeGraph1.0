package Graph;

import Graph.Base.HeadAndBodyToJson;
import Graph.Unity.MethodCall;
import Graph.Unity.ProjectInfo;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Create by lp on 2020/2/5
 * 主程序的入口
 */
public class GraphParse {
    //函数名-类名-参数类型1-参数类型2
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
        String SaveCat = "../CodeGraph1.0/src/main/resources/";
        //打开所在的目录
        Path path = Utils.openDirFileChooser().toPath();
        //项目的具体信息
        ProjectInfo projectInfo = new ProjectInfo(path);
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy().collect(path);
        ProcessMultiFile(projectRoot, SaveCat, projectInfo);
    }

    /*文件处理的入口程序*/
    public static void ProcessMultiFile(ProjectRoot projectRoot, String SaveCat, ProjectInfo projectInfo) throws Exception {
        /*MethodDeclation中的CallMethod的类型*/
        // 获得所有文件的内部类函数和外部类函数
        List<SourceRoot> sourceRoots = projectRoot.getSourceRoots();
        for (SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
            sourceRoot.getParserConfiguration().setAttributeComments(false); // Ignore comments
            Path path1 = sourceRoot.getRoot();
            List<ParseResult<CompilationUnit>> test = sourceRoot.tryToParse();
            for (ParseResult<CompilationUnit> r : sourceRoot.tryToParse()) {
                r.getResult().ifPresent(compilationUnit -> {
                    for (ClassOrInterfaceDeclaration c : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)) {
                        List<MethodDeclaration> methodDeclarationList = c.findAll(MethodDeclaration.class);
                        File pfile = null;
                        try {
                            pfile = getFile(methodDeclarationList.get(0), sourceRoots);
                        } catch (Exception e) {
                            System.out.println("java文件中不存在函数声明\n");
                            e.printStackTrace();
                        }
                        //存储第一行数据
                        new GraphParse()
                                .headOfJson(pfile, methodDeclarationList, SaveCat, projectInfo);
                        for (MethodDeclaration methodDeclaration : c.findAll(MethodDeclaration.class)) {
                            // TODO 文件的路径查找有问题
                            /*
                             * 函数调用的存储格式
                             * */
                            HashMap<MethodCallExpr, MethodCall> callMethod = Utils.getcallMethods(pfile, methodDeclaration, sourceRoots);
                            new GraphParse()
                                    .methodOfJson(pfile, methodDeclaration, callMethod, SaveCat + pfile.getName() + ".txt", projectInfo);

                        }
                    }

                });
            }
        }


    }

    public void headOfJson(File file, List<MethodDeclaration> methodDeclarations, String saveFilePath, ProjectInfo projectInfo) {
        String[] array = file.getPath().split("\\\\");
        this.fileName = file.getName();
        this.version = projectInfo.getVersion();
        // 函数名-类名.类名-参数类型-参数类型
        // 无参函数： 函数名-类名.类名-
        methodDeclarations.forEach(methodDeclaration -> this.callMethodName.add(
                methodDeclaration.getNameAsString() + "-" +
                        getClassNameOfMethod(methodDeclaration) + "-" +
                        getMethodParameter(methodDeclaration)
        ));
        Utils.saveToJsonFile(new HeadAndBodyToJson.Head(this.fileName, this.version, this.callMethodName), saveFilePath);
    }

    public void methodOfJson(File file, MethodDeclaration methodDeclaration, HashMap<MethodCallExpr, MethodCall> CalledMethod, String savaFilePath, ProjectInfo projectInfo) {
        this.fileName = file.getName();
        this.version = projectInfo.getVersion();
        this.methodName = methodDeclaration.getNameAsString() + "-" + getClassNameOfMethod(methodDeclaration) + "-" + getMethodParameter(methodDeclaration);
        HeadAndBodyToJson.Body body = new HeadAndBodyToJson.Body(file, this.fileName, this.version, this.methodName, methodDeclaration, CalledMethod);
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
                System.out.println("GraphParse Info:new MethodDeclation" + methodDeclaration.toString());

            } else {
                // TODO
                // 第二种情况再往上遍历时，会找到其他类型的节点

//                System.out.println("此情况未考虑");
//                System.exit(0);
                System.out.println("GraphParse Info:Other MethodDeclation" + methodDeclaration.toString());
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

    private static File getFile(MethodDeclaration methodDeclaration, List<SourceRoot> sourceRoots) {
        //TODO 需要测试这里的功能
        ResolvedMethodDeclaration me = methodDeclaration.resolve();
        JavaParserMethodDeclaration jme = (JavaParserMethodDeclaration) me;
        String joinPath = jme
                .getQualifiedName()
                .replace(".", "//")
                .replace("//" + methodDeclaration.getNameAsString(), "")
                + ".java";
        return MethodCall.getFullPath(sourceRoots, joinPath);

    }


}




