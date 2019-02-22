package com.zhanghaochen.smalldemos.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class SysUtils {

    /**
     * 将dp转换为px
     *
     * @param dp
     * @return
     */
    public static int convertDpToPixel(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (metrics.density * dp + 0.5f);
    }

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = GlobalParams.mApplication.getResources().getDisplayMetrics();
        return (int) (metrics.density * dp + 0.5f);
    }

    public static int convertSpToPixel(Context context, float sp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, metrics));
    }

    /**
     * 将字符串转换成算术式并进行计算
     *
     * @param str 算术
     * @return 结果
     */
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) {
                        // addition
                        x += parseTerm();
                    } else if (eat('-')) {
                        // subtraction
                        x -= parseTerm();
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) {
                        // multiplication
                        x *= parseFactor();
                    } else if (eat('/')) {
                        // division
                        x /= parseFactor();
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (eat('+')) {
                    // unary plus
                    return parseFactor();
                }
                if (eat('-')) {
                    // unary minus
                    return -parseFactor();
                }

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    // functions
                    while (ch >= 'a' && ch <= 'z') {
                        nextChar();
                    }
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) {
                        x = Math.sqrt(x);
                    } else if (func.equals("sin")) {
                        x = Math.sin(Math.toRadians(x));
                    } else if (func.equals("cos")) {
                        x = Math.cos(Math.toRadians(x));
                    } else if (func.equals("tan")) {
                        x = Math.tan(Math.toRadians(x));
                    } else {
                        throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) {
                    // exponentiation
                    x = Math.pow(x, parseFactor());
                }
                return x;
            }
        }.parse();
    }

    /**
     * 字符转化为浮点数
     */
    public static float parseFloat(String nStr) {
        float n;
        try {
            n = Float.parseFloat(nStr);
        } catch (Exception e) {
            n = 0.0f;
        }
        return n;
    }

    /**
     * 字符转化为数字(正整数)
     */
    public static int parseInt(String nStr) {
        int n;
        try {
            n = Integer.parseInt(nStr);
        } catch (Exception e) {
            n = -1;
        }
        return n;
    }

    /**
     * 字符转化为数字(正整数)
     */
    public static long parseLong(String nStr) {
        long n;
        try {
            n = Long.parseLong(nStr);
        } catch (Exception e) {
            n = -1;
        }
        return n;
    }

    /**
     * 字符转化为浮点数
     */
    public static double parseDouble(String nStr) {
        double n;
        try {
            n = Double.parseDouble(nStr);
        } catch (Exception e) {
            n = 0.0f;
        }
        return n;
    }

    public static boolean isEmpty(String string) {
        if (string == null || string.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

}
