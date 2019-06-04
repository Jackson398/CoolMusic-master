package com.cool.music.utils;

import android.content.Context;
import android.view.WindowManager;

public class ScreenUtils {
    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    /** Get the screen's width **/
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * The logical density of the display. This is a scaling factor for the
     * Density Independent Pixel unit, where one DIP is one pixel on an
     * approximately 160 dpi screen (for example a 240x320, 1.5"x2" screen),
     * providing the baseline of the system's display. Thus on a 160dpi
     * screen this density value will be 1; on a 120 dpi screen it would be
     * .75; etc.
     *
     * This value does not exactly follow the real screen size (as given by
     * xdpi and ydpi, but rather is used to scale the size of the overall UI
     * in steps based on gross changes in the display dpi. For example, a
     * 240x320 screen will have a density of 1 even if its width is
     * 1.8", 1.3", etc. However, if the screen resolution is increased to
     * 320x480 but the screen size remained 1.5"x2" then the density would
     * be increased (probably to 1.5).
     *
     * Change from dp to px depending on the phone's resolution.
     */
    public static int dp2px(float dpValue) {
        final float scale = sContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** Use reflection to get the height of the status bar **/
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = sContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = sContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
