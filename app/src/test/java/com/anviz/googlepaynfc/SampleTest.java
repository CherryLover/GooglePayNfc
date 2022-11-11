package com.anviz.googlepaynfc;

import org.junit.Test;

/**
 * @description
 * @author: Created jiangjiwei in 2022/11/10 17:30
 */
public class SampleTest {

    @Test
    public void test() {
        String aid = "F123422222";
        String result = String.format("%02X", aid.length() / 2);
        System.out.println("result " + result);
    }
}
