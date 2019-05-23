package com.test.pulldownrefreshpullupload_master.util;


import java.util.ArrayList;
import java.util.List;

public class GenerateDataUtil {

    public static List<String> generateListItems(int size, String str) {
        List<String> mList = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            mList.add(str + i);
        }
        return mList;
    }
}
