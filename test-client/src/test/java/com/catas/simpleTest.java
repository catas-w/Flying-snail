package com.catas;

import org.junit.Test;

import java.util.*;

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

    @Test
    public void test3() {
        // System.out.println(removeInvalidParentheses("q("));
        // System.out.println(removeInvalidParentheses("q)"));
        // System.out.println(removeInvalidParentheses("(a"));
        // System.out.println(removeInvalidParentheses(""));
        // System.out.println(removeInvalidParentheses("(a)())()"));
        // System.out.println(removeInvalidParentheses("(a()))(s)())()"));
        // System.out.println(removeInvalidParentheses(")("));
        // System.out.println(removeInvalidParentheses("(()"));
        System.out.println(removeInvalidParentheses("()(((((((()"));
    }

    public List<String> removeInvalidParentheses(String s) {
        Set<String> res = new HashSet<>();
        Set<String> memo = new HashSet<>();
        List<String> list = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(s);
        while (!queue.isEmpty()) {
            int size = queue.size();
            while (size-- > 0) {
                String curStr = queue.poll();
                // 合法
                if (isValid(curStr)) {
                    res.add(curStr);
                }
                // 当前字符串减去一个字符的所有子串入队
                for (int i=0; i<curStr.length(); i++) {
                    String subStr = curStr.substring(0, i) + curStr.substring(i+1);
                    if (memo.add(subStr))
                        queue.add(subStr);
                }
            }
            if (res.size() > 0) {
                break;
            }
        }
        if (res.isEmpty()) {
            res.add("");
        }
        return new ArrayList<String>(res);
    }

    public boolean isValid(String s) {
        int count = 0;
        for (char chr: s.toCharArray()) {
            if (chr >= 'a' && chr <= 'z') {
                continue;
            }
            else if (chr == '(') {
                count ++;
            } else {
                if (count > 0) {
                    if (chr == ')')
                        count --;
                } else {
                    return false;
                }
            }
        }
        return count == 0;
    }
}
