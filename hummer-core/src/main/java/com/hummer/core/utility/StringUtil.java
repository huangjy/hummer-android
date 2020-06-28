package com.hummer.core.utility;

public class StringUtil {

    public static String uppercaseFirstChar(String string) {
        String str = string;
        if (str.length()>0 &&  !Character.isUpperCase(str.charAt(0))) {
            char[] charArray = str.toCharArray();
            charArray[0] -= 32;
            str = String.valueOf(charArray);
        }
        return str;
    }
}
