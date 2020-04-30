package data;

/**
 * Create by lp on 2020/1/4
 */
public class B {
    private String name;

    public B(String name) {
        this.name = name;

    }

    public static B newInstance(String name) {
        return new B(name);

    }

    public void test() {
        System.out.println("测试数据");
    }

    public void test1(String s) {
        System.out.println("测试参数" + s);
    }

}
