package com.xie.marcwidget.utils;

import android.content.Context;

/**
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class DensityUtil {
	 /** 
     * 
     */  
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     *
     */  
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }

    public static String formatNumber(float number, int digitCount, boolean separateThousands) {
        return formatNumber(number, digitCount, separateThousands, '.');
    }

    /**
     * Math.pow(...) is very expensive, so avoid calling it and create it
     * yourself.
     */
    private static final int POW_10[] = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
    };


    public static String formatNumber(float number, int digitCount, boolean separateThousands,
                                      char separateChar) {

        char[] out = new char[35];

        boolean neg = false;
        if (number == 0) {
            return "0";
        }

        boolean zero = false;
        if (number < 1 && number > -1) {
            zero = true;
        }

        if (number < 0) {
            neg = true;
            number = -number;
        }

        if (digitCount > POW_10.length) {
            digitCount = POW_10.length - 1;
        }

        number *= POW_10[digitCount];
        long lval = Math.round(number);
        int ind = out.length - 1;
        int charCount = 0;
        boolean decimalPointAdded = false;

        while (lval != 0 || charCount < (digitCount + 1)) {
            int digit = (int) (lval % 10);
            lval = lval / 10;
            out[ind--] = (char) (digit + '0');
            charCount++;

            // add decimal point
            if (charCount == digitCount) {
                out[ind--] = ',';
                charCount++;
                decimalPointAdded = true;

                // add thousand separators
            } else if (separateThousands && lval != 0 && charCount > digitCount) {

                if (decimalPointAdded) {

                    if ((charCount - digitCount) % 4 == 0) {
                        out[ind--] = separateChar;
                        charCount++;
                    }

                } else {

                    if ((charCount - digitCount) % 4 == 3) {
                        out[ind--] = separateChar;
                        charCount++;
                    }
                }
            }
        }

        // if number around zero (between 1 and -1)
        if (zero) {
            out[ind--] = '0';
            charCount += 1;
        }

        // if the number is negative
        if (neg) {
            out[ind--] = '-';
            charCount += 1;
        }

        int start = out.length - charCount;

        // use this instead of "new String(...)" because of issue < Android 4.0
        return String.valueOf(out, start, out.length - start);
    }
}  

