package com.test.pulldownrefreshpullupload_master.pulltorefresh;

public interface Pullable {
    /**
     * To determine whether a drop down is possible, return false if you don't need the drop down
     * function, return true if you need it.
     */
    boolean canPullDown();

    /**
     * To determine whether a pull up is possible, return false if you don't need the pull up
     * function, return true if you need it.
     */
    boolean canPullUp();
}
