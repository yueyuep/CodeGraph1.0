package Graph;

import Graph.Base.HeadAndBodyToJson;
import Graph.Unity.FileMethodDeclationInfo;
import Graph.Unity.MethodCall;
import Graph.Unity.MethodDeclationInfo;
import Graph.Unity.ProjectInfo;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
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
        ProcessMultiFile(projectRoot, projectInfo);
    }

    /*文件处理的入口程序*/
    public static void ProcessMultiFile(ProjectRoot projectRoot, ProjectInfo projectInfo) throws Exception {
        /*MethodDeclation中的CallMethod的类型*/
        // 获得所有文件的内部类函数和外部类函数
        List<SourceRoot> sourceRoots = projectRoot.getSourceRoots();
        for (SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
            sourceRoot.getParserConfiguration().setAttributeComments(false);

            // Ignore comments
            //Path path1 = sourceRoot.getRoot();
            //List<ParseResult<CompilationUnit>> test = sourceRoot.tryToParse();
            for (ParseResult<CompilationUnit> r : sourceRoot.tryToParse()) {
                r.getResult().ifPresent(compilationUnit -> {
                    //todo 内部类ClassOrInterfaceDeclaration也会被遍历出来。区分出来
                    FileMethodDeclationInfo fileMethodDeclationInfo = new FileMethodDeclationInfo(compilationUnit, sourceRoots);
                    ClassOrInterfaceDeclaration parentClassOrInterfaceDeclaration = fileMethodDeclationInfo.getParentClassOrInterfaceDeclaration();

                    File pfile = fileMethodDeclationInfo.getFile();
                    List<MethodDeclaration> methodDeclarationList = fileMethodDeclationInfo.getMethodDeclarationList();
                    System.out.println("==========处理文件：" + pfile.toString());
                    /*保存的文件路径*/
                    String SaveCat = getSavePath(projectInfo, pfile);
                    //存储第一行数据
                    new GraphParse()
                            .headOfJson(pfile, fileMethodDeclationInfo.getMethodDeclationInfoList(), SaveCat, projectInfo);

                    //注解类型先过滤掉
                    for (MethodDeclationInfo methodDeclationInfo : fileMethodDeclationInfo.getMethodDeclationInfoList()) {
                        // TODO 文件的路径查找有问题
                        /*
                         * 函数调用的存储格式
                         * */
                        HashMap<MethodCallExpr, MethodCall> callMethod = Utils.getcallMethods(pfile, methodDeclationInfo.getMethodDeclaration(), sourceRoots);

                        new GraphParse()
                                .methodOfJson(pfile, methodDeclationInfo, callMethod, SaveCat, projectInfo);
                    }

                });
            }
        }


    }

    public void headOfJson(File file, List<MethodDeclationInfo> methodDeclationInfos, String saveFilePath, ProjectInfo projectInfo) {
        /*获得文件得相对路径*/
        this.fileName = file.toString().replace(projectInfo.getPrifxPath(), "");
        this.version = projectInfo.getVersion();
        // 函数名-类名.类名-参数类型-参数类型
        // TODO(new、其他情况) 无参函数： 函数名-类名.类名-
        methodDeclationInfos.forEach(methodDeclationInfo -> this.callMethodName.add(
                methodDeclationInfo.getMethodDeclaration().getNameAsString() + "-" +
                        methodDeclationInfo.getMeOfClassName() + "-" +
                        methodDeclationInfo.getMethodParameter()
        ));
        //TODO 此处的保存路径，需要设置的与原路径相同
        Utils.saveToJsonFile(new HeadAndBodyToJson.Head(this.fileName, this.version, this.callMethodName), saveFilePath);
    }

    public void methodOfJson(File file, MethodDeclationInfo methodDeclationInfo, HashMap<MethodCallExpr, MethodCall> CalledMethod, String savaFilePath, ProjectInfo projectInfo) {
        this.fileName = file.toString().replace(projectInfo.getPrifxPath(), "");
        this.version = projectInfo.getVersion();
        this.methodName = methodDeclationInfo.getMethodDeclaration()
                .getNameAsString() + "-" + methodDeclationInfo.getMeOfClassName() + "-" + methodDeclationInfo.getMethodParameter();

        HeadAndBodyToJson.Body body = new HeadAndBodyToJson.Body(file, this.fileName, this.version, this.methodName, methodDeclationInfo.getMethodDeclaration(), CalledMethod);
        body.addFeatureMethodOfJson(projectInfo);
        Utils.saveToJsonFile(body, savaFilePath);
    }

    public static String getSavePath(ProjectInfo projectInfo, File pfile) {
        String name1 = pfile.toString().replace(projectInfo.getVersion(), projectInfo.getVersion() + "_G");
        String name2 = name1.replace(pfile.getName(), "");
        File cat = new File(name2);
        if (!cat.exists()) {
            cat.mkdirs();
        }

        return cat + "\\" + pfile.getName().replace(".java", ".txt");


    }


}




