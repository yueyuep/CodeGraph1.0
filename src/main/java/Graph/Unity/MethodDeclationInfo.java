package Graph.Unity;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * Create by lp on 2020/4/29
 */
public class MethodDeclationInfo {
    //直接父类（内部类函数的直接父类函数）
    ClassOrInterfaceDeclaration classOrInterfaceDeclaration;
    MethodDeclaration methodDeclaration;

    public MethodDeclationInfo(MethodDeclaration me) {
        //构造函数
        this.methodDeclaration = me;
        //todo 测试这块
        this.classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) me.getParentNode().get();

    }

    public ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration() {
        return classOrInterfaceDeclaration;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }
}
