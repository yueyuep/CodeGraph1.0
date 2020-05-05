package data;


import java.util.Arrays;
import java.util.Comparator;

/**
 * Create by lp on 2020/1/4
 */
public class A {
    /*测试new情况下的函数声明*/
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
