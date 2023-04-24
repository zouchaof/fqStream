package com.lambda.function;

import java.util.HashMap;
import java.util.Map;

public class SumTest {

    public static String longestPalindrome(String s) {

        int max = 0;
        int start = 0;
        int end = 0;
        char[] sc = s.toCharArray();
        Map<Character, Short> map = new HashMap<>();
        for (short i = 0; i < sc.length; i++) {
            if(map.containsKey(sc[i]) && max < i - map.get(sc[i])){
                start = map.get(sc[i]);
                end = i;
                max = end - start;
            }
            map.put(sc[i], i);
        }
        return s.substring(start, end);
    }

    public static void main(String[] args) {
        System.out.println(longestPalindrome("ababd"));
    }

}
