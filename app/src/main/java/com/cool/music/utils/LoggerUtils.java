package com.cool.music.utils;


import android.support.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * Created by hu.qinghui on 2019/6/17.
 */

public class LoggerUtils {

      static {
          Logger.clearLogAdapters();

          FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                  .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                  .methodCount(0)         // (Optional) How many method line to show. Default 2
                  .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
//                  .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                  .tag("CoolMusic")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                  .build();

          Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

          /**
           * Log adapter checks whether the log should be printed or not by checking this function.
           * If you want to disable/hide logs for output, override isLoggable method. true will print
           * the log message, false will ignore it.
           */
          Logger.addLogAdapter(new AndroidLogAdapter() {
              @Override
              public boolean isLoggable(int priority, @Nullable String tag) {
                  return BuildConfig.DEBUG;
              }
          });
      }

    public static void debug(Class<? extends Object> clazz ,String message){
        Logger.d(message, clazz);
    }

    public static void fmtDebug(Class<? extends Object> clazz,String fmtString,Object...value){
        if(StringUtils.isBlank(fmtString)){
            return ;
        }
        if(null != value && value.length != 0){
            fmtString = String.format(fmtString, value);
        }
        debug(clazz, fmtString);
    }

    public static void error(Class<? extends Object> clazz ,String message,Exception e){
        if(null == e){
            Logger.e(message, clazz);
            return ;
        }
        Logger.e(message, e);
    }

    public static void error(Class<? extends Object> clazz ,String message){
        error(clazz, message, null);
    }

    public static void fmtError(Class<? extends Object> clazz,Exception e,String fmtString,Object...value){
        if(StringUtils.isBlank(fmtString)){
            return ;
        }
        if(null != value && value.length != 0){
            fmtString = String.format(fmtString, value);
        }
        error(clazz, fmtString, e);
    }

    public static void fmtError(Class<? extends Object> clazz,
                                String fmtString, Object...value) {
        if(StringUtils.isBlank(fmtString)){
            return ;
        }
        if(null != value && value.length != 0){
            fmtString = String.format(fmtString, value);
        }
        error(clazz, fmtString);
    }

}
