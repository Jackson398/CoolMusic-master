package com.cool.music.utils;

/**
 * Created by hu.qinghui on 2019/6/17.
 *
 * String instruments
 * mainly rewrite some methods of StringUtils to make it easier to use.
 */

public class StringUtils {

    /**
     * Determines that multiple or single objects are null at one time.
     * @param objects
     * @return Returns true as long as one element is Blank.
     */
    public static boolean isBlank(Object...objects){
        Boolean result = false ;
        for (Object object : objects) {
            if(null == object || "".equals(object.toString().trim())
                    || "null".equals(object.toString().trim())){
                result = true ;
                break ;
            }
        }
        return result ;
    }

    public static boolean isNotBlank(Object...objects){
        return !isBlank(objects);
    }
    public static boolean isBlank(String...objects){
        Object[] object = objects ;
        return isBlank(object);
    }
    public static boolean isNotBlank(String...objects){
        Object[] object = objects ;
        return !isBlank(object);
    }
    public static boolean isBlank(String str){
        Object object = str ;
        return isBlank(object);
    }
    public static boolean isNotBlank(String str){
        Object object = str ;
        return !isBlank(object);
    }
}
