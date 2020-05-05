package data;

/**
 * Create by lp on 2020/1/4
 */
public interface A {
    /*接口中静态方法测试，以及接口中的静态方法调用*/
    static void sayHello() {
        System.out.println("接口静态方法");
    }

    public static void main(String[] args) {
        int[] arrays = new int[]{1, 2, 3, 4, 5};
        System.out.println("测试");
        B b = new B();
        b.test();
        b.test1("lipeng");
    }

}
