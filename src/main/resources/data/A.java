package data;


import java.util.Arrays;
import java.util.Comparator;

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

    public static <E> OrderedPSet<E> from(final Collection<? extends E> list) {
        if (list instanceof OrderedPSet) return (OrderedPSet<E>) list;
        return OrderedPSet.<E>empty().plusAll(list);
    }

    public static void main(String[] args) {
        int[] arrays = new int[]{1, 2, 3, 4, 5};
        System.out.println("测试");
        B b = B.newInstance("test").test();
        /*new 情况下的函数声明*/
        Arrays.sort(args, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return 0;
            }
        });

        b.test1("lipeng");
    }

}
