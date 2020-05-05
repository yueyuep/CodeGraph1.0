package data;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;


/**
 * Create by lp on 2020/1/4
 *
 */
public class A {
    public static void main(String[] args) {
        //测试第三方API调用功能
        CompilationUnit compilationUnit = StaticJavaParser.parse("");
        int[] arrays = new int[]{1, 2, 3, 4, 5};
        System.out.println("测试");
        B b = new B();
        b.test();
        b.test1("lipeng");
    }

}
