package com.buuz135.industrial.utils;

import java.text.DecimalFormat;

public class NumberUtils {

    private static DecimalFormat formatterWithUnits = new DecimalFormat("####0.#");

    public static String getFormatedBigNumber(int number) {
        if (number >= 1000000000) { //BILLION
            float numb = number / 1000000000F;
            return formatterWithUnits.format(numb) + "B";
        } else if (number >= 1000000) { //MILLION
            float numb = number / 1000000F;
            if (number > 100000000) numb = Math.round(numb);
            return formatterWithUnits.format(numb) + "M";
        } else if (number >= 1000) { //THOUSANDS
            float numb = number / 1000F;
            if (number > 100000) numb = Math.round(numb);
            return formatterWithUnits.format(numb) + "K";
        }
        return String.valueOf(number);
    }
}
