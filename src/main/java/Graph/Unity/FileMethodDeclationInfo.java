package Graph.Unity;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:lp on 2020/4/29 0:29
 * @Param:
 * @return:
 * @Description:记录单个文件的具体信息(文件路径，函数声明)
 */
public class FileMethodDeclationInfo {
    ClassOrInterfaceDeclaration parentClassOrInterfaceDeclaration;
    File file = null;
    List<MethodDeclationInfo> methodDeclationInfoList = new ArrayList<>();

    public FileMethodDeclationInfo(CompilationUnit cu, List<SourceRoot> sourceRoots) {
        //确定外部父类对象
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            //TODO 一定是非空的吗？
            if (classOrInterfaceDeclaration.getParentNode().get() instanceof CompilationUnit) {
                this.parentClassOrInterfaceDeclaration = classOrInterfaceDeclaration;
            }
        }
        this.methodDeclationInfoList = init_methodDeclationInfo(parentClassOrInterfaceDeclaration);
        this.file = init_file(sourceRoots);


    }

    //初始化文件下的函数声明信息
    private List<MethodDeclationInfo> init_methodDeclationInfo(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        List<MethodDeclationInfo> methodDeclationInfos = new ArrayList<>();
        for (MethodDeclaration methodDeclaration : classOrInterfaceDeclaration.findAll(MethodDeclaration.class)) {
            methodDeclationInfos.add(new MethodDeclationInfo(methodDeclaration));
        }
        return methodDeclationInfos;

    }

    private File init_file(List<SourceRoot> sourceRoots) {
        /**
         *@Author:lp on 2020/4/29 0:05
         *@Param: [methodDeclarationList, sourceRoots]
         *@return: java.io.File
         *@Description:返回我们处理函数声明所在的文件路径，需要解决外部类，内部类
         */

        //TODO 需要测试这里的功能，对给定的函数申明找到函数声明所在的具体文件，内部类、外部类
        String joinPath = "";
        for (MethodDeclationInfo meInfo : this.methodDeclationInfoList) {
            //todo 测试找不到的情况
            if (meInfo.getClassOrInterfaceDeclaration() == this.parentClassOrInterfaceDeclaration) {
                ResolvedMethodDeclaration me = meInfo.getMethodDeclaration().resolve();
                JavaParserMethodDeclaration jme = (JavaParserMethodDeclaration) me;
                joinPath = jme
                        .getQualifiedName()
                        .replace(".", "//")
                        .replace("//" + meInfo.getMethodDeclaration().getNameAsString(), "")
                        + ".java";
            }
        }

        return MethodCall.getFullPath(sourceRoots, joinPath);

    }

    public List<MethodDeclaration> getMethodDeclarationList() {
        List<MethodDeclaration> methodDeclarationList = new ArrayList<>();
        this.methodDeclationInfoList.stream().forEach(meInfo -> methodDeclarationList.add(meInfo.getMethodDeclaration()));
        return methodDeclarationList;
    }

    public ClassOrInterfaceDeclaration getParentClassOrInterfaceDeclaration() {
        return parentClassOrInterfaceDeclaration;
    }

    public File getFile() {
        return file;
    }

    public List<MethodDeclationInfo> getMethodDeclationInfoList() {
        return methodDeclationInfoList;
    }
}
