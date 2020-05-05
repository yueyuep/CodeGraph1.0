package data;


import java.util.Arrays;
import java.util.Comparator;

/**
 * Create by lp on 2020/1/4
 */
public class A {
    /*外部类->内部类->new->函数申明（调用）*/

    //无法拿到new下的函数声明所在的类名路径（得到的是匿名类对象）

    class innerClass {
        public static void main(String[] args) {
            int[] arrays = new int[]{1, 2, 3, 4, 5};
            Arrays.sort(arrays, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o2 - o1;
                }
            });
            System.out.println("测试");
            B b = new B();
            b.test();
            b.test1("lipeng");

        }


    }
}