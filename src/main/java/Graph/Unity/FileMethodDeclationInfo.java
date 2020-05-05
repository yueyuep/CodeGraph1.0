package Graph.Unity;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
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
    //我们的Java文件是什么类型的
    CompilationUnit cu;
    TypeDeclaration typeDeclaration;
    //父类对象暂定
    ClassOrInterfaceDeclaration parentClassOrInterfaceDeclaration;
    File file = null;
    List<MethodDeclationInfo> methodDeclationInfoList = new ArrayList<>();

    public FileMethodDeclationInfo(CompilationUnit cu, List<SourceRoot> sourceRoots) {
        this.cu = cu;
        //确定外部父类对象
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            //TODO 一定是非空的吗？
            if (classOrInterfaceDeclaration.getParentNode().get() instanceof CompilationUnit) {
                this.parentClassOrInterfaceDeclaration = classOrInterfaceDeclaration;
            }
        }
        if (parentClassOrInterfaceDeclaration == null) {
            NodeList<TypeDeclaration<?>> nodeList = cu.getTypes();
            System.out.println("断点");
        }
        this.methodDeclationInfoList = init_methodDeclationInfo(parentClassOrInterfaceDeclaration);
        this.typeDeclaration = init_typeDeclation(cu);
        this.file = init_file(sourceRoots);
        //NodeList<TypeDeclaration<?>> nodeList1 = cu.getTypes();


    }

    //初始化文件下的函数声明信息
    private List<MethodDeclationInfo> init_methodDeclationInfo(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        List<MethodDeclationInfo> methodDeclationInfos = new ArrayList<>();
        try {
            //TODO 可能classOrInterfaceDeclaration为空
            for (MethodDeclaration methodDeclaration : classOrInterfaceDeclaration.findAll(MethodDeclaration.class)) {
                methodDeclationInfos.add(new MethodDeclationInfo(cu, methodDeclaration, parentClassOrInterfaceDeclaration));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("init_methodDeclationInfo");
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
        /*for (MethodDeclationInfo meInfo : this.methodDeclationInfoList) {
            //todo 测试找不到的情况
            if (meInfo.getClassOrInterfaceDeclaration() == this.parentClassOrInterfaceDeclaration) {
                ResolvedMethodDeclaration me = meInfo.getMethodDeclaration().resolve();
                JavaParserMethodDeclaration jme = (JavaParserMethodDeclaration) me;
                String jme_name = jme.getQualifiedName();
                String fp = meInfo.getMethodDeclaration().getNameAsString();
                joinPath = (jme
                        .getQualifiedName()
                        .replace(".", "//") + "//")
                        .replace("//" + meInfo.getMethodDeclaration().getNameAsString() + "//", "");
                joinPath = joinPath + ".java";
                break;
            }
        }*/
        try {
            ResolvedTypeDeclaration resolvedTypeDeclaration = this.typeDeclaration.resolve();
            //ResolvedReferenceTypeDeclaration referenceTypeDeclaration = this.parentClassOrInterfaceDeclaration.resolve();
            String qualifiedName = resolvedTypeDeclaration.getQualifiedName();
            joinPath = qualifiedName.replace(".", "//") + ".java";
        } catch (Exception e) {
            System.out.println("端点");
        }


        return MethodCall.getFullPath(sourceRoots, joinPath);

    }

    private TypeDeclaration init_typeDeclation(CompilationUnit cu) {
        //TODO get(0)
        return cu.getTypes().get(0);

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

    public CompilationUnit getCu() {
        return cu;
    }
}
