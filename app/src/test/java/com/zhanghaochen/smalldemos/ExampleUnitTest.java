package com.zhanghaochen.smalldemos;

import com.zhanghaochen.smalldemos.utils.SysUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testEval() {
        assertEquals(-1, SysUtils.eval("sin(270)"), 0.1);
    }
}
