package com.catas;

import org.junit.Test;

import java.util.*;

public class simpleTest2 {

    @Test
    public void test1() {
        List<List<String>> equations = new ArrayList<>();
        equations.add(Arrays.asList("a", "b"));
        equations.add(Arrays.asList("b", "c"));
        equations.add(Arrays.asList("c", "d"));
        equations.add(Arrays.asList("c", "e"));
        equations.add(Arrays.asList("f", "d"));
        List<List<String>> queries = new ArrayList<>();
        queries.add(Arrays.asList("a", "c"));
        queries.add(Arrays.asList("b", "a"));
        queries.add(Arrays.asList("a", "e"));
        queries.add(Arrays.asList("a", "a"));
        queries.add(Arrays.asList("c", "f"));
        queries.add(Arrays.asList("x", "x"));
        System.out.println(Arrays.toString(calcEquation(equations, new double[]{2, 3, 1, 3, 0.5}, queries)));
    }

    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        int index = 0;
        Map<String, Integer> map = new HashMap<>();
        // 给所有字符编号
        for (List<String> eq: equations) {
            for (String s: eq) {
                if (!map.containsKey(s)) {
                    map.put(s, index ++);
                }
            }
        }

        double[][] graph = new double[index][index];

        for (int i=0; i<values.length; i++) {
            String s1 = equations.get(i).get(0);
            String s2 = equations.get(i).get(1);
            int num1 = map.get(s1);
            int num2 = map.get(s2);
            graph[num1][num2] = values[i];
            graph[num2][num1] = 1 / values[i];
        }

        index = 0;
        double[] res = new double[queries.size()];
        for (List<String> ls: queries) {
            if (!map.containsKey(ls.get(0)) || !map.containsKey(ls.get(1))) {
                res[index++] = -1;
                continue;
            }
            int from = map.get(ls.get(0));
            int to = map.get(ls.get(1));
            res[index ++] = bfs(graph, from, to);
        }
        return res;
    }

    public double bfs(double[][] graph, int from, int to) {
        if (from == to) {
            return 1;
        }

        boolean[] visited = new boolean[graph.length];
        Queue<List<Number>> queue = new LinkedList<>();
        queue.add(new ArrayList<>(Arrays.asList(from, 1.0)));
        while (!queue.isEmpty()) {
            int size = queue.size();
            while (size-- > 0) {
                List<Number> ls = queue.poll();
                Integer num = (Integer) ls.get(0);
                Double weight = (Double) ls.get(1);
                if (visited[num])
                    continue;
                visited[num] = true;
                for (int i=0; i<graph.length; i++) {
                    double val = graph[num][i];
                    if (val != 0) {
                        double curWeight = weight * val;
                        if (i == to)
                            return curWeight;

                        queue.add(new ArrayList<>(Arrays.asList(i,curWeight)));
                    }
                }
            }
        }
        return -1;
    }
}
