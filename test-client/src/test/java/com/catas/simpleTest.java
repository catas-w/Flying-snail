package com.catas;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class simpleTest {

    @Test
    public void test1() {
        ArrayList<String> integers = new ArrayList<>(Arrays.asList("1","2","3",null));
        String res = String.join(",", integers);
        System.out.println(res);
        assert res.equals("1,2,3,null");
    }

    @Test
    public void test2() {
        String s = "1,2,3,null";
        String[] strings = s.split(",");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(strings));
        // System.out.println(Arrays.toString(strings));
        // System.out.println(list);
        Queue<String> queue = new LinkedList<>(Arrays.asList(strings));
        System.out.println(queue);
        queue.poll();
        System.out.println(queue);
    }
}
