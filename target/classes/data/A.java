package mdata;


/**
 * Create by lp on 2020/1/4
 */
public class A {
    class innerClass {
        public innerClass() {

        }

        public static void main(String[] args) {
            System.out.println("内部类函数测试");
        }
    }

    public static void main(String[] args) {
        int[] arrays = new int[]{1, 2, 3, 4, 5};
        System.out.println("测试");
        B b = new B();
        b.test();
        b.test1("lipeng");
    }

}
