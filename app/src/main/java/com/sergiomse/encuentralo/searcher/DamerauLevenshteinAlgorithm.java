package com.sergiomse.encuentralo.searcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by sergio on 10/10/2015.
 */
public class DamerauLevenshteinAlgorithm {

    public static void main(String args[]) {

        test02();

    }

    private static void test02() {
        System.out.println(calculateConfidence("Casa", "casé"));
    }


    public static float calculateConfidence(String str1, String str2) {
        str1 = normalizeString(str1);
        str2 = normalizeString(str2);
        return 1.0f - damerauLevenshteinDistance(str1, str2) / (float) Math.max(str1.length(), str2.length());
    }

    private static final String unicode00c0 = "AAAAAAACEEEEIIII" +
            "DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
            "aaaaaaaceeeeiiii" +
            "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
            "AaAaAaCcCcCcCcDd" +
            "DdEeEeEeEeEeGgGg" +
            "GgGgHhHhIiIiIiIi" +
            "IiJjJjKkkLlLlLlL" +
            "lLlNnNnNnnNnOoOo" +
            "OoOoRrRrRrSsSsSs" +
            "SsTtTtTtUuUuUuUu" +
            "UuUuWwYyYZzZzZzF";

    /**
     * Returns string without tildes - 7 bit approximation.
     * Not affecting "ñ" and "ç"
     * @param source string to convert
     * @return corresponding string without diacritics
     */
    private static String removeTildes(String source) {
        char[] result = new char[source.length()];
        char c;
        for (int i = 0; i < source.length(); i++) {
            c = source.charAt(i);
            if (c >= '\u00c0'
                    && c <= '\u017f'
                    && c != '\u00d1' // Ñ
                    && c != '\u00f1' // ñ
                    && c != '\u00c7' // Ç
                    && c != '\u00e7')// ç
            {
                c = unicode00c0.charAt((int) c - '\u00c0');
            }
            result[i] = c;
        }
        return new String(result);
    }


    private static String normalizeString(String str) {
        str = str.trim();
        str = removeTildes(str);
        return str.toLowerCase();
    }

    private static int damerauLevenshteinDistance(String str1, String str2) {
        int score[][] = new int [str1.length() + 2] [str2.length() + 2];

        int INF = str1.length() + str2.length();

        score [0] [0] = INF;

        for (int i = 0; i <= str1.length(); i++) {
            score [i + 1] [1] = i;
            score [i + 1] [0] = INF;
        }

        for (int j = 0; j <= str2.length(); j++) {
            score [1] [j + 1] = j;
            score [0] [j + 1] = INF;
        }

        Map<Character, Integer> sd = new HashMap<>();
        for (Character letter : (str1 + str2).toCharArray())
        {
            if (!sd.containsKey(letter))
                sd.put(letter, 0);
        }

        for (int i = 1; i <= str1.length(); i++)
        {
            int DB = 0;
            for (int j = 1; j <= str2.length(); j++)
            {
                int i1 = sd.get(str2.charAt(j - 1));
                int j1 = DB;

                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    score[i + 1] [j + 1] = score[i] [j];
                    DB = j;
                } else {
                    score[i + 1] [j + 1] = Math.min(score[i] [j], Math.min(score[i + 1] [j], score[i] [j + 1])) + 1;
                }

                score[i + 1] [j + 1] = Math.min(score[i + 1] [j + 1], score[i1] [j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
            }

            sd.put(str1.charAt(i - 1), i);
        }

        return score[str1.length() + 1] [str2.length() + 1];
    }
}
