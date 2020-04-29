package Graph;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Create by lp on 2020/4/29
 */
public class GraphParseTest {

    @Test
    /*测试主类*/
    public void test_main1() {
        try {
            /*注意我们的包名必须是实际的，必须按照项目的真实情况来处理的*/
            GraphParse.main(new String[]{""});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}