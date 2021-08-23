package com.catas;

import org.junit.Test;

import java.util.*;

public class simpleTest {

    @Test
    public void test1() {
        // System.out.println(Arrays.toString(reconstructQueue()));
        // StringBuilder stringBuilder = new StringBuilder();
        // StringBuffer stringBuffer = new StringBuffer();
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1,1);
        map.put(2,2);
        map.put(3,2);
        Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().getKey());
        }
    }


    public String compileSeq (String input) {
        // write code here
        if (input == null || input.length() == 0)
            return "";
        String[] nums = input.split(",");
        int len = nums.length;
        int[][] graph = new int[len][len];
        int[] indegree = new int[len];
        for (int i=0; i < nums.length; i++) {
            int num = Integer.valueOf(nums[i]);
            if (num != -1) {
                graph[num][i] = 1;
                indegree[i] ++;
            }
        }

        Deque<Integer> queue = new LinkedList<>();
        StringBuilder res = new StringBuilder();

        for (int i=0; i < len; i++) {
            if (indegree[i] == 0)
                queue.addLast(i);
        }

        while (!queue.isEmpty()) {
            int num = queue.pollFirst();
            res.append(num).append(",");
            for (int i=len-1; i >= 0; i--) {
                if (graph[num][i] > 0) {
                    indegree[i] --;
                    if (indegree[i] == 0)
                        queue.addFirst(i);
                }
            }
        }
        res.deleteCharAt(res.length() - 1);
        return res.toString();
    }

    public int[][] reconstructQueue(int[][] people) {
        if (people.length == 0) {
            return new int[0][0];
        }
        Arrays.sort(people, (o1, o2) -> {
            if (o1[0] == o2[0]) {
                return Integer.compare(o1[1], o2[1]);
            } else {
                return Integer.compare(o1[0], o2[0]);
            }
        });
        List<int[]> ls = new LinkedList<>();
        for (int[] p : people) {
            ls.add(p[1], p);
        }
        return ls.toArray(new int[people.length][2]);
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

    @Test
    public void testTK() {
        System.out.println(Arrays.toString(topKFrequent(new int[]{1, 1, 2, 2, 2, 3, 4, 4, 5, 5, 6, 6, 6}, 2)));
        System.out.println(Arrays.toString(topKFrequent(new int[]{-1,-1,2,2,3}, 2)));
        System.out.println(Arrays.toString(topKFrequent(new int[]{-1,-1}, 1)));
    }

    public int[] topKFrequent(int[] nums, int k) {
        int len = nums.length;
        int[] res = new int[k];
        // int[] count = new int[len + 1];
        Map<Integer, Integer> count = new HashMap<>();

        Queue[] seq = new Queue[len + 1];

        for (int n: nums) {
            int frequence = count.getOrDefault(n, 0) + 1;
            count.put(n, frequence);
        }

        for (int i: count.keySet()) {
            int frequence = count.get(i);
            if (seq[frequence] == null) {
                seq[frequence] = new LinkedList<Integer>();
            }
            seq[frequence].add(i);

        }
        int index = 0;
        while (k > 0 && len > 0) {
            while (seq[len] != null && !seq[len].isEmpty()) {
                // res[index++] = seq[len];
                res[index++] = (int) seq[len].poll();
                k--;
            }
            len --;
        }
        return res;
    }

    @Test
    public void testDecode() {
        System.out.println(findDisappearedNumbers(new int[]{1,2,3,3,5}));
        System.out.println(findDisappearedNumbers(new int[]{4,3,2,7,8,2,3,1}));
        System.out.println(findDisappearedNumbers(new int[]{1,3,3,4,5,5,7}));
        System.out.println(findDisappearedNumbers(new int[]{1,3,3,7,5,5,7}));
        System.out.println(findDisappearedNumbers(new int[]{1,1}));
        System.out.println(findDisappearedNumbers(new int[]{}));
    }

    public List<Integer> findDisappearedNumbers(int[] nums) {
        List<Integer> res = new ArrayList<>();
        for (int i=0; i<nums.length; i++) {
            if (nums[i] == i+1) {
                continue;
            }
            int temp = nums[i];
            nums[i] = -1;
            fillArray(nums, temp-1);
        }

        for (int i=0; i<nums.length; i++) {
            if (nums[i] == -1) {
                res.add(i+1);
            }
        }
        return res;
    }

    public void fillArray(int[] nums, int index) {
        if (nums[index] == index+1) {
            return;
        }
        if (nums[index] == -1) {
            nums[index] = index + 1;
            return;
        }
        int temp = nums[index];
        nums[index] = -1;
        fillArray(nums, temp - 1);
        nums[index] = index + 1;
    }


    public String decodeString(String s) {
        i = 0;
        return getStr(s, new LinkedList<Integer>());
    }

    int i;

    public String getStr(String s, Deque<Integer> stack) {
        if (s == null || s.length() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int freq = 0;
        for (; i<s.length();) {
            char chr = s.charAt(i);
            if (chr >= '0' && chr <= '9') {
                // 当前字符为数字, 计算频率
                freq = freq * 10 + (chr - '0');
                i ++;
            } else if (chr == '[') {
                // 当前字符为左括号, 迭代获取括号内内容
                stack.push(0);
                i++;
                String substr = getStr(s, stack);
                while (freq > 0) {
                    builder.append(substr);
                    freq --;
                }
            } else if (chr == ']') {
                // 当前字符为右括号, 说明正在迭代
                stack.pop();
                i++;
                return builder.toString();
            } else {
                // 当前字符为字母
                builder.append(chr);
                i++;
            }
        }

        return builder.toString();
    }


}
