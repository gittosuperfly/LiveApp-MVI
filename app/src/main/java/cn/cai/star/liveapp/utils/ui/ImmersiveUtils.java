package cn.cai.star.liveapp.utils.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import cn.cai.star.liveapp.utils.RomUtils;
import cn.cai.star.liveapp.utils.ui.MeiZuStatusBarColorUtils;

/**
 * 沉浸式实现方案
 */
public class ImmersiveUtils {

    @SuppressLint("ObsoleteSdkInt")
    public static boolean canStartImmersiveMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isImmersiveMode(@NonNull Activity activity) {
        if (canStartImmersiveMode()) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            return (activity.getWindow().getDecorView().getSystemUiVisibility() & option) == option
                    && !ViewUtils.isFullScreen(activity);
        }
        return false;
    }

    /**
     * @param activity activity
     * @param color    状态栏背景色
     * @param dark     状态栏字体图标是否采用黑色
     */
    public static void startImmersiveMode(@NonNull Activity activity, int color, boolean dark) {
        startImmersiveMode(activity, color, dark, true);
    }

    /**
     * 此方法适用于背景全屏展示的页面，如 QuickLoginActivity
     *
     * @param activity activity
     * @param view     需要偏移的view
     * @param dark     状态栏字体图标是否采用黑色
     */
    public static void startImmersiveMode(@NonNull Activity activity, @NonNull View view,
                                          boolean dark) {
        if (canStartImmersiveMode()) {
            startImmersiveMode(activity, Color.TRANSPARENT, dark, true);
            view.setTranslationY(ViewUtils.getStatusBarHeight(activity));
        }
    }

    public static void adjustViewPaddingTop(@NonNull Context context, @NonNull View view) {
        view.setPadding(view.getPaddingLeft(), ViewUtils.getStatusBarHeight(context),
                view.getPaddingRight(), view.getPaddingBottom());
    }

    /**
     * @param activity            activity
     * @param color               状态栏背景色
     * @param dark                状态栏字体图标是否采用黑色
     * @param customImmersiveMode 是否自定义沉浸式，否表示Content View会设置一个高度为状态栏高度的padding
     */
    public static void startImmersiveMode(@NonNull Activity activity, int color, boolean dark,
                                          boolean customImmersiveMode) {
        if (!canStartImmersiveMode()) {
            return;
        }
        adjustStatusBar(activity, color, dark);
        if (!customImmersiveMode) {
            View view = activity.findViewById(android.R.id.content);
            view.setPadding(0, ViewUtils.getStatusBarHeight(activity), 0, 0);
        }
    }

    /**
     * 实现沉浸式的代码
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void adjustStatusBar(@NonNull Activity activity, int color, boolean dark) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (dark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                option |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                if (RomUtils.isMiui()) {
                    setMIUILightStatusBar(activity, true);
                } else if (RomUtils.isFlyme()) {
                    MeiZuStatusBarColorUtils.setStatusBarDarkIcon(activity, true);
                }
            }
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(color);
            window.setNavigationBarColor(window.getNavigationBarColor());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(option);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public static void setStatusBarColor(Activity activity, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (RomUtils.isMiui()) {
                setMIUILightStatusBar(activity, true);
            } else if (RomUtils.isFlyme()) {
                MeiZuStatusBarColorUtils.setStatusBarDarkIcon(activity, true);
            }
            window.setStatusBarColor(color);
        }
    }

    /**
     * 小米实现沉浸式方法
     *
     * @param activity Activity
     * @param dark     boolean
     */
    public static boolean setMIUILightStatusBar(@NonNull Activity activity, boolean dark) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag;
            @SuppressLint("PrivateApi")
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), dark ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
